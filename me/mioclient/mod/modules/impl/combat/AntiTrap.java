/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemEndCrystal
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import me.mioclient.api.events.impl.UpdateWalkingPlayerEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.world.InventoryUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiTrap
extends Module {
    private final Setting<Rotate> rotate = this.add(new Setting<Rotate>("Rotate", Rotate.NORMAL));
    private final Setting<Integer> coolDown = this.add(new Setting<Integer>("CoolDown", 400, 0, 1000));
    private final Setting<Boolean> sortY = this.add(new Setting<Boolean>("SortY", true));
    public static Set<BlockPos> placedPos = new HashSet<BlockPos>();
    private final Vec3d[] surroundTargets = new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 1.0, 1.0)};
    private int lastHotbarSlot = -1;
    private boolean switchedItem;
    private boolean offhand;
    private final Timer timer = new Timer();

    public AntiTrap() {
        super("AntiTrap", "best useful module shout out sam", Category.COMBAT, true);
    }

    @Override
    public void onEnable() {
        if (AntiTrap.fullNullCheck() || !this.timer.passedMs(this.coolDown.getValue().intValue())) {
            this.disable();
            return;
        }
        this.lastHotbarSlot = AntiTrap.mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        if (AntiTrap.fullNullCheck()) {
            return;
        }
        this.doSwap(this.lastHotbarSlot);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (!AntiTrap.fullNullCheck() && event.getStage() == 0) {
            this.doAntiTrap();
        }
    }

    private void doAntiTrap() {
        boolean bl = this.offhand = AntiTrap.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        if (!this.offhand && InventoryUtil.findHotbarBlock(ItemEndCrystal.class) == -1) {
            this.disable();
            return;
        }
        this.lastHotbarSlot = AntiTrap.mc.player.inventory.currentItem;
        ArrayList<Vec3d> targets = new ArrayList<Vec3d>();
        Collections.addAll(targets, MathUtil.convertVectors(AntiTrap.mc.player.getPositionVector(), this.surroundTargets));
        EntityPlayer closestPlayer = EntityUtil.getClosestEnemy(6.0);
        if (closestPlayer != null) {
            targets.sort((vec3d, vec3d2) -> Double.compare(closestPlayer.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), closestPlayer.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
            if (this.sortY.getValue().booleanValue()) {
                targets.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
            }
        }
        for (Vec3d vec3d3 : targets) {
            int crystalSlot = InventoryUtil.findItemInHotbar(Items.END_CRYSTAL);
            if (crystalSlot == -1) {
                this.disable();
                return;
            }
            BlockPos pos = new BlockPos(vec3d3);
            if (!BlockUtil.canPlaceCrystal(pos)) continue;
            this.doSwap(InventoryUtil.findItemInHotbar(Items.END_CRYSTAL));
            this.placeCrystal(pos);
            this.doSwap(this.lastHotbarSlot);
            this.disable();
            break;
        }
    }

    private void placeCrystal(BlockPos pos) {
        boolean mainHand;
        boolean bl = mainHand = AntiTrap.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
        if (!mainHand && !this.offhand) {
            this.disable();
            return;
        }
        RayTraceResult result = AntiTrap.mc.world.rayTraceBlocks(new Vec3d(AntiTrap.mc.player.posX, AntiTrap.mc.player.posY + (double)AntiTrap.mc.player.getEyeHeight(), AntiTrap.mc.player.posZ), new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() - 0.5, (double)pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        float[] angle = MathUtil.calcAngle(AntiTrap.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() - 0.5f), (double)((float)pos.getZ() + 0.5f)));
        switch (this.rotate.getValue()) {
            case NONE: {
                break;
            }
            case NORMAL: {
                Managers.ROTATIONS.setRotations(angle[0], angle[1]);
                break;
            }
            case PACKET: {
                AntiTrap.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], (float)MathHelper.normalizeAngle((int)((int)angle[1]), (int)360), AntiTrap.mc.player.onGround));
            }
        }
        placedPos.add(pos);
        AntiTrap.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        AntiTrap.mc.player.swingArm(EnumHand.MAIN_HAND);
        this.timer.reset();
    }

    private void doSwap(int slot) {
        AntiTrap.mc.player.inventory.currentItem = slot;
        AntiTrap.mc.playerController.updateController();
    }

    private static enum Rotate {
        NONE,
        NORMAL,
        PACKET;

    }
}

