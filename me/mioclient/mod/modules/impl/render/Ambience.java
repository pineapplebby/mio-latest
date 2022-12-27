/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.api.events.impl.RenderFogColorEvent;
import me.mioclient.api.events.impl.RenderSkyEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Ambience
extends Module {
    public static Ambience INSTANCE;
    public final Setting<Boolean> noFog = this.add(new Setting<Boolean>("NoFog", false));
    public final Setting<Boolean> nightMode = this.add(new Setting<Boolean>("NightMode", false));
    public final Setting<Color> lightMap = this.add(new Setting<Color>("LightMap", new Color(-557395713, true)).injectBoolean(false).hideAlpha());
    public final Setting<Color> sky = this.add(new Setting<Color>("OverWorldSky", new Color(0x7D7DD5)).injectBoolean(true).hideAlpha());
    public final Setting<Boolean> skyRainbow = this.add(new Setting<Boolean>("SkyRainbow", false, v -> this.sky.isOpen()));
    public final Setting<Color> skyNether = this.add(new Setting<Color>("NetherSky", new Color(0x7D7DD5)).injectBoolean(true).hideAlpha());
    public final Setting<Boolean> netherRainbow = this.add(new Setting<Boolean>("NetherSkyRainbow", false, v -> this.skyNether.isOpen()));
    public final Setting<Color> fog = this.add(new Setting<Color>("OverWorldFog", new Color(13401557)).injectBoolean(false).hideAlpha());
    public final Setting<Boolean> fogRainbow = this.add(new Setting<Boolean>("FogRainbow", false, v -> this.fog.isOpen()));
    public final Setting<Color> fogNether = this.add(new Setting<Color>("NetherFog", new Color(13401557)).injectBoolean(false).hideAlpha());
    public final Setting<Boolean> fogNetherRainbow = this.add(new Setting<Boolean>("NetherFogRainbow", false, v -> this.sky.isOpen()));

    public Ambience() {
        super("Ambience", "Custom ambience.", Category.RENDER, true);
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (this.nightMode.getValue().booleanValue()) {
            Ambience.mc.world.setWorldTime(22000L);
        }
    }

    @SubscribeEvent
    public void setFogColor(RenderFogColorEvent event) {
        if (this.fog.booleanValue && Ambience.mc.player.dimension == 0) {
            event.setColor(this.fogRainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.fog.getValue());
            event.cancel();
        } else if (this.fogNether.booleanValue && Ambience.mc.player.dimension == -1) {
            event.setColor(this.fogNetherRainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.fogNether.getValue());
            event.cancel();
        }
    }

    @SubscribeEvent
    public void setSkyColor(RenderSkyEvent event) {
        if (this.sky.booleanValue && Ambience.mc.player.dimension == 0) {
            event.setColor(this.skyRainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.sky.getValue());
            event.cancel();
        } else if (this.skyNether.booleanValue && Ambience.mc.player.dimension == -1) {
            event.setColor(this.netherRainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.skyNether.getValue());
            event.cancel();
        }
    }

    @SubscribeEvent
    public void setFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (this.noFog.getValue().booleanValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }
}

