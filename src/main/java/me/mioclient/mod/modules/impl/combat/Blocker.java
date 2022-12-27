/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.server.SPacketBlockBreakAnim
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.combat;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blocker
extends Module {
    private final Setting<Boolean> extend = this.add(new Setting<Boolean>("Extend", true));
    private final Setting<Boolean> face = this.add(new Setting<Boolean>("Face", true));
    private final Setting<Boolean> packet = this.add(new Setting<Boolean>("Packet", true));
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> render = this.add(new Setting<Boolean>("Render", true).setParent());
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true, v -> this.render.isOpen()));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true, v -> this.render.isOpen()));
    private final Map<BlockPos, Long> renderBlocks = new ConcurrentHashMap<BlockPos, Long>();
    private final Timer renderTimer = new Timer();

    public Blocker() {
        super("Blocker", "Attempts to extend your surround when it's being broken.", Category.COMBAT, true);
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
        if (event.getPacket() instanceof SPacketBlockBreakAnim && EntityUtil.isInHole((Entity)Blocker.mc.player)) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim)event.getPacket();
            BlockPos pos = packet.getPosition();
            if (Blocker.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || Blocker.mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
                return;
            }
            BlockPos playerPos = new BlockPos(Blocker.mc.player.posX, Blocker.mc.player.posY, Blocker.mc.player.posZ);
            BlockPos placePos = null;
            if (this.extend.getValue().booleanValue()) {
                if (pos.equals((Object)playerPos.north())) {
                    placePos = playerPos.north().north();
                }
                if (pos.equals((Object)playerPos.east())) {
                    placePos = playerPos.east().east();
                }
                if (pos.equals((Object)playerPos.west())) {
                    placePos = playerPos.west().west();
                }
                if (pos.equals((Object)playerPos.south())) {
                    placePos = playerPos.south().south();
                }
            }
            if (this.face.getValue().booleanValue()) {
                if (pos.equals((Object)playerPos.north())) {
                    placePos = playerPos.north().add(0, 1, 0);
                }
                if (pos.equals((Object)playerPos.east())) {
                    placePos = playerPos.east().add(0, 1, 0);
                }
                if (pos.equals((Object)playerPos.west())) {
                    placePos = playerPos.west().add(0, 1, 0);
                }
                if (pos.equals((Object)playerPos.south())) {
                    placePos = playerPos.south().add(0, 1, 0);
                }
            }
            if (placePos != null) {
                this.placeBlock(placePos);
            }
        }
        if (event.getPacket() instanceof SPacketBlockChange && this.renderBlocks.containsKey((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition())) {
            this.renderTimer.reset();
            if (((SPacketBlockChange)event.getPacket()).getBlockState().getBlock() != Blocks.AIR && this.renderTimer.passedMs(400L)) {
                this.renderBlocks.remove((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition());
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (!Blocker.mc.world.isAirBlock(pos)) {
            return;
        }
        int oldSlot = Blocker.mc.player.inventory.currentItem;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSlot == 1) {
            return;
        }
        for (Entity entity : Blocker.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            Blocker.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
            Blocker.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        Blocker.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSlot : obbySlot;
        Blocker.mc.playerController.updateController();
        this.renderBlocks.put(pos, System.currentTimeMillis());
        Managers.INTERACTIONS.placeBlock(pos, this.rotate.getValue(), this.packet.getValue(), true);
        if (Blocker.mc.player.inventory.currentItem != oldSlot) {
            Blocker.mc.player.inventory.currentItem = oldSlot;
            Blocker.mc.playerController.updateController();
        }
        Blocker.mc.player.inventory.currentItem = oldSlot;
    }
}

