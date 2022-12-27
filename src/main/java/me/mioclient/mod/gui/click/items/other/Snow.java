/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 */
package me.mioclient.mod.gui.click.items.other;

import java.util.Random;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class Snow {
    private int x;
    private int y;
    private int speed;
    private int size;

    public Snow(int x, int y, int speed, int size) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.size = size;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void drawSnow(ScaledResolution res) {
        Gui.drawRect((int)this.getX(), (int)this.getY(), (int)(this.getX() + this.size), (int)(this.getY() + this.size), (int)-1714829883);
        this.setY(this.getY() + this.speed);
        if (this.getY() > res.getScaledHeight() + 10 || this.getY() < -10) {
            this.setY(-10);
            Random random = new Random();
            this.speed = random.nextInt(10) + 1;
            this.size = random.nextInt(4) + 1;
        }
    }
}

