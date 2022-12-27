/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.culling.ICamera
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.math.MathHelper
 *  org.lwjgl.opengl.GL11
 */
package me.mioclient.mod.modules.impl.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.math.InterpolationUtil;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.impl.client.FontMod;
import me.mioclient.mod.modules.impl.misc.PopNotify;
import me.mioclient.mod.modules.impl.render.Chams;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class NameTags
extends Module {
    public static NameTags INSTANCE;
    private final Setting<Page> page = this.add(new Setting<Page>("Settings", Page.GLOBAL));
    private final Setting<Boolean> armor = this.add(new Setting<Boolean>("Armor", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> enchant = this.add(new Setting<Boolean>("Enchants", true, v -> this.armor.getValue() != false && this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> reversed = this.add(new Setting<Boolean>("Reversed", false, v -> this.armor.getValue() != false && this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> durability = this.add(new Setting<Boolean>("Durability", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> health = this.add(new Setting<Boolean>("Health", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> gameMode = this.add(new Setting<Boolean>("Gamemode", false, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> ping = this.add(new Setting<Boolean>("Ping", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> item = this.add(new Setting<Boolean>("ItemName", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> invisibles = this.add(new Setting<Boolean>("Invisibles", true, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Boolean> pops = this.add(new Setting<Boolean>("Pops", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Float> scaleFactor = this.add(new Setting<Float>("Scale", Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(3.0f), v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> rect = this.add(new Setting<Boolean>("Rectangle", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> outline = this.add(new Setting<Boolean>("Outline", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Color> outlineColor = this.add(new Setting<Color>("Color", new Color(7105779), v -> this.page.getValue() == Page.COLORS).hideAlpha());
    private final Setting<Color> secondColor = this.add(new Setting<Color>("FadeColor", new Color(-1), v -> this.page.getValue() == Page.COLORS).injectBoolean(false).hideAlpha());
    public Setting<Boolean> outlineRainbow = this.add(new Setting<Boolean>("Rainbow", false, v -> this.page.getValue() == Page.COLORS));
    private final Setting<Color> textColor = this.add(new Setting<Color>("TextColor", new Color(-1), v -> this.page.getValue() == Page.COLORS).hideAlpha());
    private final Setting<Boolean> textRainbow = this.add(new Setting<Boolean>("TextRainbow", false, v -> this.page.getValue() == Page.COLORS));
    private final ICamera camera = new Frustum();
    private final Map glCapMap = new HashMap();
    boolean shownItem;
    public static HashMap<String, Integer> totemPops;

    public NameTags() {
        super("NameTags", "Advanced name tags for players.", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        totemPops.clear();
    }

    @Override
    public void onDeath(EntityPlayer player) {
        totemPops.remove(player.getName());
    }

    @Override
    public void onTotemPop(EntityPlayer player) {
        int popCount = 1;
        if (PopNotify.fullNullCheck() || NameTags.mc.player.equals((Object)player)) {
            return;
        }
        if (totemPops.containsKey(player.getName())) {
            popCount = totemPops.get(player.getName());
            totemPops.put(player.getName(), ++popCount);
        } else {
            totemPops.put(player.getName(), popCount);
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (NameTags.mc.player != null) {
            EntityPlayerSP renderPlayer = mc.getRenderViewEntity() == null ? NameTags.mc.player : (EntityPlayer)mc.getRenderViewEntity();
            double posX = InterpolationUtil.getInterpolatedDouble(renderPlayer.lastTickPosX, renderPlayer.posX, event.getPartialTicks());
            double posY = InterpolationUtil.getInterpolatedDouble(renderPlayer.lastTickPosY, renderPlayer.posY, event.getPartialTicks());
            double posZ = InterpolationUtil.getInterpolatedDouble(renderPlayer.lastTickPosZ, renderPlayer.posZ, event.getPartialTicks());
            this.camera.setPosition(posX, posY, posZ);
            ArrayList<Object> players = new ArrayList<Object>(NameTags.mc.world.playerEntities);
            players.sort(Comparator.comparing(arg_0 -> NameTags.lambda$onRender3D$18((EntityPlayer)renderPlayer, arg_0)).reversed());
            Iterator playerItr = players.iterator();
            while (true) {
                if (!playerItr.hasNext()) {
                    return;
                }
                EntityPlayer player = (EntityPlayer)playerItr.next();
                NetworkPlayerInfo info = NameTags.mc.player.connection.getPlayerInfo(player.getGameProfile().getId());
                if (!this.camera.isBoundingBoxInFrustum(player.getEntityBoundingBox()) && !this.camera.isBoundingBoxInFrustum(player.getEntityBoundingBox().offset(0.0, 2.0, 0.0)) || player == mc.getRenderViewEntity() || !player.isEntityAlive()) continue;
                double playerX = InterpolationUtil.getInterpolatedDouble(player.lastTickPosX, player.posX, NameTags.mc.timer.renderPartialTicks) - NameTags.mc.renderManager.renderPosX;
                double playerY = InterpolationUtil.getInterpolatedDouble(player.lastTickPosY, player.posY, NameTags.mc.timer.renderPartialTicks) - NameTags.mc.renderManager.renderPosY;
                double playerZ = InterpolationUtil.getInterpolatedDouble(player.lastTickPosZ, player.posZ, NameTags.mc.timer.renderPartialTicks) - NameTags.mc.renderManager.renderPosZ;
                if (info != null && this.getGameModeShort(info.getGameType().getName()).equalsIgnoreCase("SP") && !this.invisibles.getValue().booleanValue() || player.getName().startsWith("Body #")) continue;
                this.renderNameTag(player, playerX, playerY, playerZ);
            }
        }
    }

    public void renderNameTag(EntityPlayer player, double x, double y, double z) {
        String totemPopKey;
        GL11.glEnable((int)3553);
        this.shownItem = false;
        GlStateManager.pushMatrix();
        NetworkPlayerInfo info = NameTags.mc.player.connection.getPlayerInfo(player.getGameProfile().getId());
        boolean isFriend = Managers.FRIENDS.isFriend(player.getName());
        boolean cFont = FontMod.INSTANCE.isOn();
        StringBuilder preNameTag = new StringBuilder().append(isFriend ? "\u00a7" + (isFriend ? "b" : "c") : (player.isSneaking() ? "\u00a75" : "\u00a7r")).append(this.getName(player)).append(this.gameMode.getValue() != false && info != null ? " [" + this.getGameModeShort(info.getGameType().getName()) + "]" : "").append(this.ping.getValue() != false && info != null ? " " + info.getResponseTime() + "ms" : "").append(this.health.getValue() != false ? " \u00a7" + this.getHealthColor(player.getHealth() + player.getAbsorptionAmount()) + MathHelper.ceil((float)(player.getHealth() + player.getAbsorptionAmount())) : "");
        if (Chams.INSTANCE.isOn() && Chams.INSTANCE.sneak.getValue().booleanValue()) {
            preNameTag = new StringBuilder().append(isFriend ? "\u00a7" + (isFriend ? "b" : "c") : "\u00a7r").append(this.getName(player)).append(this.gameMode.getValue() != false && info != null ? " [" + this.getGameModeShort(info.getGameType().getName()) + "]" : "").append(this.ping.getValue() != false && info != null ? " " + info.getResponseTime() + "ms" : "").append(this.health.getValue() != false ? " \u00a7" + this.getHealthColor(player.getHealth() + player.getAbsorptionAmount()) + MathHelper.ceil((float)(player.getHealth() + player.getAbsorptionAmount())) : "");
        }
        if (totemPops.get(player.getName()) != null && this.pops.getValue().booleanValue()) {
            StringBuilder totemPopKeyAppended = new StringBuilder().append(" ").append((Object)ChatFormatting.DARK_RED).append("-");
            totemPopKey = totemPopKeyAppended.append(totemPops.get(player.getName())).toString();
        } else {
            totemPopKey = "";
        }
        String postNameTag = preNameTag.append(totemPopKey).toString();
        postNameTag = (Managers.FRIENDS.isCool(player.getName()) ? (Object)ChatFormatting.GOLD + "< > " + (Object)ChatFormatting.RESET : "") + postNameTag.replace(".0", "");
        EntityPlayerSP renderPlayer = mc.getRenderViewEntity() == null ? NameTags.mc.player : (EntityPlayer)mc.getRenderViewEntity();
        float distance = renderPlayer.getDistance((Entity)player);
        float scale = (distance / 5.0f <= 2.0f ? 2.0f : distance / 5.0f * (4.1f * this.scaleFactor.getValue().floatValue() / 100.0f * 10.0f + 1.0f)) * 2.5f * (4.1f * this.scaleFactor.getValue().floatValue() / 100.0f / 10.0f);
        if ((double)distance <= 8.0) {
            scale = 0.0245f;
        }
        GL11.glTranslated((double)((float)x), (double)((double)((float)y + 2.45f) - (player.isSneaking() ? 0.4 : 0.0) + (distance / 5.0f > 2.0f ? (double)(distance / 12.0f) - 0.7 : 0.0)), (double)((float)z));
        GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-NameTags.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        float var26 = NameTags.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        GL11.glRotatef((float)NameTags.mc.getRenderManager().playerViewX, (float)var26, (float)0.0f, (float)0.0f);
        GL11.glScalef((float)(-scale), (float)(-scale), (float)scale);
        this.disableGlCap(2896, 2929);
        this.enableGlCap(3042);
        GL11.glBlendFunc((int)770, (int)771);
        int width = Managers.TEXT.getStringWidth(postNameTag) / 2 + 1;
        int outlineColor = isFriend ? new Color(0, 213, 255, 255).getRGB() : new Color(this.outlineColor.getValue().getRed(), this.outlineColor.getValue().getGreen(), this.outlineColor.getValue().getBlue(), 255).getRGB();
        int fadeOutlineColor = new Color(this.secondColor.getValue().getRed(), this.secondColor.getValue().getGreen(), this.secondColor.getValue().getBlue(), 255).getRGB();
        GlStateManager.enableTexture2D();
        if (player.isSneaking()) {
            if (Chams.INSTANCE.isOn() && Chams.INSTANCE.sneak.getValue().booleanValue()) {
                outlineColor = isFriend ? new Color(0, 213, 255, 255).getRGB() : new Color(this.outlineColor.getValue().getRed(), this.outlineColor.getValue().getGreen(), this.outlineColor.getValue().getBlue(), 255).getRGB();
            } else {
                int n = outlineColor = isFriend ? new Color(0, 213, 255, 255).getRGB() : new Color(170, 0, 170, 255).getRGB();
            }
        }
        if (this.rect.getValue().booleanValue()) {
            Gui.drawRect((int)(-width - 1), (int)8, (int)(width + 1), (int)19, (int)ColorUtil.toRGBA(0, 0, 0, 120));
        }
        if (this.outline.getValue().booleanValue()) {
            RenderUtil.drawNameTagOutline(-width - 1, 8.0f, width + 1, 19.0f, 1.0f, outlineColor, this.secondColor.booleanValue ? fadeOutlineColor : outlineColor, !isFriend && this.outlineRainbow.getValue() != false);
        }
        int textColor = this.textRainbow.getValue() != false ? Managers.COLORS.getRainbow().getRGB() : new Color(this.textColor.getValue().getRed(), this.textColor.getValue().getGreen(), this.textColor.getValue().getBlue()).getRGB();
        Managers.TEXT.drawStringWithShadow(postNameTag, -width, cFont ? 8.65f : 9.2f, textColor);
        if (this.armor.getValue().booleanValue()) {
            ItemStack renderOffhand2;
            int xOffset = -8;
            Item mainhand = player.getHeldItemMainhand().getItem();
            Item offhand = player.getHeldItemOffhand().getItem();
            if (mainhand != Items.AIR && offhand == Items.AIR) {
                xOffset = -16;
            } else if (mainhand == Items.AIR && offhand != Items.AIR) {
                xOffset = 0;
            }
            int index = 0;
            for (ItemStack renderOffhand2 : player.inventory.armorInventory) {
                if (renderOffhand2 == null) continue;
                xOffset -= 8;
                if (renderOffhand2.getItem() == Items.AIR) continue;
                ++index;
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) {
                ++index;
            }
            int cacheX = xOffset - 8;
            xOffset += 8 * (5 - index) - (index == 0 ? 4 : 0);
            if (!(this.reversed.getValue() == false ? player.getHeldItemMainhand().getItem() != Items.AIR : player.getHeldItemOffhand().getItem() != Items.AIR)) {
                if (!this.reversed.getValue().booleanValue()) {
                    this.shownItem = true;
                }
            } else {
                xOffset -= 10;
                if (this.reversed.getValue().booleanValue()) {
                    renderOffhand2 = player.getHeldItemOffhand().copy();
                    this.renderItem(player, renderOffhand2, xOffset, 7, cacheX, false);
                } else {
                    renderOffhand2 = player.getHeldItemMainhand().copy();
                    this.renderItem(player, renderOffhand2, xOffset, 7, cacheX, true);
                }
                xOffset += 18;
            }
            if (this.reversed.getValue().booleanValue()) {
                for (index = 0; index <= 3; ++index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.AIR) continue;
                    ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(player, renderStack2, xOffset, 7, cacheX, false);
                    xOffset += 16;
                }
            } else {
                for (index = 3; index >= 0; --index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.AIR) continue;
                    ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(player, renderStack2, xOffset, 7, cacheX, false);
                    xOffset += 16;
                }
            }
            if (!(this.reversed.getValue() != false ? player.getHeldItemMainhand().getItem() == Items.AIR : player.getHeldItemOffhand().getItem() == Items.AIR)) {
                xOffset += 0;
                if (this.reversed.getValue().booleanValue()) {
                    renderOffhand2 = player.getHeldItemMainhand().copy();
                    this.renderItem(player, renderOffhand2, xOffset, 7, cacheX, true);
                } else {
                    renderOffhand2 = player.getHeldItemOffhand().copy();
                    this.renderItem(player, renderOffhand2, xOffset, 7, cacheX, false);
                }
                xOffset += 8;
            }
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
        } else if (this.durability.getValue().booleanValue()) {
            int xOffset = 0;
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack == null) continue;
                xOffset -= 8;
                if (armourStack.getItem() == Items.AIR) continue;
                ++count;
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) {
                ++count;
            }
            int cacheX = xOffset - 8;
            xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);
            if (this.reversed.getValue().booleanValue()) {
                for (int index = 0; index <= 3; ++index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.AIR) continue;
                    ItemStack renderOffhand = armourStack2.copy();
                    this.renderDurability(player, renderOffhand, xOffset, 12);
                    xOffset += 16;
                }
            } else {
                for (int index = 3; index >= 0; --index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.AIR) continue;
                    ItemStack renderOffhand = armourStack2.copy();
                    this.renderDurability(player, renderOffhand, xOffset, 12);
                    xOffset += 16;
                }
            }
            GL11.glDisable((int)2848);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
        }
        GlStateManager.resetColor();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

    public void renderItem(EntityPlayer player, ItemStack stack, int x, int y, int nameX, boolean showHeldItemText) {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.clear((int)256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        NameTags.mc.getRenderItem().zLevel = -100.0f;
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y / 2 - 12);
        if (this.durability.getValue().booleanValue()) {
            mc.getRenderItem().renderItemOverlays(NameTags.mc.fontRenderer, stack, x, y / 2 - 12);
        }
        NameTags.mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.disableDepth();
        this.renderEnchant(player, stack, x, y - 18);
        if (!this.shownItem && this.item.getValue().booleanValue() && showHeldItemText) {
            Managers.TEXT.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - Managers.TEXT.getStringWidth(stack.getDisplayName()) / 2, y - 37, Color.GRAY.getRGB());
            this.shownItem = true;
        }
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    private void renderDurability(EntityPlayer player, ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.clear((int)256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)0.01f);
        GlStateManager.scale((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.disableDepth();
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
            float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
            float red = 1.0f - green;
            int damage = 100 - (int)(red * 100.0f);
            Managers.TEXT.drawStringWithShadow(damage + "%", x * 2 + 4, y - 10, ColorUtil.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.scale((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    public void renderEnchant(EntityPlayer player, ItemStack stack, int x, int y) {
        NBTTagList enchants;
        int yCount = y;
        if ((stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) && this.durability.getValue().booleanValue()) {
            float green = ((float)stack.getMaxDamage() - (float)stack.getItemDamage()) / (float)stack.getMaxDamage();
            float red = 1.0f - green;
            int damage = 100 - (int)(red * 100.0f);
            Managers.TEXT.drawStringWithShadow(damage + "%", x * 2 + 4, y - 10, ColorUtil.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        if (this.enchant.getValue().booleanValue() && (enchants = stack.getEnchantmentTagList()) != null) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment ench = Enchantment.getEnchantmentByID((int)id);
                if (ench == null || ench.isCurse()) continue;
                String enchName = level == 1 ? ench.getTranslatedName((int)level).substring(0, 3).toLowerCase() : ench.getTranslatedName((int)level).substring(0, 2).toLowerCase() + level;
                if (!(enchName = enchName.substring(0, 1).toUpperCase() + enchName.substring(1)).contains("Pr") && !enchName.contains("Bl")) continue;
                GL11.glPushMatrix();
                GL11.glScalef((float)1.0f, (float)1.0f, (float)0.0f);
                Managers.TEXT.drawStringWithShadow(enchName, x * 2 + 3, yCount, -1);
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glPopMatrix();
                yCount += 8;
            }
        }
    }

    public String getGameModeShort(String gameType) {
        if (gameType.equalsIgnoreCase("survival")) {
            return "S";
        }
        if (gameType.equalsIgnoreCase("creative")) {
            return "C";
        }
        if (gameType.equalsIgnoreCase("adventure")) {
            return "A";
        }
        return gameType.equalsIgnoreCase("spectator") ? "SP" : "NONE";
    }

    public String getHealthColor(float health) {
        if (health > 18.0f) {
            return "a";
        }
        if (health > 16.0f) {
            return "2";
        }
        if (health > 12.0f) {
            return "e";
        }
        if (health > 8.0f) {
            return "6";
        }
        return health > 5.0f ? "c" : "4";
    }

    private String getName(EntityPlayer player) {
        return player.getName();
    }

    public void enableGlCap(int cap) {
        this.setGlCap(cap, true);
    }

    public void disableGlCap(int ... caps) {
        int[] var2 = caps;
        int var3 = caps.length;
        for (int var4 = 0; var4 < var3; ++var4) {
            int cap = var2[var4];
            this.setGlCap(cap, false);
        }
    }

    public void setGlCap(int cap, boolean state) {
        this.glCapMap.put(cap, GL11.glGetBoolean((int)cap));
        this.setGlState(cap, state);
    }

    public void setGlState(int cap, boolean state) {
        if (state) {
            GL11.glEnable((int)cap);
        } else {
            GL11.glDisable((int)cap);
        }
    }

    private static /* synthetic */ Float lambda$onRender3D$18(EntityPlayer renderPlayer, Object entityPlayer) {
        return Float.valueOf(renderPlayer.getDistance((Entity)entityPlayer));
    }

    static {
        totemPops = new HashMap();
    }

    private static enum Page {
        GLOBAL,
        COLORS;

    }
}

