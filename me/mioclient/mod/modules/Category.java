/*
 * Decompiled with CFR 0.150.
 */
package me.mioclient.mod.modules;

public enum Category {
    COMBAT("Combat"),
    MISC("Misc"),
    RENDER("Render"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    EXPLOIT("Exploit"),
    CLIENT("Client"),
    HUD("HUD");

    private final String name;

    private Category(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

