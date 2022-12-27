/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.Cancelable
 */
package me.mioclient.api.events.impl;

import me.mioclient.api.events.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class DamageBlockEvent
extends Event {
    BlockPos pos;
    int progress;
    int breakerId;

    public DamageBlockEvent(BlockPos pos, int progress, int breakerId) {
        this.pos = pos;
        this.progress = progress;
        this.breakerId = breakerId;
    }

    public BlockPos getPosition() {
        return this.pos;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getBreakerId() {
        return this.breakerId;
    }
}

