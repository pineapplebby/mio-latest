/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package me.mioclient.mod.modules.impl.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import java.util.Map;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArmorWarner
extends Module {
    private final Setting<Integer> armorThreshold = this.add(new Setting<Integer>("Armor%", 20, 1, 100));
    private final Setting<Boolean> notifySelf = this.add(new Setting<Boolean>("Self", true));
    private final Setting<Boolean> notification = this.add(new Setting<Boolean>("Friends", true));
    private final Map<EntityPlayer, Integer> entityArmorArraylist = new HashMap<EntityPlayer, Integer>();

    public ArmorWarner() {
        super("ArmorWarner", "Notifies when your armor is low durability.", Category.PLAYER);
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer player : ArmorWarner.mc.world.playerEntities) {
            if (player.isDead || !Managers.FRIENDS.isFriend(player.getName())) continue;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == ItemStack.EMPTY) continue;
                int percent = EntityUtil.getDamagePercent(stack);
                if (percent <= this.armorThreshold.getValue() && !this.entityArmorArraylist.containsKey((Object)player)) {
                    if (player == ArmorWarner.mc.player && this.notifySelf.getValue().booleanValue()) {
                        Command.sendMessage((Object)ChatFormatting.RED + "Your " + this.getArmorPieceName(stack) + " low dura!");
                    }
                    if (Managers.FRIENDS.isFriend(player.getName()) && this.notification.getValue().booleanValue() && player != ArmorWarner.mc.player) {
                        ArmorWarner.mc.player.sendChatMessage("/msg " + player.getName() + " Yo, " + player.getName() + ", ur " + this.getArmorPieceName(stack) + " low dura!");
                    }
                    this.entityArmorArraylist.put(player, player.inventory.armorInventory.indexOf((Object)stack));
                }
                if (!this.entityArmorArraylist.containsKey((Object)player) || this.entityArmorArraylist.get((Object)player).intValue() != player.inventory.armorInventory.indexOf((Object)stack) || percent <= this.armorThreshold.getValue()) continue;
                this.entityArmorArraylist.remove((Object)player);
            }
            if (!this.entityArmorArraylist.containsKey((Object)player) || player.inventory.armorInventory.get(this.entityArmorArraylist.get((Object)player).intValue()) != ItemStack.EMPTY) continue;
            this.entityArmorArraylist.remove((Object)player);
        }
    }

    private String getArmorPieceName(ItemStack stack) {
        if (stack.getItem() == Items.DIAMOND_HELMET || stack.getItem() == Items.GOLDEN_HELMET || stack.getItem() == Items.IRON_HELMET || stack.getItem() == Items.CHAINMAIL_HELMET || stack.getItem() == Items.LEATHER_HELMET) {
            return "helmet is";
        }
        if (stack.getItem() == Items.DIAMOND_CHESTPLATE || stack.getItem() == Items.GOLDEN_CHESTPLATE || stack.getItem() == Items.IRON_CHESTPLATE || stack.getItem() == Items.CHAINMAIL_CHESTPLATE || stack.getItem() == Items.LEATHER_CHESTPLATE) {
            return "chest is";
        }
        if (stack.getItem() == Items.DIAMOND_LEGGINGS || stack.getItem() == Items.GOLDEN_LEGGINGS || stack.getItem() == Items.IRON_LEGGINGS || stack.getItem() == Items.CHAINMAIL_LEGGINGS || stack.getItem() == Items.LEATHER_LEGGINGS) {
            return "leggings are";
        }
        return "boots are";
    }
}

