/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.MathHelper
 */
package me.mioclient.mod.modules.impl.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.mioclient.Mio;
import me.mioclient.api.events.impl.Render2DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.managers.impl.ModuleManager;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.gui.screen.MioClickGui;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.impl.client.ClickGui;
import me.mioclient.mod.modules.impl.client.FontMod;
import me.mioclient.mod.modules.impl.combat.Aura;
import me.mioclient.mod.modules.impl.combat.AutoTrap;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

public class HUD
extends Module {
    public static HUD INSTANCE = new HUD();
    private final Setting<Page> page = this.add(new Setting<Page>("Page", Page.GLOBAL));
    public final Setting<Boolean> potionIcons = this.add(new Setting<Boolean>("NoPotionIcons", false, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> grayColors = this.add(new Setting<Boolean>("Gray", true, v -> this.page.getValue() == Page.GLOBAL));
    public Setting<Boolean> lowerCase = this.add(new Setting<Boolean>("LowerCase", false, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> renderingUp = this.add(new Setting<Boolean>("RenderingUp", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> skeetBar = this.add(new Setting<Boolean>("SkeetMode", false, v -> this.page.getValue() == Page.ELEMENTS).setParent());
    private final Setting<Boolean> jamie = this.add(new Setting<Boolean>("JamieColor", false, v -> this.page.getValue() == Page.ELEMENTS && this.skeetBar.isOpen()));
    private final Setting<Boolean> watermark = this.add(new Setting<Boolean>("Watermark", true, v -> this.page.getValue() == Page.ELEMENTS).setParent());
    public Setting<String> watermarkString = this.add(new Setting<String>("Text", "Mio", v -> !(HUD.mc.currentScreen instanceof MioClickGui) && !(HUD.mc.currentScreen instanceof GuiMainMenu)));
    private final Setting<Boolean> watermarkShort = this.add(new Setting<Boolean>("Shorten", false, v -> this.watermark.isOpen() && this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> watermarkVerColor = this.add(new Setting<Boolean>("VerColor", true, v -> this.watermark.isOpen() && this.page.getValue() == Page.ELEMENTS));
    private final Setting<Integer> waterMarkY = this.add(new Setting<Integer>("Height", 2, 2, 12, v -> this.page.getValue() == Page.ELEMENTS && this.watermark.isOpen()));
    private final Setting<Boolean> idWatermark = this.add(new Setting<Boolean>("IdWatermark", true, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> pvp = this.add(new Setting<Boolean>("PvpInfo", true, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> textRadar = this.add(new Setting<Boolean>("TextRadar", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> coords = this.add(new Setting<Boolean>("Position(XYZ)", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> direction = this.add(new Setting<Boolean>("Direction", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> armor = this.add(new Setting<Boolean>("Armor", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> lag = this.add(new Setting<Boolean>("LagNotifier", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> greeter = this.add(new Setting<Boolean>("Welcomer", false, v -> this.page.getValue() == Page.ELEMENTS).setParent());
    private final Setting<GreeterMode> greeterMode = this.add(new Setting<GreeterMode>("Mode", GreeterMode.PLAYER, v -> this.page.getValue() == Page.ELEMENTS && this.greeter.isOpen()));
    private final Setting<Boolean> greeterNameColor = this.add(new Setting<Boolean>("NameColor", true, v -> this.greeter.isOpen() && this.greeterMode.getValue() == GreeterMode.PLAYER && this.page.getValue() == Page.ELEMENTS));
    private final Setting<String> greeterText = this.add(new Setting<String>("WelcomerText", "i sniff coke and smoke dope i got 2 habbits", v -> this.greeter.isOpen() && this.greeterMode.getValue() == GreeterMode.CUSTOM && this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> arrayList = this.add(new Setting<Boolean>("ArrayList", false, v -> this.page.getValue() == Page.ELEMENTS).setParent());
    private final Setting<Boolean> jamieArray = this.add(new Setting<Boolean>("JamieArray", false, v -> this.page.getValue() == Page.ELEMENTS && this.arrayList.isOpen()));
    private final Setting<Boolean> forgeHax = this.add(new Setting<Boolean>("ForgeHax", false, v -> this.page.getValue() == Page.ELEMENTS && this.arrayList.isOpen()));
    private final Setting<Boolean> arrayListLine = this.add(new Setting<Boolean>("Outline", false, v -> this.page.getValue() == Page.ELEMENTS && this.arrayList.isOpen()));
    private final Setting<Boolean> arrayListRect = this.add(new Setting<Boolean>("Rect", false, v -> this.page.getValue() == Page.ELEMENTS && this.arrayList.isOpen()));
    private final Setting<Boolean> arrayListRectColor = this.add(new Setting<Boolean>("ColorRect", false, v -> this.page.getValue() == Page.ELEMENTS && this.arrayList.isOpen() && this.arrayListRect.getValue() != false));
    private final Setting<Boolean> arrayListGlow = this.add(new Setting<Boolean>("Glow", true, v -> this.page.getValue() == Page.ELEMENTS && this.arrayList.isOpen()));
    private final Setting<Boolean> hideInChat = this.add(new Setting<Boolean>("HideInChat", true, v -> this.page.getValue() == Page.ELEMENTS && this.arrayList.isOpen()));
    private final Setting<Boolean> potions = this.add(new Setting<Boolean>("Potions", false, v -> this.page.getValue() == Page.ELEMENTS).setParent());
    private final Setting<Boolean> potionColor = this.add(new Setting<Boolean>("PotionColor", false, v -> this.page.getValue() == Page.ELEMENTS && this.potions.isOpen()));
    private final Setting<Boolean> ping = this.add(new Setting<Boolean>("Ping", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> speed = this.add(new Setting<Boolean>("Speed", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> tps = this.add(new Setting<Boolean>("TPS", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> fps = this.add(new Setting<Boolean>("FPS", false, v -> this.page.getValue() == Page.ELEMENTS));
    private final Setting<Boolean> time = this.add(new Setting<Boolean>("Time", false, v -> this.page.getValue() == Page.ELEMENTS));
    public final Setting<ModuleManager.Ordering> ordering = this.add(new Setting<ModuleManager.Ordering>("Ordering", ModuleManager.Ordering.LENGTH, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Integer> lagTime = this.add(new Setting<Integer>("LagTime", 1000, 0, 2000, v -> this.page.getValue() == Page.GLOBAL));
    private final Timer timer = new Timer();
    private Map<String, Integer> players = new HashMap<String, Integer>();
    private int color;

    public HUD() {
        super("HUD", "HUD elements drawn on your screen", Category.CLIENT, true);
        this.setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(500L)) {
            this.players = this.getTextRadarMap();
            this.timer.reset();
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String direction;
        String str1;
        String fpsText;
        String str;
        int i;
        String grayString;
        int glowColor;
        int rectColor;
        int j;
        if (HUD.fullNullCheck()) {
            return;
        }
        int width = Managers.TEXT.scaledWidth;
        int height = Managers.TEXT.scaledHeight;
        this.color = ColorUtil.toRGBA(ClickGui.INSTANCE.color.getValue().getRed(), ClickGui.INSTANCE.color.getValue().getGreen(), ClickGui.INSTANCE.color.getValue().getBlue());
        if (this.watermark.getValue().booleanValue()) {
            String mioString = this.watermarkString.getValue() + " ";
            String verColor = this.watermarkVerColor.getValue() != false ? "" + (Object)ChatFormatting.WHITE : "";
            String verString = verColor + (this.watermarkShort.getValue() != false ? "v0.6.9-alpha".substring(0, 4) : "v0.6.9-alpha+" + Mio.VERHASH);
            if (ClickGui.INSTANCE.rainbow.getValue().booleanValue()) {
                if (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.STATIC) {
                    Managers.TEXT.drawString((this.lowerCase.getValue() != false ? mioString.toLowerCase() : mioString) + verString, 2.0f, this.waterMarkY.getValue().intValue(), Managers.COLORS.getRainbow().getRGB(), true);
                } else if (this.watermarkVerColor.getValue().booleanValue()) {
                    this.drawDoubleRainbowRollingString(this.lowerCase.getValue() != false ? mioString.toLowerCase() : mioString, verString, 2.0f, this.waterMarkY.getValue().intValue(), true);
                } else {
                    Managers.TEXT.drawRollingRainbowString((this.lowerCase.getValue() != false ? mioString.toLowerCase() : mioString) + verString, 2.0f, this.waterMarkY.getValue().intValue(), true);
                }
            } else {
                Managers.TEXT.drawString((this.lowerCase.getValue() != false ? mioString.toLowerCase() : mioString) + verString, 2.0f, this.waterMarkY.getValue().intValue(), this.color, true);
            }
        }
        Color color = new Color(ClickGui.INSTANCE.color.getValue().getRed(), ClickGui.INSTANCE.color.getValue().getGreen(), ClickGui.INSTANCE.color.getValue().getBlue());
        if (this.skeetBar.getValue().booleanValue()) {
            if (this.jamie.getValue().booleanValue()) {
                RenderUtil.drawHGradientRect(0.0f, 0.0f, (float)width / 5.0f, 1.0f, ColorUtil.toRGBA(0, 180, 255), ColorUtil.toRGBA(255, 180, 255));
                RenderUtil.drawHGradientRect((float)width / 5.0f, 0.0f, (float)width / 5.0f * 2.0f, 1.0f, ColorUtil.toRGBA(255, 180, 255), ColorUtil.toRGBA(255, 255, 255));
                RenderUtil.drawHGradientRect((float)width / 5.0f * 2.0f, 0.0f, (float)width / 5.0f * 3.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255), ColorUtil.toRGBA(255, 255, 255));
                RenderUtil.drawHGradientRect((float)width / 5.0f * 3.0f, 0.0f, (float)width / 5.0f * 4.0f, 1.0f, ColorUtil.toRGBA(255, 255, 255), ColorUtil.toRGBA(255, 180, 255));
                RenderUtil.drawHGradientRect((float)width / 5.0f * 4.0f, 0.0f, width, 1.0f, ColorUtil.toRGBA(255, 180, 255), ColorUtil.toRGBA(0, 180, 255));
            }
            if (ClickGui.INSTANCE.rainbow.getValue().booleanValue() && ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING && !this.jamie.getValue().booleanValue()) {
                int[] arrayOfInt = new int[]{1};
                RenderUtil.drawHGradientRect(0.0f, 0.0f, (float)width / 2.0f, 1.0f, ColorUtil.rainbow(arrayOfInt[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB(), ColorUtil.rainbow(20 * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB());
                RenderUtil.drawHGradientRect((float)width / 2.0f, 0.0f, width, 1.0f, ColorUtil.rainbow(20 * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB(), ColorUtil.rainbow(40 * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB());
                arrayOfInt[0] = arrayOfInt[0] + 1;
            }
            if (!ClickGui.INSTANCE.rainbow.getValue().booleanValue() && !this.jamie.getValue().booleanValue()) {
                RenderUtil.drawHGradientRect(0.0f, 0.0f, (float)width / 2.0f, 1.0f, ColorUtil.pulseColor(color, 50, 1000).getRGB(), ColorUtil.pulseColor(color, 200, 1).getRGB());
                RenderUtil.drawHGradientRect((float)width / 2.0f, 0.0f, width, 1.0f, ColorUtil.pulseColor(color, 200, 1).getRGB(), ColorUtil.pulseColor(color, 50, 1000).getRGB());
            }
        }
        if (this.textRadar.getValue().booleanValue()) {
            this.drawTextRadar(this.watermark.getValue() != false ? this.waterMarkY.getValue() + 2 : 2);
        }
        if (this.pvp.getValue().booleanValue()) {
            this.drawPvPInfo();
        }
        this.color = ColorUtil.toRGBA(ClickGui.INSTANCE.color.getValue().getRed(), ClickGui.INSTANCE.color.getValue().getGreen(), ClickGui.INSTANCE.color.getValue().getBlue());
        if (this.idWatermark.getValue().booleanValue()) {
            String mioString = "mioclient";
            String domainString = (Object)ChatFormatting.LIGHT_PURPLE + ".me";
            float offset = (float)Managers.TEXT.scaledHeight / 2.0f - 30.0f;
            if (ClickGui.INSTANCE.rainbow.getValue().booleanValue()) {
                if (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.STATIC) {
                    Managers.TEXT.drawString(mioString + domainString, 2.0f, offset, Managers.COLORS.getRainbow().getRGB(), true);
                } else {
                    Managers.TEXT.drawRollingRainbowString(mioString, 2.0f, offset, true);
                    Managers.TEXT.drawString(domainString, (float)Managers.TEXT.getStringWidth(mioString) + (FontMod.INSTANCE.isOn() ? -1.0f : 1.4f), offset, -1, true);
                }
            } else {
                Managers.TEXT.drawString(mioString + domainString, 2.0f, offset, this.color, true);
            }
        }
        int[] counter1 = new int[]{1};
        boolean inChat = HUD.mc.currentScreen instanceof GuiChat;
        long enabledMods = Managers.MODULES.modules.stream().filter(module -> module.isOn() && module.isDrawn()).count();
        int n = j = inChat && this.renderingUp.getValue() == false ? 14 : 0;
        int n2 = this.jamieArray.getValue().booleanValue() ? ColorUtil.injectAlpha(this.getJamieColor(counter1[0] + 1), 60) : (this.arrayListRectColor.getValue().booleanValue() ? (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.toRGBA(ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRed(), ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getGreen(), ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getBlue(), 60) : ColorUtil.toRGBA(Managers.COLORS.getRainbow().getRed(), Managers.COLORS.getRainbow().getGreen(), Managers.COLORS.getRainbow().getBlue(), 60)) : ColorUtil.toRGBA(ColorUtil.pulseColor(color, 50, counter1[0]).getRed(), ColorUtil.pulseColor(color, 50, counter1[0]).getGreen(), ColorUtil.pulseColor(color, 50, counter1[0]).getBlue(), 60)) : (rectColor = ColorUtil.toRGBA(10, 10, 10, 60)));
        int n3 = this.jamieArray.getValue().booleanValue() ? ColorUtil.injectAlpha(this.getJamieColor(counter1[0] + 1), 60) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.toRGBA(ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRed(), ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getGreen(), ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getBlue(), 60) : ColorUtil.toRGBA(Managers.COLORS.getRainbow().getRed(), Managers.COLORS.getRainbow().getGreen(), Managers.COLORS.getRainbow().getBlue(), 60)) : (glowColor = ColorUtil.toRGBA(ColorUtil.pulseColor(color, 50, counter1[0]).getRed(), ColorUtil.pulseColor(color, 50, counter1[0]).getGreen(), ColorUtil.pulseColor(color, 50, counter1[0]).getBlue(), 60)));
        if (this.arrayList.getValue().booleanValue()) {
            String nextStr;
            Module nextModule;
            Module module2;
            String str2;
            int k;
            if (this.renderingUp.getValue().booleanValue()) {
                if (inChat && this.hideInChat.getValue().booleanValue()) {
                    Managers.TEXT.drawString(enabledMods + " mods enabled", width - 2 - Managers.TEXT.getStringWidth(enabledMods + " mods enabled"), 2 + j, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : this.color, true);
                } else if (this.ordering.getValue() == ModuleManager.Ordering.ABC) {
                    for (k = 0; k < Managers.MODULES.sortedAbc.size(); ++k) {
                        str2 = Managers.MODULES.sortedAbc.get(k);
                        if (this.forgeHax.getValue().booleanValue()) {
                            str2 = Managers.MODULES.sortedAbc.get(k) + (Object)ChatFormatting.RESET + "<";
                        }
                        if (this.arrayListRect.getValue().booleanValue()) {
                            Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 1), (int)(j == 0 ? 0 : 2 + j * 10), (int)width, (int)(2 + j * 10 + 10), (int)rectColor);
                        }
                        if (this.arrayListGlow.getValue().booleanValue()) {
                            RenderUtil.drawGlow(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 1, j == 0 ? 0.0 : (double)(2 + j * 10 - 4), width, 2 + j * 10 + 15, glowColor);
                        }
                        if (this.arrayListLine.getValue().booleanValue()) {
                            Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 2), (int)(j == 0 ? 0 : 2 + j * 10 - 1), (int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 1), (int)(2 + j * 10 + 10), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                            int a = k + 1;
                            if (a >= Managers.MODULES.sortedAbc.size()) {
                                a = k;
                            }
                            String nextStr2 = Managers.MODULES.sortedAbc.get(a);
                            if (this.forgeHax.getValue().booleanValue()) {
                                nextStr2 = Managers.MODULES.sortedAbc.get(a) + (Object)ChatFormatting.RESET + "<";
                            }
                            Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 2), (int)(2 + (j + 1) * 10 - 1), (int)(a == k ? width : width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) + ((this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(nextStr2.toLowerCase()) : Managers.TEXT.getStringWidth(nextStr2))) - 1), (int)(2 + (j + 1) * 10), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                        }
                        Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str2.toLowerCase() : str2, width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)), 2 + j * 10, this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB()), true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (k = 0; k < Managers.MODULES.sortedLength.size(); ++k) {
                        module2 = Managers.MODULES.sortedLength.get(k);
                        String str3 = module2.getName() + (Object)ChatFormatting.GRAY + (module2.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + module2.getInfo() + (Object)ChatFormatting.GRAY + "]" : "");
                        if (this.forgeHax.getValue().booleanValue()) {
                            str3 = module2.getName() + (Object)ChatFormatting.GRAY + (module2.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + module2.getInfo() + (Object)ChatFormatting.GRAY + "]" + (Object)ChatFormatting.RESET + "<" : (Object)ChatFormatting.RESET + "<");
                        }
                        if (this.arrayListRect.getValue().booleanValue()) {
                            Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)) - 1), (int)(j == 0 ? 0 : 2 + j * 10), (int)width, (int)(2 + j * 10 + 10), (int)rectColor);
                        }
                        if (this.arrayListGlow.getValue().booleanValue()) {
                            RenderUtil.drawGlow(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)) - 1, j == 0 ? 0.0 : (double)(2 + j * 10 - 4), width, 2 + j * 10 + 15, glowColor);
                        }
                        if (this.arrayListLine.getValue().booleanValue()) {
                            Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)) - 2), (int)(j == 0 ? 0 : 2 + j * 10), (int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)) - 1), (int)(2 + j * 10 + 10), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                            int a = k + 1;
                            if (a >= Managers.MODULES.sortedLength.size()) {
                                a = k;
                            }
                            nextModule = Managers.MODULES.sortedLength.get(a);
                            nextStr = nextModule.getName() + (Object)ChatFormatting.GRAY + (nextModule.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + nextModule.getInfo() + (Object)ChatFormatting.GRAY + "]" : "");
                            if (this.forgeHax.getValue().booleanValue()) {
                                nextStr = nextModule.getName() + (Object)ChatFormatting.GRAY + (nextModule.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + nextModule.getInfo() + (Object)ChatFormatting.GRAY + "]" + (Object)ChatFormatting.RESET + "<" : (Object)ChatFormatting.RESET + "<");
                            }
                            Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)) - 2), (int)(2 + (j + 1) * 10), (int)(a == k ? width : width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)) + ((this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)) - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(nextStr.toLowerCase()) : Managers.TEXT.getStringWidth(nextStr))) - 1), (int)(2 + (j + 1) * 10 + 1), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                        }
                        Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str3.toLowerCase() : str3, width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str3.toLowerCase()) : Managers.TEXT.getStringWidth(str3)), 2 + j * 10, this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB()), true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (inChat && this.hideInChat.getValue().booleanValue()) {
                Managers.TEXT.drawString(enabledMods + " mods enabled", width - 2 - Managers.TEXT.getStringWidth(enabledMods + " mods enabled"), height - j - 11, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : this.color, true);
            } else if (this.ordering.getValue() == ModuleManager.Ordering.ABC) {
                for (k = 0; k < Managers.MODULES.sortedAbc.size(); ++k) {
                    str2 = Managers.MODULES.sortedAbc.get(k);
                    if (this.forgeHax.getValue().booleanValue()) {
                        str2 = Managers.MODULES.sortedAbc.get(k) + (Object)ChatFormatting.RESET + "<";
                    }
                    j += 10;
                    if (this.arrayListRect.getValue().booleanValue()) {
                        Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 1), (int)(height - j), (int)width, (int)(j == 1 ? height : height - j + 10), (int)rectColor);
                    }
                    if (this.arrayListGlow.getValue().booleanValue()) {
                        RenderUtil.drawGlow(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 1, height - j - 4, width, j == 1 ? (double)height : (double)(height - j + 15), glowColor);
                    }
                    if (this.arrayListLine.getValue().booleanValue()) {
                        Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 2), (int)(height - j), (int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 1), (int)(j == 1 ? height : height - j + 10), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                        int a = k + 1;
                        if (a >= Managers.MODULES.sortedAbc.size()) {
                            a = k;
                        }
                        String nextStr3 = Managers.MODULES.sortedAbc.get(a);
                        if (this.forgeHax.getValue().booleanValue()) {
                            nextStr3 = Managers.MODULES.sortedAbc.get(a) + (Object)ChatFormatting.RESET + "<";
                        }
                        Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - 2), (int)(height - j - 1), (int)(a == k ? width : width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) + ((this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)) - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(nextStr3.toLowerCase()) : Managers.TEXT.getStringWidth(nextStr3))) - 1), (int)(j == 1 ? height : height - j), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                    }
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str2.toLowerCase() : str2, width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str2.toLowerCase()) : Managers.TEXT.getStringWidth(str2)), height - j, this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB()), true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (k = 0; k < Managers.MODULES.sortedLength.size(); ++k) {
                    module2 = Managers.MODULES.sortedLength.get(k);
                    String str4 = module2.getName() + (Object)ChatFormatting.GRAY + (module2.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + module2.getInfo() + (Object)ChatFormatting.GRAY + "]" : "");
                    if (this.forgeHax.getValue().booleanValue()) {
                        str4 = module2.getName() + (Object)ChatFormatting.GRAY + (module2.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + module2.getInfo() + (Object)ChatFormatting.GRAY + "]" + (Object)ChatFormatting.RESET + "<" : (Object)ChatFormatting.RESET + "<");
                    }
                    j += 10;
                    if (this.arrayListRect.getValue().booleanValue()) {
                        Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)) - 1), (int)(height - j), (int)width, (int)(j == 1 ? height : height - j + 10), (int)rectColor);
                    }
                    if (this.arrayListGlow.getValue().booleanValue()) {
                        RenderUtil.drawGlow(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)) - 1, height - j - 4, width, j == 1 ? (double)height : (double)(height - j + 15), glowColor);
                    }
                    if (this.arrayListLine.getValue().booleanValue()) {
                        Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)) - 2), (int)(height - j), (int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)) - 1), (int)(j == 1 ? height : height - j + 10), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                        int a = k + 1;
                        if (a >= Managers.MODULES.sortedLength.size()) {
                            a = k;
                        }
                        nextModule = Managers.MODULES.sortedLength.get(a);
                        nextStr = nextModule.getName() + (Object)ChatFormatting.GRAY + (nextModule.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + nextModule.getInfo() + (Object)ChatFormatting.GRAY + "]" : "");
                        if (this.forgeHax.getValue().booleanValue()) {
                            nextStr = nextModule.getName() + (Object)ChatFormatting.GRAY + (nextModule.getInfo() != null ? " [" + (Object)ChatFormatting.WHITE + nextModule.getInfo() + (Object)ChatFormatting.GRAY + "]" + (Object)ChatFormatting.RESET + "<" : (Object)ChatFormatting.RESET + "<");
                        }
                        Gui.drawRect((int)(width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)) - 2), (int)(height - j - 1), (int)(a == k ? width : width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)) + ((this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)) - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(nextStr.toLowerCase()) : Managers.TEXT.getStringWidth(nextStr))) - 1), (int)(height - j), (int)(this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB())));
                    }
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str4.toLowerCase() : str4, width - 2 - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str4.toLowerCase()) : Managers.TEXT.getStringWidth(str4)), height - j, this.jamieArray.getValue().booleanValue() ? this.getJamieColor(counter1[0] - 2) : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB()), true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        String string = grayString = this.grayColors.getValue() != false ? String.valueOf((Object)ChatFormatting.GRAY) : "";
        int n4 = HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() != false ? 13 : (i = this.renderingUp.getValue() != false ? -2 : 0);
        if (this.renderingUp.getValue().booleanValue()) {
            if (this.potions.getValue().booleanValue()) {
                ArrayList effects = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    str = this.getColoredPotionString(potionEffect);
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str.toLowerCase() : str, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str.toLowerCase()) : Managers.TEXT.getStringWidth(str)) - 2, height - 2 - (i += 10), this.potionColor.getValue().booleanValue() ? potionEffect.getPotion().getLiquidColor() : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB()), true);
                    counter1[0] = counter1[0] + 1;
                }
            }
            if (this.speed.getValue().booleanValue()) {
                String str5 = grayString + "Speed " + (Object)ChatFormatting.WHITE + Managers.SPEED.getSpeedKpH() + " km/h";
                Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str5.toLowerCase() : str5, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str5.toLowerCase()) : Managers.TEXT.getStringWidth(str5)) - 2, height - 2 - (i += 10), ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str6 = grayString + "Time " + (Object)ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str6.toLowerCase() : str6, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str6.toLowerCase()) : Managers.TEXT.getStringWidth(str6)) - 2, height - 2 - (i += 10), ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str7 = grayString + "TPS " + (Object)ChatFormatting.WHITE + Managers.SERVER.getTPS();
                Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str7.toLowerCase() : str7, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str7.toLowerCase()) : Managers.TEXT.getStringWidth(str7)) - 2, height - 2 - (i += 10), ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + (Object)ChatFormatting.WHITE + Managers.FPS.getFPS();
            str1 = grayString + "Ping " + (Object)ChatFormatting.WHITE + Managers.SERVER.getPing();
            if (Managers.TEXT.getStringWidth(str1) > Managers.TEXT.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str1.toLowerCase() : str1, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str1.toLowerCase()) : Managers.TEXT.getStringWidth(str1)) - 2, height - 2 - (i += 10), ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? fpsText.toLowerCase() : fpsText, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(fpsText.toLowerCase()) : Managers.TEXT.getStringWidth(fpsText)) - 2, height - 2 - (i += 10), ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? fpsText.toLowerCase() : fpsText, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(fpsText.toLowerCase()) : Managers.TEXT.getStringWidth(fpsText)) - 2, height - 2 - (i += 10), ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str1.toLowerCase() : str1, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str1.toLowerCase()) : Managers.TEXT.getStringWidth(str1)) - 2, height - 2 - (i += 10), ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            if (this.potions.getValue().booleanValue()) {
                ArrayList effects = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    str = this.getColoredPotionString(potionEffect);
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str.toLowerCase() : str, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str.toLowerCase()) : Managers.TEXT.getStringWidth(str)) - 2, 2 + i++ * 10, this.potionColor.getValue().booleanValue() ? potionEffect.getPotion().getLiquidColor() : (ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB()), true);
                    counter1[0] = counter1[0] + 1;
                }
            }
            if (this.speed.getValue().booleanValue()) {
                String str8 = grayString + "Speed " + (Object)ChatFormatting.WHITE + Managers.SPEED.getSpeedKpH() + " km/h";
                Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str8.toLowerCase() : str8, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str8.toLowerCase()) : Managers.TEXT.getStringWidth(str8)) - 2, 2 + i++ * 10, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str9 = grayString + "Time " + (Object)ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str9.toLowerCase() : str9, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str9.toLowerCase()) : Managers.TEXT.getStringWidth(str9)) - 2, 2 + i++ * 10, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str10 = grayString + "TPS " + (Object)ChatFormatting.WHITE + Managers.SERVER.getTPS();
                Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str10.toLowerCase() : str10, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str10.toLowerCase()) : Managers.TEXT.getStringWidth(str10)) - 2, 2 + i++ * 10, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = grayString + "FPS " + (Object)ChatFormatting.WHITE + Managers.FPS.getFPS();
            str1 = grayString + "Ping " + (Object)ChatFormatting.WHITE + Managers.SERVER.getPing();
            if (Managers.TEXT.getStringWidth(str1) > Managers.TEXT.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str1.toLowerCase() : str1, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str1.toLowerCase()) : Managers.TEXT.getStringWidth(str1)) - 2, 2 + i++ * 10, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? fpsText.toLowerCase() : fpsText, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(fpsText.toLowerCase()) : Managers.TEXT.getStringWidth(fpsText)) - 2, 2 + i++ * 10, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? fpsText.toLowerCase() : fpsText, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(fpsText.toLowerCase()) : Managers.TEXT.getStringWidth(fpsText)) - 2, 2 + i++ * 10, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    Managers.TEXT.drawString(this.lowerCase.getValue() != false ? str1.toLowerCase() : str1, width - (this.lowerCase.getValue() != false ? Managers.TEXT.getStringWidth(str1.toLowerCase()) : Managers.TEXT.getStringWidth(str1)) - 2, 2 + i++ * 10, ClickGui.INSTANCE.rainbow.getValue().booleanValue() ? (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.ROLLING ? ColorUtil.rainbow(counter1[0] * ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB() : Managers.COLORS.getRainbow().getRGB()) : ColorUtil.pulseColor(color, 50, counter1[0]).getRGB(), true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = HUD.mc.world.getBiome(HUD.mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int)HUD.mc.player.posX;
        int posY = (int)HUD.mc.player.posY;
        int posZ = (int)HUD.mc.player.posZ;
        float nether = !inHell ? 0.125f : 8.0f;
        int hposX = (int)(HUD.mc.player.posX * (double)nether);
        int hposZ = (int)(HUD.mc.player.posZ * (double)nether);
        int yawPitch = (int)MathHelper.wrapDegrees((float)HUD.mc.player.rotationYaw);
        int p = this.coords.getValue() != false ? 0 : 11;
        i = HUD.mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = (this.lowerCase.getValue() != false ? "XYZ: ".toLowerCase() : "XYZ: ") + (Object)ChatFormatting.WHITE + (inHell ? posX + ", " + posY + ", " + posZ + (Object)ChatFormatting.GRAY + " [" + (Object)ChatFormatting.WHITE + hposX + ", " + hposZ + (Object)ChatFormatting.GRAY + "]" + (Object)ChatFormatting.WHITE : posX + ", " + posY + ", " + posZ + (Object)ChatFormatting.GRAY + " [" + (Object)ChatFormatting.WHITE + hposX + ", " + hposZ + (Object)ChatFormatting.GRAY + "]");
        String string2 = direction = this.direction.getValue() != false ? Managers.ROTATIONS.getDirection4D(false) : "";
        String yaw = this.direction.getValue() != false ? (this.lowerCase.getValue() != false ? "Yaw: ".toLowerCase() : "Yaw: ") + (Object)ChatFormatting.WHITE + yawPitch : "";
        String coords = this.coords.getValue() != false ? coordinates : "";
        i += 10;
        if (HUD.mc.currentScreen instanceof GuiChat && this.direction.getValue().booleanValue()) {
            yaw = "";
            direction = (this.lowerCase.getValue() != false ? "Yaw: ".toLowerCase() : "Yaw: ") + (Object)ChatFormatting.WHITE + yawPitch + (Object)ChatFormatting.RESET + " " + this.getFacingDirectionShort();
        }
        if (ClickGui.INSTANCE.rainbow.getValue().booleanValue()) {
            String rainbowCoords;
            String string3 = this.coords.getValue() != false ? (this.lowerCase.getValue() != false ? "XYZ: ".toLowerCase() : "XYZ: ") + (Object)ChatFormatting.WHITE + (inHell ? posX + ", " + posY + ", " + posZ + (Object)ChatFormatting.GRAY + " [" + (Object)ChatFormatting.WHITE + hposX + ", " + hposZ + (Object)ChatFormatting.GRAY + "]" + (Object)ChatFormatting.WHITE : posX + ", " + posY + ", " + posZ + (Object)ChatFormatting.GRAY + " [" + (Object)ChatFormatting.WHITE + hposX + ", " + hposZ + (Object)ChatFormatting.GRAY + "]") : (rainbowCoords = "");
            if (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.STATIC) {
                Managers.TEXT.drawString(direction, 2.0f, height - i - 11 + p, Managers.COLORS.getRainbow().getRGB(), true);
                Managers.TEXT.drawString(yaw, 2.0f, height - i - 22 + p, Managers.COLORS.getRainbow().getRGB(), true);
                Managers.TEXT.drawString(rainbowCoords, 2.0f, height - i, Managers.COLORS.getRainbow().getRGB(), true);
            } else {
                if (HUD.mc.currentScreen instanceof GuiChat && this.direction.getValue().booleanValue()) {
                    this.drawDoubleRainbowRollingString(this.lowerCase.getValue() != false ? "Yaw: ".toLowerCase() : "Yaw: ", "" + (Object)ChatFormatting.WHITE + yawPitch, 2.0f, height - i - 11 + p, true);
                    String uh = "Yaw: " + (Object)ChatFormatting.WHITE + yawPitch;
                    Managers.TEXT.drawRollingRainbowString(" " + this.getFacingDirectionShort(), 2.0f + (float)Managers.TEXT.getStringWidth(uh), height - i - 11 + p, true);
                } else {
                    Managers.TEXT.drawRollingRainbowString(this.direction.getValue() != false ? direction : "", 2.0f, height - i - 11 + p, true);
                    this.drawDoubleRainbowRollingString(this.direction.getValue().booleanValue() ? (this.lowerCase.getValue().booleanValue() ? "Yaw: ".toLowerCase() : "Yaw: ") : "", this.direction.getValue() != false ? "" + (Object)ChatFormatting.WHITE + yawPitch : "", 2.0f, height - i - 22 + p, true);
                }
                this.drawDoubleRainbowRollingString(this.coords.getValue().booleanValue() ? (this.lowerCase.getValue().booleanValue() ? "XYZ: ".toLowerCase() : "XYZ: ") : "", this.coords.getValue() != false ? "" + (Object)ChatFormatting.WHITE + (inHell ? posX + ", " + posY + ", " + posZ + (Object)ChatFormatting.GRAY + " [" + (Object)ChatFormatting.WHITE + hposX + ", " + hposZ + (Object)ChatFormatting.GRAY + "]" + (Object)ChatFormatting.WHITE : posX + ", " + posY + ", " + posZ + (Object)ChatFormatting.GRAY + " [" + (Object)ChatFormatting.WHITE + hposX + ", " + hposZ + (Object)ChatFormatting.GRAY + "]") : "", 2.0f, height - i, true);
            }
        } else {
            Managers.TEXT.drawString(direction, 2.0f, height - i - 11 + p, this.color, true);
            Managers.TEXT.drawString(yaw, 2.0f, height - i - 22 + p, this.color, true);
            Managers.TEXT.drawString(coords, 2.0f, height - i, this.color, true);
        }
        if (this.armor.getValue().booleanValue()) {
            this.drawArmorHUD();
        }
        if (this.greeter.getValue().booleanValue()) {
            this.drawWelcomer();
        }
        if (this.lag.getValue().booleanValue()) {
            this.drawLagOMeter();
        }
    }

    private void drawWelcomer() {
        String text;
        int width = Managers.TEXT.scaledWidth;
        String nameColor = this.greeterNameColor.getValue() != false ? "" + (Object)ChatFormatting.WHITE : "";
        String string = text = this.lowerCase.getValue() != false ? "Welcome, ".toLowerCase() : "Welcome, ";
        if (this.greeterMode.getValue() == GreeterMode.PLAYER) {
            if (this.greeter.getValue().booleanValue()) {
                text = text + nameColor + HUD.mc.player.getDisplayNameString();
            }
            if (ClickGui.INSTANCE.rainbow.getValue().booleanValue()) {
                if (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.STATIC) {
                    Managers.TEXT.drawString(text + (Object)ChatFormatting.RESET + " :')", (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(text) / 2.0f + 2.0f, 2.0f, Managers.COLORS.getRainbow().getRGB(), true);
                } else if (this.greeterNameColor.getValue().booleanValue()) {
                    this.drawDoubleRainbowRollingString(this.lowerCase.getValue() != false ? "Welcome,".toLowerCase() : "Welcome,", (FontMod.INSTANCE.isOn() ? "" : " ") + (Object)ChatFormatting.WHITE + HUD.mc.player.getDisplayNameString(), (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(text) / 2.0f + 2.0f, 2.0f, true);
                    Managers.TEXT.drawRollingRainbowString(" :')", (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(text) / 2.0f + 1.5f + (float)Managers.TEXT.getStringWidth(text) - (FontMod.INSTANCE.isOn() ? 1.5f : 0.0f), 2.0f, true);
                } else {
                    Managers.TEXT.drawRollingRainbowString((this.lowerCase.getValue() != false ? "Welcome,".toLowerCase() : "Welcome, ") + HUD.mc.player.getDisplayNameString() + " :')", (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(text) / 2.0f + 2.0f, 2.0f, true);
                }
            } else {
                Managers.TEXT.drawString(text + (Object)ChatFormatting.RESET + " :')", (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.color, true);
            }
        } else {
            String lel = this.greeterText.getValue();
            if (this.greeter.getValue().booleanValue()) {
                lel = this.greeterText.getValue();
            }
            if (ClickGui.INSTANCE.rainbow.getValue().booleanValue()) {
                if (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.STATIC) {
                    Managers.TEXT.drawString(lel, (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(lel) / 2.0f + 2.0f, 2.0f, Managers.COLORS.getRainbow().getRGB(), true);
                } else {
                    Managers.TEXT.drawRollingRainbowString(lel, (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(lel) / 2.0f + 2.0f, 2.0f, true);
                }
            } else {
                Managers.TEXT.drawString(lel, (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(lel) / 2.0f + 2.0f, 2.0f, this.color, true);
            }
        }
    }

    private void drawLagOMeter() {
        int width = Managers.TEXT.scaledWidth;
        if (Managers.SERVER.isServerNotResponding()) {
            String text = (Object)ChatFormatting.RED + (this.lowerCase.getValue() != false ? "Server is lagging for ".toLowerCase() : "Server is lagging for ") + MathUtil.round((float)Managers.SERVER.serverRespondingTime() / 1000.0f, 1) + "s.";
            Managers.TEXT.drawString(text, (float)width / 2.0f - (float)Managers.TEXT.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.color, true);
        }
    }

    private void drawArmorHUD() {
        int width = Managers.TEXT.scaledWidth;
        int height = Managers.TEXT.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            Managers.TEXT.drawStringWithShadow(s, x + 19 - 2 - Managers.TEXT.getStringWidth(s), y + 9, 0xFFFFFF);
            int dmg = 0;
            int itemDurability = is.getMaxDamage() - is.getItemDamage();
            float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
            float red = 1.0f - green;
            dmg = 100 - (int)(red * 100.0f);
            Managers.TEXT.drawStringWithShadow(dmg + "", x + 8 - Managers.TEXT.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    private void drawPvPInfo() {
        float yOffset = (float)Managers.TEXT.scaledHeight / 2.0f;
        int totemCount = HUD.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totemCount += HUD.mc.player.getHeldItemOffhand().getCount();
        }
        int pingCount = Managers.SERVER.getPing();
        EntityPlayer target = EntityUtil.getClosestEnemy(7.0);
        String totemString = "" + (Object)(totemCount != 0 ? ChatFormatting.GREEN : ChatFormatting.RED) + totemCount;
        String htrColor = String.valueOf((Object)(target != null && HUD.mc.player.getDistance((Entity)target) <= Aura.INSTANCE.range.getValue().floatValue() ? ChatFormatting.GREEN : ChatFormatting.DARK_RED));
        String plrColor = String.valueOf((Object)(target != null && HUD.mc.player.getDistance((Entity)target) <= 5.0f && AutoTrap.INSTANCE.isOn() ? ChatFormatting.GREEN : ChatFormatting.DARK_RED));
        String htr = "HTR";
        String plr = "PLR";
        String pingColor = pingCount < 40 ? String.valueOf((Object)ChatFormatting.GREEN) : (pingCount < 65 ? String.valueOf((Object)ChatFormatting.DARK_GREEN) : (pingCount < 80 ? String.valueOf((Object)ChatFormatting.YELLOW) : (pingCount < 110 ? String.valueOf((Object)ChatFormatting.RED) : (pingCount < 160 ? String.valueOf((Object)ChatFormatting.DARK_RED) : String.valueOf((Object)ChatFormatting.DARK_RED)))));
        String safetyColor = !EntityUtil.isSafe((Entity)HUD.mc.player, 0, true) ? String.valueOf((Object)ChatFormatting.DARK_RED) : String.valueOf((Object)ChatFormatting.GREEN);
        Managers.TEXT.drawString(htrColor + htr, 2.0f, yOffset - 20.0f, this.color, true);
        Managers.TEXT.drawString(plrColor + plr, 2.0f, yOffset - 10.0f, this.color, true);
        Managers.TEXT.drawString(pingColor + pingCount + " MS", 2.0f, yOffset, this.color, true);
        Managers.TEXT.drawString(totemString, 2.0f, yOffset + 10.0f, this.color, true);
        Managers.TEXT.drawString(safetyColor + "LBY", 2.0f, yOffset + 20.0f, this.color, true);
    }

    private void drawDoubleRainbowRollingString(String first, String second, float x, float y, boolean shadow) {
        Managers.TEXT.drawRollingRainbowString(first, x, y, shadow);
        Managers.TEXT.drawString(second, x + (float)Managers.TEXT.getStringWidth(first), y, -1, shadow);
    }

    private void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = Managers.TEXT.getFontHeight() + 7 + yOffset;
            for (Map.Entry<String, Integer> player : this.players.entrySet()) {
                String text = player.getKey() + " ";
                int textHeight = Managers.TEXT.getFontHeight() + 1;
                if (ClickGui.INSTANCE.rainbow.getValue().booleanValue()) {
                    if (ClickGui.INSTANCE.hudRainbow.getValue() == ClickGui.HudRainbow.STATIC) {
                        Managers.TEXT.drawString(text, 2.0f, y, ColorUtil.rainbow(ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB(), true);
                        y += textHeight;
                        continue;
                    }
                    Managers.TEXT.drawString(text, 2.0f, y, ColorUtil.rainbow(ClickGui.INSTANCE.rainbowDelay.getValue()).getRGB(), true);
                    y += textHeight;
                    continue;
                }
                Managers.TEXT.drawString(text, 2.0f, y, this.color, true);
                y += textHeight;
            }
        }
    }

    private Map<String, Integer> getTextRadarMap() {
        Map<String, Integer> retval = new HashMap<String, Integer>();
        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder distanceSB = new StringBuilder();
        for (EntityPlayer player : HUD.mc.world.playerEntities) {
            if (player.isInvisible() || player.getName().equals(HUD.mc.player.getName())) continue;
            int distanceInt = (int)HUD.mc.player.getDistance((Entity)player);
            String distance = dfDistance.format(distanceInt);
            if (distanceInt >= 25) {
                distanceSB.append((Object)ChatFormatting.GREEN);
            } else if (distanceInt > 10) {
                distanceSB.append((Object)ChatFormatting.YELLOW);
            } else {
                distanceSB.append((Object)ChatFormatting.RED);
            }
            distanceSB.append(distance);
            retval.put((Managers.FRIENDS.isCool(player.getName()) ? (Object)ChatFormatting.GOLD + "< > " + (Object)ChatFormatting.RESET : "") + (Object)(Managers.FRIENDS.isFriend(player) ? ChatFormatting.AQUA : ChatFormatting.RESET) + player.getName() + " " + (Object)ChatFormatting.WHITE + "[" + (Object)ChatFormatting.RESET + distanceSB + "m" + (Object)ChatFormatting.WHITE + "] " + (Object)ChatFormatting.GREEN, (int)HUD.mc.player.getDistance((Entity)player));
            distanceSB.setLength(0);
        }
        if (!retval.isEmpty()) {
            retval = MathUtil.sortByValue(retval, false);
        }
        return retval;
    }

    private int getJamieColor(int n) {
        int n2 = Managers.MODULES.getEnabledModules().size();
        int n3 = new Color(91, 206, 250).getRGB();
        int n4 = Color.WHITE.getRGB();
        int n5 = new Color(245, 169, 184).getRGB();
        int n6 = n2 / 5;
        if (n < n6) {
            return n3;
        }
        if (n < n6 * 2) {
            return n5;
        }
        if (n < n6 * 3) {
            return n4;
        }
        if (n < n6 * 4) {
            return n5;
        }
        if (n < n6 * 5) {
            return n3;
        }
        return n3;
    }

    private String getFacingDirectionShort() {
        int dirnumber = Managers.ROTATIONS.getYaw4D();
        if (dirnumber == 0) {
            return "(+Z)";
        }
        if (dirnumber == 1) {
            return "(-X)";
        }
        if (dirnumber == 2) {
            return "(-Z)";
        }
        if (dirnumber == 3) {
            return "(+X)";
        }
        return "Loading...";
    }

    private String getColoredPotionString(PotionEffect effect) {
        Potion potion = effect.getPotion();
        return I18n.format((String)potion.getName(), (Object[])new Object[0]) + " " + (effect.getAmplifier() + 1) + " " + (Object)ChatFormatting.WHITE + Potion.getPotionDurationString((PotionEffect)effect, (float)1.0f);
    }

    private static enum Page {
        ELEMENTS,
        GLOBAL;

    }

    private static enum GreeterMode {
        PLAYER,
        CUSTOM;

    }
}

