/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.renderer.entity.layers.LayerArmorBase
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 */
package me.mioclient.asm.mixins;

import me.mioclient.mod.modules.impl.render.Chams;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LayerArmorBase.class})
public class MixinLayerArmorBase {
    @Shadow
    private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
    }

    @Inject(method={"doRenderLayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/layers/LayerArmorBase;renderArmorLayer(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V", shift=At.Shift.BEFORE)}, cancellable=true)
    public void doRenderLayerHook(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo info) {
        Chams mod = Chams.INSTANCE;
        if (mod.isOn() && entity instanceof EntityPlayer) {
            float newLimbSwingAmount;
            info.cancel();
            float newLimbSwing = mod.isOn() && mod.noInterp.getValue() != false ? 0.0f : limbSwing;
            float f = newLimbSwingAmount = mod.isOn() && mod.noInterp.getValue() != false ? 0.0f : limbSwingAmount;
            if (!mod.self.getValue().booleanValue() && entity instanceof EntityPlayerSP) {
                this.renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                return;
            }
            this.renderLayers(entity, newLimbSwing, newLimbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        } else {
            this.renderLayers(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    private void renderLayers(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.renderArmorLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
        this.renderArmorLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
        this.renderArmorLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
        this.renderArmorLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
    }
}

