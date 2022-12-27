/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraftforge.client.event.ClientChatReceivedEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BetterChat
extends Module {
    public static BetterChat INSTANCE;
    public final Setting<Boolean> rect = this.add(new Setting<Boolean>("Rect", true).setParent());
    public final Setting<Boolean> colorRect = this.add(new Setting<Boolean>("ColorRect", false, v -> this.rect.isOpen()));
    public final Setting<Boolean> infinite = this.add(new Setting<Boolean>("InfiniteChat", false));
    public final Setting<Boolean> suffix = this.add(new Setting<Boolean>("Suffix", false).setParent());
    public final Setting<Boolean> suffix2b = this.add(new Setting<Boolean>("2b2tSuffix", false, v -> this.suffix.isOpen()));
    public final Setting<Boolean> time = this.add(new Setting<Boolean>("TimeStamps", false).setParent());
    public final Setting<Bracket> bracket = this.add(new Setting<Bracket>("Bracket", Bracket.TRIANGLE, v -> this.time.isOpen()));

    public BetterChat() {
        super("BetterChat", "Modifies your chat", Category.MISC, true);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String string = ((CPacketChatMessage)event.getPacket()).getMessage();
        }
        if (this.suffix.getValue().booleanValue() && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet;
            if (this.suffix2b.getValue().booleanValue()) {
                packet = (CPacketChatMessage)event.getPacket();
                String message = packet.getMessage();
                if (message.startsWith("/") || message.startsWith("!")) {
                    return;
                }
                if ((message = message + " | mio").length() >= 256) {
                    message = message.substring(0, 256);
                }
                packet.message = message;
            } else {
                packet = (CPacketChatMessage)event.getPacket();
                String message = packet.getMessage();
                if (message.startsWith("/") || message.startsWith("!")) {
                    return;
                }
                if ((message = message + " \u22c6 \u1d0d\u026a\u1d0f").length() >= 256) {
                    message = message.substring(0, 256);
                }
                packet.message = message;
            }
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (this.time.getValue().booleanValue()) {
            Date date = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
            String strDate = dateFormatter.format(date);
            String leBracket1 = this.bracket.getValue() == Bracket.TRIANGLE ? "<" : "[";
            String leBracket2 = this.bracket.getValue() == Bracket.TRIANGLE ? ">" : "]";
            TextComponentString time = new TextComponentString((Object)ChatFormatting.GRAY + leBracket1 + (Object)ChatFormatting.WHITE + strDate + (Object)ChatFormatting.GRAY + leBracket2 + (Object)ChatFormatting.RESET + " ");
            event.setMessage(time.appendSibling(event.getMessage()));
        }
    }

    private static enum Bracket {
        SQUARE,
        TRIANGLE;

    }
}

