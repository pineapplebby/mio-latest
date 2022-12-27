/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.audio.SoundHandler
 *  net.minecraft.client.audio.SoundManager
 *  net.minecraftforge.fml.common.ObfuscationReflectionHelper
 */
package me.mioclient.mod.commands.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.mod.commands.Command;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand
extends Command {
    public ReloadSoundCommand() {
        super("sound", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        try {
            SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, (Object)mc.getSoundHandler(), (String[])new String[]{"sndManager", "sndManager"});
            sndManager.reloadSoundSystem();
            Command.sendMessage((Object)ChatFormatting.GREEN + "Reloaded Sound System.");
        }
        catch (Exception e) {
            System.out.println((Object)ChatFormatting.RED + "Could not restart sound manager: " + e);
            e.printStackTrace();
            Command.sendMessage((Object)ChatFormatting.RED + "Couldnt Reload Sound System!");
        }
    }
}

