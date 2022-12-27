/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.World
 */
package me.mioclient.mod.modules.impl.misc;

import me.mioclient.api.util.math.Timer;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class KillEffects
extends Module {
    private final Setting<Lightning> lightning = this.add(new Setting<Lightning>("Lightning", Lightning.NORMAL));
    private final Setting<KillSound> killSound = this.add(new Setting<KillSound>("KillSound", KillSound.OFF));
    private final Timer timer = new Timer();

    public KillEffects() {
        super("KillEffects", "jajaja hypixel mode", Category.MISC);
    }

    @Override
    public void onDeath(EntityPlayer player) {
        if (player == null || player == KillEffects.mc.player || player.getHealth() > 0.0f || KillEffects.mc.player.isDead || KillEffects.nullCheck() || KillEffects.fullNullCheck()) {
            return;
        }
        if (this.timer.passedMs(1500L)) {
            SoundEvent sound;
            if (this.lightning.getValue() != Lightning.OFF) {
                KillEffects.mc.world.spawnEntity((Entity)new EntityLightningBolt((World)KillEffects.mc.world, player.posX, player.posY, player.posZ, true));
                if (this.lightning.getValue() == Lightning.NORMAL) {
                    KillEffects.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 0.5f, 1.0f);
                }
            }
            if (this.killSound.getValue() != KillSound.OFF && (sound = this.getSound()) != null) {
                KillEffects.mc.player.playSound(sound, 1.0f, 1.0f);
            }
            this.timer.reset();
        }
    }

    private SoundEvent getSound() {
        switch (this.killSound.getValue()) {
            case CS: {
                return new SoundEvent(new ResourceLocation("mio", "kill_sound_cs"));
            }
            case NEVERLOSE: {
                return new SoundEvent(new ResourceLocation("mio", "kill_sound_nl"));
            }
            case HYPIXEL: {
                return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
            }
        }
        return null;
    }

    private static enum KillSound {
        CS,
        NEVERLOSE,
        HYPIXEL,
        OFF;

    }

    private static enum Lightning {
        NORMAL,
        SILENT,
        OFF;

    }
}

