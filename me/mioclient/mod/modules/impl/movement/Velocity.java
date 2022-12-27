/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.projectile.EntityFishHook
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 *  net.minecraft.network.play.server.SPacketExplosion
 *  net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.movement;

import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.asm.accessors.ISPacketEntityVelocity;
import me.mioclient.asm.accessors.ISPacketExplosion;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity
extends Module {
    public static Velocity INSTANCE;
    private final Setting<Float> horizontal = this.add(new Setting<Float>("Horizontal", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    private final Setting<Float> vertical = this.add(new Setting<Float>("Vertical", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    private final Setting<Boolean> blockPush = this.add(new Setting<Boolean>("BlockPush", true));

    public Velocity() {
        super("Velocity", "Cancels all the pushing your player receives.", Category.MOVEMENT, true);
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        return "H" + MathUtil.round(this.horizontal.getValue().floatValue(), 1) + "%V" + MathUtil.round(this.vertical.getValue().floatValue(), 1) + "%";
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        Object packet;
        if (Velocity.fullNullCheck()) {
            return;
        }
        float h = this.horizontal.getValue().floatValue() / 100.0f;
        float v = this.vertical.getValue().floatValue() / 100.0f;
        if (event.getPacket() instanceof EntityFishHook) {
            event.cancel();
        }
        if (event.getPacket() instanceof SPacketExplosion) {
            packet = (ISPacketExplosion)event.getPacket();
            packet.setX(packet.getX() * h);
            packet.setY(packet.getY() * v);
            packet.setZ(packet.getZ() * h);
        }
        if (event.getPacket() instanceof SPacketEntityVelocity && (packet = (ISPacketEntityVelocity)event.getPacket()).getEntityID() == Velocity.mc.player.getEntityId()) {
            if (this.horizontal.getValue().floatValue() == 0.0f && this.vertical.getValue().floatValue() == 0.0f) {
                event.cancel();
            } else {
                packet.setX((int)((float)packet.getX() * h));
                packet.setY((int)((float)packet.getY() * v));
                packet.setZ((int)((float)packet.getZ() * h));
            }
        }
    }

    @SubscribeEvent
    public void onPushOutOfBlocks(PlayerSPPushOutOfBlocksEvent event) {
        if (this.blockPush.getValue().booleanValue()) {
            event.setCanceled(true);
        }
    }
}

