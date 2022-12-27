/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.EntityViewRenderEvent$CameraSetup
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.player;

import me.mioclient.api.events.impl.TurnEvent;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeLook
extends Module {
    private float dYaw;
    private float dPitch;

    public FreeLook() {
        super("FreeLook", "Rotate your camera and not your player in 3rd person.", Category.PLAYER, true);
    }

    @Override
    public void onEnable() {
        this.dYaw = 0.0f;
        this.dPitch = 0.0f;
        FreeLook.mc.gameSettings.thirdPersonView = 1;
    }

    @Override
    public void onDisable() {
        FreeLook.mc.gameSettings.thirdPersonView = 0;
    }

    @Override
    public void onTick() {
        if (FreeLook.mc.gameSettings.thirdPersonView != 1) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (FreeLook.mc.gameSettings.thirdPersonView > 0) {
            event.setYaw(event.getYaw() + this.dYaw);
            event.setPitch(event.getPitch() + this.dPitch);
        }
    }

    @SubscribeEvent
    public void onTurn(TurnEvent event) {
        if (FreeLook.mc.gameSettings.thirdPersonView > 0) {
            this.dYaw = (float)((double)this.dYaw + (double)event.getYaw() * 0.15);
            this.dPitch = (float)((double)this.dPitch - (double)event.getPitch() * 0.15);
            this.dPitch = MathHelper.clamp((float)this.dPitch, (float)-90.0f, (float)90.0f);
            event.cancel();
        }
    }
}

