/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package me.mioclient.mod.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.api.events.impl.ClientEvent;
import me.mioclient.api.events.impl.Render2DEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.mod.Mod;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.settings.Bind;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class Module
extends Mod {
    public Setting<Boolean> enabled = this.add(new Setting<Boolean>("Enabled", this.getName().equalsIgnoreCase("HUD")));
    public Setting<Boolean> drawn = this.add(new Setting<Boolean>("Drawn", true));
    public Setting<Bind> bind = this.add(new Setting<Bind>("Keybind", this.getName().equalsIgnoreCase("ClickGui") ? new Bind(21) : new Bind(-1)));
    private final String description;
    private final Category category;
    private final boolean shouldListen;

    public Module(String name, String description, Category category, boolean listen) {
        super(name);
        this.description = description;
        this.category = category;
        this.shouldListen = listen;
    }

    public Module(String name, String description, Category category) {
        super(name);
        this.description = description;
        this.category = category;
        this.shouldListen = false;
    }

    public void enable() {
        this.enabled.setValue(true);
        this.onEnable();
        Command.sendMessageWithID((Object)ChatFormatting.DARK_AQUA + this.getName() + "\u00a7r.enabled =\u00a7r" + (Object)ChatFormatting.GREEN + " true.", this.hashCode());
        if (this.isOn() && this.shouldListen) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
    }

    public void disable() {
        this.enabled.setValue(false);
        this.onDisable();
        Command.sendMessageWithID((Object)ChatFormatting.DARK_AQUA + this.getName() + "\u00a7r.enabled =\u00a7r" + (Object)ChatFormatting.RED + " false.", this.hashCode());
        if (this.shouldListen) {
            MinecraftForge.EVENT_BUS.unregister((Object)this);
        }
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isOn() ? 1 : 0, this);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            if (!this.isOn()) {
                this.enable();
            } else {
                this.disable();
            }
        }
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public boolean isOff() {
        return this.enabled.getValue() == false;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public boolean isListening() {
        return this.shouldListen && this.isOn();
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void onUnload() {
    }

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public void onTotemPop(EntityPlayer player) {
    }

    public void onDeath(EntityPlayer player) {
    }

    public String getInfo() {
        return null;
    }

    public String getArrayListInfo() {
        return this.getName() + (Object)ChatFormatting.GRAY + (this.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + this.getInfo() + (Object)ChatFormatting.GRAY + "]" : "");
    }

    public Category getCategory() {
        return this.category;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public String getDescription() {
        return this.description;
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }
}

