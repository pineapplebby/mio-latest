/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.mod.modules.impl.misc;

import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;

public class UnfocusedCPU
extends Module {
    public static UnfocusedCPU INSTANCE;
    public Setting<Integer> unfocusedFps = this.add(new Setting<Integer>("UnfocusedFPS", 5, 1, 30));

    public UnfocusedCPU() {
        super("UnfocusedCPU", "Decreases your framerate when minecraft is unfocused.", Category.MISC);
        INSTANCE = this;
    }
}

