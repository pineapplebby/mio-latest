/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package me.mioclient.mod.commands.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.impl.client.FontMod;
import me.mioclient.mod.modules.impl.client.HUD;

public class WatermarkCommand
extends Command {
    public WatermarkCommand() {
        super("watermark", new String[]{"<watermark>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 2) {
            FontMod fontMod = FontMod.INSTANCE;
            boolean customFont = fontMod.isOn();
            if (commands[0] != null) {
                if (customFont) {
                    fontMod.disable();
                }
                HUD.getInstance().watermarkString.setValue(commands[0]);
                if (customFont) {
                    fontMod.enable();
                }
                WatermarkCommand.sendMessage("Watermark set to " + (Object)ChatFormatting.GREEN + commands[0]);
            } else {
                WatermarkCommand.sendMessage("Not a valid command... Possible usage: <New Watermark>");
            }
        }
    }
}

