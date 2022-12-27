/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderPearl
 *  net.minecraft.entity.item.EntityExpBottle
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class BreadCrumbs
extends Module {
    private final Setting<Float> lineWidth = this.add(new Setting<Float>("Width", Float.valueOf(0.8f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    private final Setting<Integer> timeExisted = this.add(new Setting<Integer>("Delay", 1000, 100, 3000));
    private final Setting<Boolean> xp = this.add(new Setting<Boolean>("Exp", true));
    private final Setting<Boolean> arrow = this.add(new Setting<Boolean>("Arrows", true));
    private final Setting<Boolean> self = this.add(new Setting<Boolean>("Self", true));
    private final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(125, 125, 213)).hideAlpha());
    private final Setting<Color> secondColor = this.add(new Setting<Color>("SecondColor", new Color(12550399)).injectBoolean(false).hideAlpha());
    protected Map trails = new HashMap();

    public BreadCrumbs() {
        super("BreadCrumbs", "Draws trails behind projectiles and you (bread crumbs)", Category.RENDER, true);
    }

    @Override
    public void onTick() {
        if (!BreadCrumbs.nullCheck()) {
            for (Entity entity : BreadCrumbs.mc.world.loadedEntityList) {
                if (!this.isValid(entity)) continue;
                if (this.trails.containsKey(entity.getUniqueID())) {
                    if (entity.isDead) {
                        if (((ItemTrail)this.trails.get((Object)entity.getUniqueID())).timer.isPaused()) {
                            ((ItemTrail)this.trails.get((Object)entity.getUniqueID())).timer.resetDelay();
                        }
                        ((ItemTrail)this.trails.get((Object)entity.getUniqueID())).timer.setPaused(false);
                        continue;
                    }
                    ((ItemTrail)this.trails.get((Object)entity.getUniqueID())).positions.add(new Position(entity.getPositionVector()));
                    continue;
                }
                this.trails.put(entity.getUniqueID(), new ItemTrail(entity));
            }
            if (this.self.getValue().booleanValue()) {
                if (this.trails.containsKey(BreadCrumbs.mc.player.getUniqueID())) {
                    ItemTrail playerTrail = (ItemTrail)this.trails.get(BreadCrumbs.mc.player.getUniqueID());
                    playerTrail.timer.resetDelay();
                    ArrayList<Position> toRemove = new ArrayList<Position>();
                    for (Object o : playerTrail.positions) {
                        Position position = (Position)o;
                        if (System.currentTimeMillis() - position.time <= (long)this.timeExisted.getValue().intValue()) continue;
                        toRemove.add(position);
                    }
                    playerTrail.positions.removeAll(toRemove);
                    playerTrail.positions.add(new Position(BreadCrumbs.mc.player.getPositionVector()));
                } else {
                    this.trails.put(BreadCrumbs.mc.player.getUniqueID(), new ItemTrail((Entity)BreadCrumbs.mc.player));
                }
            } else {
                this.trails.remove(BreadCrumbs.mc.player.getUniqueID());
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!BreadCrumbs.nullCheck()) {
            Iterator iterator = this.trails.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry o;
                Map.Entry entry = o = iterator.next();
                if (((ItemTrail)entry.getValue()).entity.isDead || BreadCrumbs.mc.world.getEntityByID(((ItemTrail)entry.getValue()).entity.getEntityId()) == null) {
                    if (((ItemTrail)entry.getValue()).timer.isPaused()) {
                        ((ItemTrail)entry.getValue()).timer.resetDelay();
                    }
                    ((ItemTrail)entry.getValue()).timer.setPaused(false);
                }
                if (((ItemTrail)entry.getValue()).timer.isPassed()) continue;
                this.drawTrail((ItemTrail)entry.getValue());
            }
        }
    }

    public void drawTrail(ItemTrail trail) {
        double fadeAmount = MathUtil.normalize(System.currentTimeMillis() - trail.timer.getStartTime(), 0.0, this.timeExisted.getValue().intValue());
        int alpha = (int)(fadeAmount * 255.0);
        alpha = MathHelper.clamp((int)alpha, (int)0, (int)255);
        alpha = 255 - alpha;
        alpha = trail.timer.isPaused() ? 255 : alpha;
        Color fadeColor = new Color(this.secondColor.getValue().getRed(), this.secondColor.getValue().getGreen(), this.secondColor.getValue().getBlue(), alpha);
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)((Number)this.lineWidth.getValue()).floatValue());
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        this.buildBuffer(builder, trail, new Color(this.color.getValue().getRGB()), this.secondColor.booleanValue ? fadeColor : new Color(this.color.getValue().getRGB()));
        tessellator.draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glEnable((int)3553);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    public void buildBuffer(BufferBuilder builder, ItemTrail trail, Color start, Color end) {
        for (Object o : trail.positions) {
            Position p = (Position)o;
            Vec3d pos = BreadCrumbs.updateToCamera(p.pos);
            double value = MathUtil.normalize(trail.positions.indexOf(p), 0.0, trail.positions.size());
            BreadCrumbs.addBuilderVertex(builder, pos.x, pos.y, pos.z, ColorUtil.interpolate((float)value, start, end));
        }
    }

    boolean isValid(Entity e) {
        return e instanceof EntityEnderPearl || e instanceof EntityExpBottle && this.xp.getValue() != false || e instanceof EntityArrow && this.arrow.getValue() != false && e.ticksExisted <= this.timeExisted.getValue();
    }

    public static Vec3d updateToCamera(Vec3d vec) {
        return new Vec3d(vec.x - BreadCrumbs.mc.getRenderManager().viewerPosX, vec.y - BreadCrumbs.mc.getRenderManager().viewerPosY, vec.z - BreadCrumbs.mc.getRenderManager().viewerPosZ);
    }

    public static void addBuilderVertex(BufferBuilder bufferBuilder, double x, double y, double z, Color color) {
        bufferBuilder.pos(x, y, z).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
    }

    public class Timer {
        long startTime = System.currentTimeMillis();
        long delay;
        boolean paused;

        public boolean isPassed() {
            return !this.paused && System.currentTimeMillis() - this.startTime >= this.delay;
        }

        public long getTime() {
            return System.currentTimeMillis() - this.startTime;
        }

        public void resetDelay() {
            this.startTime = System.currentTimeMillis();
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        public void setPaused(boolean paused) {
            this.paused = paused;
        }

        public boolean isPaused() {
            return this.paused;
        }

        public long getStartTime() {
            return this.startTime;
        }
    }

    public class ItemTrail {
        public Entity entity;
        public List positions;
        public Timer timer;

        public ItemTrail(Entity entity) {
            this.entity = entity;
            this.positions = new ArrayList();
            this.timer = new Timer();
            this.timer.setDelay(((Integer)BreadCrumbs.this.timeExisted.getValue()).intValue());
            this.timer.setPaused(true);
        }
    }

    public static class Position {
        public Vec3d pos;
        public long time;

        public Position(Vec3d pos) {
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o != null && this.getClass() == o.getClass()) {
                Position position = (Position)o;
                return this.time == position.time && Objects.equals((Object)this.pos, (Object)position.pos);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.pos, this.time});
        }
    }
}

