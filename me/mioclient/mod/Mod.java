/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.mod;

import java.util.ArrayList;
import java.util.List;
import me.mioclient.api.util.Wrapper;
import me.mioclient.mod.gui.screen.MioClickGui;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;

public class Mod
implements Wrapper {
    public List<Setting> settings = new ArrayList<Setting>();
    private String name;

    public Mod(String name) {
        this.name = name;
    }

    public Mod() {
    }

    public Setting add(Setting setting) {
        setting.setMod(this);
        this.settings.add(setting);
        if (this instanceof Module && Mod.mc.currentScreen instanceof MioClickGui) {
            MioClickGui.INSTANCE.updateModule((Module)this);
        }
        return setting;
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.settings) {
            if (!setting.getName().equalsIgnoreCase(name)) continue;
            return setting;
        }
        return null;
    }

    public void resetSettings() {
        this.settings = new ArrayList<Setting>();
    }

    public String getName() {
        return this.name;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public static boolean nullCheck() {
        return Mod.mc.player == null;
    }

    public static boolean fullNullCheck() {
        return Mod.mc.player == null || Mod.mc.world == null;
    }

    public static boolean spawnCheck() {
        return Mod.mc.player.ticksExisted > 15;
    }
}

