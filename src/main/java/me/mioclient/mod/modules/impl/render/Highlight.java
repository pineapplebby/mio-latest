/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class Highlight
extends Module {
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true));
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", false));
    private final Setting<Boolean> depth = this.add(new Setting<Boolean>("Depth", true));
    private final Setting<Float> lineWidth = this.add(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    private final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(125, 125, 213, 150)));
    private final Setting<Boolean> rainbow = this.add(new Setting<Boolean>("Rainbow", false));

    public Highlight() {
        super("Highlight", "Highlights the block u look at.", Category.RENDER);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = Highlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = ray.getBlockPos();
            RenderUtil.drawSelectionBoxESP(pos, this.rainbow.getValue() != false ? Managers.COLORS.getRainbow() : this.color.getValue(), false, new Color(-1), this.lineWidth.getValue().floatValue(), this.line.getValue(), this.box.getValue(), this.color.getValue().getAlpha(), this.depth.getValue());
        }
    }
}

