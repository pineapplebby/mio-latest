/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.api.managers.impl;

import java.awt.Color;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.mod.gui.click.Component;
import me.mioclient.mod.modules.impl.client.ClickGui;

public class ColorManager {
    private Color current = new Color(-1);

    public boolean isRainbow() {
        return ClickGui.INSTANCE.rainbow.getValue();
    }

    public Color getCurrent() {
        if (this.isRainbow()) {
            return this.getRainbow();
        }
        return this.current;
    }

    public int getCurrentWithAlpha(int alpha) {
        if (this.isRainbow()) {
            return ColorUtil.toRGBA(ColorUtil.injectAlpha(this.getRainbow(), alpha));
        }
        return ColorUtil.toRGBA(ColorUtil.injectAlpha(this.current, alpha));
    }

    public int getCurrentGui(int alpha) {
        if (this.isRainbow()) {
            return ColorUtil.rainbow(Component.counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB();
        }
        return ColorUtil.toRGBA(ColorUtil.injectAlpha(this.current, alpha));
    }

    public Color getRainbow() {
        return ColorUtil.rainbow(ClickGui.INSTANCE.rainbowDelay.getValue());
    }

    public Color getFriendColor(int alpha) {
        return new Color(0, 191, 255, alpha);
    }

    public void setCurrent(Color color) {
        this.current = color;
    }
}

