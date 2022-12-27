/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  org.lwjgl.input.Mouse
 */
package me.mioclient.mod.gui.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import me.mioclient.api.managers.Managers;
import me.mioclient.mod.Mod;
import me.mioclient.mod.gui.click.Component;
import me.mioclient.mod.gui.click.items.Item;
import me.mioclient.mod.gui.click.items.buttons.ModuleButton;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class MioAppearance
extends GuiScreen {
    private static MioAppearance HUDEditorClickGui;
    private static MioAppearance INSTANCE;
    private final ArrayList<Component> components = new ArrayList();

    public MioAppearance() {
        this.setInstance();
        this.load();
    }

    public static MioAppearance getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MioAppearance();
        }
        return INSTANCE;
    }

    public static MioAppearance getClickGui() {
        return MioAppearance.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (final Category category : Managers.MODULES.getCategories()) {
            if (category != Category.HUD) continue;
            this.components.add(new Component(category.getName(), x += 90, 4, true){

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    Managers.MODULES.getModulesByCategory(category).forEach(module -> this.addButton(new ModuleButton((Module)module)));
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Mod::getName)));
    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton)item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.checkMouseWheel();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

    public void onGuiClosed() {
        try {
            super.onGuiClosed();
            this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        INSTANCE = new MioAppearance();
    }
}

