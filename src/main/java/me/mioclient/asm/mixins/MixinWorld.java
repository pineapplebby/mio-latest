/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package me.mioclient.asm.mixins;

import com.google.common.base.Predicate;
import java.util.List;
import me.mioclient.api.events.impl.RenderSkyEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={World.class})
public class MixinWorld {
    @Redirect(method={"getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/chunk/Chunk;getEntitiesOfTypeWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lcom/google/common/base/Predicate;)V"))
    public <T extends Entity> void getEntitiesOfTypeWithinAABBHook(Chunk chunk, Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter) {
        try {
            chunk.getEntitiesOfTypeWithinAABB(entityClass, aabb, listToFill, filter);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Inject(method={"onEntityAdded"}, at={@At(value="HEAD")})
    private void onEntityAdded(Entity entityIn, CallbackInfo ci) {
    }

    @Inject(method={"getSkyColor"}, at={@At(value="HEAD")}, cancellable=true)
    public void getSkyColorHook(Entity entityIn, float partialTicks, CallbackInfoReturnable<Vec3d> info) {
        RenderSkyEvent renderSkyEvent = new RenderSkyEvent();
        MinecraftForge.EVENT_BUS.post((Event)renderSkyEvent);
        if (renderSkyEvent.isCanceled()) {
            info.cancel();
            info.setReturnValue(new Vec3d((double)renderSkyEvent.getColor().getRed() / 255.0, (double)renderSkyEvent.getColor().getGreen() / 255.0, (double)renderSkyEvent.getColor().getBlue() / 255.0));
        }
    }
}

