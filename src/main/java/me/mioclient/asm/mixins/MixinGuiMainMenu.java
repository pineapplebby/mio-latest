/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiMainMenu
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.GlStateManager
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package me.mioclient.asm.mixins;

import java.io.IOException;
import me.mioclient.api.util.render.shader.GLSLShader;
import me.mioclient.mod.gui.screen.MioClickGui;
import me.mioclient.mod.modules.impl.client.ClickGui;
import me.mioclient.mod.modules.settings.Bind;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiMainMenu.class})
public class MixinGuiMainMenu
extends GuiScreen {
    public GLSLShader shader;
    public long initTime;
    private boolean isGuiOpen;

    @Inject(method={"keyTyped"}, at={@At(value="HEAD")}, cancellable=true)
    protected void keyTyped(char typedChar, int keyCode, CallbackInfo info) {
        if (keyCode == ((Bind)ClickGui.INSTANCE.bind.getValue()).getKey()) {
            ClickGui.INSTANCE.enable();
            this.isGuiOpen = true;
        }
        if (keyCode == 1) {
            ClickGui.INSTANCE.disable();
            this.isGuiOpen = false;
        }
        if (this.isGuiOpen) {
            try {
                MioClickGui.INSTANCE.keyTyped(typedChar, keyCode);
            }
            catch (Exception exception) {
                // empty catch block
            }
            info.cancel();
        }
    }

    @Inject(method={"drawScreen(IIF)V"}, at={@At(value="TAIL")})
    public void drawScreenTailHook(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        if (this.isGuiOpen) {
            MioClickGui.INSTANCE.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Inject(method={"mouseClicked"}, at={@At(value="HEAD")}, cancellable=true)
    public void mouseClickedHook(int mouseX, int mouseY, int mouseButton, CallbackInfo info) {
        if (this.isGuiOpen) {
            MioClickGui.INSTANCE.mouseClicked(mouseX, mouseY, mouseButton);
            info.cancel();
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.isGuiOpen) {
            MioClickGui.INSTANCE.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Inject(method={"drawPanorama"}, at={@At(value="TAIL")})
    public void drawPanoramaTailHook(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        try {
            this.shader = new GLSLShader("/assets/minecraft/textures/mio/shader/fragment/fsh/shader.fsh");
        }
        catch (IOException iOException) {
            // empty catch block
        }
        GlStateManager.disableCull();
        this.shader.useShader(this.width * 2, this.height * 2, mouseX * 2, mouseY * 2, (float)(System.currentTimeMillis() - this.initTime) / 1000.0f);
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)-1.0f, (float)-1.0f);
        GL11.glVertex2f((float)-1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)1.0f, (float)-1.0f);
        GL11.glEnd();
        GL20.glUseProgram((int)0);
    }

    @Inject(method={"initGui"}, at={@At(value="HEAD")})
    private void initHook(CallbackInfo info) {
        this.initTime = System.currentTimeMillis();
    }
}

