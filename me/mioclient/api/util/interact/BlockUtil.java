/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockCake
 *  net.minecraft.block.BlockCarpet
 *  net.minecraft.block.BlockDeadBush
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityExpBottle
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.mioclient.api.util.interact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.mioclient.api.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockUtil
implements Wrapper {
    public static IBlockState getState(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return BlockUtil.getState(pos).getBlock();
    }

    public static BlockPos[] getHorizontalOffsets(BlockPos pos) {
        return new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west(), pos.down()};
    }

    public static int getPlaceAbility(BlockPos pos, boolean raytrace) {
        return BlockUtil.getPlaceAbility(pos, raytrace, true);
    }

    public static int getPlaceAbility(BlockPos pos, boolean raytrace, boolean checkForEntities) {
        Block block = BlockUtil.getBlock(pos);
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (raytrace && !BlockUtil.raytraceCheck(pos, 0.0f)) {
            return -1;
        }
        if (checkForEntities && BlockUtil.checkForEntities(pos)) {
            return 1;
        }
        for (EnumFacing side : BlockUtil.getPossibleSides(pos)) {
            if (!BlockUtil.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            if (!BlockUtil.getBlock(neighbor).canCollideCheck(BlockUtil.getState(neighbor), false) || BlockUtil.canReplace(neighbor)) continue;
            facings.add(side);
        }
        return facings;
    }

    public static boolean canReplace(BlockPos pos) {
        return BlockUtil.getState(pos).getMaterial().isReplaceable();
    }

    public static boolean canPlaceCrystal(BlockPos pos) {
        BlockPos boost = pos.add(0, 1, 0);
        BlockPos boost2 = pos.add(0, 2, 0);
        try {
            return (BlockUtil.getBlock(pos) == Blocks.BEDROCK || BlockUtil.getBlock(pos) == Blocks.OBSIDIAN) && BlockUtil.getBlock(boost) == Blocks.AIR && BlockUtil.getBlock(boost2) == Blocks.AIR && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtil.getBlock(pos).canCollideCheck(BlockUtil.getState(pos), false);
    }

    public static boolean checkForEntities(BlockPos blockPos) {
        for (Entity entity : BlockUtil.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem || entity instanceof EntityEnderCrystal || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityArrow || !new AxisAlignedBB(blockPos).intersects(entity.getEntityBoundingBox())) continue;
            return true;
        }
        return false;
    }

    public static boolean raytraceCheck(BlockPos pos, float height) {
        return BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d((double)pos.getX(), (double)((float)pos.getY() + height), (double)pos.getZ()), false, true, false) == null;
    }

    public static boolean isHole(BlockPos posIn) {
        for (BlockPos pos : BlockUtil.getHorizontalOffsets(posIn)) {
            if (BlockUtil.getBlock(pos) != Blocks.AIR && (BlockUtil.getBlock(pos) == Blocks.BEDROCK || BlockUtil.getBlock(pos) == Blocks.OBSIDIAN || BlockUtil.getBlock(pos) == Blocks.ENDER_CHEST)) continue;
            return false;
        }
        return true;
    }

    public static boolean isUnsafe(Block block) {
        List<Block> unsafeBlocks = Arrays.asList(new Block[]{Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL});
        return unsafeBlocks.contains((Object)block);
    }

    public static boolean isSlab(Block block) {
        return block instanceof BlockSlab || block instanceof BlockCarpet || block instanceof BlockCake;
    }

    public static boolean isStair(Block block) {
        return block instanceof BlockStairs;
    }

    public static boolean isFence(Block block) {
        return block instanceof BlockFence || block instanceof BlockFenceGate;
    }
}

