/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.api.managers.impl;

import java.util.LinkedList;

public final class FpsManager {
    private int fps;
    private final LinkedList<Long> frames = new LinkedList();

    public void update() {
        long time = System.nanoTime();
        this.frames.add(time);
        while (true) {
            long f = this.frames.getFirst();
            long ONE_SECOND = 1000000000L;
            if (time - f <= 1000000000L) break;
            this.frames.remove();
        }
        this.fps = this.frames.size();
    }

    public int getFPS() {
        return this.fps;
    }

    public float getFrametime() {
        return 0.004166667f;
    }
}

