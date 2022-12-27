/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.GraphicsEnvironment;
import me.mioclient.api.events.impl.ClientEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FontMod
extends Module {
    public static FontMod INSTANCE;
    public final Setting<String> font = this.add(new Setting<String>("Font", "Verdana"));
    public final Setting<Boolean> antiAlias = this.add(new Setting<Boolean>("AntiAlias", true));
    public final Setting<Boolean> metrics = this.add(new Setting<Boolean>("Metrics", true));
    public final Setting<Boolean> global = this.add(new Setting<Boolean>("Global", false));
    public final Setting<Integer> size = this.add(new Setting<Integer>("Size", 17, 12, 30));
    public final Setting<Style> style = this.add(new Setting<Style>("Style", Style.PLAIN));
    private boolean reload;

    public FontMod() {
        super("Fonts", "Custom font for all of the clients text. Use the font command.", Category.CLIENT, true);
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        return this.font.getValue();
    }

    @Override
    public void onTick() {
        if (this.reload) {
            Managers.TEXT.init();
            this.reload = false;
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getMod().equals(this)) {
            if (setting.getName().equals("Font") && !this.checkFont(setting.getPlannedValue().toString())) {
                Command.sendMessage((Object)ChatFormatting.RED + "That font doesn't exist.");
                event.cancel();
                return;
            }
            this.reload = true;
        }
    }

    private boolean checkFont(String font) {
        for (String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!s.equals(font)) continue;
            return true;
        }
        return false;
    }

    public int getFont() {
        switch (this.style.getValue()) {
            case BOLD: {
                return 1;
            }
            case ITALIC: {
                return 2;
            }
            case BOLDITALIC: {
                return 3;
            }
        }
        return 0;
    }

    private static enum Style {
        PLAIN,
        BOLD,
        ITALIC,
        BOLDITALIC;

    }
}

