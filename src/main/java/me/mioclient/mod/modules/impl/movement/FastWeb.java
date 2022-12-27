/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.mod.modules.impl.movement;

import me.mioclient.api.managers.Managers;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;

public class FastWeb
extends Module {
    private final Setting<Mode> mode = this.add(new Setting<Mode>("Mode", Mode.FAST));
    private final Setting<Float> fastSpeed = this.add(new Setting<Float>("FastSpeed", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), v -> this.mode.getValue() == Mode.FAST));

    public FastWeb() {
        super("FastWeb", "So you don't need to keep timer on keybind", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        Managers.TIMER.reset();
    }

    @Override
    public String getInfo() {
        return Managers.TEXT.normalizeCases((Object)this.mode.getValue());
    }

    @Override
    public void onUpdate() {
        if (FastWeb.fullNullCheck()) {
            return;
        }
        if (FastWeb.mc.player.isInWeb) {
            if (this.mode.getValue() == Mode.FAST && FastWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                Managers.TIMER.reset();
                FastWeb.mc.player.motionY -= (double)this.fastSpeed.getValue().floatValue();
            } else if (this.mode.getValue() == Mode.STRICT && !FastWeb.mc.player.onGround && FastWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                Managers.TIMER.set(8.0f);
            } else {
                Managers.TIMER.reset();
            }
        } else {
            Managers.TIMER.reset();
        }
    }

    private static enum Mode {
        FAST,
        STRICT;

    }
}

