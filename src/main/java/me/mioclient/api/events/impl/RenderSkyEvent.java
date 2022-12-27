/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.Cancelable
 */
package me.mioclient.api.events.impl;

import java.awt.Color;
import me.mioclient.api.events.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class RenderSkyEvent
extends Event {
    private Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}

