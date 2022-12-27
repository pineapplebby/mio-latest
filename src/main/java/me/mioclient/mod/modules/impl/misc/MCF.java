/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  org.lwjgl.input.Mouse
 */
package me.mioclient.mod.modules.impl.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

public class MCF
extends Module {
    private boolean didClick;

    public MCF() {
        super("MCF", "Middle click your fellow friends.", Category.MISC);
    }

    @Override
    public void onUpdate() {
        if (Mouse.isButtonDown((int)2)) {
            if (!this.didClick && MCF.mc.currentScreen == null) {
                this.onClick();
            }
            this.didClick = true;
        } else {
            this.didClick = false;
        }
    }

    private void onClick() {
        Entity entity;
        RayTraceResult result = MCF.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            if (Managers.FRIENDS.isFriend(entity.getName())) {
                Managers.FRIENDS.removeFriend(entity.getName());
                Command.sendMessage((Object)ChatFormatting.RED + entity.getName() + (Object)ChatFormatting.RED + " has been unfriended.");
            } else {
                Managers.FRIENDS.addFriend(entity.getName());
                Command.sendMessage((Object)ChatFormatting.AQUA + entity.getName() + (Object)ChatFormatting.GREEN + " has been friended.");
            }
        }
        this.didClick = true;
    }
}

