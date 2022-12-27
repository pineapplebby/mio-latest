/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemEnderPearl
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.World
 *  org.lwjgl.input.Mouse
 */
package me.mioclient.mod.modules.impl.player;

import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.world.InventoryUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

public class KeyPearl
extends Module {
    private final Setting<Mode> mode = this.add(new Setting<Mode>("Mode", Mode.MIDDLECLICK));
    private final Setting<Boolean> antiFriend = this.add(new Setting<Boolean>("NoPlayerTrace", true));
    private boolean clicked;

    public KeyPearl() {
        super("KeyPearl", "Throws a pearl.", Category.PLAYER);
    }

    @Override
    public String getInfo() {
        return Managers.TEXT.normalizeCases((Object)this.mode.getValue());
    }

    @Override
    public void onEnable() {
        if (!KeyPearl.fullNullCheck() && this.mode.getValue() == Mode.KEY) {
            this.throwPearl();
            this.disable();
        }
    }

    @Override
    public void onTick() {
        if (this.mode.getValue() == Mode.MIDDLECLICK) {
            if (Mouse.isButtonDown((int)2)) {
                if (!this.clicked) {
                    this.throwPearl();
                }
                this.clicked = true;
            } else {
                this.clicked = false;
            }
        }
    }

    private void throwPearl() {
        Entity entity;
        RayTraceResult result;
        if (this.antiFriend.getValue().booleanValue() && (result = KeyPearl.mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            return;
        }
        int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
        boolean offhand = KeyPearl.mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL;
        boolean bl = offhand;
        if (pearlSlot != -1 || offhand) {
            int oldslot = KeyPearl.mc.player.inventory.currentItem;
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(pearlSlot, false);
            }
            KeyPearl.mc.playerController.processRightClick((EntityPlayer)KeyPearl.mc.player, (World)KeyPearl.mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            if (!offhand) {
                InventoryUtil.switchToHotbarSlot(oldslot, false);
            }
        }
    }

    private static enum Mode {
        KEY,
        MIDDLECLICK;

    }
}

