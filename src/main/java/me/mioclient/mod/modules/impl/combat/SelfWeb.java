/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package me.mioclient.mod.modules.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Arrays;
import java.util.List;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SelfWeb
extends Module {
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", true));
    private final Setting<Boolean> smart = this.add(new Setting<Boolean>("Smart", false).setParent());
    private final Setting<Integer> enemyRange = this.add(new Setting<Integer>("EnemyRange", 4, 0, 8, v -> this.smart.isOpen()));
    private int newSlot = -1;
    private boolean sneak;
    public final List<Block> blackList = Arrays.asList(new Block[]{Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER});

    public SelfWeb() {
        super("SelfWeb", "Places webs at your feet", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if (SelfWeb.mc.player != null) {
            this.newSlot = this.getHotbarItem();
            if (this.newSlot == -1) {
                Command.sendMessage("[" + this.getName() + "] " + (Object)ChatFormatting.RED + "No Webs in hotbar. disabling...");
                this.toggle();
            }
        }
    }

    @Override
    public void onDisable() {
        if (SelfWeb.mc.player != null && this.sneak) {
            SelfWeb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)SelfWeb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneak = false;
        }
    }

    @Override
    public void onUpdate() {
        if (SelfWeb.fullNullCheck()) {
            return;
        }
        if (this.smart.getValue().booleanValue()) {
            EntityPlayer target = this.getClosestTarget();
            if (target == null) {
                return;
            }
            if (Managers.FRIENDS.isFriend(target.getName())) {
                return;
            }
            if (SelfWeb.mc.player.getDistance((Entity)target) < (float)this.enemyRange.getValue().intValue() && this.isSafe()) {
                int last_slot = SelfWeb.mc.player.inventory.currentItem;
                SelfWeb.mc.player.inventory.currentItem = this.newSlot;
                SelfWeb.mc.playerController.updateController();
                this.placeBlock(this.getFloorPos());
                SelfWeb.mc.player.inventory.currentItem = last_slot;
            }
        } else {
            int last_slot = SelfWeb.mc.player.inventory.currentItem;
            SelfWeb.mc.player.inventory.currentItem = this.newSlot;
            SelfWeb.mc.playerController.updateController();
            this.placeBlock(this.getFloorPos());
            SelfWeb.mc.player.inventory.currentItem = last_slot;
            this.disable();
        }
    }

    private EntityPlayer getClosestTarget() {
        if (SelfWeb.mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : SelfWeb.mc.world.playerEntities) {
            if (target == SelfWeb.mc.player || !EntityUtil.isLiving((Entity)target) || target.getHealth() <= 0.0f || closestTarget != null && SelfWeb.mc.player.getDistance((Entity)target) > SelfWeb.mc.player.getDistance((Entity)closestTarget)) continue;
            closestTarget = target;
        }
        return closestTarget;
    }

    private int getHotbarItem() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = SelfWeb.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != Item.getItemById((int)30)) continue;
            return i;
        }
        return -1;
    }

    private boolean isSafe() {
        BlockPos player_block = this.getFloorPos();
        return SelfWeb.mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR && SelfWeb.mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR && SelfWeb.mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR && SelfWeb.mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR && SelfWeb.mc.world.getBlockState(player_block).getBlock() == Blocks.AIR;
    }

    private void placeBlock(BlockPos pos) {
        if (!SelfWeb.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return;
        }
        if (!this.checkForNeighbours(pos)) {
            return;
        }
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!this.canBeClicked(neighbor)) continue;
            if (this.blackList.contains((Object)SelfWeb.mc.world.getBlockState(neighbor).getBlock())) {
                SelfWeb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)SelfWeb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                this.sneak = true;
            }
            Vec3d hitVec = new Vec3d((Vec3i)neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
            if (this.rotate.getValue().booleanValue()) {
                Managers.ROTATIONS.lookAtVec3dPacket(hitVec, false);
            }
            SelfWeb.mc.playerController.processRightClickBlock(SelfWeb.mc.player, SelfWeb.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            SelfWeb.mc.player.swingArm(EnumHand.MAIN_HAND);
            return;
        }
    }

    private boolean checkForNeighbours(BlockPos blockPos) {
        if (!this.hasNeighbour(blockPos)) {
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = blockPos.offset(side);
                if (!this.hasNeighbour(neighbour)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (SelfWeb.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) continue;
            return true;
        }
        return false;
    }

    private boolean canBeClicked(BlockPos pos) {
        return BlockUtil.getBlock(pos).canCollideCheck(BlockUtil.getState(pos), false);
    }

    private BlockPos getFloorPos() {
        return new BlockPos(Math.floor(SelfWeb.mc.player.posX), Math.floor(SelfWeb.mc.player.posY), Math.floor(SelfWeb.mc.player.posZ));
    }
}

