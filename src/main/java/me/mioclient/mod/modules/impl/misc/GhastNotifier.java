/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.init.SoundEvents
 */
package me.mioclient.mod.modules.impl.misc;

import java.util.HashSet;
import java.util.Set;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.SoundEvents;

public class GhastNotifier
extends Module {
    private final Setting<Boolean> chat = this.add(new Setting<Boolean>("Chat", true).setParent());
    private final Setting<Boolean> censorCoords = this.add(new Setting<Boolean>("CensorCoords", false, v -> this.chat.isOpen()));
    private final Setting<Boolean> sound = this.add(new Setting<Boolean>("Sound", true));
    private final Set<Entity> ghasts = new HashSet<Entity>();

    public GhastNotifier() {
        super("GhastNotify", "Helps you find ghasts", Category.MISC);
    }

    @Override
    public void onEnable() {
        this.ghasts.clear();
    }

    @Override
    public void onUpdate() {
        for (Entity entity : GhastNotifier.mc.world.getLoadedEntityList()) {
            if (!(entity instanceof EntityGhast) || this.ghasts.contains((Object)entity)) continue;
            if (this.chat.getValue().booleanValue()) {
                if (this.censorCoords.getValue().booleanValue()) {
                    Command.sendMessage("There is a ghast!");
                } else {
                    Command.sendMessage("There is a ghast at: " + entity.getPosition().getX() + "X, " + entity.getPosition().getY() + "Y, " + entity.getPosition().getZ() + "Z.");
                }
            }
            this.ghasts.add(entity);
            if (!this.sound.getValue().booleanValue()) continue;
            GhastNotifier.mc.player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
        }
    }
}

