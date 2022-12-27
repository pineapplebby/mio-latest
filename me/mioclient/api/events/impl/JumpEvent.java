/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.api.events.impl;

import me.mioclient.api.events.Event;

public class JumpEvent
extends Event {
    public double motionX;
    public double motionY;

    public JumpEvent(double motionX, double motionY) {
        this.motionX = motionX;
        this.motionY = motionY;
    }
}

