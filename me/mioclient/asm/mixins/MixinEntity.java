/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.MoverType
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package me.mioclient.asm.mixins;

import me.mioclient.api.events.impl.StepEvent;
import me.mioclient.api.events.impl.TurnEvent;
import me.mioclient.api.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class}, priority=0x7FFFFFFF)
public abstract class MixinEntity {
    @Shadow
    private int entityId;
    @Shadow
    protected boolean isInWeb;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;

    @Shadow
    public void move(MoverType type, double x, double y, double z) {
    }

    @Shadow
    public abstract boolean equals(Object var1);

    @Shadow
    public abstract int getEntityId();

    @Inject(method={"turn"}, at={@At(value="HEAD")}, cancellable=true)
    public void onTurnHook(float yaw, float pitch, CallbackInfo info) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method={"move"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;getEntityBoundingBox()Lnet/minecraft/util/math/AxisAlignedBB;", ordinal=12, shift=At.Shift.BEFORE)})
    public void onStepPre(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (((Entity)Entity.class.cast(this)).equals((Object)Wrapper.mc.player)) {
            StepEvent event = new StepEvent(0);
            MinecraftForge.EVENT_BUS.post((Event)event);
        }
    }

    @Inject(method={"move"}, at={@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;setEntityBoundingBox(Lnet/minecraft/util/math/AxisAlignedBB;)V", ordinal=7, shift=At.Shift.AFTER)})
    public void onStepPost(MoverType type, double x, double y, double z, CallbackInfo info) {
        if (((Entity)Entity.class.cast(this)).equals((Object)Wrapper.mc.player)) {
            StepEvent event = new StepEvent(1);
            MinecraftForge.EVENT_BUS.post((Event)event);
        }
    }
}

