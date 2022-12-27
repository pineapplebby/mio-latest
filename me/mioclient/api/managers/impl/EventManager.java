/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.network.play.server.SPacketPlayerListItem
 *  net.minecraft.network.play.server.SPacketPlayerListItem$Action
 *  net.minecraft.network.play.server.SPacketPlayerListItem$AddPlayerData
 *  net.minecraft.network.play.server.SPacketTimeUpdate
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.ClientChatEvent
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Text
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.InputEvent$KeyInputEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientConnectedToServerEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientDisconnectionFromServerEvent
 *  org.lwjgl.input.Keyboard
 */
package me.mioclient.api.managers.impl;

import com.google.common.base.Strings;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.api.events.impl.ConnectionEvent;
import me.mioclient.api.events.impl.DeathEvent;
import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.events.impl.Render2DEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.events.impl.RenderFogColorEvent;
import me.mioclient.api.events.impl.TotemPopEvent;
import me.mioclient.api.events.impl.UpdateWalkingPlayerEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.math.Timer;
import me.mioclient.mod.Mod;
import me.mioclient.mod.commands.Command;
import me.mioclient.mod.modules.impl.client.HUD;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

public class EventManager
extends Mod {
    private final Timer logoutTimer = new Timer();
    private final AtomicBoolean tickOngoing = new AtomicBoolean(false);

    public void init() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public boolean ticksOngoing() {
        return this.tickOngoing.get();
    }

    public void onUnload() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!EventManager.fullNullCheck() && event.getEntity().getEntityWorld().isRemote && event.getEntityLiving().equals((Object)EventManager.mc.player)) {
            Managers.MODULES.onUpdate();
            Managers.MODULES.sortModules(HUD.getInstance().ordering.getValue());
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        this.logoutTimer.reset();
        Managers.MODULES.onLogin();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Managers.CONFIGS.saveConfig(Managers.CONFIGS.config.replaceFirst("hvhlegend/", ""));
        Managers.MODULES.onLogout();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (EventManager.fullNullCheck()) {
            return;
        }
        Managers.MODULES.onTick();
        for (EntityPlayer player : EventManager.mc.world.playerEntities) {
            if (player == null || player.getHealth() > 0.0f) continue;
            MinecraftForge.EVENT_BUS.post((Event)new DeathEvent(player));
            Managers.MODULES.onDeath(player);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (EventManager.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 0) {
            Managers.SPEED.updateValues();
            Managers.ROTATIONS.updateRotations();
            Managers.POSITION.updatePosition();
        }
        if (event.getStage() == 1) {
            Managers.ROTATIONS.resetRotations();
            Managers.POSITION.restorePosition();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketEntityStatus packet;
        if (event.getStage() != 0) {
            return;
        }
        Managers.SERVER.onPacketReceived();
        if (event.getPacket() instanceof SPacketEntityStatus && (packet = (SPacketEntityStatus)event.getPacket()).getOpCode() == 35 && packet.getEntity((World)EventManager.mc.world) instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)packet.getEntity((World)EventManager.mc.world);
            MinecraftForge.EVENT_BUS.post((Event)new TotemPopEvent(player));
            Managers.MODULES.onTotemPop(player);
        }
        if (event.getPacket() instanceof SPacketPlayerListItem && !EventManager.fullNullCheck() && this.logoutTimer.passedS(1.0)) {
            packet = (SPacketPlayerListItem)event.getPacket();
            if (SPacketPlayerListItem.Action.ADD_PLAYER != packet.getAction() && SPacketPlayerListItem.Action.REMOVE_PLAYER != packet.getAction()) {
                return;
            }
            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> !Strings.isNullOrEmpty((String)data.getProfile().getName()) || data.getProfile().getId() != null).forEach(arg_0 -> EventManager.lambda$onPacketReceive$1((SPacketPlayerListItem)packet, arg_0));
        }
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            Managers.SERVER.update();
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Managers.FPS.update();
        EventManager.mc.profiler.startSection("hvhlegend");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.shadeModel((int)7425);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth((float)1.0f);
        Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
        Managers.MODULES.onRender3D(render3dEvent);
        GlStateManager.glLineWidth((float)1.0f);
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableCull();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        EventManager.mc.profiler.endSection();
    }

    @SubscribeEvent
    public void renderHUD(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            Managers.TEXT.updateResolution();
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            ScaledResolution resolution = new ScaledResolution(mc);
            Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
            Managers.MODULES.onRender2D(render2DEvent);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            Managers.MODULES.onKeyInput(Keyboard.getEventKey());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                EventManager.mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                if (event.getMessage().length() > 1) {
                    Managers.COMMANDS.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    Command.sendMessage("Please enter a command.");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Command.sendMessage((Object)ChatFormatting.RED + "An error occurred while running this command. Check the log!");
            }
        }
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        RenderFogColorEvent fogColorEvent = new RenderFogColorEvent();
        MinecraftForge.EVENT_BUS.post((Event)fogColorEvent);
        if (fogColorEvent.isCanceled()) {
            event.setRed((float)fogColorEvent.getColor().getRed() / 255.0f);
            event.setGreen((float)fogColorEvent.getColor().getGreen() / 255.0f);
            event.setBlue((float)fogColorEvent.getColor().getBlue() / 255.0f);
        }
    }

    private static /* synthetic */ void lambda$onPacketReceive$1(SPacketPlayerListItem packet, SPacketPlayerListItem.AddPlayerData data) {
        UUID id = data.getProfile().getId();
        switch (packet.getAction()) {
            case ADD_PLAYER: {
                String name = data.getProfile().getName();
                MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(0, id, name));
                break;
            }
            case REMOVE_PLAYER: {
                EntityPlayer entity = EventManager.mc.world.getPlayerEntityByUUID(id);
                if (entity != null) {
                    String logoutName = entity.getName();
                    MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(1, entity, id, logoutName));
                    break;
                }
                MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(2, id, null));
            }
        }
    }
}

