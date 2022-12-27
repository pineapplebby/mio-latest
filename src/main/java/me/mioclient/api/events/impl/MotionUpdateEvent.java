/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.api.events.impl;

import me.mioclient.api.events.Event;

public class MotionUpdateEvent
extends Event {
    public float rotationYaw;
    public float rotationPitch;
    public double posX;
    public double posY;
    public double posZ;
    public boolean onGround;
    public boolean noClip;
    public int stage;

    public MotionUpdateEvent(float rotationYaw, float rotationPitch, double posX, double posY, double posZ, boolean onGround, boolean noClip, int stage) {
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = onGround;
        this.noClip = noClip;
        this.stage = stage;
    }
}

