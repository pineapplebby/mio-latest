/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityShulkerBox
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.combat;

import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.world.InventoryUtil;
import me.mioclient.asm.accessors.ICPacketPlayer;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiRegear
extends Module {
    private final Setting<Integer> range = this.add(new Setting<Integer>("Range", 5, 0, 8));
    private final Setting<Boolean> autoSwap = this.add(new Setting<Boolean>("AutoSwap", true));
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", true));
    private float yaw;
    private float pitch;

    public AntiRegear() {
        super("AntiRegear", "Shulker nuker.", Category.COMBAT, true);
    }

    @Override
    public void onDisable() {
        if (this.rotate.getValue().booleanValue()) {
            this.yaw = AntiRegear.mc.player.rotationYaw;
            this.pitch = AntiRegear.mc.player.rotationPitch;
        }
    }

    @Override
    public void onUpdate() {
        if (this.getBlock() != null) {
            int oldSlot = AntiRegear.mc.player.inventory.currentItem;
            if (this.autoSwap.getValue().booleanValue() && InventoryUtil.findItemInHotbar(Items.DIAMOND_PICKAXE) != -1) {
                AntiRegear.mc.player.inventory.currentItem = InventoryUtil.findItemInHotbar(Items.DIAMOND_PICKAXE);
            }
            if (this.rotate.getValue().booleanValue()) {
                Vec3d vec = new Vec3d((double)this.getBlock().getPos().getX() + 0.5, (double)(this.getBlock().getPos().getY() - 1), (double)this.getBlock().getPos().getZ() + 0.5);
                float[] rotations = Managers.ROTATIONS.getAngle(vec);
                this.yaw = rotations[0];
                this.pitch = rotations[1];
            }
            AntiRegear.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.getBlock().getPos(), EnumFacing.SOUTH));
            AntiRegear.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.getBlock().getPos(), EnumFacing.SOUTH));
            AntiRegear.mc.player.inventory.currentItem = oldSlot;
        }
        if (this.rotate.getValue().booleanValue() && this.getBlock() == null) {
            this.yaw = AntiRegear.mc.player.rotationYaw;
            this.pitch = AntiRegear.mc.player.rotationPitch;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        Object packet;
        if (this.rotate.getValue().booleanValue() && (packet = event.getPacket()) instanceof CPacketPlayer) {
            ((ICPacketPlayer)packet).setYaw(this.yaw);
            ((ICPacketPlayer)packet).setPitch(this.pitch);
        }
    }

    private TileEntity getBlock() {
        TileEntity out = null;
        for (TileEntity entity : AntiRegear.mc.world.loadedTileEntityList) {
            if (!(entity instanceof TileEntityShulkerBox) || !(entity.getDistanceSq(AntiRegear.mc.player.posX, AntiRegear.mc.player.posY, AntiRegear.mc.player.posZ) <= (double)(this.range.getValue() * this.range.getValue()))) continue;
            out = entity;
        }
        return out;
    }
}

