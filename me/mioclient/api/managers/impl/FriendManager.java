/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.mioclient.api.managers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.mioclient.api.util.entity.ProfileUtil;
import me.mioclient.mod.Mod;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager
extends Mod {
    private List<Friend> friends = new ArrayList<Friend>();

    public FriendManager() {
        super("Friends");
    }

    public boolean isCool(String name) {
        List<String> coolList = Arrays.asList("asphyxia1337", "Olype", "AsphyxiasKitty", "rootbeerguy1212", "FemboyPride", "BibleThot", "z75", "Megyn", "antikurdish", "D3R6", "tr011", "FourSixFive", "ThePeaceKeepers", "solar1z");
        return coolList.contains(name);
    }

    public boolean isFriend(String name) {
        this.cleanFriends();
        return this.friends.stream().anyMatch(friend -> ((Friend)friend).username.equalsIgnoreCase(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return this.isFriend(player.getName());
    }

    public void addFriend(String name) {
        Friend friend = this.getFriendByName(name);
        if (friend != null) {
            this.friends.add(friend);
        }
        this.cleanFriends();
    }

    public void removeFriend(String name) {
        this.cleanFriends();
        for (Friend friend : this.friends) {
            if (!friend.getUsername().equalsIgnoreCase(name)) continue;
            this.friends.remove(friend);
            break;
        }
    }

    public void onLoad() {
        this.friends = new ArrayList<Friend>();
        this.resetSettings();
    }

    public void saveFriends() {
        this.resetSettings();
        this.cleanFriends();
        for (Friend friend : this.friends) {
            this.add(new Setting<String>(friend.getUuid().toString(), friend.getUsername()));
        }
    }

    public void cleanFriends() {
        this.friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
    }

    public List<Friend> getFriends() {
        this.cleanFriends();
        return this.friends;
    }

    public Friend getFriendByName(String input) {
        UUID uuid = ProfileUtil.getUUIDFromName(input);
        if (uuid != null) {
            Friend friend = new Friend(input, uuid);
            return friend;
        }
        return null;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
    }

    public static class Friend {
        private final String username;
        private final UUID uuid;

        public Friend(String username, UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }

        public String getUsername() {
            return this.username;
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}

