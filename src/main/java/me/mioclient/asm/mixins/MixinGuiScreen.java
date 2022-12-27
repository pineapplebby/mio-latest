/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.item.ItemShulkerBox
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package me.mioclient.asm.mixins;

import java.awt.Color;
import me.mioclient.api.events.impl.RenderToolTipEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.gui.click.items.other.Particle;
import me.mioclient.mod.gui.screen.MioClickGui;
import me.mioclient.mod.modules.impl.client.ClickGui;
import me.mioclient.mod.modules.impl.misc.ToolTips;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiScreen.class})
public abstract class MixinGuiScreen
extends Gui {
    private final Particle.Util particles = new Particle.Util(300);
    private boolean hoveringShulker;
    private ItemStack shulkerStack;
    private String shulkerName;

    @Inject(method={"renderToolTip"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        RenderToolTipEvent event = new RenderToolTipEvent(stack, x, y);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            info.cancel();
        }
        if (stack.getItem() instanceof ItemShulkerBox) {
            this.hoveringShulker = true;
            this.shulkerStack = stack;
            this.shulkerName = stack.getDisplayName();
        } else {
            this.hoveringShulker = false;
        }
    }

    @Inject(method={"mouseClicked"}, at={@At(value="HEAD")})
    public void mouseClickedHook(int mouseX, int mouseY, int mouseButton, CallbackInfo info) {
        if (mouseButton == 2 && this.hoveringShulker && ToolTips.INSTANCE.wheelPeek.getValue().booleanValue() && ToolTips.INSTANCE.isOn()) {
            ToolTips.drawShulkerGui(this.shulkerStack, this.shulkerName);
        }
    }

    @Inject(method={"drawScreen"}, at={@At(value="HEAD")})
    public void drawScreenHook(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        if (Wrapper.mc.currentScreen != null) {
            if (ClickGui.INSTANCE.particles.getValue().booleanValue()) {
                this.particles.drawParticles();
            }
            if (ClickGui.INSTANCE.background.getValue().booleanValue() && Wrapper.mc.world != null) {
                RenderUtil.drawVGradientRect(0.0f, 0.0f, Managers.TEXT.scaledWidth, Managers.TEXT.scaledHeight, new Color(0, 0, 0, 0).getRGB(), Managers.COLORS.getCurrentWithAlpha(60));
            }
            if (Wrapper.mc.world == null) {
                Managers.TEXT.drawStringWithShadow("mioclient.me v0.6.9-alpha e4e9564ed7caaac078a61bbef88fa1b91ccac41c 25/12/2022 17:20:04", 2.0f, 2.0f, Managers.COLORS.getCurrent().getRGB());
            }
        }
    }

    @Inject(method={"drawWorldBackground(I)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void drawWorldBackgroundHook(int tint, CallbackInfo info) {
        if (Wrapper.mc.world != null && ClickGui.INSTANCE.cleanGui.getValue().booleanValue() && !(Wrapper.mc.currentScreen instanceof MioClickGui)) {
            info.cancel();
        }
    }
}

