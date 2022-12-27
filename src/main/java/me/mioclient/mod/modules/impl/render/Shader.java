/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDownloadTerrain
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityExpBottle
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.Display
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.api.events.impl.RenderItemInFirstPersonEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.math.InterpolationUtil;
import me.mioclient.api.util.render.shader.framebuffer.impl.ItemShader;
import me.mioclient.asm.accessors.IEntityRenderer;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

public class Shader
extends Module {
    public static Shader INSTANCE;
    private final Setting<Boolean> players = this.add(new Setting<Boolean>("Players", false));
    private final Setting<Boolean> crystals = this.add(new Setting<Boolean>("Crystals", false));
    private final Setting<Boolean> xp = this.add(new Setting<Boolean>("Exp", false));
    private final Setting<Boolean> items = this.add(new Setting<Boolean>("Items", false));
    private final Setting<Boolean> self = this.add(new Setting<Boolean>("Self", true));
    private final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(-8553003, true)));
    private final Setting<Boolean> glow = this.add(new Setting<Boolean>("Glow", true).setParent());
    private final Setting<Float> radius = this.add(new Setting<Float>("Radius", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.glow.isOpen()));
    private final Setting<Float> smoothness = this.add(new Setting<Float>("Smoothness", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.glow.isOpen()));
    private final Setting<Integer> alpha = this.add(new Setting<Integer>("Alpha", 50, 1, 50, v -> this.glow.isOpen()));
    private final Setting<Boolean> model = this.add(new Setting<Boolean>("Model", true));
    private final Setting<Integer> range = this.add(new Setting<Integer>("Range", 75, 5, 250));
    private final Setting<Boolean> fovOnly = this.add(new Setting<Boolean>("FOVOnly", false));
    private boolean forceRender;

    public Shader() {
        super("Shader", "Is in beta test stage.", Category.RENDER, true);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void renderItemInFirstPerson(RenderItemInFirstPersonEvent event) {
        if (Shader.fullNullCheck() || !this.isOn() || event.getStage() != 0 || this.forceRender || !this.self.getValue().booleanValue()) {
            return;
        }
        event.cancel();
    }

    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        if (Shader.fullNullCheck() || !this.isOn()) {
            return;
        }
        if ((Display.isActive() || Display.isVisible()) && Shader.mc.gameSettings.thirdPersonView == 0 && !(Shader.mc.currentScreen instanceof GuiDownloadTerrain)) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            GlStateManager.enableDepth();
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableAlpha();
            ItemShader shader = ItemShader.INSTANCE;
            shader.mix = (float)this.color.getValue().getAlpha() / 255.0f;
            shader.alpha = (float)(205 + this.alpha.getValue()) / 255.0f;
            shader.model = this.model.getValue();
            shader.startDraw(mc.getRenderPartialTicks());
            this.forceRender = true;
            Shader.mc.world.loadedEntityList.stream().filter(entity -> entity != null && (entity != Shader.mc.player || entity != mc.getRenderViewEntity()) && mc.getRenderManager().getEntityRenderObject(entity) != null && (entity instanceof EntityPlayer && this.players.getValue() != false && !((EntityPlayer)entity).isSpectator() || entity instanceof EntityEnderCrystal && this.crystals.getValue() != false || entity instanceof EntityExpBottle && this.xp.getValue() != false || entity instanceof EntityItem && this.items.getValue() != false)).forEach(entity -> {
                Render render;
                if (entity.getDistance((Entity)Shader.mc.player) > (float)this.range.getValue().intValue() || this.fovOnly.getValue().booleanValue() && !Managers.ROTATIONS.isInFov(entity.getPosition())) {
                    return;
                }
                Vec3d vector = InterpolationUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer)entity).hurtTime = 0;
                }
                if ((render = mc.getRenderManager().getEntityRenderObject(entity)) != null) {
                    try {
                        render.doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            });
            if (this.self.getValue().booleanValue()) {
                ((IEntityRenderer)Shader.mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
            }
            this.forceRender = false;
            shader.stopDraw(this.color.getValue(), this.glow.getValue() != false ? this.radius.getValue().floatValue() : 0.0f, this.smoothness.getValue().floatValue());
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }
}

