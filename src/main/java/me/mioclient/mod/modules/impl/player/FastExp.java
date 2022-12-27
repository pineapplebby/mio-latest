/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemExpBottle
 */
package me.mioclient.mod.modules.impl.player;

import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.world.InventoryUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.item.ItemExpBottle;

public class FastExp
extends Module {
    private final Setting<Integer> delay = this.add(new Setting<Integer>("Delay", 1, 0, 5));
    private final Timer delayTimer = new Timer();

    public FastExp() {
        super("FastExp", "Fast projectile.", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        if (FastExp.fullNullCheck()) {
            return;
        }
        if (InventoryUtil.holdingItem(ItemExpBottle.class) && this.delayTimer.passedMs(this.delay.getValue() * 20)) {
            FastExp.mc.rightClickDelayTimer = 1;
            this.delayTimer.reset();
        }
    }
}

