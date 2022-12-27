/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.client.event.ClientChatReceivedEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.Display
 */
package me.mioclient.mod.modules.impl.client;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

public class Desktop
extends Module {
    private final Setting<Boolean> onlyTabbed = this.add(new Setting<Boolean>("OnlyTabbed", false));
    private final Setting<Boolean> visualRange = this.add(new Setting<Boolean>("VisualRange", true));
    private final Setting<Boolean> selfPop = this.add(new Setting<Boolean>("TotemPop", true));
    private final Setting<Boolean> mention = this.add(new Setting<Boolean>("Mention", true));
    private final Setting<Boolean> dm = this.add(new Setting<Boolean>("DM", true));
    private List<Entity> players;
    private final List<Entity> knownPlayers = new ArrayList<Entity>();
    Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
    TrayIcon icon = new TrayIcon(this.image, "Mio");

    public Desktop() {
        super("Desktop", "Desktop notifications.", Category.CLIENT, true);
    }

    @Override
    public void onDisable() {
        this.knownPlayers.clear();
        this.removeIcon();
    }

    @Override
    public void onEnable() {
        this.addIcon();
    }

    @Override
    public void onLoad() {
        if (this.isOn()) {
            this.addIcon();
        }
    }

    @Override
    public void onUnload() {
        this.onDisable();
    }

    @Override
    public void onUpdate() {
        if (Desktop.fullNullCheck() || !this.visualRange.getValue().booleanValue()) {
            return;
        }
        try {
            if (!Display.isActive() && this.onlyTabbed.getValue().booleanValue()) {
                return;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.players = Desktop.mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer).collect(Collectors.toList());
        try {
            for (Entity entity2 : this.players) {
                if (!(entity2 instanceof EntityPlayer) || entity2.getName().equalsIgnoreCase(Desktop.mc.player.getName()) || this.knownPlayers.contains((Object)entity2) || Managers.FRIENDS.isFriend(entity2.getName())) continue;
                this.knownPlayers.add(entity2);
                this.icon.displayMessage("Mio", entity2.getName() + " has entered your visual range!", TrayIcon.MessageType.INFO);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.knownPlayers.removeIf(entity -> entity instanceof EntityPlayer && !entity.getName().equalsIgnoreCase(Desktop.mc.player.getName()) && !this.players.contains(entity));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void onTotemPop(EntityPlayer player) {
        if (Desktop.fullNullCheck() || player != Desktop.mc.player || !this.selfPop.getValue().booleanValue()) {
            return;
        }
        this.icon.displayMessage("Mio", "You are popping!", TrayIcon.MessageType.WARNING);
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (Desktop.fullNullCheck()) {
            return;
        }
        String message = String.valueOf((Object)event.getMessage());
        if (message.contains(Desktop.mc.player.getName()) && this.mention.getValue().booleanValue()) {
            this.icon.displayMessage("Mio", "New chat mention!", TrayIcon.MessageType.INFO);
        }
        if (message.contains("whispers:") && this.dm.getValue().booleanValue()) {
            this.icon.displayMessage("Mio", "New direct message!", TrayIcon.MessageType.INFO);
        }
    }

    private void addIcon() {
        SystemTray tray = SystemTray.getSystemTray();
        this.icon.setImageAutoSize(true);
        this.icon.setToolTip("mioclient.me v0.6.9-alpha");
        try {
            tray.add(this.icon);
        }
        catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void removeIcon() {
        SystemTray tray = SystemTray.getSystemTray();
        tray.remove(this.icon);
    }
}

