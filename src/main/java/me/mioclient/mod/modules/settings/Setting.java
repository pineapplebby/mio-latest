/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package me.mioclient.mod.modules.settings;

import java.awt.Color;
import java.util.function.Predicate;
import me.mioclient.api.events.impl.ClientEvent;
import me.mioclient.mod.Mod;
import me.mioclient.mod.modules.settings.Bind;
import me.mioclient.mod.modules.settings.EnumConverter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Setting<T> {
    private final String name;
    private Predicate<T> visibility;
    private Mod mod;
    private final T defaultValue;
    private T value;
    private T plannedValue;
    private T minValue;
    private T maxValue;
    private boolean restriction;
    public boolean open;
    public boolean parent;
    public boolean hideAlpha;
    public boolean hasBoolean;
    public boolean booleanValue;

    public Setting(String nameIn, T defaultValueIn, T minValueIn, T maxValueIn, Predicate<T> visibilityIn) {
        this.name = nameIn;
        this.defaultValue = defaultValueIn;
        this.value = defaultValueIn;
        this.minValue = minValueIn;
        this.maxValue = maxValueIn;
        this.plannedValue = defaultValueIn;
        this.visibility = visibilityIn;
        this.restriction = true;
    }

    public Setting(String nameIn, T defaultValueIn, Predicate<T> visibilityIn) {
        this.name = nameIn;
        this.defaultValue = defaultValueIn;
        this.value = defaultValueIn;
        this.plannedValue = defaultValueIn;
        this.visibility = visibilityIn;
    }

    public Setting(String nameIn, T defaultValueIn, T minValueIn, T maxValueIn) {
        this.name = nameIn;
        this.defaultValue = defaultValueIn;
        this.value = defaultValueIn;
        this.minValue = minValueIn;
        this.maxValue = maxValueIn;
        this.plannedValue = defaultValueIn;
        this.restriction = true;
    }

    public Setting(String nameIn, T defaultValueIn) {
        this.name = nameIn;
        this.defaultValue = defaultValueIn;
        this.value = defaultValueIn;
        this.plannedValue = defaultValueIn;
    }

    public T getValue() {
        return this.value;
    }

    public T getPlannedValue() {
        return this.plannedValue;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getMinValue() {
        return this.minValue;
    }

    public T getMaxValue() {
        return this.maxValue;
    }

    public String getName() {
        return this.name;
    }

    public Mod getMod() {
        return this.mod;
    }

    public String getCurrentEnumName() {
        return EnumConverter.getProperName((Enum)this.value);
    }

    public <T> String getClassName(T value) {
        return value.getClass().getSimpleName();
    }

    public String getType() {
        if (this.isEnumSetting()) {
            return "Enum";
        }
        return this.getClassName(this.defaultValue);
    }

    public void setValue(T valueIn) {
        this.setPlannedValue(valueIn);
        if (this.restriction) {
            if (((Number)this.minValue).floatValue() > ((Number)valueIn).floatValue()) {
                this.setPlannedValue(this.minValue);
            }
            if (((Number)this.maxValue).floatValue() < ((Number)valueIn).floatValue()) {
                this.setPlannedValue(this.maxValue);
            }
        }
        ClientEvent event = new ClientEvent(this);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            this.value = this.plannedValue;
        } else {
            this.plannedValue = this.value;
        }
    }

    public void setPlannedValue(T valueIn) {
        this.plannedValue = valueIn;
    }

    public Setting<T> setParent() {
        this.parent = true;
        return this;
    }

    public void setMod(Mod modIn) {
        this.mod = modIn;
    }

    public void setEnumValue(String value) {
        for (Enum e : (Enum[])((Enum)this.value).getClass().getEnumConstants()) {
            if (!e.name().equalsIgnoreCase(value)) continue;
            this.value = e;
        }
    }

    public void increaseEnum() {
        this.plannedValue = EnumConverter.increaseEnum((Enum)this.value);
        ClientEvent event = new ClientEvent(this);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            this.value = this.plannedValue;
        } else {
            this.plannedValue = this.value;
        }
    }

    public Setting<T> injectBoolean(boolean valueIn) {
        if (this.value instanceof Color) {
            this.hasBoolean = true;
            this.booleanValue = valueIn;
        }
        return this;
    }

    public Setting<T> hideAlpha() {
        this.hideAlpha = true;
        return this;
    }

    public boolean isNumberSetting() {
        return this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float;
    }

    public boolean isEnumSetting() {
        return !this.isNumberSetting() && !(this.value instanceof String) && !(this.value instanceof Bind) && !(this.value instanceof Character) && !(this.value instanceof Boolean) && !(this.value instanceof Color);
    }

    public boolean isStringSetting() {
        return this.value instanceof String;
    }

    public boolean isVisible() {
        if (this.visibility == null) {
            return true;
        }
        return this.visibility.test(this.getValue());
    }

    public boolean isOpen() {
        return this.open && this.parent;
    }

    public boolean hasRestriction() {
        return this.restriction;
    }
}

