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
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class VoidESP
extends Module {
    private final Setting<Integer> rangeX = this.add(new Setting<Integer>("RangeX", 10, 0, 25));
    private final Setting<Integer> rangeY = this.add(new Setting<Integer>("RangeY", 5, 0, 25));
    private final Setting<Mode> mode = this.add(new Setting<Mode>("Mode", Mode.FULL));
    private final Setting<Integer> height = this.add(new Setting<Integer>("Height", 1, 0, 4, v -> this.mode.getValue() == Mode.FULL));
    private final Setting<Boolean> fill = this.add(new Setting<Boolean>("Fill", true));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Outline", true));
    private final Setting<Boolean> wireframe = this.add(new Setting<Boolean>("Wireframe", true));
    private final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(1692929536, true)));

    public VoidESP() {
        super("VoidESP", "Highlights void blocks", Category.RENDER);
    }

    @Override
    public String getInfo() {
        return Managers.TEXT.normalizeCases((Object)this.mode.getValue());
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!VoidESP.fullNullCheck()) {
            assert (VoidESP.mc.renderViewEntity != null);
            Vec3i playerPos = new Vec3i(VoidESP.mc.renderViewEntity.posX, VoidESP.mc.renderViewEntity.posY, VoidESP.mc.renderViewEntity.posZ);
            for (int x = playerPos.getX() - this.rangeX.getValue(); x < playerPos.getX() + this.rangeX.getValue(); ++x) {
                for (int z = playerPos.getZ() - this.rangeX.getValue(); z < playerPos.getZ() + this.rangeX.getValue(); ++z) {
                    for (int y = playerPos.getY() + this.rangeY.getValue(); y > playerPos.getY() - this.rangeY.getValue(); --y) {
                        BlockPos pos = new BlockPos(x, y, z);
                        double h = 0.0;
                        if (this.mode.getValue() == Mode.FLAT) {
                            h = -1.0;
                        } else if (this.mode.getValue() == Mode.SLAB) {
                            h = -0.8;
                        }
                        if (!this.isVoid(pos)) continue;
                        if (this.mode.getValue() == Mode.FULL) {
                            if (this.height.getValue() == 1 || !VoidESP.isAir(pos.up())) {
                                this.drawVoidESP(pos, this.color.getValue(), false, new Color(-1), 0.8f, this.line.getValue(), this.fill.getValue(), this.color.getValue().getAlpha(), true, 0.0, false, false, false, false, 0, this.wireframe.getValue(), true);
                                continue;
                            }
                            if (this.height.getValue() == 2 && VoidESP.isAir(pos.up())) {
                                this.drawVoidESP(pos, this.color.getValue(), false, new Color(-1), 0.8f, this.line.getValue(), this.fill.getValue(), this.color.getValue().getAlpha(), true, 1.0, false, false, false, false, 0, this.wireframe.getValue(), true);
                                continue;
                            }
                            if (this.height.getValue() == 3 && VoidESP.isAir(pos.up()) && VoidESP.isAir(pos.up().up())) {
                                this.drawVoidESP(pos, this.color.getValue(), false, new Color(-1), 0.8f, this.line.getValue(), this.fill.getValue(), this.color.getValue().getAlpha(), true, 2.0, false, false, false, false, 0, this.wireframe.getValue(), true);
                                continue;
                            }
                            if (this.height.getValue() != 4 || !VoidESP.isAir(pos.up()) || !VoidESP.isAir(pos.up().up()) || !VoidESP.isAir(pos.up().up().up())) continue;
                            this.drawVoidESP(pos, this.color.getValue(), false, new Color(-1), 0.8f, this.line.getValue(), this.fill.getValue(), this.color.getValue().getAlpha(), true, 3.0, false, false, false, false, 0, this.wireframe.getValue(), true);
                            continue;
                        }
                        this.drawVoidESP(pos, this.color.getValue(), false, new Color(-1), 0.8f, this.line.getValue(), this.fill.getValue(), this.color.getValue().getAlpha(), true, h, false, false, false, false, 0, this.wireframe.getValue(), true);
                    }
                }
            }
        }
    }

    private boolean isVoid(BlockPos pos) {
        if (pos.getY() != 0) {
            return false;
        }
        return BlockUtil.getBlock(pos) != Blocks.BEDROCK;
    }

    public static boolean isAir(BlockPos pos) {
        return BlockUtil.getBlock(pos) == Blocks.AIR;
    }

    private void drawVoidESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height, boolean gradientBox, boolean gradientOutline, boolean invertGradientBox, boolean invertGradientOutline, int gradientAlpha, boolean cross, boolean flatCross) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), height, gradientBox, invertGradientBox, gradientAlpha);
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, height, gradientOutline, invertGradientOutline, gradientAlpha, false);
        }
        if (cross) {
            RenderUtil.drawBlockWireframe(pos, secondC ? secondColor : color, lineWidth, height, flatCross);
        }
    }

    private static enum Mode {
        FLAT,
        SLAB,
        FULL;

    }
}

