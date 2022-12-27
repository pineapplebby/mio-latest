/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.util.MovementInputFromOptions
 *  org.lwjgl.input.Keyboard
 */
package me.mioclient.asm.mixins;

import me.mioclient.api.util.Wrapper;
import me.mioclient.mod.modules.impl.client.ClickGui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={MovementInputFromOptions.class})
public class MixinMovementInputFromOptions
implements Wrapper {
    @Redirect(method={"updatePlayerMoveState"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
    public boolean isKeyPressed(KeyBinding keyBinding) {
        return ClickGui.INSTANCE.guiMove.getValue() != false && !(MixinMovementInputFromOptions.mc.currentScreen instanceof GuiChat) ? Keyboard.isKeyDown((int)keyBinding.getKeyCode()) : keyBinding.isKeyDown();
    }
}

