/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package me.mioclient.asm.mixins;

import me.mioclient.mod.modules.impl.render.Model;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderItem.class})
public class MixinRenderItem {
    private float angle;
    Minecraft mc = Minecraft.getMinecraft();

    @Inject(method={"renderItemModel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", shift=At.Shift.BEFORE)})
    private void renderCustom(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        Model mod = Model.INSTANCE;
        float scale = 1.0f;
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        if (leftHanded && mod.isOn() && this.mc.player.getHeldItemOffhand() == stack) {
            scale = mod.offScale.getValue().floatValue();
            xOffset = mod.offX.getValue().floatValue();
            yOffset = mod.offY.getValue().floatValue();
        } else if (mod.isOn() && this.mc.player.getHeldItemMainhand() == stack) {
            scale = mod.mainScale.getValue().floatValue();
            xOffset -= mod.mainX.getValue().floatValue();
            yOffset = mod.mainY.getValue().floatValue();
        }
        if (mod.isOn()) {
            GlStateManager.scale((float)scale, (float)scale, (float)scale);
            if (this.mc.player.getActiveItemStack() != stack) {
                GlStateManager.translate((float)xOffset, (float)yOffset, (float)0.0f);
            }
        }
    }

    @Inject(method={"renderItemModel"}, at={@At(value="HEAD")})
    public void renderItem(ItemStack stack, IBakedModel bakedmodel, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        Model mod = Model.INSTANCE;
        if (mod.isOn() && (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)) {
            if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND && this.mc.player.getActiveHand() == EnumHand.OFF_HAND && this.mc.player.isHandActive()) {
                return;
            }
            if (mod.isOn() && (mod.spinX.getValue().booleanValue() || mod.spinY.getValue().booleanValue())) {
                GlStateManager.rotate((float)this.angle, (float)(mod.spinX.getValue() != false ? this.angle : 0.0f), (float)(mod.spinY.getValue() != false ? this.angle : 0.0f), (float)0.0f);
                this.angle += 1.0f;
            }
        } else {
            if (this.mc.player.getActiveHand() == EnumHand.MAIN_HAND && this.mc.player.isHandActive()) {
                return;
            }
            if (mod.isOn() && (mod.spinX.getValue().booleanValue() || mod.spinY.getValue().booleanValue())) {
                GlStateManager.rotate((float)this.angle, (float)(mod.spinX.getValue() != false ? this.angle : 0.0f), (float)(mod.spinY.getValue() != false ? this.angle : 0.0f), (float)0.0f);
                this.angle += 1.0f;
            }
        }
    }
}

