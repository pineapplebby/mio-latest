/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.api.util.world.InventoryUtil;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTrap
extends Module {
    public static AutoTrap INSTANCE;
    private final Setting<Integer> tickDelay = this.add(new Setting<Integer>("TickDelay", 50, 0, 250));
    private final Setting<Integer> blocksPerTick = this.add(new Setting<Integer>("BPT", 2, 1, 30));
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", true));
    private final Setting<Boolean> raytrace = this.add(new Setting<Boolean>("Raytrace", false));
    private final Setting<Boolean> extraTop = this.add(new Setting<Boolean>("Extra", false));
    private final Setting<Boolean> antiStep = this.add(new Setting<Boolean>("AntiStep", false));
    private final Setting<Boolean> legs = this.add(new Setting<Boolean>("Legs", false));
    private final Setting<Boolean> packet = this.add(new Setting<Boolean>("Packet", false));
    private final Setting<Boolean> clean = this.add(new Setting<Boolean>("Clean", true));
    private final Setting<Boolean> eChests = this.add(new Setting<Boolean>("EChests", true));
    private final Setting<Boolean> render = this.add(new Setting<Boolean>("Render", true).setParent());
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true, v -> this.render.isOpen()));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true, v -> this.render.isOpen()));
    public static boolean isPlacing;
    private final Timer delayTimer = new Timer();
    private final Map<BlockPos, Integer> retryMap = new HashMap<BlockPos, Integer>();
    private final Map<BlockPos, Long> renderBlocks = new ConcurrentHashMap<BlockPos, Long>();
    private final Timer renderTimer = new Timer();
    private final Timer retryTimer = new Timer();
    private EntityPlayer target;
    private boolean didPlace;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements;
    private BlockPos startPos;

    public AutoTrap() {
        super("AutoTrap", "Traps other players", Category.COMBAT, true);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (AutoTrap.fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedPos((Entity)AutoTrap.mc.player);
        this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        this.retryMap.clear();
    }

    @Override
    public void onDisable() {
        if (AutoTrap.fullNullCheck()) {
            return;
        }
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        Managers.ROTATIONS.resetRotationsPacket();
    }

    @Override
    public void onTick() {
        if (AutoTrap.fullNullCheck()) {
            return;
        }
        this.doTrap();
    }

    @Override
    public String getInfo() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.render.getValue().booleanValue()) {
            this.renderTimer.reset();
            this.renderBlocks.forEach((pos, time) -> {
                int lineA = 255;
                int fillA = 80;
                if (System.currentTimeMillis() - time > 400L) {
                    this.renderTimer.reset();
                    this.renderBlocks.remove(pos);
                } else {
                    long endTime = System.currentTimeMillis() - time - 100L;
                    double normal = MathUtil.normalize(endTime, 0.0, 500.0);
                    normal = MathHelper.clamp((double)normal, (double)0.0, (double)1.0);
                    normal = -normal + 1.0;
                    int firstAl = (int)(normal * (double)lineA);
                    int secondAl = (int)(normal * (double)fillA);
                    RenderUtil.drawBoxESP(new BlockPos((Vec3i)pos), Managers.COLORS.getCurrent(), true, new Color(255, 255, 255, firstAl), 0.7f, this.line.getValue(), this.box.getValue(), secondAl, true, 0.0);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockChange && this.renderBlocks.containsKey((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition())) {
            this.renderTimer.reset();
            if (((SPacketBlockChange)event.getPacket()).getBlockState().getBlock() != Blocks.AIR && this.renderTimer.passedMs(400L)) {
                this.renderBlocks.remove((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition());
            }
        }
    }

    private void doTrap() {
        if (AutoTrap.nullCheck() || this.check()) {
            return;
        }
        for (Vec3d pos : this.getOffsets()) {
            BlockPos currentPos = new BlockPos(pos);
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            int oldSlot = AutoTrap.mc.player.inventory.currentItem;
            if (obbySlot == -1 && eChestSlot == -1) {
                Command.sendMessage("[" + this.getName() + "] " + (Object)ChatFormatting.RED + "No obi in hotbar. disabling...");
                this.disable();
            }
            if (BlockUtil.getPlaceAbility(currentPos, this.raytrace.getValue()) == 1 && (this.retryMap.get((Object)currentPos) == null || this.retryMap.get((Object)currentPos) < 4)) {
                this.placeBlock(currentPos, obbySlot == -1 && this.eChests.getValue() != false ? eChestSlot : obbySlot, oldSlot);
                this.retryMap.put(currentPos, this.retryMap.get((Object)currentPos) == null ? 1 : this.retryMap.get((Object)currentPos) + 1);
                this.retryTimer.reset();
                continue;
            }
            if (BlockUtil.getPlaceAbility(currentPos, this.raytrace.getValue()) != 3) continue;
            this.renderBlocks.put(currentPos, System.currentTimeMillis());
            this.placeBlock(currentPos, obbySlot == -1 && this.eChests.getValue() != false ? eChestSlot : obbySlot, oldSlot);
        }
        if (this.didPlace) {
            this.delayTimer.reset();
        }
    }

    private List<Vec3d> getOffsets() {
        boolean onEChest = BlockUtil.getBlock(new BlockPos(this.target.getPositionVector())) == Blocks.ENDER_CHEST && this.target.posY - (double)((int)this.target.posY) > 0.5;
        List<Vec3d> vec = EntityUtil.getTrapOffsetList(this.target.getPositionVector().add(0.0, onEChest ? 1.0 : 0.0, 0.0), this.extraTop.getValue(), this.antiStep.getValue(), this.legs.getValue(), false, false, this.raytrace.getValue());
        vec.sort((vec3d, vec3d2) -> Double.compare(AutoTrap.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoTrap.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        vec.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        return vec;
    }

    private boolean check() {
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.isOff()) {
            return true;
        }
        if (!this.startPos.equals((Object)EntityUtil.getRoundedPos((Entity)AutoTrap.mc.player))) {
            this.disable();
            return true;
        }
        if (this.retryTimer.passedMs(2000L)) {
            this.retryMap.clear();
            this.retryTimer.reset();
        }
        if (AutoTrap.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoTrap.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(10.0, true);
        return this.target == null || !this.delayTimer.passedMs(this.tickDelay.getValue().intValue());
    }

    private EntityPlayer getTarget(double range, boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : AutoTrap.mc.world.playerEntities) {
            if (!EntityUtil.isValid((Entity)player, range) || trapped && EntityUtil.isTrapped(player, this.extraTop.getValue(), this.antiStep.getValue(), false, false, false) || Managers.SPEED.getPlayerSpeed(player) > 10.0) continue;
            if (target == null) {
                target = player;
                distance = AutoTrap.mc.player.getDistanceSq((Entity)player);
                continue;
            }
            if (!(AutoTrap.mc.player.getDistanceSq((Entity)player) < distance)) continue;
            target = player;
            distance = AutoTrap.mc.player.getDistanceSq((Entity)player);
        }
        return target;
    }

    private void placeBlock(BlockPos pos, int blockSlot, int oldSlot) {
        if (this.placements < this.blocksPerTick.getValue() && AutoTrap.mc.player.getDistanceSq(pos) <= MathUtil.square(5.0)) {
            isPlacing = true;
            if (BlockUtil.checkForEntities(pos)) {
                return;
            }
            AutoTrap.mc.player.inventory.currentItem = blockSlot;
            AutoTrap.mc.playerController.updateController();
            this.renderBlocks.put(pos, System.currentTimeMillis());
            Managers.INTERACTIONS.placeBlock(pos, this.rotate.getValue(), this.packet.getValue(), this.clean.getValue());
            AutoTrap.mc.player.inventory.currentItem = oldSlot;
            AutoTrap.mc.playerController.updateController();
        }
        this.didPlace = true;
        ++this.placements;
    }
}

