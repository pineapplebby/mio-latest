/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.util.math.MathHelper
 */
package me.mioclient.api.managers.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Font;
import java.util.regex.Pattern;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.mod.Mod;
import me.mioclient.mod.gui.font.CustomFont;
import me.mioclient.mod.modules.impl.client.ClickGui;
import me.mioclient.mod.modules.impl.client.FontMod;
import me.mioclient.mod.modules.impl.player.NameProtect;
import net.minecraft.util.math.MathHelper;

public class TextManager
extends Mod {
    private final Timer idleTimer = new Timer();
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, true);
    private boolean idling;
    public final String syncCode = "\u00a7(";

    public TextManager() {
        this.updateResolution();
    }

    public void init() {
        if (FontMod.INSTANCE == null) {
            FontMod.INSTANCE = new FontMod();
        }
        FontMod fonts = FontMod.INSTANCE;
        try {
            this.setFontRenderer(new Font(fonts.font.getValue(), fonts.getFont(), fonts.size.getValue()), fonts.antiAlias.getValue(), fonts.metrics.getValue());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public String getPrefix() {
        return "\u00a7r" + (Object)ChatFormatting.WHITE + "[\u00a7rMio\u00a7(] " + (Object)ChatFormatting.RESET;
    }

    public String normalizeCases(Object o) {
        return Character.toUpperCase(o.toString().charAt(0)) + o.toString().toLowerCase().substring(1);
    }

    public float drawStringNoCFont(String text, float x, float y, int color, boolean shadow) {
        TextManager.mc.fontRenderer.drawString(text, x, y, color, shadow);
        return x;
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public float drawString(String text, float x, float y, int color, boolean shadow) {
        NameProtect nameProtect = NameProtect.INSTANCE;
        String string = text = nameProtect.isOn() ? text.replaceAll(mc.getSession().getUsername(), nameProtect.name.getValue()) : text;
        if (FontMod.INSTANCE.isOn()) {
            if (shadow) {
                this.customFont.drawStringWithShadow(text, x, y, color);
            } else {
                this.customFont.drawString(text, x, y, color);
            }
            return x;
        }
        TextManager.mc.fontRenderer.drawString(text, x, y, color, shadow);
        return x;
    }

    public void drawRollingRainbowString(String text, float x, float y, boolean shadow) {
        Pattern.compile("(?i)\u00a7[0-9A-FK-OR]").matcher(text).replaceAll("");
        int[] arrayOfInt = new int[]{1};
        char[] stringToCharArray = text.toCharArray();
        float f = 0.0f + x;
        for (char c : stringToCharArray) {
            this.drawString(String.valueOf(c), f, y, ColorUtil.rainbow(arrayOfInt[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB(), shadow);
            f += (float)this.getStringWidth(String.valueOf(c));
            arrayOfInt[0] = arrayOfInt[0] + 1;
        }
    }

    public int getStringWidth(String text) {
        NameProtect nameProtect = NameProtect.INSTANCE;
        String string = text = nameProtect.isOn() ? text.replaceAll(mc.getSession().getUsername(), nameProtect.name.getValue()) : text;
        if (FontMod.INSTANCE.isOn()) {
            return this.customFont.getStringWidth(text);
        }
        return TextManager.mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        if (FontMod.INSTANCE.isOn()) {
            String text = "A";
            return this.customFont.getStringHeight(text);
        }
        return TextManager.mc.fontRenderer.FONT_HEIGHT;
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public Font getCurrentFont() {
        return this.customFont.getFont();
    }

    public void updateResolution() {
        this.scaledWidth = TextManager.mc.displayWidth;
        this.scaledHeight = TextManager.mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = TextManager.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = this.scaledWidth / this.scaleFactor;
        double scaledHeightD = this.scaledHeight / this.scaleFactor;
        this.scaledWidth = MathHelper.ceil((double)scaledWidthD);
        this.scaledHeight = MathHelper.ceil((double)scaledHeightD);
    }

    public String getIdleSign() {
        if (this.idleTimer.passedMs(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }
        if (this.idling) {
            return "_";
        }
        return "";
    }
}

