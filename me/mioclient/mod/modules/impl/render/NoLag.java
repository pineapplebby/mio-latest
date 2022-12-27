/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityTNTPrimed
 *  net.minecraft.entity.passive.EntityParrot
 *  net.minecraft.entity.projectile.EntityWitherSkull
 *  net.minecraft.network.play.server.SPacketChat
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.events.impl.RenderEntityEvent;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoLag
extends Module {
    public static NoLag INSTANCE;
    public Setting<Boolean> antiChina = this.add(new Setting<Boolean>("AntiChina", true));
    public Setting<Boolean> skulls = this.add(new Setting<Boolean>("WitherSkulls", false));
    public Setting<Boolean> tnt = this.add(new Setting<Boolean>("PrimedTNT", false));
    public Setting<Boolean> scoreBoards = this.add(new Setting<Boolean>("ScoreBoards", true));
    public Setting<Boolean> glowing = this.add(new Setting<Boolean>("GlowingEntities", true));
    public Setting<Boolean> parrots = this.add(new Setting<Boolean>("Parrots", true));

    public NoLag() {
        super("NoLag", "Removes several things that may cause fps drops.", Category.RENDER, true);
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (!this.glowing.getValue().booleanValue()) {
            return;
        }
        for (Entity entity : NoLag.mc.world.loadedEntityList) {
            if (!entity.isGlowing()) continue;
            entity.setGlowing(false);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        String chat;
        if (event.getPacket() instanceof SPacketChat && this.antiChina.getValue().booleanValue() && ((chat = ((SPacketChat)event.getPacket()).chatComponent.getUnformattedText()).contains("\u3f01") || chat.contains("\u3801") || chat.contains("\u6301") || chat.contains("\u6401") || chat.contains("\u1b01") || chat.contains("\u1201") || chat.contains("\u0101") || chat.contains("\u5b01"))) {
            event.cancel();
            ((SPacketChat)event.getPacket()).chatComponent = null;
            Command.sendMessageWithID("[" + this.getName() + "] " + (Object)ChatFormatting.RED + "Removed some chinese text :')", -343435);
        }
    }

    @SubscribeEvent
    public void onRenderEntity(RenderEntityEvent event) {
        if (event.getEntity() != null) {
            if (this.skulls.getValue().booleanValue() && event.getEntity() instanceof EntityWitherSkull) {
                event.cancel();
            }
            if (this.tnt.getValue().booleanValue() && event.getEntity() instanceof EntityTNTPrimed) {
                event.cancel();
            }
            if (this.parrots.getValue().booleanValue() && event.getEntity() instanceof EntityParrot) {
                event.cancel();
            }
        }
    }
}

