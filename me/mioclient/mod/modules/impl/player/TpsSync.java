/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.mod.modules.impl.player;

import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;

public class TpsSync
extends Module {
    public static TpsSync INSTANCE;
    public Setting<Boolean> attack = this.add(new Setting<Boolean>("Attack", false));
    public Setting<Boolean> mining = this.add(new Setting<Boolean>("Mine", true));

    public TpsSync() {
        super("TpsSync", "Syncs your client with the TPS.", Category.PLAYER);
        INSTANCE = this;
    }
}

