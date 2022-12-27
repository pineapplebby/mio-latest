/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiIngame
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.scoreboard.ScoreObjective
 */
package me.mioclient.asm.mixins;

import me.mioclient.mod.modules.impl.client.HUD;
import me.mioclient.mod.modules.impl.render.NoLag;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiIngame.class})
public class MixinGuiInGame
extends Gui {
    @Inject(method={"renderPotionEffects"}, at={@At(value="HEAD")}, cancellable=true)
    protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo info) {
        HUD mod = HUD.getInstance();
        if (mod.potionIcons.getValue().booleanValue() && mod.isOn()) {
            info.cancel();
        }
    }

    @Inject(method={"renderScoreboard"}, at={@At(value="HEAD")}, cancellable=true)
    protected void renderScoreboardHook(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo info) {
        NoLag mod = NoLag.INSTANCE;
        if (mod.scoreBoards.getValue().booleanValue() && mod.isOn()) {
            info.cancel();
        }
    }
}

