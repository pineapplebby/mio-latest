/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.api.util.render;

import java.awt.Color;
import me.mioclient.mod.modules.impl.client.ClickGui;

public class ColorUtil {
    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorUtil.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return ColorUtil.toRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }

    public static int toHex(int r, int g, int b) {
        return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static Color injectAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static int injectAlpha(int color, int alpha) {
        return ColorUtil.toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), alpha);
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        if (ClickGui.INSTANCE.rainbowMode.getValue() == ClickGui.Rainbow.DOUBLE) {
            return ColorUtil.gradientColor(ClickGui.INSTANCE.color.getValue(), ClickGui.INSTANCE.secondColor.getValue(), Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + 20.0f / (float)(delay / 15 * 2 + 10) * 2.0f) % 2.0f - 1.0f));
        }
        if (ClickGui.INSTANCE.rainbowMode.getValue() == ClickGui.Rainbow.PLAIN) {
            return ColorUtil.pulseColor(ClickGui.INSTANCE.color.getValue(), 50, delay);
        }
        return Color.getHSBColor((float)(rainbowState % 360.0 / 360.0), ClickGui.INSTANCE.rainbowSaturation.getValue().floatValue() / 255.0f, ClickGui.INSTANCE.rainbowBrightness.getValue().floatValue() / 255.0f);
    }

    public static int rainbow(int delay, int alpha) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return ColorUtil.toRGBA((int)(rainbowState % 360.0 / 360.0), ClickGui.INSTANCE.rainbowSaturation.getValue().intValue(), ClickGui.INSTANCE.rainbowBrightness.getValue().intValue(), alpha);
    }

    public static Color pulseColor(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / Float.intBitsToFloat(Float.floatToIntBits(0.0013786979f) ^ 0x7ECEB56D) + (float)index / (float)count * Float.intBitsToFloat(Float.floatToIntBits(0.09192204f) ^ 0x7DBC419F)) % Float.intBitsToFloat(Float.floatToIntBits(0.7858098f) ^ 0x7F492AD5) - Float.intBitsToFloat(Float.floatToIntBits(6.46708f) ^ 0x7F4EF252));
        brightness = Float.intBitsToFloat(Float.floatToIntBits(18.996923f) ^ 0x7E97F9B3) + Float.intBitsToFloat(Float.floatToIntBits(2.7958195f) ^ 0x7F32EEB5) * brightness;
        hsb[2] = brightness % Float.intBitsToFloat(Float.floatToIntBits(0.8992331f) ^ 0x7F663424);
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color gradientColor(Color color1, Color color2, double offset) {
        if (offset > 1.0) {
            double left = offset % 1.0;
            int off = (int)offset;
            offset = off % 2 == 0 ? left : 1.0 - left;
        }
        double inverse_percent = 1.0 - offset;
        int redPart = (int)((double)color1.getRed() * inverse_percent + (double)color2.getRed() * offset);
        int greenPart = (int)((double)color1.getGreen() * inverse_percent + (double)color2.getGreen() * offset);
        int bluePart = (int)((double)color1.getBlue() * inverse_percent + (double)color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public static int toRGBA(Color color) {
        return ColorUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static Color interpolate(float value, Color start, Color end) {
        float sr = (float)start.getRed() / 255.0f;
        float sg = (float)start.getGreen() / 255.0f;
        float sb = (float)start.getBlue() / 255.0f;
        float sa = (float)start.getAlpha() / 255.0f;
        float er = (float)end.getRed() / 255.0f;
        float eg = (float)end.getGreen() / 255.0f;
        float eb = (float)end.getBlue() / 255.0f;
        float ea = (float)end.getAlpha() / 255.0f;
        float r = sr * value + er * (1.0f - value);
        float g = sg * value + eg * (1.0f - value);
        float b = sb * value + eb * (1.0f - value);
        float a = sa * value + ea * (1.0f - value);
        return new Color(r, g, b, a);
    }
}

