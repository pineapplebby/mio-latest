/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.GameType
 *  net.minecraft.world.World
 */
package me.mioclient.mod.modules.impl.player;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class FakePlayer
extends Module {
    private final Setting<String> name = this.add(new Setting<String>("Name", "Herobrine"));

    public FakePlayer() {
        super("FakePlayer", "Summons a client-side fake player.", Category.PLAYER, true);
    }

    @Override
    public String getInfo() {
        return this.name.getValue();
    }

    @Override
    public void onEnable() {
        Command.sendMessage("Spawned a fakeplayer with the name " + this.name.getValue() + ".");
        if (FakePlayer.mc.player == null || FakePlayer.mc.player.isDead) {
            this.disable();
            return;
        }
        EntityOtherPlayerMP player = new EntityOtherPlayerMP((World)FakePlayer.mc.world, new GameProfile(UUID.fromString("0f75a81d-70e5-43c5-b892-f33c524284f2"), this.name.getValue()));
        player.copyLocationAndAnglesFrom((Entity)FakePlayer.mc.player);
        player.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
        player.rotationYaw = FakePlayer.mc.player.rotationYaw;
        player.rotationPitch = FakePlayer.mc.player.rotationPitch;
        player.setGameType(GameType.SURVIVAL);
        player.inventory.copyInventory(FakePlayer.mc.player.inventory);
        player.setHealth(20.0f);
        FakePlayer.mc.world.addEntityToWorld(-12345, (Entity)player);
        player.onLivingUpdate();
    }

    @Override
    public void onDisable() {
        if (FakePlayer.mc.world != null) {
            FakePlayer.mc.world.removeEntityFromWorld(-12345);
        }
    }

    @Override
    public void onLogout() {
        if (this.isOn()) {
            this.disable();
        }
    }
}

