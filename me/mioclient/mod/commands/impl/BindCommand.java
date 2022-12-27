/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.lwjgl.input.Keyboard
 */
package me.mioclient.mod.commands.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Bind;
import org.lwjgl.input.Keyboard;

public class BindCommand
extends Command {
    public BindCommand() {
        super("bind", new String[]{"<module>", "<bind>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            BindCommand.sendMessage("Please specify a module.");
            return;
        }
        String rkey = commands[1];
        String moduleName = commands[0];
        Module module = Managers.MODULES.getModuleByName(moduleName);
        if (module == null) {
            BindCommand.sendMessage("Unknown module '" + module + "'!");
            return;
        }
        if (rkey == null) {
            BindCommand.sendMessage(module.getName() + " is bound to " + (Object)ChatFormatting.GRAY + module.getBind().toString());
            return;
        }
        int key = Keyboard.getKeyIndex((String)rkey.toUpperCase());
        if (rkey.equalsIgnoreCase("none")) {
            key = -1;
        }
        if (key == 0) {
            BindCommand.sendMessage("Unknown key '" + rkey + "'!");
            return;
        }
        module.bind.setValue(new Bind(key));
        BindCommand.sendMessage("Bind for " + (Object)ChatFormatting.GREEN + module.getName() + (Object)ChatFormatting.WHITE + " set to " + (Object)ChatFormatting.GRAY + rkey.toUpperCase());
    }
}

