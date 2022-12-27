/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;

public class CrystalChams
extends Module {
    public static CrystalChams INSTANCE;
    private final Setting<Page> page = this.add(new Setting<Page>("Settings", Page.GLOBAL));
    public Setting<Boolean> fill = this.add(new Setting<Boolean>("Fill", true, v -> this.page.getValue() == Page.GLOBAL).setParent());
    public Setting<Boolean> xqz = this.add(new Setting<Boolean>("XQZ", true, v -> this.page.getValue() == Page.GLOBAL && this.fill.isOpen()));
    public Setting<Boolean> wireframe = this.add(new Setting<Boolean>("Wireframe", true, v -> this.page.getValue() == Page.GLOBAL));
    public Setting<Model> model = this.add(new Setting<Model>("Model", Model.XQZ, v -> this.page.getValue() == Page.GLOBAL));
    public Setting<Boolean> glint = this.add(new Setting<Boolean>("Glint", false, v -> this.page.getValue() == Page.GLOBAL));
    public Setting<Float> scale = this.add(new Setting<Float>("Scale", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.page.getValue() == Page.GLOBAL));
    public Setting<Boolean> changeSpeed = this.add(new Setting<Boolean>("ChangeSpeed", false, v -> this.page.getValue() == Page.GLOBAL).setParent());
    public Setting<Float> spinSpeed = this.add(new Setting<Float>("SpinSpeed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.page.getValue() == Page.GLOBAL && this.changeSpeed.isOpen()));
    public Setting<Float> floatFactor = this.add(new Setting<Float>("FloatFactor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> this.page.getValue() == Page.GLOBAL && this.changeSpeed.isOpen()));
    public Setting<Float> lineWidth = this.add(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f), v -> this.page.getValue() == Page.COLORS));
    public Setting<Boolean> rainbow = this.add(new Setting<Boolean>("Rainbow", false, v -> this.page.getValue() == Page.COLORS));
    public Setting<Color> color = this.add(new Setting<Color>("Color", new Color(132, 132, 241, 150), v -> this.page.getValue() == Page.COLORS));
    public Setting<Color> lineColor = this.add(new Setting<Color>("LineColor", new Color(255, 255, 255), v -> this.page.getValue() == Page.COLORS).injectBoolean(false));
    public Setting<Color> modelColor = this.add(new Setting<Color>("ModelColor", new Color(125, 125, 213, 150), v -> this.page.getValue() == Page.COLORS).injectBoolean(false));

    public CrystalChams() {
        super("CrystalChams", "Draws a pretty ESP around end crystals.", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        String info = null;
        if (this.fill.getValue().booleanValue()) {
            info = "Fill";
        } else if (this.wireframe.getValue().booleanValue()) {
            info = "Wireframe";
        }
        if (this.wireframe.getValue().booleanValue() && this.fill.getValue().booleanValue()) {
            info = "Both";
        }
        return info;
    }

    public static enum Model {
        XQZ,
        VANILLA,
        OFF;

    }

    public static enum Page {
        COLORS,
        GLOBAL;

    }
}

