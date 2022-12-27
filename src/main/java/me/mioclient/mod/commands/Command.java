/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentBase
 */
package me.mioclient.mod.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.Mod;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public abstract class Command
extends Mod {
    protected String name;
    protected String[] commands;

    public Command(String name) {
        super(name);
        this.name = name;
        this.commands = new String[]{""};
    }

    public Command(String name, String[] commands) {
        super(name);
        this.name = name;
        this.commands = commands;
    }

    public static void sendMessage(String message) {
        Command.sendSilentMessage(Managers.TEXT.getPrefix() + (Object)ChatFormatting.GRAY + message);
    }

    public static void sendSilentMessage(String message) {
        if (Command.nullCheck()) {
            return;
        }
        Command.mc.player.sendMessage((ITextComponent)new ChatMessage(message));
    }

    public static String getCommandPrefix() {
        return Managers.COMMANDS.getCommandPrefix();
    }

    public static void sendMessageWithID(String message, int id) {
        if (!Command.nullCheck()) {
            Command.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)new ChatMessage(Managers.TEXT.getPrefix() + (Object)ChatFormatting.GRAY + message), id);
        }
    }

    public abstract void execute(String[] var1);

    public String complete(String str) {
        if (this.name.toLowerCase().startsWith(str)) {
            return this.name;
        }
        for (String command : this.commands) {
            if (!command.toLowerCase().startsWith(str)) continue;
            return command;
        }
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String[] getCommands() {
        return this.commands;
    }

    public static class ChatMessage
    extends TextComponentBase {
        private final String text;

        public ChatMessage(String text) {
            Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String replacement = matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }

        public String getUnformattedComponentText() {
            return this.text;
        }

        public ITextComponent createCopy() {
            return null;
        }

        public ITextComponent shallowCopy() {
            return new ChatMessage(this.text);
        }
    }
}

