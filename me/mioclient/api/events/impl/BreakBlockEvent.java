/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 */
package me.mioclient.api.events.impl;

import me.mioclient.api.events.Event;
import net.minecraft.util.math.BlockPos;

public class BreakBlockEvent
extends Event {
    BlockPos pos;

    public BreakBlockEvent(BlockPos blockPos) {
        this.pos = blockPos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }
}

