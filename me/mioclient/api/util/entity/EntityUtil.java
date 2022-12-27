/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockDeadBush
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAgeable
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityMinecart
 *  net.minecraft.entity.monster.EntityEnderman
 *  net.minecraft.entity.monster.EntityIronGolem
 *  net.minecraft.entity.monster.EntityPigZombie
 *  net.minecraft.entity.passive.EntityAmbientCreature
 *  net.minecraft.entity.passive.EntitySquid
 *  net.minecraft.entity.passive.EntityWolf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityFireball
 *  net.minecraft.entity.projectile.EntityShulkerBullet
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package me.mioclient.api.util.entity;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityUtil
implements Wrapper {
    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null) {
                return true;
            }
            if (EntityUtil.getDamagePercent(piece) >= durability) continue;
            return true;
        }
        return false;
    }

    public static boolean isFeetVisible(Entity entity) {
        return EntityUtil.mc.world.rayTraceBlocks(new Vec3d(EntityUtil.mc.player.posX, EntityUtil.mc.player.posX + (double)EntityUtil.mc.player.getEyeHeight(), EntityUtil.mc.player.posZ), new Vec3d(entity.posX, entity.posY, entity.posZ), false, true, false) == null;
    }

    public static boolean isValid(Entity entity, double range) {
        boolean invalid = entity == null || EntityUtil.isDead(entity) || entity.equals((Object)EntityUtil.mc.player) || entity instanceof EntityPlayer && Managers.FRIENDS.isFriend(entity.getName()) || EntityUtil.mc.player.getDistanceSq(entity) > MathUtil.square(range);
        return !invalid;
    }

    public static boolean isInHole(Entity entity) {
        return BlockUtil.isHole(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        return EntityUtil.getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
    }

    public static boolean isHoldingWeapon(EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemAxe;
    }

    public static boolean isHolding32k(EntityPlayer player) {
        return EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.SHARPNESS, (ItemStack)player.getHeldItemMainhand()) >= 1000;
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocksList(entity, height, floor).size() == 0;
    }

    public static boolean isSafe(Entity entity) {
        return EntityUtil.isSafe(entity, 0, false);
    }

    public static boolean isPassive(Entity entity) {
        if (entity instanceof EntityWolf && ((EntityWolf)entity).isAngry()) {
            return false;
        }
        if (entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid) {
            return true;
        }
        return entity instanceof EntityIronGolem && ((EntityIronGolem)entity).getRevengeTarget() == null;
    }

    public static boolean isMobAggressive(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            if (((EntityPigZombie)entity).isArmsRaised() || ((EntityPigZombie)entity).isAngry()) {
                return true;
            }
        } else {
            if (entity instanceof EntityWolf) {
                return ((EntityWolf)entity).isAngry() && !EntityUtil.mc.player.equals((Object)((EntityWolf)entity).getOwner());
            }
            if (entity instanceof EntityEnderman) {
                return ((EntityEnderman)entity).isScreaming();
            }
        }
        return EntityUtil.isHostileMob(entity);
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    public static boolean isProjectile(Entity entity) {
        return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isHostileMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !EntityUtil.isNeutralMob(entity);
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isAlive(Entity entity) {
        return EntityUtil.isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0f;
    }

    public static boolean isDead(Entity entity) {
        return !EntityUtil.isAlive(entity);
    }

    public static EntityPlayer getCopiedPlayer(EntityPlayer player) {
        final int count = player.getItemInUseCount();
        EntityPlayer copied = new EntityPlayer((World)EntityUtil.mc.world, new GameProfile(UUID.randomUUID(), player.getName())){

            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return false;
            }

            public int getItemInUseCount() {
                return count;
            }
        };
        copied.setSneaking(player.isSneaking());
        copied.swingProgress = player.swingProgress;
        copied.limbSwing = player.limbSwing;
        copied.limbSwingAmount = player.prevLimbSwingAmount;
        copied.inventory.copyInventory(player.inventory);
        copied.setPrimaryHand(player.getPrimaryHand());
        copied.ticksExisted = player.ticksExisted;
        copied.setEntityId(player.getEntityId());
        copied.copyLocationAndAnglesFrom((Entity)player);
        return copied;
    }

    public static int getHitCoolDown(EntityPlayer player) {
        Item item = player.getHeldItemMainhand().getItem();
        if (item instanceof ItemSword) {
            return 600;
        }
        if (item instanceof ItemPickaxe) {
            return 850;
        }
        if (item == Items.IRON_AXE) {
            return 1100;
        }
        if (item == Items.STONE_HOE) {
            return 500;
        }
        if (item == Items.IRON_HOE) {
            return 350;
        }
        if (item == Items.WOODEN_AXE || item == Items.STONE_AXE) {
            return 1250;
        }
        if (item instanceof ItemSpade || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.WOODEN_HOE || item == Items.GOLDEN_HOE) {
            return 1000;
        }
        return 250;
    }

    public static EntityPlayer getClosestEnemy(double distance) {
        EntityPlayer closest = null;
        for (EntityPlayer player : EntityUtil.mc.world.playerEntities) {
            if (!EntityUtil.isValid((Entity)player, distance)) continue;
            if (closest == null) {
                closest = player;
                continue;
            }
            if (!(EntityUtil.mc.player.getDistanceSq((Entity)player) < EntityUtil.mc.player.getDistanceSq((Entity)closest))) continue;
            closest = player;
        }
        return closest;
    }

    public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean extraTop, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        if (!antiStep && EntityUtil.getUnsafeBlocksList((Entity)player, 2, false).size() == 4) {
            vec3ds.addAll(EntityUtil.getUnsafeBlocksList((Entity)player, 2, false));
        }
        for (int i = 0; i < EntityUtil.getTrapOffsets(extraTop, antiStep, legs, platform, antiDrop).length; ++i) {
            Vec3d vector = EntityUtil.getTrapOffsets(extraTop, antiStep, legs, platform, antiDrop)[i];
            BlockPos targetPos = new BlockPos(player.getPositionVector()).add(vector.x, vector.y, vector.z);
            Block block = EntityUtil.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static List<Vec3d> getTrapOffsetList(Vec3d vec, boolean extraTop, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        List<Vec3d> offset;
        ArrayList<Vec3d> retval = new ArrayList<Vec3d>();
        if (!antiStep && (offset = EntityUtil.getUnsafeBlocksList(vec, 2, false)).size() == 4) {
            block5: for (Vec3d vector : offset) {
                BlockPos pos = new BlockPos(vec).add(vector.x, vector.y, vector.z);
                switch (BlockUtil.getPlaceAbility(pos, raytrace)) {
                    case 0: {
                        break;
                    }
                    case -1: 
                    case 1: 
                    case 2: {
                        continue block5;
                    }
                    case 3: {
                        retval.add(vec.add(vector));
                    }
                }
                Collections.addAll(retval, MathUtil.convertVectors(vec, EntityUtil.getTrapOffsets(extraTop, false, legs, platform, antiDrop)));
                return retval;
            }
        }
        Collections.addAll(retval, MathUtil.convertVectors(vec, EntityUtil.getTrapOffsets(extraTop, antiStep, legs, platform, antiDrop)));
        return retval;
    }

    public static Vec3d[] getTrapOffsets(boolean extraTop, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        ArrayList<Vec3d> offsetArray = new ArrayList<Vec3d>(EntityUtil.getOffsetList(1, false));
        offsetArray.add(new Vec3d(0.0, 2.0, 0.0));
        if (extraTop) {
            offsetArray.add(new Vec3d(0.0, 3.0, 0.0));
        }
        if (antiStep) {
            offsetArray.addAll(EntityUtil.getOffsetList(2, false));
        }
        if (legs) {
            offsetArray.addAll(EntityUtil.getOffsetList(0, false));
        }
        if (platform) {
            offsetArray.addAll(EntityUtil.getOffsetList(-1, false));
            offsetArray.add(new Vec3d(0.0, -1.0, 0.0));
        }
        if (antiDrop) {
            offsetArray.add(new Vec3d(0.0, -2.0, 0.0));
        }
        Vec3d[] array = new Vec3d[offsetArray.size()];
        return offsetArray.toArray((T[])array);
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, (double)y, 0.0));
        offsets.add(new Vec3d(1.0, (double)y, 0.0));
        offsets.add(new Vec3d(0.0, (double)y, -1.0));
        offsets.add(new Vec3d(0.0, (double)y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, (double)(y - 1), 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray((T[])array);
    }

    public static List<Vec3d> getUnsafeBlocksList(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = EntityUtil.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static List<Vec3d> getUnsafeBlocksList(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocksList(entity.getPositionVector(), height, floor);
    }

    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static BlockPos getRoundedPos(Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
    }

    public static int getDamagePercent(ItemStack stack) {
        return (int)((double)(stack.getMaxDamage() - stack.getItemDamage()) / Math.max(0.1, (double)stack.getMaxDamage()) * 100.0);
    }

    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && EntityUtil.mc.player != null) {
            EntityUtil.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)EntityUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }
}

