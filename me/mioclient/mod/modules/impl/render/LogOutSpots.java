/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package me.mioclient.mod.modules.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.mioclient.api.events.impl.ConnectionEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.math.InterpolationUtil;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.api.util.render.entity.StaticModelPlayer;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.impl.client.FontMod;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class LogOutSpots
extends Module {
    private final Setting<Float> range = this.add(new Setting<Float>("Range", Float.valueOf(150.0f), Float.valueOf(50.0f), Float.valueOf(500.0f)));
    private final Setting<Boolean> rect = this.add(new Setting<Boolean>("Rectangle", true));
    private final Setting<Boolean> outline = this.add(new Setting<Boolean>("Outline", true));
    private final Setting<Boolean> time = this.add(new Setting<Boolean>("Time", true));
    private final Setting<Boolean> coords = this.add(new Setting<Boolean>("Coords", true));
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true));
    private final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(-1766449377, true)));
    private final Setting<Boolean> rainbow = this.add(new Setting<Boolean>("Rainbow", false));
    private final Setting<Boolean> chams = this.add(new Setting<Boolean>("Chams", true).setParent());
    private final Setting<Color> fillColor = this.add(new Setting<Color>("ChamsColor", new Color(190, 0, 0, 100), v -> this.chams.isOpen()));
    private final Setting<Color> lineColor = this.add(new Setting<Color>("LineColor", new Color(255, 255, 255, 120), v -> this.chams.isOpen()).injectBoolean(false));
    final Date date = new Date();
    protected final Map<UUID, LogOutSpot> spots = new ConcurrentHashMap<UUID, LogOutSpot>();

    public LogOutSpots() {
        super("LogOutSpots", "Displays logout spots for players.", Category.RENDER, true);
    }

    @Override
    public void onEnable() {
        this.spots.clear();
    }

    @Override
    public void onDisable() {
        this.spots.clear();
    }

    @Override
    public void onLogout() {
        this.spots.clear();
    }

    @Override
    public void onTick() {
        for (LogOutSpot spot : this.spots.values()) {
            if (!(LogOutSpots.mc.player.getDistanceSq((Entity)spot.getPlayer()) >= (double)this.range.getValue().floatValue())) continue;
            this.spots.remove(spot.getPlayer().getUniqueID());
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (LogOutSpot spot : this.spots.values()) {
            AxisAlignedBB bb = InterpolationUtil.getInterpolatedAxis(spot.getBoundingBox());
            if (this.chams.getValue().booleanValue()) {
                Color fill;
                StaticModelPlayer model = spot.getModel();
                double x = spot.getX() - LogOutSpots.mc.getRenderManager().viewerPosX;
                double y = spot.getY() - LogOutSpots.mc.getRenderManager().viewerPosY;
                double z = spot.getZ() - LogOutSpots.mc.getRenderManager().viewerPosZ;
                GL11.glPushMatrix();
                GL11.glPushAttrib((int)1048575);
                GL11.glDisable((int)3553);
                GL11.glDisable((int)2896);
                GL11.glDisable((int)2929);
                GL11.glEnable((int)2848);
                GL11.glEnable((int)3042);
                GlStateManager.blendFunc((int)770, (int)771);
                GlStateManager.translate((double)x, (double)y, (double)z);
                GlStateManager.rotate((float)(180.0f - model.getYaw()), (float)0.0f, (float)1.0f, (float)0.0f);
                GlStateManager.enableRescaleNormal();
                GlStateManager.scale((float)-1.0f, (float)-1.0f, (float)1.0f);
                double widthX = bb.maxX - bb.minX + 1.0;
                double widthZ = bb.maxZ - bb.minZ + 1.0;
                GlStateManager.scale((double)widthX, (double)(bb.maxY - bb.minY), (double)widthZ);
                GlStateManager.translate((float)0.0f, (float)-1.501f, (float)0.0f);
                Color color = fill = this.rainbow.getValue() != false ? ColorUtil.injectAlpha(Managers.COLORS.getRainbow(), this.fillColor.getValue().getAlpha()) : this.fillColor.getValue();
                Color line = this.rainbow.getValue() != false ? ColorUtil.injectAlpha(Managers.COLORS.getRainbow(), this.lineColor.booleanValue ? this.lineColor.getValue().getAlpha() : this.fillColor.getValue().getAlpha()) : (this.lineColor.booleanValue ? this.lineColor.getValue() : this.fillColor.getValue());
                RenderUtil.glColor(fill);
                GL11.glPolygonMode((int)1032, (int)6914);
                model.render(0.0625f);
                RenderUtil.glColor(line);
                GL11.glLineWidth((float)1.0f);
                GL11.glPolygonMode((int)1032, (int)6913);
                model.render(0.0625f);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
            if (this.box.getValue().booleanValue()) {
                RenderUtil.drawBlockOutline(bb, this.rainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.color.getValue(), 1.0f, false);
            }
            double x = InterpolationUtil.getInterpolatedDouble(spot.getPlayer().lastTickPosX, spot.getPlayer().posX, event.getPartialTicks()) - LogOutSpots.mc.getRenderManager().renderPosX;
            double y = InterpolationUtil.getInterpolatedDouble(spot.getPlayer().lastTickPosY, spot.getPlayer().posY, event.getPartialTicks()) - LogOutSpots.mc.getRenderManager().renderPosY;
            double z = InterpolationUtil.getInterpolatedDouble(spot.getPlayer().lastTickPosZ, spot.getPlayer().posZ, event.getPartialTicks()) - LogOutSpots.mc.getRenderManager().renderPosZ;
            this.drawNameTag(spot.getName(), x, y, z);
        }
    }

    @SubscribeEvent
    public void onConnection(ConnectionEvent event) {
        EntityPlayer player;
        if (event.getStage() == 0) {
            if (event.getName().equals(mc.getSession().getProfile().getName())) {
                return;
            }
            this.spots.remove(event.getUuid());
        } else if (event.getStage() == 1 && (player = event.getPlayer()) != null) {
            LogOutSpot spot = new LogOutSpot(player);
            this.spots.put(player.getUniqueID(), spot);
        }
    }

    private void drawNameTag(String name, double x, double y, double z) {
        y += 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = InterpolationUtil.getInterpolatedDouble(camera.prevPosX, camera.posX, mc.getRenderPartialTicks());
        camera.posY = InterpolationUtil.getInterpolatedDouble(camera.prevPosY, camera.posY, mc.getRenderPartialTicks());
        camera.posZ = InterpolationUtil.getInterpolatedDouble(camera.prevPosZ, camera.posZ, mc.getRenderPartialTicks());
        String displayTag = name + (this.coords.getValue() != false ? " XYZ: " + (int)x + ", " + (int)y + ", " + (int)z : "") + (this.time.getValue() != false ? " " + (Object)ChatFormatting.GRAY + "(" + this.getLogOutTime() + ")" : "");
        double distance = camera.getDistance(x + LogOutSpots.mc.getRenderManager().viewerPosX, y + LogOutSpots.mc.getRenderManager().viewerPosY, z + LogOutSpots.mc.getRenderManager().viewerPosZ);
        int width = Managers.TEXT.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + 5.0 * (distance * (double)0.6f)) / 1000.0;
        if (distance <= 8.0) {
            scale = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)((float)x), (float)((float)y + 1.4f), (float)((float)z));
        GlStateManager.rotate((float)(-LogOutSpots.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)LogOutSpots.mc.getRenderManager().playerViewX, (float)(LogOutSpots.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.scale((double)(-scale), (double)(-scale), (double)scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (this.rect.getValue().booleanValue()) {
            RenderUtil.drawRect(-width - 2, -(Managers.TEXT.getFontHeight() + 1), (float)width + 2.0f, 1.5f, 0x55000000);
        }
        if (this.outline.getValue().booleanValue()) {
            RenderUtil.drawNameTagOutline(-width - 2, -(Managers.TEXT.getFontHeight() + 1), (float)width + 2.0f, 1.5f, 0.8f, this.color.getValue().getRGB(), this.color.getValue().darker().getRGB(), this.rainbow.getValue());
        }
        GlStateManager.disableBlend();
        Managers.TEXT.drawStringWithShadow(displayTag, -width, FontMod.INSTANCE.isOn() ? (float)(-(Managers.TEXT.getFontHeight() + 1)) : (float)(-(Managers.TEXT.getFontHeight() - 1)), this.rainbow.getValue() != false ? Managers.COLORS.getRainbow().getRGB() : this.color.getValue().getRGB());
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
        GlStateManager.popMatrix();
    }

    private String getLogOutTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
        return dateFormatter.format(this.date);
    }

    protected static class LogOutSpot
    implements Wrapper {
        private final String name;
        private final StaticModelPlayer model;
        private final AxisAlignedBB boundingBox;
        private final EntityPlayer player;
        private final double x;
        private final double y;
        private final double z;

        public LogOutSpot(EntityPlayer player) {
            this.name = player.getName();
            this.model = new StaticModelPlayer(EntityUtil.getCopiedPlayer(player), player instanceof AbstractClientPlayer && ((AbstractClientPlayer)player).getSkinType().equals("slim"), 0.0f);
            this.model.disableArmorLayers();
            this.boundingBox = player.getEntityBoundingBox();
            this.x = player.posX;
            this.y = player.posY;
            this.z = player.posZ;
            this.player = player;
        }

        public String getName() {
            return this.name;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public double getDistance() {
            return LogOutSpot.mc.player.getDistance(this.x, this.y, this.z);
        }

        public AxisAlignedBB getBoundingBox() {
            return this.boundingBox;
        }

        public StaticModelPlayer getModel() {
            return this.model;
        }

        public EntityPlayer getPlayer() {
            return this.player;
        }
    }
}

