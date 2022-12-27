/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.api.managers.impl;

import me.mioclient.mod.Mod;

public class TimerManager
extends Mod {
    public float timer = 1.0f;

    public void set(float factor) {
        if (factor < 0.1f) {
            factor = 0.1f;
        }
        this.timer = factor;
    }

    public void reset() {
        this.timer = 1.0f;
    }

    public float get() {
        return this.timer;
    }
}

