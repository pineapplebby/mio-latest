/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 */
package me.mioclient.asm.mixins;

import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.Wrapper;
import me.mioclient.mod.modules.impl.client.FontMod;
import me.mioclient.mod.modules.impl.player.NameProtect;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);

    @Inject(method={"drawString(Ljava/lang/String;FFIZ)I"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info) {
        FontMod fontMod;
        if (FontMod.INSTANCE == null) {
            FontMod.INSTANCE = new FontMod();
        }
        if ((fontMod = FontMod.INSTANCE).isOn() && fontMod.global.getValue().booleanValue() && Managers.TEXT != null) {
            float result = Managers.TEXT.drawString(text, x, y, color, dropShadow);
            info.setReturnValue((int)result);
        }
    }

    @Redirect(method={"renderString(Ljava/lang/String;FFIZ)I"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer renderer, String text, boolean shadow) {
        NameProtect nameProtect;
        if (NameProtect.INSTANCE == null) {
            NameProtect.INSTANCE = new NameProtect();
        }
        text = (nameProtect = NameProtect.INSTANCE).isOn() ? text.replaceAll(Wrapper.mc.getSession().getUsername(), nameProtect.name.getValue()) : text;
        this.renderStringAtPos(text, shadow);
    }
}

