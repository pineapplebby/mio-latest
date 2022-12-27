/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBed
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.tileentity.TileEntityDispenser
 *  net.minecraft.tileentity.TileEntityEnderChest
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.tileentity.TileEntityHopper
 *  net.minecraft.tileentity.TileEntityShulkerBox
 *  net.minecraft.tileentity.TileEntitySign
 */
package me.mioclient.mod.modules.impl.render;

import java.awt.Color;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;

public class TileESP
extends Module {
    private final Setting<Boolean> beds = this.add(new Setting<Boolean>("Beds", true));
    private final Setting<Boolean> chests = this.add(new Setting<Boolean>("Chests", true));
    private final Setting<Boolean> eChests = this.add(new Setting<Boolean>("EChests", true));
    private final Setting<Boolean> shulkers = this.add(new Setting<Boolean>("Shulkers", true));
    private final Setting<Boolean> signs = this.add(new Setting<Boolean>("Signs", true));
    private final Setting<Boolean> dispensers = this.add(new Setting<Boolean>("Dispensers", true));
    private final Setting<Boolean> hoppers = this.add(new Setting<Boolean>("Hoppers", true));
    private final Setting<Boolean> furnaces = this.add(new Setting<Boolean>("Furnaces", true));
    private int count;

    public TileESP() {
        super("TileESP", "Highlights tile entities such as storages and signs.", Category.RENDER, true);
    }

    @Override
    public String getInfo() {
        return String.valueOf(this.count);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        this.count = 0;
        for (TileEntity entity : TileESP.mc.world.loadedTileEntityList) {
            if (!this.isValid(entity)) continue;
            RenderUtil.drawSelectionBoxESP(entity.getPos(), this.getColor(entity), false, new Color(-1), 1.0f, true, true, 100, false);
            ++this.count;
        }
    }

    private Color getColor(TileEntity entity) {
        if (entity instanceof TileEntityChest) {
            return new Color(155, 127, 77, 100);
        }
        if (entity instanceof TileEntityBed) {
            return new Color(190, 49, 49, 100);
        }
        if (entity instanceof TileEntityEnderChest) {
            return new Color(124, 37, 196, 100);
        }
        if (entity instanceof TileEntityShulkerBox) {
            return new Color(255, 1, 175, 100);
        }
        if (entity instanceof TileEntityFurnace || entity instanceof TileEntityDispenser || entity instanceof TileEntityHopper) {
            return new Color(150, 150, 150, 100);
        }
        return new Color(255, 255, 255, 100);
    }

    private boolean isValid(TileEntity entity) {
        return entity instanceof TileEntityChest && this.chests.getValue() != false || entity instanceof TileEntityBed && this.beds.getValue() != false || entity instanceof TileEntityEnderChest && this.eChests.getValue() != false || entity instanceof TileEntityShulkerBox && this.shulkers.getValue() != false || entity instanceof TileEntityFurnace && this.furnaces.getValue() != false || entity instanceof TileEntityDispenser && this.dispensers.getValue() != false || entity instanceof TileEntityHopper && this.hoppers.getValue() != false || entity instanceof TileEntitySign && this.signs.getValue() != false;
    }
}

