/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemExpBottle
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumHand
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package me.mioclient.mod.modules.impl.combat;

import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.world.InventoryUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Bind;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class PacketExp
extends Module {
    public static PacketExp INSTANCE;
    protected final Setting<Mode> mode = this.add(new Setting<Mode>("Mode", Mode.KEY));
    protected final Setting<Integer> delay = this.add(new Setting<Integer>("Delay", 1, 0, 5));
    private final Timer delayTimer = new Timer();

    public PacketExp() {
        super("PacketExp", "Robot module", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        return Managers.TEXT.normalizeCases((Object)this.mode.getValue());
    }

    @Override
    public void onTick() {
        if (!PacketExp.fullNullCheck() && this.mode.getValue() == Mode.MIDDLECLICK && Mouse.isButtonDown((int)2)) {
            this.throwExp();
        } else if (this.check() && this.mode.getValue() == Mode.KEY && Keyboard.isKeyDown((int)((Bind)this.bind.getValue()).getKey())) {
            this.enable();
            this.throwExp();
        }
        if (this.check() && this.mode.getValue() == Mode.KEY && !Keyboard.isKeyDown((int)((Bind)this.bind.getValue()).getKey())) {
            this.disable();
        }
    }

    private void throwExp() {
        int oldSlot = PacketExp.mc.player.inventory.currentItem;
        int newSlot = InventoryUtil.findHotbarBlock(ItemExpBottle.class);
        if (newSlot != -1 && this.delayTimer.passedMs(this.delay.getValue() * 20)) {
            PacketExp.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(newSlot));
            PacketExp.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            PacketExp.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(oldSlot));
            this.delayTimer.reset();
        }
    }

    private boolean check() {
        if (PacketExp.nullCheck() || PacketExp.fullNullCheck()) {
            return false;
        }
        return ((Bind)this.bind.getValue()).getKey() != -1;
    }

    protected static enum Mode {
        KEY,
        MIDDLECLICK;

    }
}

