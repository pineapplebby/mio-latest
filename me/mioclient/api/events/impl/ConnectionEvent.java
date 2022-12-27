/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.mioclient.api.events.impl;

import java.util.UUID;
import me.mioclient.api.events.Event;
import net.minecraft.entity.player.EntityPlayer;

public class ConnectionEvent
extends Event {
    private final UUID uuid;
    private final EntityPlayer player;
    private final String name;

    public ConnectionEvent(int stage, UUID uuid, String name) {
        super(stage);
        this.uuid = uuid;
        this.name = name;
        this.player = null;
    }

    public ConnectionEvent(int stage, EntityPlayer player, UUID uuid, String name) {
        super(stage);
        this.player = player;
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}

