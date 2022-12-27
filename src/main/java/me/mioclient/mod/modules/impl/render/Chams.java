/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Chams
extends Module {
    public static Chams INSTANCE;
    private final Setting<Page> page = this.add(new Setting<Page>("Settings", Page.GLOBAL));
    public final Setting<Boolean> fill = this.add(new Setting<Boolean>("Fill", true, v -> this.page.getValue() == Page.GLOBAL).setParent());
    public final Setting<Boolean> xqz = this.add(new Setting<Boolean>("XQZ", true, v -> this.page.getValue() == Page.GLOBAL && this.fill.isOpen()));
    public final Setting<Boolean> wireframe = this.add(new Setting<Boolean>("Wireframe", true, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Model> model = this.add(new Setting<Model>("Model", Model.XQZ, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Boolean> self = this.add(new Setting<Boolean>("Self", true, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Boolean> noInterp = this.add(new Setting<Boolean>("NoInterp", false, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Boolean> sneak = this.add(new Setting<Boolean>("Sneak", false, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Boolean> glint = this.add(new Setting<Boolean>("Glint", false, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Float> lineWidth = this.add(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f), v -> this.page.getValue() == Page.COLORS));
    public final Setting<Boolean> rainbow = this.add(new Setting<Boolean>("Rainbow", false, v -> this.page.getValue() == Page.COLORS));
    public final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(132, 132, 241, 150), v -> this.page.getValue() == Page.COLORS));
    public final Setting<Color> lineColor = this.add(new Setting<Color>("LineColor", new Color(255, 255, 255), v -> this.page.getValue() == Page.COLORS).injectBoolean(false));
    public final Setting<Color> modelColor = this.add(new Setting<Color>("ModelColor", new Color(125, 125, 213, 150), v -> this.page.getValue() == Page.COLORS).injectBoolean(false));

    public Chams() {
        super("Chams", "Draws a pretty ESP around other players.", Category.RENDER, true);
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

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
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

