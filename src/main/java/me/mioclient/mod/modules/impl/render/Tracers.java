/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 */
package me.mioclient.mod.modules.impl.render;

import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Map;
import me.mioclient.api.events.impl.Render2DEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Tracers
extends Module {
    private final Setting<Mode> mode = this.add(new Setting<Mode>("Mode", Mode.ARROW));
    private final Setting<Integer> range = this.add(new Setting<Integer>("Range", 100, 10, 200));
    private final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(11935519)));
    private final Setting<Integer> radius = this.add(new Setting<Integer>("Radius", 80, 10, 200, v -> this.mode.getValue() == Mode.ARROW));
    private final Setting<Float> size = this.add(new Setting<Float>("Size", Float.valueOf(7.5f), Float.valueOf(5.0f), Float.valueOf(25.0f), v -> this.mode.getValue() == Mode.ARROW));
    private final Setting<Boolean> outline = this.add(new Setting<Boolean>("Outline", true, v -> this.mode.getValue() == Mode.ARROW));
    private final EntityListener entityListener = new EntityListener();
    private final Frustum frustum = new Frustum();

    public Tracers() {
        super("Tracers", "Points to the players on your screen", Category.RENDER);
    }

    @Override
    public String getInfo() {
        return String.valueOf((Object)this.mode.getValue());
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (this.mode.getValue() == Mode.ARROW) {
            this.entityListener.render();
            Tracers.mc.world.loadedEntityList.forEach(o -> {
                if (o instanceof EntityPlayer && this.isValid((EntityPlayer)o)) {
                    EntityPlayer entity = (EntityPlayer)o;
                    Vec3d pos = this.entityListener.getEntityLowerBounds().get((Object)entity);
                    if (pos != null && !this.isOnScreen(pos) && !this.isInViewFrustum((Entity)entity)) {
                        int alpha = (int)MathHelper.clamp((float)(255.0f - 255.0f / (float)this.range.getValue().intValue() * Tracers.mc.player.getDistance((Entity)entity)), (float)100.0f, (float)255.0f);
                        Color friendColor = new Color(0, 191, 255);
                        Color color = Managers.FRIENDS.isFriend(entity.getName()) ? ColorUtil.injectAlpha(friendColor, alpha) : ColorUtil.injectAlpha(this.color.getValue(), alpha);
                        int x = Display.getWidth() / 2 / (Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale);
                        int y = Display.getHeight() / 2 / (Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale);
                        float yaw = this.getRotations((EntityLivingBase)entity) - Tracers.mc.player.rotationYaw;
                        GL11.glTranslatef((float)x, (float)y, (float)0.0f);
                        GL11.glRotatef((float)yaw, (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glTranslatef((float)(-x), (float)(-y), (float)0.0f);
                        RenderUtil.drawArrowPointer(x, y - this.radius.getValue(), this.size.getValue().floatValue(), 2.0f, 1.0f, this.outline.getValue(), 1.1f, color.getRGB());
                        GL11.glTranslatef((float)x, (float)y, (float)0.0f);
                        GL11.glRotatef((float)(-yaw), (float)0.0f, (float)0.0f, (float)1.0f);
                        GL11.glTranslatef((float)(-x), (float)(-y), (float)0.0f);
                    }
                }
            });
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.mode.getValue() == Mode.TRACER) {
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glEnable((int)3042);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            GL11.glLineWidth((float)0.6f);
            GL11.glDisable((int)3553);
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            GL11.glBegin((int)1);
            for (EntityPlayer entity : Tracers.mc.world.playerEntities) {
                if (entity == Tracers.mc.player) continue;
                this.drawTraces((Entity)entity);
            }
            GL11.glEnd();
            GL11.glEnable((int)3553);
            GL11.glDisable((int)2848);
            GL11.glEnable((int)2929);
            GL11.glDepthMask((boolean)true);
            GL11.glDisable((int)3042);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    private boolean isOnScreen(Vec3d pos) {
        int n3;
        int n2;
        int n;
        if (!(pos.x > -1.0)) {
            return false;
        }
        if (!(pos.y < 1.0)) {
            return false;
        }
        if (!(pos.x > -1.0)) {
            return false;
        }
        if (!(pos.z < 1.0)) {
            return false;
        }
        int n4 = n = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        if (!(pos.x / (double)n >= 0.0)) {
            return false;
        }
        int n5 = n2 = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        if (!(pos.x / (double)n2 <= (double)Display.getWidth())) {
            return false;
        }
        int n6 = n3 = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        if (!(pos.y / (double)n3 >= 0.0)) {
            return false;
        }
        int n42 = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        return pos.y / (double)n42 <= (double)Display.getHeight();
    }

    private boolean isInViewFrustum(Entity entity) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        this.frustum.setPosition(current.posX, current.posY, current.posZ);
        return this.frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private boolean isValid(EntityPlayer entity) {
        return entity != Tracers.mc.player && entity.isEntityAlive();
    }

    private float getRotations(EntityLivingBase ent) {
        double x = ent.posX - Tracers.mc.player.posX;
        double z = ent.posZ - Tracers.mc.player.posZ;
        return (float)(-(Math.atan2(x, z) * 57.29577951308232));
    }

    private void drawTraces(Entity entity) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)mc.getRenderPartialTicks() - Tracers.mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)mc.getRenderPartialTicks() - Tracers.mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)mc.getRenderPartialTicks() - Tracers.mc.getRenderManager().viewerPosZ;
        Vec3d eyes = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(Tracers.mc.getRenderViewEntity().rotationPitch))).rotateYaw(-((float)Math.toRadians(Tracers.mc.getRenderViewEntity().rotationYaw)));
        boolean isFriend = Managers.FRIENDS.isFriend(entity.getName());
        GL11.glColor4f((float)((isFriend ? 0.0f : (float)this.color.getValue().getRed()) / 255.0f), (float)((isFriend ? 191.0f : (float)this.color.getValue().getGreen()) / 255.0f), (float)((isFriend ? 255.0f : (float)this.color.getValue().getBlue()) / 255.0f), (float)MathHelper.clamp((float)(255.0f - 255.0f / (float)this.range.getValue().intValue() * Tracers.mc.player.getDistance(entity)), (float)100.0f, (float)255.0f));
        GL11.glVertex3d((double)eyes.x, (double)(eyes.y + (double)mc.getRenderViewEntity().getEyeHeight()), (double)eyes.z);
        GL11.glVertex3d((double)x, (double)y, (double)z);
    }

    private static class EntityListener {
        private final Map<Entity, Vec3d> entityUpperBounds = Maps.newHashMap();
        private final Map<Entity, Vec3d> entityLowerBounds = Maps.newHashMap();

        private EntityListener() {
        }

        private void render() {
            if (!this.entityUpperBounds.isEmpty()) {
                this.entityUpperBounds.clear();
            }
            if (!this.entityLowerBounds.isEmpty()) {
                this.entityLowerBounds.clear();
            }
            for (Entity e : Wrapper.mc.world.loadedEntityList) {
                Vec3d bound = this.getEntityRenderPosition(e);
                bound.add(new Vec3d(0.0, (double)e.height + 0.2, 0.0));
                Vec3d upperBounds = RenderUtil.get2DPos(bound.x, bound.y, bound.z);
                Vec3d lowerBounds = RenderUtil.get2DPos(bound.x, bound.y - 2.0, bound.z);
                if (upperBounds == null || lowerBounds == null) continue;
                this.entityUpperBounds.put(e, upperBounds);
                this.entityLowerBounds.put(e, lowerBounds);
            }
        }

        private Vec3d getEntityRenderPosition(Entity entity) {
            double partial = Wrapper.mc.timer.renderPartialTicks;
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - Wrapper.mc.getRenderManager().viewerPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - Wrapper.mc.getRenderManager().viewerPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - Wrapper.mc.getRenderManager().viewerPosZ;
            return new Vec3d(x, y, z);
        }

        public Map<Entity, Vec3d> getEntityLowerBounds() {
            return this.entityLowerBounds;
        }
    }

    private static enum Mode {
        TRACER,
        ARROW;

    }
}

