/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderPearl
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.mioclient.mod.modules.impl.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;

public class PearlNotify
extends Module {
    private boolean flag;

    public PearlNotify() {
        super("PearlNotify", "Notifies pearl throws.", Category.MISC);
    }

    @Override
    public void onEnable() {
        this.flag = true;
    }

    @Override
    public void onUpdate() {
        Entity enderPearl = null;
        if (PearlNotify.mc.world == null || PearlNotify.mc.player == null) {
            return;
        }
        for (Object e : PearlNotify.mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderPearl)) continue;
            enderPearl = e;
            break;
        }
        if (enderPearl == null) {
            this.flag = true;
            return;
        }
        EntityPlayer closestPlayer = null;
        for (EntityPlayer entity : PearlNotify.mc.world.playerEntities) {
            if (closestPlayer == null) {
                closestPlayer = entity;
                continue;
            }
            if (closestPlayer.getDistance(enderPearl) <= entity.getDistance(enderPearl)) continue;
            closestPlayer = entity;
        }
        if (closestPlayer == PearlNotify.mc.player) {
            this.flag = false;
        }
        if (closestPlayer != null && this.flag) {
            String faceing = enderPearl.getHorizontalFacing().toString();
            if (faceing.equals("West")) {
                faceing = "East";
            } else if (faceing.equals("East")) {
                faceing = "West";
            }
            Command.sendMessageWithID(Managers.FRIENDS.isFriend(closestPlayer.getName()) ? (Object)ChatFormatting.AQUA + closestPlayer.getName() + (Object)ChatFormatting.GRAY + " has just thrown a pearl heading " + (Object)ChatFormatting.AQUA + faceing + "!" : (Object)ChatFormatting.RED + closestPlayer.getName() + (Object)ChatFormatting.GRAY + " has just thrown a pearl heading " + (Object)ChatFormatting.RED + faceing + "!", closestPlayer.getEntityId());
            this.flag = false;
        }
    }
}

