/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleESP
extends Module {
    private final Setting<Page> page = this.add(new Setting<Page>("Settings", Page.GLOBAL));
    private final Setting<Boolean> renderOwn = this.add(new Setting<Boolean>("RenderOwn", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> fov = this.add(new Setting<Boolean>("FovOnly", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Integer> range = this.add(new Setting<Integer>("Range", 5, 0, 25, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true, v -> this.page.getValue() == Page.GLOBAL).setParent());
    private final Setting<Boolean> gradientBox = this.add(new Setting<Boolean>("FadeBox", false, v -> this.box.isOpen() && this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> invertGradientBox = this.add(new Setting<Boolean>("InvertBoxFade", false, v -> this.box.isOpen() && this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> outline = this.add(new Setting<Boolean>("Outline", true, v -> this.page.getValue() == Page.GLOBAL).setParent());
    private final Setting<Boolean> gradientOutline = this.add(new Setting<Boolean>("FadeLine", false, v -> this.outline.isOpen() && this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> invertGradientOutline = this.add(new Setting<Boolean>("InvertLineFade", false, v -> this.outline.isOpen() && this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> separateHeight = this.add(new Setting<Boolean>("SeparateHeight", false, v -> this.outline.isOpen() && this.page.getValue() == Page.GLOBAL));
    private final Setting<Double> lineHeight = this.add(new Setting<Double>("LineHeight", -1.1, -2.0, 2.0, v -> this.outline.isOpen() && this.page.getValue() == Page.GLOBAL && this.separateHeight.getValue() != false));
    private final Setting<Boolean> wireframe = this.add(new Setting<Boolean>("Wireframe", true, v -> this.page.getValue() == Page.GLOBAL).setParent());
    private final Setting<WireframeMode> wireframeMode = this.add(new Setting<WireframeMode>("Mode", WireframeMode.FULL, v -> this.wireframe.isOpen() && this.page.getValue() == Page.GLOBAL));
    private final Setting<Double> height = this.add(new Setting<Double>("Height", -1.1, -2.0, 2.0, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Integer> boxAlpha = this.add(new Setting<Integer>("BoxAlpha", 80, 0, 255, v -> this.box.getValue() != false && this.page.getValue() == Page.GLOBAL));
    private final Setting<Float> lineWidth = this.add(new Setting<Float>("LineWidth", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> (this.outline.getValue() != false || this.wireframe.getValue() != false) && this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> rainbow = this.add(new Setting<Boolean>("Rainbow", false, v -> this.page.getValue() == Page.COLORS));
    private final Setting<Color> obbyColor = this.add(new Setting<Color>("Obby", new Color(12721437), v -> this.page.getValue() == Page.COLORS));
    private final Setting<Color> brockColor = this.add(new Setting<Color>("Bedrock", new Color(2595403), v -> this.page.getValue() == Page.COLORS));
    private final Setting<Boolean> customOutline = this.add(new Setting<Boolean>("LineColor", false, v -> this.page.getValue() == Page.COLORS).setParent());
    private final Setting<Color> obbyLineColor = this.add(new Setting<Color>("ObbyLine", new Color(-1), v -> this.customOutline.isOpen() && this.page.getValue() == Page.COLORS));
    private final Setting<Color> brockLineColor = this.add(new Setting<Color>("BedrockLine", new Color(-1), v -> this.customOutline.isOpen() && this.page.getValue() == Page.COLORS));

    public HoleESP() {
        super("HoleESP", "Shows safe spots near you.", Category.RENDER);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        assert (HoleESP.mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(HoleESP.mc.renderViewEntity.posX, HoleESP.mc.renderViewEntity.posY, HoleESP.mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getValue(); x < playerPos.getX() + this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - this.range.getValue(); z < playerPos.getZ() + this.range.getValue(); ++z) {
                int rangeY = 5;
                for (int y = playerPos.getY() + rangeY; y > playerPos.getY() - rangeY; --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Color safeColor = this.rainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.brockColor.getValue();
                    Color color = this.rainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.obbyColor.getValue();
                    Color safecColor = this.brockLineColor.getValue();
                    Color cColor = this.obbyLineColor.getValue();
                    if (!HoleESP.mc.world.getBlockState(pos).getBlock().equals((Object)Blocks.AIR) || !HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals((Object)Blocks.AIR) || !HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals((Object)Blocks.AIR) || pos.equals((Object)new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) && !this.renderOwn.getValue().booleanValue() || !Managers.ROTATIONS.isInFov(pos) && this.fov.getValue().booleanValue()) continue;
                    if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north(2)).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                        this.drawDoubles(true, pos, safeColor, this.customOutline.getValue(), safecColor, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.separateHeight.getValue() != false ? this.lineHeight.getValue().doubleValue() : this.height.getValue().doubleValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), 0, this.wireframe.getValue(), this.wireframeMode.getValue() == WireframeMode.FLAT);
                    } else if (!(HoleESP.mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.north().up()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.north().down()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north().down()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.north(2)).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north(2)).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.east()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.east()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.north().east()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north().east()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.north().west()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.north().west()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.BEDROCK || HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.OBSIDIAN && HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.BEDROCK)) {
                        this.drawDoubles(true, pos, color, this.customOutline.getValue(), cColor, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.separateHeight.getValue() != false ? this.lineHeight.getValue().doubleValue() : this.height.getValue().doubleValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), 0, this.wireframe.getValue(), this.wireframeMode.getValue() == WireframeMode.FLAT);
                    }
                    if (HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.east().up()).getBlock() == Blocks.AIR && HoleESP.mc.world.getBlockState(pos.east().down()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east(2)).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east(2).down()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                        this.drawDoubles(false, pos, safeColor, this.customOutline.getValue(), safecColor, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.separateHeight.getValue() != false ? this.lineHeight.getValue().doubleValue() : this.height.getValue().doubleValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), 0, this.wireframe.getValue(), this.wireframeMode.getValue() == WireframeMode.FLAT);
                    } else if (!(HoleESP.mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.east().up()).getBlock() != Blocks.AIR || HoleESP.mc.world.getBlockState(pos.east().down()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().down()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.east(2)).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east(2)).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.north()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.north()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.east().north()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().north()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.east().south()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east().south()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() != Blocks.OBSIDIAN || HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() != Blocks.OBSIDIAN)) {
                        this.drawDoubles(false, pos, color, this.customOutline.getValue(), cColor, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.separateHeight.getValue() != false ? this.lineHeight.getValue().doubleValue() : this.height.getValue().doubleValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), 0, this.wireframe.getValue(), this.wireframeMode.getValue() == WireframeMode.FLAT);
                    }
                    if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                        this.drawHoleESP(pos, safeColor, this.customOutline.getValue(), safecColor, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.separateHeight.getValue() != false ? this.lineHeight.getValue().doubleValue() : this.height.getValue().doubleValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), 0, this.wireframe.getValue(), this.wireframeMode.getValue() == WireframeMode.FLAT);
                        continue;
                    }
                    if (!BlockUtil.isUnsafe(HoleESP.mc.world.getBlockState(pos.down()).getBlock()) || !BlockUtil.isUnsafe(HoleESP.mc.world.getBlockState(pos.east()).getBlock()) || !BlockUtil.isUnsafe(HoleESP.mc.world.getBlockState(pos.west()).getBlock()) || !BlockUtil.isUnsafe(HoleESP.mc.world.getBlockState(pos.south()).getBlock()) || !BlockUtil.isUnsafe(HoleESP.mc.world.getBlockState(pos.north()).getBlock())) continue;
                    this.drawHoleESP(pos, color, this.customOutline.getValue(), cColor, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), this.separateHeight.getValue() != false ? this.lineHeight.getValue().doubleValue() : this.height.getValue().doubleValue(), this.gradientBox.getValue(), this.gradientOutline.getValue(), this.invertGradientBox.getValue(), this.invertGradientOutline.getValue(), 0, this.wireframe.getValue(), this.wireframeMode.getValue() == WireframeMode.FLAT);
                }
            }
        }
    }

    public void drawDoubles(boolean faceNorth, BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height, double lineHeight, boolean gradientBox, boolean gradientOutline, boolean invertGradientBox, boolean invertGradientOutline, int gradientAlpha, boolean cross, boolean flatCross) {
        this.drawHoleESP(pos, color, secondC, secondColor, lineWidth, outline, box, boxAlpha, air, height, lineHeight, gradientBox, gradientOutline, invertGradientBox, invertGradientOutline, gradientAlpha, cross, flatCross);
        this.drawHoleESP(faceNorth ? pos.north() : pos.east(), color, secondC, secondColor, lineWidth, outline, box, boxAlpha, air, height, lineHeight, gradientBox, gradientOutline, invertGradientBox, invertGradientOutline, gradientAlpha, cross, flatCross);
    }

    public void drawHoleESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height, double lineHeight, boolean gradientBox, boolean gradientOutline, boolean invertGradientBox, boolean invertGradientOutline, int gradientAlpha, boolean cross, boolean flatCross) {
        if (box) {
            RenderUtil.drawBox(pos, ColorUtil.injectAlpha(color, boxAlpha), height, gradientBox, invertGradientBox, gradientAlpha);
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, lineHeight, gradientOutline, invertGradientOutline, gradientAlpha, false);
        }
        if (cross) {
            RenderUtil.drawBlockWireframe(pos, secondC ? secondColor : color, lineWidth, height, flatCross);
        }
    }

    private static enum WireframeMode {
        FLAT,
        FULL;

    }

    private static enum Page {
        COLORS,
        GLOBAL;

    }
}

