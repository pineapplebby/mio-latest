/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.api.events.impl.Render2DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventoryPreview
extends Module {
    public Setting<XOffset> xOffset = this.add(new Setting<XOffset>("XOffset", XOffset.CUSTOM));
    public Setting<Integer> x = this.add(new Setting<Integer>("X", 500, 0, 1000, v -> this.xOffset.getValue() == XOffset.CUSTOM));
    public Setting<YOffset> yOffset = this.add(new Setting<YOffset>("YOffset", YOffset.CUSTOM));
    public Setting<Integer> y = this.add(new Setting<Integer>("Y", 2, 0, 1000, v -> this.yOffset.getValue() == YOffset.CUSTOM));
    public Setting<Boolean> outline = this.add(new Setting<Boolean>("Outline", true).setParent());
    public Setting<Color> lineColor = this.add(new Setting<Color>("LineColor", new Color(10, 10, 10, 100), v -> this.outline.isOpen()));
    public Setting<Color> secondColor = this.add(new Setting<Color>("SecondColor", new Color(30, 30, 30, 100), v -> this.outline.isOpen()).injectBoolean(true));
    public Setting<Boolean> rect = this.add(new Setting<Boolean>("Rect", true).setParent());
    public Setting<Color> rectColor = this.add(new Setting<Color>("RectColor", new Color(10, 10, 10, 50), v -> this.rect.isOpen()));

    public InventoryPreview() {
        super("InventoryPreview", "Allows you to see your own inventory without opening it.", Category.RENDER, true);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int y;
        int x;
        if (InventoryPreview.fullNullCheck()) {
            return;
        }
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.disableDepth();
        int n = this.xOffset.getValue() == XOffset.CUSTOM ? this.x.getValue() : (x = this.xOffset.getValue() == XOffset.LEFT ? 0 : Managers.TEXT.scaledWidth - 172);
        int n2 = this.yOffset.getValue() == YOffset.CUSTOM ? this.y.getValue() : (y = this.yOffset.getValue() == YOffset.TOP ? 0 : Managers.TEXT.scaledHeight - 74);
        if (this.outline.getValue().booleanValue()) {
            RenderUtil.drawNameTagOutline((float)x + 6.5f, (float)y + 16.5f, (float)x + 171.5f, (float)y + 73.5f, 1.0f, this.lineColor.getValue().getRGB(), this.secondColor.booleanValue ? this.secondColor.getValue().getRGB() : this.lineColor.getValue().getRGB(), false);
        }
        if (this.rect.getValue().booleanValue()) {
            RenderUtil.drawRect(x + 7, y + 17, x + 171, y + 73, this.rectColor.getValue().getRGB());
        }
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        NonNullList items = InventoryPreview.mc.player.inventory.mainInventory;
        for (int i = 0; i < items.size() - 9; ++i) {
            int iX = x + i % 9 * 18 + 8;
            int iY = y + i / 9 * 18 + 18;
            ItemStack stack = (ItemStack)items.get(i + 9);
            InventoryPreview.mc.getItemRenderer().itemRenderer.zLevel = 501.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(stack, iX, iY);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(InventoryPreview.mc.fontRenderer, stack, iX, iY, null);
            InventoryPreview.mc.getItemRenderer().itemRenderer.zLevel = 0.0f;
        }
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static enum YOffset {
        CUSTOM,
        TOP,
        BOTTOM;

    }

    private static enum XOffset {
        CUSTOM,
        LEFT,
        RIGHT;

    }
}

