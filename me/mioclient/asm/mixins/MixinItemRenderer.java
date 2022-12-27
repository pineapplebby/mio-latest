/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  org.lwjgl.opengl.GL11
 */
package me.mioclient.asm.mixins;

import java.awt.Color;
import me.mioclient.api.events.impl.RenderItemInFirstPersonEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.impl.render.Chams;
import me.mioclient.mod.modules.impl.render.Model;
import me.mioclient.mod.modules.impl.render.Shader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {
    @Shadow
    @Final
    public Minecraft mc;
    private boolean injection = true;

    @Shadow
    public abstract void renderItemInFirstPerson(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7);

    @Redirect(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    public void renderItemInFirstPerson(ItemRenderer itemRenderer, EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        RenderItemInFirstPersonEvent eventPre = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, 0);
        MinecraftForge.EVENT_BUS.post((Event)eventPre);
        if (!eventPre.isCanceled()) {
            itemRenderer.renderItemSide(entitylivingbaseIn, eventPre.getStack(), eventPre.getTransformType(), leftHanded);
        }
        RenderItemInFirstPersonEvent eventPost = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, 1);
        MinecraftForge.EVENT_BUS.post((Event)eventPost);
    }

    @Inject(method={"renderFireInFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (Shader.INSTANCE.isOn()) {
            info.cancel();
        }
    }

    @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float partialTicks, float rotationPitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress, CallbackInfo info) {
        Chams mod = Chams.INSTANCE;
        if (this.injection) {
            info.cancel();
            boolean isFriend = Managers.FRIENDS.isFriend(player.getName());
            this.injection = false;
            if (mod.isOn() && mod.self.getValue().booleanValue() && hand == EnumHand.MAIN_HAND && stack.isEmpty()) {
                Color color;
                Color rainbow;
                if (mod.model.getValue() == Chams.Model.VANILLA) {
                    this.renderItemInFirstPerson(player, partialTicks, rotationPitch, hand, swingProgress, stack, equippedProgress);
                } else if (mod.model.getValue() == Chams.Model.XQZ) {
                    GL11.glEnable((int)32823);
                    GlStateManager.enablePolygonOffset();
                    GL11.glPolygonOffset((float)1.0f, (float)-1000000.0f);
                    if (mod.modelColor.booleanValue) {
                        rainbow = Managers.COLORS.getRainbow();
                        color = isFriend ? Managers.COLORS.getFriendColor(mod.modelColor.getValue().getAlpha()) : (mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.modelColor.getValue().getAlpha()) : new Color(mod.modelColor.getValue().getRed(), mod.modelColor.getValue().getGreen(), mod.modelColor.getValue().getBlue(), mod.modelColor.getValue().getAlpha()));
                        RenderUtil.glColor(color);
                    }
                    this.renderItemInFirstPerson(player, partialTicks, rotationPitch, hand, swingProgress, stack, equippedProgress);
                    GL11.glDisable((int)32823);
                    GlStateManager.disablePolygonOffset();
                    GL11.glPolygonOffset((float)1.0f, (float)1000000.0f);
                }
                if (mod.wireframe.getValue().booleanValue()) {
                    rainbow = Managers.COLORS.getRainbow();
                    color = isFriend ? Managers.COLORS.getFriendColor(mod.lineColor.booleanValue ? mod.lineColor.getValue().getAlpha() : mod.color.getValue().getAlpha()) : (mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.color.getValue().getAlpha()) : (mod.lineColor.booleanValue ? new Color(mod.lineColor.getValue().getRed(), mod.lineColor.getValue().getGreen(), mod.lineColor.getValue().getBlue(), mod.lineColor.getValue().getAlpha()) : new Color(mod.color.getValue().getRed(), mod.color.getValue().getGreen(), mod.color.getValue().getBlue(), mod.color.getValue().getAlpha())));
                    GL11.glPushMatrix();
                    GL11.glPushAttrib((int)1048575);
                    GL11.glPolygonMode((int)1032, (int)6913);
                    GL11.glDisable((int)3553);
                    GL11.glDisable((int)2896);
                    GL11.glDisable((int)2929);
                    GL11.glEnable((int)2848);
                    GL11.glEnable((int)3042);
                    GlStateManager.blendFunc((int)770, (int)771);
                    RenderUtil.glColor(color);
                    GlStateManager.glLineWidth((float)mod.lineWidth.getValue().floatValue());
                    this.renderItemInFirstPerson(player, partialTicks, rotationPitch, hand, swingProgress, stack, equippedProgress);
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
                if (mod.fill.getValue().booleanValue()) {
                    rainbow = Managers.COLORS.getRainbow();
                    color = isFriend ? Managers.COLORS.getFriendColor(mod.color.getValue().getAlpha()) : (mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.color.getValue().getAlpha()) : new Color(mod.color.getValue().getRed(), mod.color.getValue().getGreen(), mod.color.getValue().getBlue(), mod.color.getValue().getAlpha()));
                    GL11.glPushAttrib((int)1048575);
                    GL11.glDisable((int)3008);
                    GL11.glDisable((int)3553);
                    GL11.glDisable((int)2896);
                    GL11.glEnable((int)3042);
                    GL11.glBlendFunc((int)770, (int)771);
                    GL11.glLineWidth((float)1.5f);
                    GL11.glEnable((int)2960);
                    if (mod.xqz.getValue().booleanValue()) {
                        GL11.glDisable((int)2929);
                        GL11.glDepthMask((boolean)false);
                    }
                    GL11.glEnable((int)10754);
                    RenderUtil.glColor(color);
                    this.renderItemInFirstPerson(player, partialTicks, rotationPitch, hand, swingProgress, stack, equippedProgress);
                    if (mod.xqz.getValue().booleanValue()) {
                        GL11.glEnable((int)2929);
                        GL11.glDepthMask((boolean)true);
                    }
                    GL11.glEnable((int)3042);
                    GL11.glEnable((int)2896);
                    GL11.glEnable((int)3553);
                    GL11.glEnable((int)3008);
                    GL11.glPopAttrib();
                }
                if (mod.glint.getValue().booleanValue()) {
                    rainbow = Managers.COLORS.getRainbow();
                    color = isFriend ? Managers.COLORS.getFriendColor(mod.color.getValue().getAlpha()) : (mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.color.getValue().getAlpha()) : new Color(mod.color.getValue().getRed(), mod.color.getValue().getGreen(), mod.color.getValue().getBlue(), mod.color.getValue().getAlpha()));
                    GL11.glPushMatrix();
                    GL11.glPushAttrib((int)1048575);
                    GL11.glPolygonMode((int)1032, (int)6914);
                    GL11.glDisable((int)2896);
                    GL11.glDepthRange((double)0.0, (double)0.1);
                    GL11.glEnable((int)3042);
                    RenderUtil.glColor(color);
                    GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_COLOR, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
                    float f = (float)player.ticksExisted + this.mc.getRenderPartialTicks();
                    this.mc.getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
                    for (int i = 0; i < 2; ++i) {
                        GlStateManager.matrixMode((int)5890);
                        GlStateManager.loadIdentity();
                        GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                        GlStateManager.rotate((float)(30.0f - (float)i * 60.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                        GlStateManager.translate((float)0.0f, (float)(f * (0.001f + (float)i * 0.003f) * 20.0f), (float)0.0f);
                        GlStateManager.matrixMode((int)5888);
                        this.renderItemInFirstPerson(player, partialTicks, rotationPitch, hand, swingProgress, stack, equippedProgress);
                    }
                    GlStateManager.matrixMode((int)5890);
                    GlStateManager.loadIdentity();
                    GlStateManager.matrixMode((int)5888);
                    GL11.glDisable((int)3042);
                    GL11.glDepthRange((double)0.0, (double)1.0);
                    GL11.glEnable((int)2896);
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            } else {
                this.renderItemInFirstPerson(player, partialTicks, rotationPitch, hand, swingProgress, stack, equippedProgress);
            }
            this.injection = true;
        }
    }

    @Inject(method={"rotateArm"}, at={@At(value="HEAD")}, cancellable=true)
    public void rotateArmHook(float partialTicks, CallbackInfo info) {
        Model mod = Model.INSTANCE;
        if (mod.isOn() && mod.noSway.getValue().booleanValue()) {
            info.cancel();
        }
    }
}

