/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 */
package me.mioclient.mod.modules.impl.render;

import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class Model
extends Module {
    public static Model INSTANCE;
    public Setting<Page> settings = this.add(new Setting<Page>("Settings", Page.OFFSETS));
    public Setting<Double> mainScale = this.add(new Setting<Double>("MainScale", 1.0, 0.0, 2.0, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Double> mainX = this.add(new Setting<Double>("MainX", 0.0, -1.0, 1.0, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Double> mainY = this.add(new Setting<Double>("MainY", 0.0, -1.0, 1.0, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Double> offScale = this.add(new Setting<Double>("OffScale", 1.0, 0.0, 2.0, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Double> offX = this.add(new Setting<Double>("OffX", 0.0, -1.0, 1.0, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Double> offY = this.add(new Setting<Double>("OffY", 0.0, -1.0, 1.0, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Boolean> spinY = this.add(new Setting<Boolean>("SpinX", false, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Boolean> spinX = this.add(new Setting<Boolean>("SpinY", false, v -> this.settings.getValue() == Page.OFFSETS));
    public Setting<Boolean> customSwing = this.add(new Setting<Boolean>("CustomSwing", false, v -> this.settings.getValue() == Page.OTHERS).setParent());
    public Setting<Swing> swing = this.add(new Setting<Swing>("Swing", Swing.MAINHAND, v -> this.settings.getValue() == Page.OTHERS && this.customSwing.isOpen()));
    public Setting<Boolean> slowSwing = this.add(new Setting<Boolean>("SlowSwing", false, v -> this.settings.getValue() == Page.OTHERS));
    public Setting<Boolean> noSway = this.add(new Setting<Boolean>("NoSway", false, v -> this.settings.getValue() == Page.OTHERS));
    public Setting<Boolean> instantSwap = this.add(new Setting<Boolean>("InstantSwap", false, v -> this.settings.getValue() == Page.OTHERS));
    public Setting<Boolean> swordChange = this.add(new Setting<Boolean>("SwordHandSwap", false, v -> this.settings.getValue() == Page.OTHERS));

    public Model() {
        super("Model", "Changes view model.", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.instantSwap.getValue().booleanValue() && (double)Model.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            Model.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            Model.mc.entityRenderer.itemRenderer.itemStackMainHand = Model.mc.player.getHeldItemMainhand();
        }
        if (this.customSwing.getValue().booleanValue()) {
            if (this.swing.getValue() == Swing.OFFHAND) {
                Model.mc.player.swingingHand = EnumHand.OFF_HAND;
            } else if (this.swing.getValue() == Swing.MAINHAND) {
                Model.mc.player.swingingHand = EnumHand.MAIN_HAND;
            }
        }
        if (this.swordChange.getValue().booleanValue()) {
            if (EntityUtil.isHoldingWeapon((EntityPlayer)Model.mc.player)) {
                Model.mc.player.setPrimaryHand(EnumHandSide.LEFT);
            } else {
                Model.mc.player.setPrimaryHand(EnumHandSide.RIGHT);
            }
        }
    }

    public static enum Swing {
        MAINHAND,
        OFFHAND,
        SERVER;

    }

    private static enum Page {
        OTHERS,
        OFFSETS;

    }
}

