/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.block.BlockWeb
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.api.util.world.InventoryUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleFiller
extends Module {
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> packet = this.add(new Setting<Boolean>("Packet", false));
    private final Setting<Boolean> webs = this.add(new Setting<Boolean>("Webs", false));
    private final Setting<Boolean> autoDisable = this.add(new Setting<Boolean>("AutoDisable", true));
    private final Setting<Double> range = this.add(new Setting<Integer>("Radius", (Integer)4.0, (Integer)0.0, 6));
    private final Setting<Boolean> smart = this.add(new Setting<Boolean>("Smart", false).setParent());
    private final Setting<Logic> logic = this.add(new Setting<Logic>("Logic", Logic.PLAYER, v -> this.smart.isOpen()));
    private final Setting<Integer> smartRange = this.add(new Setting<Integer>("EnemyRange", 4, 0, 6, v -> this.smart.isOpen()));
    private final Setting<Boolean> render = this.add(new Setting<Boolean>("Render", true).setParent());
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true, v -> this.render.isOpen()));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true, v -> this.render.isOpen()));
    private final Map<BlockPos, Long> renderBlocks = new ConcurrentHashMap<BlockPos, Long>();
    private final Timer renderTimer = new Timer();
    private EntityPlayer closestTarget;

    public HoleFiller() {
        super("HoleFiller", "Fills all safe spots in radius.", Category.COMBAT, true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.closestTarget = null;
        Managers.ROTATIONS.resetRotationsPacket();
    }

    @Override
    public String getInfo() {
        if (this.smart.getValue().booleanValue()) {
            return Managers.TEXT.normalizeCases((Object)this.logic.getValue());
        }
        return "Normal";
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

    @Override
    public void onUpdate() {
        if (HoleFiller.mc.world == null) {
            return;
        }
        if (this.smart.getValue().booleanValue()) {
            this.findClosestTarget();
        }
        List<BlockPos> blocks = this.getPlacePositions();
        BlockPos q = null;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        if (obbySlot == -1 && eChestSlot == -1) {
            return;
        }
        if (this.webs.getValue().booleanValue() && webSlot == -1 && obbySlot == -1 && eChestSlot == -1) {
            return;
        }
        int originalSlot = HoleFiller.mc.player.inventory.currentItem;
        for (BlockPos blockPos : blocks) {
            if (!HoleFiller.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty()) continue;
            if (this.smart.getValue().booleanValue() && this.isInRange(blockPos)) {
                q = blockPos;
                continue;
            }
            if (this.smart.getValue().booleanValue() && this.isInRange(blockPos) && this.logic.getValue() == Logic.HOLE && this.closestTarget.getDistanceSq(blockPos) <= (double)this.smartRange.getValue().intValue()) {
                q = blockPos;
                continue;
            }
            q = blockPos;
        }
        if (q != null && HoleFiller.mc.player.onGround) {
            HoleFiller.mc.player.inventory.currentItem = this.webs.getValue().booleanValue() ? (webSlot == -1 ? (obbySlot == -1 ? eChestSlot : obbySlot) : webSlot) : (obbySlot == -1 ? eChestSlot : obbySlot);
            HoleFiller.mc.playerController.updateController();
            this.renderBlocks.put(q, System.currentTimeMillis());
            Managers.INTERACTIONS.placeBlock(q, this.rotate.getValue(), this.packet.getValue(), false);
            if (HoleFiller.mc.player.inventory.currentItem != originalSlot) {
                HoleFiller.mc.player.inventory.currentItem = originalSlot;
                HoleFiller.mc.playerController.updateController();
            }
            HoleFiller.mc.player.swingArm(EnumHand.MAIN_HAND);
            HoleFiller.mc.player.inventory.currentItem = originalSlot;
        }
        if (q == null && this.autoDisable.getValue().booleanValue() && !this.smart.getValue().booleanValue()) {
            this.disable();
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

    private boolean isHole(BlockPos pos) {
        BlockPos boost = pos.add(0, 1, 0);
        BlockPos boost2 = pos.add(0, 0, 0);
        BlockPos boost3 = pos.add(0, 0, -1);
        BlockPos boost4 = pos.add(1, 0, 0);
        BlockPos boost5 = pos.add(-1, 0, 0);
        BlockPos boost6 = pos.add(0, 0, 1);
        BlockPos boost7 = pos.add(0, 2, 0);
        BlockPos boost8 = pos.add(0.5, 0.5, 0.5);
        BlockPos boost9 = pos.add(0, -1, 0);
        return !(HoleFiller.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost7).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost3).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost3).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost4).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost4).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost5).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost5).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost6).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost6).getBlock() != Blocks.BEDROCK || HoleFiller.mc.world.getBlockState(boost8).getBlock() != Blocks.AIR || HoleFiller.mc.world.getBlockState(boost9).getBlock() != Blocks.OBSIDIAN && HoleFiller.mc.world.getBlockState(boost9).getBlock() != Blocks.BEDROCK);
    }

    private BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(HoleFiller.mc.player.posX), Math.floor(HoleFiller.mc.player.posY), Math.floor(HoleFiller.mc.player.posZ));
    }

    private BlockPos getClosestTargetPos() {
        if (this.closestTarget != null) {
            return new BlockPos(Math.floor(this.closestTarget.posX), Math.floor(this.closestTarget.posY), Math.floor(this.closestTarget.posZ));
        }
        return null;
    }

    private void findClosestTarget() {
        List playerList = HoleFiller.mc.world.playerEntities;
        this.closestTarget = null;
        for (EntityPlayer target : playerList) {
            if (target == HoleFiller.mc.player || Managers.FRIENDS.isFriend(target.getName()) || !EntityUtil.isLiving((Entity)target) || target.getHealth() <= 0.0f) continue;
            if (this.closestTarget == null) {
                this.closestTarget = target;
                continue;
            }
            if (!(HoleFiller.mc.player.getDistance((Entity)target) < HoleFiller.mc.player.getDistance((Entity)this.closestTarget))) continue;
            this.closestTarget = target;
        }
    }

    private boolean isInRange(BlockPos blockPos) {
        NonNullList positions = NonNullList.create();
        positions.addAll((Collection)this.getSphere(this.getPlayerPos(), this.range.getValue().floatValue(), this.range.getValue().intValue()).stream().filter(this::isHole).collect(Collectors.toList()));
        return positions.contains((Object)blockPos);
    }

    private List<BlockPos> getPlacePositions() {
        NonNullList positions = NonNullList.create();
        if (this.smart.getValue().booleanValue() && this.closestTarget != null) {
            positions.addAll((Collection)this.getSphere(this.getClosestTargetPos(), this.smartRange.getValue().floatValue(), this.range.getValue().intValue()).stream().filter(this::isHole).filter(this::isInRange).collect(Collectors.toList()));
        } else if (!this.smart.getValue().booleanValue()) {
            positions.addAll((Collection)this.getSphere(this.getPlayerPos(), this.range.getValue().floatValue(), this.range.getValue().intValue()).stream().filter(this::isHole).collect(Collectors.toList()));
        }
        return positions;
    }

    private List<BlockPos> getSphere(BlockPos loc, float r, int h) {
        ArrayList<BlockPos> circleBlocks = new ArrayList<BlockPos>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                float f2;
                float f;
                int y = cy - (int)r;
                while ((f = (float)y) < (f2 = (float)cy + r)) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (cy - y) * (cy - y);
                    if (dist < (double)(r * r)) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleBlocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleBlocks;
    }

    private static enum Logic {
        PLAYER,
        HOLE;

    }
}

