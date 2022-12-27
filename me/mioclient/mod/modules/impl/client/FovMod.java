/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.settings.GameSettings$Options
 *  net.minecraft.init.MobEffects
 *  net.minecraftforge.client.event.FOVUpdateEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.client;

import me.mioclient.api.events.impl.PerspectiveEvent;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FovMod
extends Module {
    private static FovMod INSTANCE = new FovMod();
    private final Setting<Page> page = this.add(new Setting<Page>("Settings", Page.FOV));
    private final Setting<Boolean> customFov = this.add(new Setting<Boolean>("CustomFov", false, v -> this.page.getValue() == Page.FOV).setParent());
    private final Setting<Float> fov = this.add(new Setting<Float>("FOV", Float.valueOf(120.0f), Float.valueOf(10.0f), Float.valueOf(180.0f), v -> this.page.getValue() == Page.FOV && this.customFov.isOpen()));
    private final Setting<Boolean> aspectRatio = this.add(new Setting<Boolean>("AspectRatio", false, v -> this.page.getValue() == Page.FOV).setParent());
    private final Setting<Float> aspectFactor = this.add(new Setting<Float>("AspectFactor", Float.valueOf(1.8f), Float.valueOf(0.1f), Float.valueOf(3.0f), v -> this.page.getValue() == Page.FOV && this.aspectRatio.isOpen()));
    private final Setting<Boolean> defaults = this.add(new Setting<Boolean>("Defaults", false, v -> this.page.getValue() == Page.ADVANCED));
    private final Setting<Float> sprint = this.add(new Setting<Float>("SprintAdd", Float.valueOf(1.15f), Float.valueOf(1.0f), Float.valueOf(2.0f), v -> this.page.getValue() == Page.ADVANCED));
    private final Setting<Float> speed = this.add(new Setting<Float>("SwiftnessAdd", Float.valueOf(1.15f), Float.valueOf(1.0f), Float.valueOf(2.0f), v -> this.page.getValue() == Page.ADVANCED));

    public FovMod() {
        super("FovMod", "FOV modifier.", Category.CLIENT, true);
        this.setInstance();
    }

    public static FovMod getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FovMod();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.customFov.getValue().booleanValue()) {
            FovMod.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
        if (this.defaults.getValue().booleanValue()) {
            this.sprint.setValue(Float.valueOf(1.15f));
            this.speed.setValue(Float.valueOf(1.15f));
            this.defaults.setValue(false);
        }
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent event) {
        float fov = 1.0f;
        if (event.getEntity().isSprinting()) {
            fov = this.sprint.getValue().floatValue();
            if (event.getEntity().isPotionActive(MobEffects.SPEED)) {
                fov = this.speed.getValue().floatValue();
            }
        }
        event.setNewfov(fov);
    }

    @SubscribeEvent
    public void onPerspectiveUpdate(PerspectiveEvent event) {
        if (this.aspectRatio.getValue().booleanValue()) {
            event.setAngle(this.aspectFactor.getValue().floatValue());
        }
    }

    public static enum Page {
        FOV,
        ADVANCED;

    }
}

