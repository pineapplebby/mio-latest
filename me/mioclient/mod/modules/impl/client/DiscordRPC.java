/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiMainMenu
 */
package me.mioclient.mod.modules.impl.client;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import java.util.Random;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordRPC
extends Module {
    private final club.minnced.discord.rpc.DiscordRPC rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
    private final DiscordRichPresence presence = new DiscordRichPresence();
    private Thread thread;
    private final String[] state = new String[]{"butterfly v9", "1110101001001010", "jordohooks", "SX-Hack v3.6b", "hvhlegende est. 2021", "flying over thousands of blocks with the power of miohake$$", "OskarMajewskiWare", "Gaming", "w/ the fellas", "mioclient", "Hazelwood Drive, Ballyspilliane, Killarney, Co. Kerry", "Owned By Alexander Pravshin", "195.155.194.117"};

    public DiscordRPC() {
        super("DiscordRPC", "Discord rich presence", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.stop();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.isOn()) {
            this.start();
        }
    }

    private void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        this.rpc.Discord_Initialize("1016673155693158420", handlers, true, "");
        this.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        this.presence.details = "Mio v0.6.9-alpha";
        this.presence.state = this.state[new Random().nextInt(this.state.length)];
        this.presence.largeImageKey = "big";
        this.presence.largeImageText = "mio v0.6.9-alpha";
        String string = DiscordRPC.mc.currentScreen instanceof GuiMainMenu ? "idling" : (this.presence.smallImageKey = DiscordRPC.mc.currentServerData != null ? "multiplayer" : "singleplayer");
        this.presence.smallImageText = DiscordRPC.mc.currentScreen instanceof GuiMainMenu ? "Idling." : (DiscordRPC.mc.currentServerData != null ? "Playing multiplayer." : "Playing singleplayer.");
        this.rpc.Discord_UpdatePresence(this.presence);
        this.thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                this.rpc.Discord_RunCallbacks();
                this.presence.details = "Mio v0.6.9-alpha";
                this.presence.state = this.state[new Random().nextInt(this.state.length)];
                String string = DiscordRPC.mc.currentScreen instanceof GuiMainMenu ? "iding" : (this.presence.smallImageKey = DiscordRPC.mc.currentServerData != null ? "multiplayer" : "singleplayer");
                this.presence.smallImageText = DiscordRPC.mc.currentScreen instanceof GuiMainMenu ? "Iding." : (DiscordRPC.mc.currentServerData != null ? "Playing multiplayer." : "Playing singleplayer.");
                this.rpc.Discord_UpdatePresence(this.presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }, "DiscordRPC-Callback-Handler");
        this.thread.start();
    }

    private void stop() {
        if (this.thread != null && !this.thread.isInterrupted()) {
            this.thread.interrupt();
        }
        this.rpc.Discord_Shutdown();
    }
}

