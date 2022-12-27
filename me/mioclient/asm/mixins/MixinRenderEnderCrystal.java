/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.entity.RenderEnderCrystal
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package me.mioclient.asm.mixins;

import java.awt.Color;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.impl.render.CrystalChams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    @Redirect(method={"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        float newAgeInTicks;
        CrystalChams mod = CrystalChams.INSTANCE;
        float newLimbSwingAmount = mod.changeSpeed.getValue() != false ? limbSwingAmount * mod.spinSpeed.getValue().floatValue() : limbSwingAmount;
        float f = newAgeInTicks = mod.changeSpeed.getValue() != false ? ageInTicks * mod.floatFactor.getValue().floatValue() : ageInTicks;
        if (mod.isOn()) {
            Color color;
            Color rainbow;
            GlStateManager.scale((float)mod.scale.getValue().floatValue(), (float)mod.scale.getValue().floatValue(), (float)mod.scale.getValue().floatValue());
            if (mod.model.getValue() == CrystalChams.Model.VANILLA) {
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
            } else if (mod.model.getValue() == CrystalChams.Model.XQZ) {
                GL11.glEnable((int)32823);
                GlStateManager.enablePolygonOffset();
                GL11.glPolygonOffset((float)1.0f, (float)-1000000.0f);
                if (mod.modelColor.booleanValue) {
                    rainbow = Managers.COLORS.getRainbow();
                    color = mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.modelColor.getValue().getAlpha()) : new Color(mod.modelColor.getValue().getRed(), mod.modelColor.getValue().getGreen(), mod.modelColor.getValue().getBlue(), mod.modelColor.getValue().getAlpha());
                    RenderUtil.glColor(color);
                }
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
                GL11.glDisable((int)32823);
                GlStateManager.disablePolygonOffset();
                GL11.glPolygonOffset((float)1.0f, (float)1000000.0f);
            }
            if (mod.wireframe.getValue().booleanValue()) {
                rainbow = Managers.COLORS.getRainbow();
                color = mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.color.getValue().getAlpha()) : (mod.lineColor.booleanValue ? new Color(mod.lineColor.getValue().getRed(), mod.lineColor.getValue().getGreen(), mod.lineColor.getValue().getBlue(), mod.lineColor.getValue().getAlpha()) : new Color(mod.color.getValue().getRed(), mod.color.getValue().getGreen(), mod.color.getValue().getBlue(), mod.color.getValue().getAlpha()));
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
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
            if (mod.fill.getValue().booleanValue()) {
                rainbow = Managers.COLORS.getRainbow();
                color = mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.color.getValue().getAlpha()) : new Color(mod.color.getValue().getRed(), mod.color.getValue().getGreen(), mod.color.getValue().getBlue(), mod.color.getValue().getAlpha());
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
                model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
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
            if (mod.glint.getValue().booleanValue() && entity instanceof EntityEnderCrystal) {
                rainbow = Managers.COLORS.getRainbow();
                color = mod.rainbow.getValue() != false ? new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), mod.color.getValue().getAlpha()) : new Color(mod.color.getValue().getRed(), mod.color.getValue().getGreen(), mod.color.getValue().getBlue(), mod.color.getValue().getAlpha());
                GL11.glPushMatrix();
                GL11.glPushAttrib((int)1048575);
                GL11.glPolygonMode((int)1032, (int)6914);
                GL11.glDisable((int)2896);
                GL11.glDepthRange((double)0.0, (double)0.1);
                GL11.glEnable((int)3042);
                RenderUtil.glColor(color);
                GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_COLOR, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
                float f2 = (float)entity.ticksExisted + Wrapper.mc.getRenderPartialTicks();
                Wrapper.mc.getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/misc/enchanted_item_glint.png"));
                for (int i = 0; i < 2; ++i) {
                    GlStateManager.matrixMode((int)5890);
                    GlStateManager.loadIdentity();
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                    GlStateManager.rotate((float)(30.0f - (float)i * 60.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                    GlStateManager.translate((float)0.0f, (float)(f2 * (0.001f + (float)i * 0.003f) * 20.0f), (float)0.0f);
                    GlStateManager.matrixMode((int)5888);
                    model.render(entity, limbSwing, newLimbSwingAmount, newAgeInTicks, netHeadYaw, headPitch, scale);
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
            GlStateManager.scale((float)(1.0f / mod.scale.getValue().floatValue()), (float)(1.0f / mod.scale.getValue().floatValue()), (float)(1.0f / mod.scale.getValue().floatValue()));
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
}

