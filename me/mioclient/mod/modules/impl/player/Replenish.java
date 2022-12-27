/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package me.mioclient.mod.modules.impl.player;

import java.util.ArrayList;
import me.mioclient.api.util.math.Timer;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Replenish
extends Module {
    private final Setting<Integer> delay = this.add(new Setting<Integer>("Delay", 2, 0, 10));
    private final Setting<Integer> gapThreshold = this.add(new Setting<Integer>("GapStack", 50, 50, 64));
    private final Setting<Integer> expThreshold = this.add(new Setting<Integer>("XPStack", 50, 50, 64));
    private final Timer timer = new Timer();
    private final ArrayList<Item> Hotbar = new ArrayList();

    public Replenish() {
        super("Replenish", "Replenishes your hotbar.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        if (Replenish.fullNullCheck()) {
            return;
        }
        this.Hotbar.clear();
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Replenish.mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && !this.Hotbar.contains((Object)stack.getItem())) {
                this.Hotbar.add(stack.getItem());
                continue;
            }
            this.Hotbar.add(Items.AIR);
        }
    }

    @Override
    public void onUpdate() {
        if (Replenish.mc.currentScreen != null) {
            return;
        }
        if (!this.timer.passedMs(this.delay.getValue() * 1000)) {
            return;
        }
        for (int i = 0; i < 9; ++i) {
            if (!this.RefillSlotIfNeed(i)) continue;
            this.timer.reset();
            return;
        }
    }

    private boolean RefillSlotIfNeed(int slot) {
        ItemStack stack = Replenish.mc.player.inventory.getStackInSlot(slot);
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            return false;
        }
        if (!stack.isStackable()) {
            return false;
        }
        if (stack.getCount() >= stack.getMaxStackSize()) {
            return false;
        }
        if (stack.getItem().equals((Object)Items.GOLDEN_APPLE) && stack.getCount() >= this.gapThreshold.getValue()) {
            return false;
        }
        if (stack.getItem().equals((Object)Items.EXPERIENCE_BOTTLE) && stack.getCount() > this.expThreshold.getValue()) {
            return false;
        }
        for (int i = 9; i < 36; ++i) {
            ItemStack item = Replenish.mc.player.inventory.getStackInSlot(i);
            if (item.isEmpty() || !this.CanItemBeMergedWith(stack, item)) continue;
            Replenish.mc.playerController.windowClick(Replenish.mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, (EntityPlayer)Replenish.mc.player);
            Replenish.mc.playerController.updateController();
            return true;
        }
        return false;
    }

    private boolean CanItemBeMergedWith(ItemStack source, ItemStack stack) {
        return source.getItem() == stack.getItem() && source.getDisplayName().equals(stack.getDisplayName());
    }
}

