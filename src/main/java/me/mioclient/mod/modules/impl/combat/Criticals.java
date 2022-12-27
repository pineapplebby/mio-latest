/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockWeb
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.combat;

import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.impl.combat.Aura;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals
extends Module {
    private final Setting<Mode> mode = this.add(new Setting<Mode>("Mode", Mode.PACKET));
    private final Setting<Boolean> webs = this.add(new Setting<Boolean>("Webs", false, v -> this.mode.getValue() == Mode.NCP));
    private final Setting<Boolean> onlyAura = this.add(new Setting<Boolean>("OnlyAura", false));
    private final Setting<Boolean> vehicles = this.add(new Setting<Boolean>("Vehicles", true).setParent());

    public Criticals() {
        super("Criticals", "Always do as much damage as you can!", Category.COMBAT, true);
    }

    @Override
    public String getInfo() {
        return this.mode.getValue() == Mode.NCP ? String.valueOf((Object)this.mode.getValue()) : Managers.TEXT.normalizeCases((Object)this.mode.getValue());
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (Criticals.nullCheck() || Criticals.fullNullCheck()) {
            return;
        }
        if (Aura.target == null && this.onlyAura.getValue().booleanValue()) {
            return;
        }
        if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && Criticals.mc.player.onGround && Criticals.mc.player.collidedVertically && !Criticals.mc.player.isInLava() && !Criticals.mc.player.isInWater()) {
            Entity attackedEntity = ((CPacketUseEntity)event.getPacket()).getEntityFromWorld((World)Criticals.mc.world);
            if (attackedEntity instanceof EntityEnderCrystal || attackedEntity == null) {
                return;
            }
            switch (this.mode.getValue()) {
                case PACKET: {
                    Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0625101, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0125, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                    break;
                }
                case NCP: {
                    if (this.webs.getValue().booleanValue() && Criticals.mc.world.getBlockState(new BlockPos((Entity)Criticals.mc.player)).getBlock() instanceof BlockWeb) {
                        Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0625101, Criticals.mc.player.posZ, false));
                        Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                        Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.0125, Criticals.mc.player.posZ, false));
                        Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY, Criticals.mc.player.posZ, false));
                        break;
                    }
                    Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.11, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 0.1100013579, Criticals.mc.player.posZ, false));
                    Criticals.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Criticals.mc.player.posX, Criticals.mc.player.posY + 1.3579E-6, Criticals.mc.player.posZ, false));
                }
            }
            Criticals.mc.player.onCriticalHit(attackedEntity);
        }
    }

    private static enum Mode {
        PACKET,
        NCP;

    }
}

