/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package me.mioclient.mod.commands.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.commands.Command;

public class PrefixCommand
extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage((Object)ChatFormatting.GREEN + "The current prefix is " + Managers.COMMANDS.getCommandPrefix());
            return;
        }
        Managers.COMMANDS.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to " + (Object)ChatFormatting.GRAY + commands[0]);
    }
}

