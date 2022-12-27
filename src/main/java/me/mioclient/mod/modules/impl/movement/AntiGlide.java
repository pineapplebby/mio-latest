/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.MovementInput
 */
package me.mioclient.mod.modules.impl.movement;

import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovementInput;

public class AntiGlide
extends Module {
    private final Setting<Boolean> onGround = this.add(new Setting<Boolean>("OnGround", true));
    private final Setting<Boolean> ice = this.add(new Setting<Boolean>("Ice", true));

    public AntiGlide() {
        super("AntiGlide", "Prevents inertial moving.", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        this.setIceSlipperiness(0.98f);
    }

    @Override
    public void onUpdate() {
        if (this.onGround.getValue().booleanValue() && !AntiGlide.mc.player.onGround) {
            return;
        }
        MovementInput input = AntiGlide.mc.player.movementInput;
        if ((double)input.moveForward == 0.0 && (double)input.moveStrafe == 0.0) {
            AntiGlide.mc.player.motionX = 0.0;
            AntiGlide.mc.player.motionZ = 0.0;
        }
        if (this.ice.getValue().booleanValue() && AntiGlide.mc.player.getRidingEntity() == null) {
            this.setIceSlipperiness(0.6f);
        } else {
            this.setIceSlipperiness(0.98f);
        }
    }

    private void setIceSlipperiness(float in) {
        Blocks.ICE.setDefaultSlipperiness(in);
        Blocks.FROSTED_ICE.setDefaultSlipperiness(in);
        Blocks.PACKED_ICE.setDefaultSlipperiness(in);
    }
}

