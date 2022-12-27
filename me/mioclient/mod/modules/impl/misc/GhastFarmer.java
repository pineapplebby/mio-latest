/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 */
package me.mioclient.mod.modules.impl.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;

public class GhastFarmer
extends Module {
    private final Setting<Boolean> notifySound = this.add(new Setting<Boolean>("Sound", false));
    public int currentX;
    public int currentY;
    public int currentZ;
    public int itemX;
    public int itemY;
    public int itemZ;
    public int ghastX;
    public int ghastY;
    public int ghastZ;
    public boolean ding;

    public GhastFarmer() {
        super("GhastFarmer", "Auto Ghast Farmer", Category.MISC);
    }

    @Override
    public void onEnable() {
        if (GhastFarmer.mc.player == null || GhastFarmer.mc.world == null) {
            return;
        }
        this.currentX = (int)GhastFarmer.mc.player.posX;
        this.currentY = (int)GhastFarmer.mc.player.posY;
        this.currentZ = (int)GhastFarmer.mc.player.posZ;
    }

    @Override
    public void onDisable() {
        if (GhastFarmer.mc.player == null || GhastFarmer.mc.world == null) {
            return;
        }
        GhastFarmer.mc.player.sendChatMessage("#stop");
    }

    @Override
    public void onUpdate() {
        try {
            Class.forName("baritone.api.BaritoneAPI");
            if (GhastFarmer.mc.player == null || GhastFarmer.mc.world == null) {
                return;
            }
            Entity ghastEnt = null;
            double dist = Double.longBitsToDouble(Double.doubleToLongBits(0.017520017079696953) ^ 0x7FC8F0C47187D7FBL);
            for (Entity entity : GhastFarmer.mc.world.loadedEntityList) {
                double ghastDist;
                if (!(entity instanceof EntityGhast) || !((ghastDist = (double)GhastFarmer.mc.player.getDistance(entity)) < dist)) continue;
                dist = ghastDist;
                ghastEnt = entity;
                this.ghastX = (int)entity.posX;
                this.ghastY = (int)entity.posY;
                this.ghastZ = (int)entity.posZ;
                this.ding = true;
            }
            if (this.ding) {
                if (this.notifySound.getValue().booleanValue()) {
                    GhastFarmer.mc.player.playSound(SoundEvents.BLOCK_NOTE_BELL, Float.intBitsToFloat(Float.floatToIntBits(5.2897425f) ^ 0x7F294592), Float.intBitsToFloat(Float.floatToIntBits(5.5405655f) ^ 0x7F314C50));
                }
                this.ding = false;
            }
            ArrayList entityItems = new ArrayList();
            entityItems.addAll(GhastFarmer.mc.world.loadedEntityList.stream().filter(GhastFarmer::lambda$onUpdate$0).map(GhastFarmer::lambda$onUpdate$1).filter(GhastFarmer::lambda$onUpdate$2).collect(Collectors.toList()));
            Entity itemEnt = null;
            Iterator iterator = entityItems.iterator();
            while (iterator.hasNext()) {
                Entity item;
                itemEnt = item = (Entity)iterator.next();
                this.itemX = (int)item.posX;
                this.itemY = (int)item.posY;
                this.itemZ = (int)item.posZ;
            }
            if (ghastEnt != null) {
                GhastFarmer.mc.player.sendChatMessage("#goto " + this.ghastX + " " + this.ghastY + " " + this.ghastZ);
            } else if (itemEnt != null) {
                GhastFarmer.mc.player.sendChatMessage("#goto " + this.itemX + " " + this.itemY + " " + this.itemZ);
            } else {
                GhastFarmer.mc.player.sendChatMessage("#goto " + this.currentX + " " + this.currentY + " " + this.currentZ);
            }
        }
        catch (Exception e) {
            Command.sendMessage("[" + this.getName() + "] " + (Object)ChatFormatting.RED + "This mod needs Baritone API! Download at: " + (Object)ChatFormatting.DARK_AQUA + "https://github.com/cabaletta/baritone/releases/download/v1.2.15/baritone-api-forge-1.2.15.jar");
            this.disable();
        }
    }

    private static boolean lambda$onUpdate$2(EntityItem entityItem) {
        Object entityItem2 = null;
        return entityItem2.getItem().getItem() == Items.GHAST_TEAR;
    }

    private static EntityItem lambda$onUpdate$1(Entity entity) {
        Object entity2 = null;
        return entity2;
    }

    private static boolean lambda$onUpdate$0(Entity entity) {
        Object entity2 = null;
        return entity2 instanceof EntityItem;
    }
}

