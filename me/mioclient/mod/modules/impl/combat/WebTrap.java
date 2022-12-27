/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockWeb
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
import java.util.ArrayList;
import java.util.Comparator;
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
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WebTrap
extends Module {
    public static boolean isPlacing;
    private final Setting<Integer> delay = this.add(new Setting<Integer>("TickDelay", 50, 0, 250));
    private final Setting<Integer> blocksPerPlace = this.add(new Setting<Integer>("BPT", 1, 1, 30));
    private final Setting<Boolean> packet = this.add(new Setting<Boolean>("Packet", false));
    private final Setting<Boolean> disable = this.add(new Setting<Boolean>("AutoDisable", false));
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", true));
    private final Setting<Boolean> raytrace = this.add(new Setting<Boolean>("Raytrace", false));
    private final Setting<Boolean> feet = this.add(new Setting<Boolean>("Feet", true));
    private final Setting<Boolean> face = this.add(new Setting<Boolean>("Face", false));
    private final Setting<Boolean> render = this.add(new Setting<Boolean>("Render", true).setParent());
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true, v -> this.render.isOpen()));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true, v -> this.render.isOpen()));
    private final Timer timer = new Timer();
    private EntityPlayer target;
    private boolean didPlace;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements;
    private BlockPos startPos;
    private final Map<BlockPos, Long> renderBlocks = new ConcurrentHashMap<BlockPos, Long>();
    private final Timer renderTimer = new Timer();

    public WebTrap() {
        super("WebTrap", "Traps other players in webs", Category.COMBAT, true);
    }

    @Override
    public void onEnable() {
        if (WebTrap.fullNullCheck()) {
            return;
        }
        this.startPos = EntityUtil.getRoundedPos((Entity)WebTrap.mc.player);
        this.lastHotbarSlot = WebTrap.mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.doSwap(this.lastHotbarSlot);
    }

    @Override
    public void onTick() {
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
        if (this.check()) {
            return;
        }
        this.doWebTrap();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    private void doWebTrap() {
        List<Vec3d> placeTargets = this.getPlacements();
        this.placeList(placeTargets);
    }

    private List<Vec3d> getPlacements() {
        ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        Vec3d baseVec = this.target.getPositionVector();
        if (this.feet.getValue().booleanValue()) {
            list.add(baseVec);
        }
        if (this.face.getValue().booleanValue()) {
            list.add(baseVec.add(0.0, 1.0, 0.0));
        }
        return list;
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(WebTrap.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), WebTrap.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (Vec3d vec3d3 : list) {
            BlockPos position = new BlockPos(vec3d3);
            int placeability = BlockUtil.getPlaceAbility(position, this.raytrace.getValue());
            if (placeability != 3 && placeability != 1) continue;
            int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
            this.doSwap(webSlot);
            this.renderBlocks.put(position, System.currentTimeMillis());
            this.placeBlock(position);
            this.doSwap(this.lastHotbarSlot);
        }
    }

    private boolean check() {
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (this.isOff()) {
            return true;
        }
        if (this.disable.getValue().booleanValue() && !this.startPos.equals((Object)EntityUtil.getRoundedPos((Entity)WebTrap.mc.player))) {
            this.disable();
            return true;
        }
        if (webSlot == -1) {
            Command.sendMessage("[" + this.getName() + "] " + (Object)ChatFormatting.RED + "No webs in hotbar. disabling...");
            this.toggle();
            return true;
        }
        if (WebTrap.mc.player.inventory.currentItem != this.lastHotbarSlot && WebTrap.mc.player.inventory.currentItem != webSlot) {
            this.lastHotbarSlot = WebTrap.mc.player.inventory.currentItem;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.target = this.getTarget(10.0);
        return this.target == null || !this.timer.passedMs(this.delay.getValue().intValue());
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : WebTrap.mc.world.playerEntities) {
            if (!EntityUtil.isValid((Entity)player, range) || player.isInWeb || Managers.SPEED.getPlayerSpeed(player) > 30.0) continue;
            if (target == null) {
                target = player;
                distance = WebTrap.mc.player.getDistanceSq((Entity)player);
                continue;
            }
            if (!(WebTrap.mc.player.getDistanceSq((Entity)player) < distance)) continue;
            target = player;
            distance = WebTrap.mc.player.getDistanceSq((Entity)player);
        }
        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue() && WebTrap.mc.player.getDistanceSq(pos) <= MathUtil.square(6.0)) {
            isPlacing = true;
            Managers.INTERACTIONS.placeBlock(pos, this.rotate.getValue(), this.packet.getValue(), false, true);
            this.didPlace = true;
            ++this.placements;
        }
    }

    private void doSwap(int slot) {
        WebTrap.mc.player.inventory.currentItem = slot;
        WebTrap.mc.playerController.updateController();
    }
}

