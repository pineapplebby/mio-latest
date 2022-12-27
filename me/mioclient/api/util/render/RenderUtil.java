/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderGlobal
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.culling.ICamera
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 */
package me.mioclient.api.util.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.ImageIO;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.Wrapper;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.InterpolationUtil;
import me.mioclient.api.util.render.ColorUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RenderUtil
implements Wrapper {
    public static RenderItem itemRender = mc.getRenderItem();
    public static ICamera camera = new Frustum();
    private static final FloatBuffer screenCoords = BufferUtils.createFloatBuffer((int)3);
    private static final IntBuffer viewport = BufferUtils.createIntBuffer((int)16);
    private static final FloatBuffer modelView = BufferUtils.createFloatBuffer((int)16);
    private static final FloatBuffer projection = BufferUtils.createFloatBuffer((int)16);

    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height, boolean gradientBox, boolean gradientOutline, boolean invertGradientBox, boolean invertGradientOutline, int gradientAlpha) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), height, gradientBox, invertGradientBox, gradientAlpha);
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, height, gradientOutline, invertGradientOutline, gradientAlpha, false);
        }
    }

    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), height, false, false, 0);
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, height, false, false, 0, false);
        }
    }

    public static void drawBox(BlockPos pos, Color color, double height, boolean fade, boolean invertFade, int alpha) {
        if (fade) {
            Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            RenderUtil.drawFadingBox(pos, invertFade ? endColor : color, invertFade ? color : endColor, height);
            return;
        }
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY + height, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            RenderGlobal.renderFilledBox((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            GL11.glDisable((int)2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawFadingBox(BlockPos pos, Color startColor, Color endColor, double height) {
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.UP) continue;
            RenderUtil.drawFadingSide(pos, face, startColor, endColor, height);
        }
    }

    public static void drawFadingSide(BlockPos pos, EnumFacing face, Color startColor, Color endColor, double height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        IBlockState state = BlockUtil.getState(pos);
        Vec3d interp = InterpolationUtil.getInterpolatedPos((Entity)RenderUtil.mc.player, mc.getRenderPartialTicks(), false);
        AxisAlignedBB bb = state.getSelectedBoundingBox((World)RenderUtil.mc.world, pos).grow((double)0.002f).offset(-interp.x, -interp.y, -interp.z).expand(0.0, 0.0, 0.0);
        float red = (float)startColor.getRed() / 255.0f;
        float green = (float)startColor.getGreen() / 255.0f;
        float blue = (float)startColor.getBlue() / 255.0f;
        float alpha = (float)startColor.getAlpha() / 255.0f;
        float red2 = (float)endColor.getRed() / 255.0f;
        float green2 = (float)endColor.getGreen() / 255.0f;
        float blue2 = (float)endColor.getBlue() / 255.0f;
        float alpha2 = (float)endColor.getAlpha() / 255.0f;
        double x1 = 0.0;
        double y1 = 0.0;
        double z1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double z2 = 0.0;
        if (face == EnumFacing.DOWN) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.minY;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.UP) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.maxY + height;
            y2 = bb.maxY + height;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.EAST) {
            x1 = bb.maxX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY + height;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.WEST) {
            x1 = bb.minX;
            x2 = bb.minX;
            y1 = bb.minY;
            y2 = bb.maxY + height;
            z1 = bb.minZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.SOUTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY + height;
            z1 = bb.maxZ;
            z2 = bb.maxZ;
        } else if (face == EnumFacing.NORTH) {
            x1 = bb.minX;
            x2 = bb.maxX;
            y1 = bb.minY;
            y2 = bb.maxY + height;
            z1 = bb.minZ;
            z2 = bb.minZ;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask((boolean)false);
        builder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        if (face == EnumFacing.EAST || face == EnumFacing.WEST || face == EnumFacing.NORTH || face == EnumFacing.SOUTH) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
        } else if (face == EnumFacing.UP) {
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y1, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x1, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z1).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
            builder.pos(x2, y2, z2).color(red2, green2, blue2, alpha2).endVertex();
        } else if (face == EnumFacing.DOWN) {
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
            builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air, double height, boolean fade, boolean invertFade, int alpha, boolean depth) {
        if (fade) {
            Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            RenderUtil.drawFadingOutline(pos, invertFade ? color : endColor, invertFade ? endColor : color, linewidth, height);
            return;
        }
        IBlockState state = RenderUtil.mc.world.getBlockState(pos);
        if ((air || state.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            AxisAlignedBB blockAxis = new AxisAlignedBB((double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, (double)(pos.getX() + 1) - RenderUtil.mc.getRenderManager().viewerPosX, (double)(pos.getY() + 1) - RenderUtil.mc.getRenderManager().viewerPosY + height, (double)(pos.getZ() + 1) - RenderUtil.mc.getRenderManager().viewerPosZ);
            RenderUtil.drawBlockOutline(blockAxis.grow((double)0.002f), color, linewidth, depth);
        }
    }

    public static void drawBlockOutline(AxisAlignedBB bb, Color color, float linewidth, boolean depth) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        if (depth) {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)true);
        } else {
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
        }
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawFadingOutline(BlockPos pos, Color startColor, Color endColor, float linewidth, double height) {
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = InterpolationUtil.getInterpolatedPos((Entity)RenderUtil.mc.player, mc.getRenderPartialTicks(), false);
        RenderUtil.drawFadingOutline(iblockstate.getSelectedBoundingBox((World)RenderUtil.mc.world, pos).grow((double)0.002f).offset(-interp.x, -interp.y, -interp.z).expand(0.0, 0.0, 0.0), startColor, endColor, linewidth, height);
    }

    public static void drawFadingOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth, double height) {
        float red = (float)startColor.getRed() / 255.0f;
        float green = (float)startColor.getGreen() / 255.0f;
        float blue = (float)startColor.getBlue() / 255.0f;
        float alpha = (float)startColor.getAlpha() / 255.0f;
        float red2 = (float)endColor.getRed() / 255.0f;
        float green2 = (float)endColor.getGreen() / 255.0f;
        float blue2 = (float)endColor.getBlue() / 255.0f;
        float alpha2 = (float)endColor.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawSelectionBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean depth) {
        AxisAlignedBB bbPos = RenderUtil.mc.world.getBlockState(pos).getSelectedBoundingBox((World)RenderUtil.mc.world, pos).grow(0.002);
        AxisAlignedBB bb = InterpolationUtil.getInterpolatedAxis(bbPos);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(bbPos)) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            if (depth) {
                GlStateManager.enableDepth();
                GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean)true);
            } else {
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean)false);
            }
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            GL11.glLineWidth((float)lineWidth);
            if (box) {
                RenderGlobal.renderFilledBox((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)boxAlpha / 255.0f));
            }
            if (outline) {
                RenderUtil.drawBlockOutline(bb, secondC ? secondColor : color, lineWidth, depth);
            }
            GL11.glDisable((int)2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBoxESP(AxisAlignedBB bbPos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean depth) {
        AxisAlignedBB axisAlignedBB = bbPos.offset(-RenderUtil.mc.getRenderManager().viewerPosX, -RenderUtil.mc.getRenderManager().viewerPosY, -RenderUtil.mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(bbPos)) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            if (depth) {
                GlStateManager.enableDepth();
                GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean)true);
            } else {
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask((boolean)false);
            }
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            GL11.glLineWidth((float)lineWidth);
            if (box) {
                RenderGlobal.renderFilledBox((AxisAlignedBB)axisAlignedBB, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)boxAlpha / 255.0f));
            }
            if (outline) {
                RenderUtil.drawBlockOutline(axisAlignedBB, secondC ? secondColor : color, lineWidth, depth);
            }
            GL11.glDisable((int)2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBlockWireframe(BlockPos pos, Color color, float lineWidth, double height, boolean onlyBottom) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)lineWidth);
        double x = (double)pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX;
        double y = (double)pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY;
        double z = (double)pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ;
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0 + height, z + 1.0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        if (!onlyBottom) {
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
            bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawEntityBoxESP(Entity entity, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha) {
        Vec3d interp = InterpolationUtil.getInterpolatedPos(entity, mc.getRenderPartialTicks(), true);
        AxisAlignedBB bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)0, (int)1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)lineWidth);
        if (entity instanceof EntityPlayer && Managers.FRIENDS.isFriend(entity.getName())) {
            color = Managers.COLORS.getFriendColor(color.getAlpha());
        }
        if (box) {
            RenderGlobal.renderFilledBox((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)boxAlpha / 255.0f));
        }
        GL11.glDisable((int)2848);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (outline) {
            RenderUtil.drawBlockOutline(bb, secondC ? secondColor : color, lineWidth, false);
        }
    }

    public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.shadeModel((int)7425);
        GL11.glLineWidth((float)thickness);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x, (double)y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x1, (double)y1, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GL11.glDisable((int)2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawGradientLine(float x, float y, float x1, float y1, float thickness, int hex, int secondHex) {
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red2 = (float)(secondHex >> 16 & 0xFF) / 255.0f;
        float green2 = (float)(secondHex >> 8 & 0xFF) / 255.0f;
        float blue2 = (float)(secondHex & 0xFF) / 255.0f;
        float alpha2 = (float)(secondHex >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.shadeModel((int)7425);
        GL11.glLineWidth((float)thickness);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x, (double)y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x, (double)y1, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x1, (double)y1, 0.0).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.pos((double)x1, (double)y, 0.0).color(red2, green2, blue2, alpha2).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GL11.glDisable((int)2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)x, (double)h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)w, (double)h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)w, (double)y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        Gui.drawScaledCustomSizeModalRect((int)x, (int)y, (float)u, (float)v, (int)uWidth, (int)vHeight, (int)width, (int)height, (float)tileWidth, (float)tileHeight);
    }

    public static void drawVGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawHGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)left, (double)top, 0.0).color(f1, f2, f3, f4).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0.0).color(f1, f2, f3, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGlow(double x, double y, double x1, double y1, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        RenderUtil.drawVGradientRect((int)x, (int)y, (int)x1, (int)(y + (y1 - y) / 2.0), ColorUtil.toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), 0), color);
        RenderUtil.drawVGradientRect((int)x, (int)(y + (y1 - y) / 2.0), (int)x1, (int)y1, color, ColorUtil.toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), 0));
        int radius = (int)((y1 - y) / 2.0);
        RenderUtil.drawPolygonPart(x, y + (y1 - y) / 2.0, radius, 0, color, ColorUtil.toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), 0));
        RenderUtil.drawPolygonPart(x, y + (y1 - y) / 2.0, radius, 1, color, ColorUtil.toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), 0));
        RenderUtil.drawPolygonPart(x1, y + (y1 - y) / 2.0, radius, 2, color, ColorUtil.toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), 0));
        RenderUtil.drawPolygonPart(x1, y + (y1 - y) / 2.0, radius, 3, color, ColorUtil.toRGBA(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), 0));
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawPolygonPart(double x, double y, int radius, int part, int color, int endcolor) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float alpha1 = (float)(endcolor >> 24 & 0xFF) / 255.0f;
        float red1 = (float)(endcolor >> 16 & 0xFF) / 255.0f;
        float green1 = (float)(endcolor >> 8 & 0xFF) / 255.0f;
        float blue1 = (float)(endcolor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        double TWICE_PI = Math.PI * 2;
        for (int i = part * 90; i <= part * 90 + 90; ++i) {
            double angle = Math.PI * 2 * (double)i / 360.0 + Math.toRadians(180.0);
            bufferbuilder.pos(x + Math.sin(angle) * (double)radius, y + Math.cos(angle) * (double)radius, 0.0).color(red1, green1, blue1, alpha1).endVertex();
        }
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glBegin((int)9);
        for (int i = 0; i <= 360; ++i) {
            GL11.glVertex2d((double)((double)x + Math.sin((double)i * 3.141526 / 180.0) * (double)radius), (double)((double)y + Math.cos((double)i * 3.141526 / 180.0) * (double)radius));
        }
        GL11.glEnd();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawNameTagOutline(float x, float y, float width, float height, float lineWidth, int color, int secondColor, boolean rainbow) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        RenderUtil.drawLine(x *= 2.0f, y *= 2.0f, x, height *= 2.0f, lineWidth, rainbow ? ColorUtil.rainbow(5000).getRGB() : color);
        RenderUtil.drawLine(width *= 2.0f, y, width, height, lineWidth, rainbow ? ColorUtil.rainbow(1000).getRGB() : secondColor);
        RenderUtil.drawGradientLine(x, y, width, y, lineWidth, rainbow ? ColorUtil.rainbow(5000).getRGB() : color, rainbow ? ColorUtil.rainbow(1000).getRGB() : secondColor);
        RenderUtil.drawGradientLine(x, height, width, height, lineWidth, rainbow ? ColorUtil.rainbow(5000).getRGB() : color, rainbow ? ColorUtil.rainbow(1000).getRGB() : secondColor);
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawArrowPointer(float x, float y, float size, float widthDiv, float heightDiv, boolean outline, float outlineWidth, int color) {
        boolean blend = GL11.glIsEnabled((int)3042);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        RenderUtil.glColor(color);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
        GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)y);
        GL11.glEnd();
        if (outline) {
            GL11.glLineWidth((float)outlineWidth);
            GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)alpha);
            GL11.glBegin((int)2);
            GL11.glVertex2d((double)x, (double)y);
            GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
            GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
            GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
            GL11.glVertex2d((double)x, (double)y);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        if (!blend) {
            GL11.glDisable((int)3042);
        }
        GL11.glDisable((int)2848);
    }

    public static ByteBuffer readImageToBuffer(InputStream in) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(in);
        int[] pixelIndex = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * pixelIndex.length);
        Arrays.stream(pixelIndex).map(i -> i << 8 | i >> 24 & 0xFF).forEach(bytebuffer::putInt);
        bytebuffer.flip();
        return bytebuffer;
    }

    public static Vec3d get2DPos(double x, double y, double z) {
        GL11.glGetFloat((int)2982, (FloatBuffer)modelView);
        GL11.glGetFloat((int)2983, (FloatBuffer)projection);
        GL11.glGetInteger((int)2978, (IntBuffer)viewport);
        boolean out = GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)modelView, (FloatBuffer)projection, (IntBuffer)viewport, (FloatBuffer)screenCoords);
        if (out) {
            return new Vec3d((double)screenCoords.get(0), (double)((float)Display.getHeight() - screenCoords.get(1)), (double)screenCoords.get(2));
        }
        return null;
    }

    public static void glColor(Color color) {
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
    }

    public static void glColor(int color) {
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }
}

