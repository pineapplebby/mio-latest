/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.player;

import java.awt.Color;
import me.mioclient.api.events.impl.MotionUpdateEvent;
import me.mioclient.api.events.impl.MoveEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.events.impl.UpdateWalkingPlayerEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.asm.accessors.IEntityPlayerSP;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.impl.client.ClickGui;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold
extends Module {
    private final Setting<Boolean> tower = this.add(new Setting<Boolean>("Tower", true));
    private final Setting<Boolean> stopMotion = this.add(new Setting<Boolean>("StopMotion", true));
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", true));
    private final Setting<Boolean> autoSwitch = this.add(new Setting<Boolean>("AutoSwap", true));
    private final Setting<Boolean> ignoreWebs = this.add(new Setting<Boolean>("IgnoreWebs", true));
    private final Setting<Boolean> ignoreEChests = this.add(new Setting<Boolean>("IgnoreEChests", true));
    private final Setting<Boolean> render = this.add(new Setting<Boolean>("Render", true).setParent());
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true, v -> this.render.isOpen()));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true, v -> this.render.isOpen()));
    private BlockPosWithFacing current;
    private final Timer timer = new Timer();

    public Scaffold() {
        super("Scaffold", "Block fly.", Category.PLAYER, true);
    }

    @Override
    public String getInfo() {
        return String.valueOf(this.getValidBlocks());
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.render.getValue().booleanValue() && this.current != null) {
            GlStateManager.pushMatrix();
            RenderUtil.drawBoxESP(this.current.pos, ClickGui.INSTANCE.rainbow.getValue() != false ? Managers.COLORS.getRainbow() : Managers.COLORS.getCurrent(), true, new Color(255, 255, 255, 255), 0.9f, this.line.getValue(), this.box.getValue(), 80, true, 0.0);
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue() && this.current != null && this.getValidBlocks() > 0) {
            float packetYaw;
            float[] rotations = this.getRotations(this.current.pos, this.current.facing);
            float diff = MathHelper.wrapDegrees((float)(rotations[0] - (packetYaw = ((IEntityPlayerSP)Scaffold.mc.player).getLastReportedYaw())));
            if (Math.abs(diff) > 90.0f) {
                rotations[0] = packetYaw + diff * (90.0f / Math.abs(diff));
            }
            Managers.ROTATIONS.setRotations(rotations[0], rotations[1]);
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (Scaffold.nullCheck()) {
            return;
        }
        if (this.stopMotion.getValue().booleanValue()) {
            this.stopMotion(event);
        }
    }

    @SubscribeEvent
    public void onMotionUpdate(MotionUpdateEvent event) {
        block9: {
            BlockPos blockPos;
            Scaffold scaffold;
            int n;
            block12: {
                block11: {
                    block10: {
                        int n3;
                        block8: {
                            Block block;
                            boolean full;
                            double offset;
                            BlockPos blockPos2;
                            Item item;
                            int n2;
                            if (Scaffold.nullCheck()) {
                                return;
                            }
                            if (this.getValidBlocks() <= 0 || Double.compare(Scaffold.mc.player.posY, 257.0) > 0) {
                                this.current = null;
                                return;
                            }
                            if (this.getValidBlocks() <= 0 || !this.autoSwitch.getValue().booleanValue() && !(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
                                return;
                            }
                            if (event.stage != 0) break block8;
                            this.current = null;
                            if (!Scaffold.mc.player.isSneaking() && (n2 = this.getBlockSlot()) != -1 && (item = Scaffold.mc.player.inventory.getStackInSlot(n2).getItem()) instanceof ItemBlock && Scaffold.mc.world.getBlockState(blockPos2 = new BlockPos(Scaffold.mc.player.posX, Scaffold.mc.player.posY - (offset = (full = (block = ((ItemBlock)item).getBlock()).getDefaultState().isFullBlock()) ? 1.0 : 0.01), Scaffold.mc.player.posZ)).getMaterial().isReplaceable() && (full || this.blockCheck(n2))) {
                                Scaffold scaffold2 = this;
                                scaffold2.current = this.extendNeighbours(blockPos2);
                                if (scaffold2.current != null && this.rotate.getValue().booleanValue()) {
                                    float[] rotations = this.getRotations(this.current.pos, this.current.facing);
                                    event.rotationYaw = rotations[0];
                                    event.rotationPitch = rotations[1];
                                    return;
                                }
                            }
                            break block9;
                        }
                        if (this.current == null) break block9;
                        n = Scaffold.mc.player.inventory.currentItem;
                        if (!(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && this.isBlockValid(((ItemBlock)Scaffold.mc.player.getHeldItemMainhand().getItem()).getBlock()) || !this.autoSwitch.getValue().booleanValue() || (n3 = this.getBlockSlot()) == -1)) {
                            Scaffold.mc.player.inventory.currentItem = n3;
                            Scaffold.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(Scaffold.mc.player.inventory.currentItem));
                        }
                        if (!Scaffold.mc.player.movementInput.jump || Scaffold.mc.player.moveForward != 0.0f || Scaffold.mc.player.moveStrafing != 0.0f || !this.tower.getValue().booleanValue()) break block10;
                        Scaffold.mc.player.setVelocity(0.0, 0.42, 0.0);
                        if (!this.timer.passed(1500L)) break block11;
                        Scaffold.mc.player.motionY = -0.28;
                        scaffold = this;
                        this.timer.reset();
                        break block12;
                    }
                    this.timer.reset();
                }
                scaffold = this;
            }
            BlockPos blockPos3 = blockPos = scaffold.current.pos;
            boolean shouldSneak = Scaffold.mc.world.getBlockState(blockPos).getBlock().onBlockActivated((World)Scaffold.mc.world, blockPos3, Scaffold.mc.world.getBlockState(blockPos3), (EntityPlayer)Scaffold.mc.player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
            if (shouldSneak) {
                Scaffold.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Scaffold.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, blockPos, this.current.facing, new Vec3d((double)blockPos.getX() + Math.random(), Scaffold.mc.world.getBlockState((BlockPos)blockPos).getSelectedBoundingBox((World)Scaffold.mc.world, (BlockPos)blockPos).maxY - 0.01, (double)blockPos.getZ() + Math.random()), EnumHand.MAIN_HAND);
            Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
            if (shouldSneak) {
                Scaffold.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Scaffold.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            Scaffold.mc.player.inventory.currentItem = n;
            Scaffold.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(Scaffold.mc.player.inventory.currentItem));
        }
    }

    private boolean isOffsetBBEmpty(double x, double y, double z) {
        return Scaffold.mc.world.getCollisionBoxes((Entity)Scaffold.mc.player, Scaffold.mc.player.getEntityBoundingBox().offset(x, y, z)).isEmpty();
    }

    private void stopMotion(MoveEvent event) {
        double x = event.motionX;
        double y = event.motionY;
        double z = event.motionZ;
        if (Scaffold.mc.player.onGround && !Scaffold.mc.player.noClip) {
            double increment = 0.05;
            while (x != 0.0 && this.isOffsetBBEmpty(x, -2.0, 0.0)) {
                if (x < increment && x >= -increment) {
                    x = 0.0;
                    continue;
                }
                if (x > 0.0) {
                    x -= increment;
                    continue;
                }
                x += increment;
            }
            while (z != 0.0 && this.isOffsetBBEmpty(0.0, -2.0, z)) {
                if (z < increment && z >= -increment) {
                    z = 0.0;
                    continue;
                }
                if (z > 0.0) {
                    z -= increment;
                    continue;
                }
                z += increment;
            }
            while (x != 0.0 && z != 0.0 && this.isOffsetBBEmpty(x, -2.0, z)) {
                x = x < increment && x >= -increment ? 0.0 : (x > 0.0 ? (x -= increment) : (x += increment));
                if (z < increment && z >= -increment) {
                    z = 0.0;
                    continue;
                }
                if (z > 0.0) {
                    z -= increment;
                    continue;
                }
                z += increment;
            }
        }
        event.motionX = x;
        event.motionY = y;
        event.motionZ = z;
    }

    private boolean isBlockValid(Block block) {
        return block.getDefaultState().getMaterial().isSolid();
    }

    private BlockPosWithFacing checkForNeighbours(BlockPos pos) {
        if (this.isBlockValid(Scaffold.mc.world.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockPosWithFacing(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.isBlockValid(Scaffold.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
            return new BlockPosWithFacing(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.isBlockValid(Scaffold.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock())) {
            return new BlockPosWithFacing(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.isBlockValid(Scaffold.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock())) {
            return new BlockPosWithFacing(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.isBlockValid(Scaffold.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock())) {
            return new BlockPosWithFacing(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private BlockPosWithFacing extendNeighbours(BlockPos pos) {
        BlockPosWithFacing extended = this.checkForNeighbours(pos);
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(-1, 0, 0));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(1, 0, 0));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(0, 0, 1));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(0, 0, -1));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(-2, 0, 0));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(2, 0, 0));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(0, 0, 2));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(0, 0, -2));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(pos.add(0, -1, 0));
        BlockPos blockPos2 = pos.add(0, -1, 0);
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(blockPos2.add(1, 0, 0));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(blockPos2.add(-1, 0, 0));
        if (extended != null) {
            return extended;
        }
        extended = this.checkForNeighbours(blockPos2.add(0, 0, 1));
        if (extended != null) {
            return extended;
        }
        return this.checkForNeighbours(blockPos2.add(0, 0, -1));
    }

    private int getBlockSlot() {
        if (Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && this.isBlockValid(((ItemBlock)Scaffold.mc.player.getHeldItemMainhand().getItem()).getBlock())) {
            return Scaffold.mc.player.inventory.currentItem;
        }
        int n = 0;
        int n2 = 0;
        while (n2 < 9) {
            if (Scaffold.mc.player.inventory.getStackInSlot(n).getCount() != 0 && Scaffold.mc.player.inventory.getStackInSlot(n).getItem() instanceof ItemBlock) {
                if ((!this.ignoreEChests.getValue().booleanValue() || this.ignoreEChests.getValue().booleanValue() && !Scaffold.mc.player.inventory.getStackInSlot(n).getItem().equals((Object)Item.getItemFromBlock((Block)Blocks.ENDER_CHEST))) && this.isBlockValid(((ItemBlock)Scaffold.mc.player.inventory.getStackInSlot(n).getItem()).getBlock())) {
                    return n;
                }
                if ((!this.ignoreWebs.getValue().booleanValue() || this.ignoreWebs.getValue().booleanValue() && !Scaffold.mc.player.inventory.getStackInSlot(n).getItem().equals((Object)Item.getItemFromBlock((Block)Blocks.WEB))) && this.isBlockValid(((ItemBlock)Scaffold.mc.player.inventory.getStackInSlot(n).getItem()).getBlock())) {
                    return n;
                }
            }
            n2 = ++n;
        }
        return -1;
    }

    private boolean blockCheck(int in) {
        Item item = Scaffold.mc.player.inventory.getStackInSlot(in).getItem();
        if (item instanceof ItemBlock) {
            Vec3d vec3d = Scaffold.mc.player.getPositionVector();
            Block block = ((ItemBlock)item).getBlock();
            return Scaffold.mc.world.rayTraceBlocks(vec3d, vec3d.add(0.0, -block.getDefaultState().getSelectedBoundingBox((World)Scaffold.mc.world, (BlockPos)BlockPos.ORIGIN).maxY, 0.0), false, true, false) == null;
        }
        return false;
    }

    private int getValidBlocks() {
        int n2 = 0;
        for (int n = 36; n < 45; ++n) {
            ItemStack itemStack;
            if (!Scaffold.mc.player.inventoryContainer.getSlot(n).getHasStack() || !((itemStack = Scaffold.mc.player.inventoryContainer.getSlot(n).getStack()).getItem() instanceof ItemBlock) || !this.isBlockValid(((ItemBlock)itemStack.getItem()).getBlock())) continue;
            n2 += itemStack.getCount();
        }
        return n2;
    }

    private Vec3d getPositionEyes() {
        return new Vec3d(Scaffold.mc.player.posX, Scaffold.mc.player.posY + (double)Scaffold.mc.player.getEyeHeight(), Scaffold.mc.player.posZ);
    }

    private float[] getRotations(BlockPos pos, EnumFacing facing) {
        Vec3d vec3d = new Vec3d((double)pos.getX() + 0.5, Scaffold.mc.world.getBlockState((BlockPos)pos).getSelectedBoundingBox((World)Scaffold.mc.world, (BlockPos)pos).maxY - 0.01, (double)pos.getZ() + 0.5);
        vec3d = vec3d.add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        Vec3d eyes = this.getPositionEyes();
        double d = vec3d.x - eyes.x;
        double d2 = vec3d.y - eyes.y;
        double d3 = vec3d.z - eyes.z;
        double d6 = Math.sqrt(d * d + d3 * d3);
        float f = (float)(Math.toDegrees(Math.atan2(d3, d)) - 90.0);
        float f2 = (float)(-Math.toDegrees(Math.atan2(d2, d6)));
        float[] ret = new float[]{Scaffold.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(f - Scaffold.mc.player.rotationYaw)), Scaffold.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(f2 - Scaffold.mc.player.rotationPitch))};
        return ret;
    }

    public static class BlockPosWithFacing {
        public BlockPos pos;
        public EnumFacing facing;

        public BlockPosWithFacing(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }
    }
}

