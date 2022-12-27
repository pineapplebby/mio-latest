/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package me.mioclient.api.managers.impl;

import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.asm.accessors.IEntityPlayerSP;
import me.mioclient.mod.Mod;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationManager
extends Mod {
    private float yaw;
    private float pitch;

    public void updateRotations() {
        this.yaw = RotationManager.mc.player.rotationYaw;
        this.pitch = RotationManager.mc.player.rotationPitch;
    }

    public void resetRotations() {
        RotationManager.mc.player.rotationYaw = this.yaw;
        RotationManager.mc.player.rotationYawHead = this.yaw;
        RotationManager.mc.player.rotationPitch = this.pitch;
    }

    public void setRotations(float yaw, float pitch) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
        RotationManager.mc.player.rotationPitch = pitch;
    }

    public void lookAtPos(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Wrapper.mc.getRenderPartialTicks()), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() - 0.5f), (double)((float)pos.getZ() + 0.5f)));
        this.setRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Wrapper.mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        this.setRotations(angle[0], angle[1]);
    }

    public void lookAtVec3dPacket(Vec3d vec, boolean normalize, boolean update) {
        float[] angle = this.getAngle(vec);
        RotationManager.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], normalize ? (float)MathHelper.normalizeAngle((int)((int)angle[1]), (int)360) : angle[1], RotationManager.mc.player.onGround));
        if (update) {
            ((IEntityPlayerSP)RotationManager.mc.player).setLastReportedYaw(angle[0]);
            ((IEntityPlayerSP)RotationManager.mc.player).setLastReportedPitch(angle[1]);
        }
    }

    public void lookAtVec3dPacket(Vec3d vec, boolean normalize) {
        float[] angle = this.getAngle(vec);
        RotationManager.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], normalize ? (float)MathHelper.normalizeAngle((int)((int)angle[1]), (int)360) : angle[1], RotationManager.mc.player.onGround));
    }

    public void resetRotationsPacket() {
        float[] angle = new float[]{RotationManager.mc.player.rotationYaw, RotationManager.mc.player.rotationPitch};
        RotationManager.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], angle[1], RotationManager.mc.player.onGround));
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float[] getAngle(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(RotationManager.mc.player.posX, RotationManager.mc.player.posY + (double)RotationManager.mc.player.getEyeHeight(), RotationManager.mc.player.posZ);
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{RotationManager.mc.player.rotationYaw + MathHelper.wrapDegrees((float)(yaw - RotationManager.mc.player.rotationYaw)), RotationManager.mc.player.rotationPitch + MathHelper.wrapDegrees((float)(pitch - RotationManager.mc.player.rotationPitch))};
    }

    public float[] injectYawStep(float[] angle, float steps) {
        float packetYaw;
        float diff;
        if (steps < 0.1f) {
            steps = 0.1f;
        }
        if (steps > 1.0f) {
            steps = 1.0f;
        }
        if (steps < 1.0f && angle != null && Math.abs(diff = MathHelper.wrapDegrees((float)(angle[0] - (packetYaw = ((IEntityPlayerSP)RotationManager.mc.player).getLastReportedYaw())))) > 180.0f * steps) {
            angle[0] = packetYaw + diff * (180.0f * steps / Math.abs(diff));
        }
        return new float[]{angle[0], angle[1]};
    }

    public int getYaw4D() {
        return MathHelper.floor((double)((double)(RotationManager.mc.player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
    }

    public String getDirection4D(boolean northRed) {
        int yaw = this.getYaw4D();
        if (yaw == 0) {
            return "South (+Z)";
        }
        if (yaw == 1) {
            return "West (-X)";
        }
        if (yaw == 2) {
            return (northRed ? "\u00c2\u00a7c" : "") + "North (-Z)";
        }
        if (yaw == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }

    public boolean isInFov(BlockPos pos) {
        int yaw = this.getYaw4D();
        if (yaw == 0 && (double)pos.getZ() - BlockUtil.mc.player.getPositionVector().z < 0.0) {
            return false;
        }
        if (yaw == 1 && (double)pos.getX() - BlockUtil.mc.player.getPositionVector().x > 0.0) {
            return false;
        }
        if (yaw == 2 && (double)pos.getZ() - BlockUtil.mc.player.getPositionVector().z > 0.0) {
            return false;
        }
        return yaw != 3 || (double)pos.getX() - BlockUtil.mc.player.getPositionVector().x >= 0.0;
    }
}

