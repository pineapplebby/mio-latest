/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntityShulkerBox
 */
package me.mioclient.asm.mixins;

import me.mioclient.mod.modules.impl.movement.Velocity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TileEntityShulkerBox.class})
public abstract class MixinTileEntityShulkerBox {
    @Inject(method={"moveCollidedEntities"}, at={@At(value="HEAD")}, cancellable=true)
    public void moveCollidedEntitiesHook(CallbackInfo info) {
        if (Velocity.INSTANCE.isOn()) {
            info.cancel();
        }
    }
}

