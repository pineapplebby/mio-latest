/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.util.EnumHand
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package me.mioclient.mod.modules.impl.combat;

import java.awt.Color;
import java.util.Random;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.events.impl.UpdateWalkingPlayerEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.render.ColorUtil;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Aura
extends Module {
    public static Aura INSTANCE;
    private final Setting<Page> page = this.add(new Setting<Page>("Settings", Page.GLOBAL));
    private final Setting<TargetMode> targetMode = this.add(new Setting<TargetMode>("Filter", TargetMode.SMART, v -> this.page.getValue() == Page.GLOBAL));
    public final Setting<Float> range = this.add(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(7.0f), v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Float> wallRange = this.add(new Setting<Float>("WallRange", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(7.0f), v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", true, v -> this.page.getValue() == Page.GLOBAL).setParent());
    private final Setting<Boolean> lookBack = this.add(new Setting<Boolean>("LookBack", true, v -> this.page.getValue() == Page.GLOBAL && this.rotate.isOpen()));
    private final Setting<Float> yawStep = this.add(new Setting<Float>("YawStep", Float.valueOf(0.3f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.page.getValue() == Page.GLOBAL && this.rotate.isOpen()));
    private final Setting<Float> pitchAdd = this.add(new Setting<Float>("PitchAdd", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(25.0f), v -> this.page.getValue() == Page.GLOBAL && this.rotate.isOpen()));
    private final Setting<Boolean> randomPitch = this.add(new Setting<Boolean>("RandomizePitch", false, v -> this.page.getValue() == Page.GLOBAL && this.rotate.isOpen()));
    private final Setting<Float> amplitude = this.add(new Setting<Float>("Amplitude", Float.valueOf(3.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f), v -> this.page.getValue() == Page.GLOBAL && this.rotate.isOpen() && this.randomPitch.getValue() != false));
    private final Setting<Boolean> oneEight = this.add(new Setting<Boolean>("OneEight", false, v -> this.page.getValue() == Page.GLOBAL).setParent());
    private final Setting<Float> minCps = this.add(new Setting<Float>("MinCps", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(20.0f), v -> this.page.getValue() == Page.GLOBAL && this.oneEight.isOpen()));
    private final Setting<Float> maxCps = this.add(new Setting<Float>("MaxCps", Float.valueOf(9.0f), Float.valueOf(0.0f), Float.valueOf(20.0f), v -> this.page.getValue() == Page.GLOBAL && this.oneEight.isOpen()));
    private final Setting<Float> randomDelay = this.add(new Setting<Float>("RandomDelay", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> fovCheck = this.add(new Setting<Boolean>("FovCheck", false, v -> this.page.getValue() == Page.GLOBAL).setParent());
    private final Setting<Float> angle = this.add(new Setting<Float>("Angle", Float.valueOf(180.0f), Float.valueOf(0.0f), Float.valueOf(180.0f), v -> this.page.getValue() == Page.GLOBAL && this.fovCheck.isOpen()));
    private final Setting<Boolean> stopSprint = this.add(new Setting<Boolean>("StopSprint", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> armorBreak = this.add(new Setting<Boolean>("ArmorBreak", false, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> whileEating = this.add(new Setting<Boolean>("WhileEating", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> weaponOnly = this.add(new Setting<Boolean>("WeaponOnly", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> tpsSync = this.add(new Setting<Boolean>("TpsSync", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> packet = this.add(new Setting<Boolean>("Packet", false, v -> this.page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> swing = this.add(new Setting<Boolean>("Swing", true, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> sneak = this.add(new Setting<Boolean>("Sneak", false, v -> this.page.getValue() == Page.ADVANCED));
    private final Setting<RenderMode> render = this.add(new Setting<RenderMode>("Render", RenderMode.JELLO, v -> this.page.getValue() == Page.GLOBAL));
    private final Setting<Float> targetHealth = this.add(new Setting<Float>("Health", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.targetMode.getValue() == TargetMode.SMART && this.page.getValue() == Page.TARGETS));
    private final Setting<Boolean> players = this.add(new Setting<Boolean>("Players", true, v -> this.page.getValue() == Page.TARGETS));
    private final Setting<Boolean> animals = this.add(new Setting<Boolean>("Animals", false, v -> this.page.getValue() == Page.TARGETS));
    private final Setting<Boolean> neutrals = this.add(new Setting<Boolean>("Neutrals", false, v -> this.page.getValue() == Page.TARGETS));
    private final Setting<Boolean> others = this.add(new Setting<Boolean>("Others", false, v -> this.page.getValue() == Page.TARGETS));
    private final Setting<Boolean> projectiles = this.add(new Setting<Boolean>("Projectiles", false, v -> this.page.getValue() == Page.TARGETS));
    private final Setting<Boolean> hostiles = this.add(new Setting<Boolean>("Hostiles", true, v -> this.page.getValue() == Page.TARGETS).setParent());
    private final Setting<Boolean> onlyGhasts = this.add(new Setting<Boolean>("OnlyGhasts", false, v -> this.hostiles.isOpen() && this.page.getValue() == Page.TARGETS));
    private final Setting<Boolean> teleport = this.add(new Setting<Boolean>("Teleport", false, v -> this.page.getValue() == Page.ADVANCED).setParent());
    private final Setting<Float> teleportRange = this.add(new Setting<Float>("TpRange", Float.valueOf(15.0f), Float.valueOf(0.1f), Float.valueOf(50.0f), v -> this.teleport.isOpen() && this.page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> lagBack = this.add(new Setting<Boolean>("LagBack", true, v -> this.teleport.isOpen() && this.page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> delay32k = this.add(new Setting<Boolean>("32kDelay", false, v -> this.page.getValue() == Page.ADVANCED));
    private final Setting<Integer> time32k = this.add(new Setting<Integer>("32kTime", 5, 1, 50, v -> this.page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> multi32k = this.add(new Setting<Boolean>("Multi32k", false, v -> this.page.getValue() == Page.ADVANCED));
    private final Setting<Integer> packetAmount32k = this.add(new Setting<Integer>("32kPackets", 2, v -> this.delay32k.getValue() == false && this.page.getValue() == Page.ADVANCED));
    private final Timer timer = new Timer();
    protected static Entity target;

    public Aura() {
        super("Aura", "Attacks entities in radius.", Category.COMBAT, true);
        INSTANCE = this;
    }

    @Override
    public String getInfo() {
        String modeInfo = Managers.TEXT.normalizeCases((Object)this.targetMode.getValue());
        String targetInfo = target instanceof EntityPlayer ? ", " + target.getName() : "";
        return modeInfo + targetInfo;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (target != null) {
            if (this.render.getValue() == RenderMode.OLD) {
                RenderUtil.drawEntityBoxESP(target, Managers.COLORS.getCurrent(), true, new Color(255, 255, 255, 130), 0.7f, true, true, 35);
            } else if (this.render.getValue() == RenderMode.JELLO) {
                double everyTime = 1500.0;
                double drawTime = (double)System.currentTimeMillis() % everyTime;
                boolean drawMode = drawTime > everyTime / 2.0;
                double drawPercent = drawTime / (everyTime / 2.0);
                drawPercent = !drawMode ? 1.0 - drawPercent : (drawPercent -= 1.0);
                drawPercent = this.easeInOutQuad(drawPercent);
                Aura.mc.entityRenderer.disableLightmap();
                GL11.glPushMatrix();
                GL11.glDisable((int)3553);
                GL11.glBlendFunc((int)770, (int)771);
                GL11.glEnable((int)2848);
                GL11.glEnable((int)3042);
                GL11.glDisable((int)2929);
                GL11.glDisable((int)2884);
                GL11.glShadeModel((int)7425);
                Aura.mc.entityRenderer.disableLightmap();
                double radius = Aura.target.width;
                double height = (double)Aura.target.height + 0.1;
                double x = Aura.target.lastTickPosX + (Aura.target.posX - Aura.target.lastTickPosX) * (double)mc.getRenderPartialTicks() - Aura.mc.renderManager.viewerPosX;
                double y = Aura.target.lastTickPosY + (Aura.target.posY - Aura.target.lastTickPosY) * (double)mc.getRenderPartialTicks() - Aura.mc.renderManager.viewerPosY + height * drawPercent;
                double z = Aura.target.lastTickPosZ + (Aura.target.posZ - Aura.target.lastTickPosZ) * (double)mc.getRenderPartialTicks() - Aura.mc.renderManager.viewerPosZ;
                double eased = height / 3.0 * (drawPercent > 0.5 ? 1.0 - drawPercent : drawPercent) * (double)(drawMode ? -1 : 1);
                for (int segments = 0; segments < 360; segments += 5) {
                    Color color = Managers.COLORS.isRainbow() ? Managers.COLORS.getRainbow() : Managers.COLORS.getCurrent();
                    double x1 = x - Math.sin((double)segments * Math.PI / 180.0) * radius;
                    double z1 = z + Math.cos((double)segments * Math.PI / 180.0) * radius;
                    double x2 = x - Math.sin((double)(segments - 5) * Math.PI / 180.0) * radius;
                    double z2 = z + Math.cos((double)(segments - 5) * Math.PI / 180.0) * radius;
                    GL11.glBegin((int)7);
                    GL11.glColor4f((float)((float)ColorUtil.pulseColor(color, 200, 1).getRed() / 255.0f), (float)((float)ColorUtil.pulseColor(color, 200, 1).getGreen() / 255.0f), (float)((float)ColorUtil.pulseColor(color, 200, 1).getBlue() / 255.0f), (float)0.0f);
                    GL11.glVertex3d((double)x1, (double)(y + eased), (double)z1);
                    GL11.glVertex3d((double)x2, (double)(y + eased), (double)z2);
                    GL11.glColor4f((float)((float)ColorUtil.pulseColor(color, 200, 1).getRed() / 255.0f), (float)((float)ColorUtil.pulseColor(color, 200, 1).getGreen() / 255.0f), (float)((float)ColorUtil.pulseColor(color, 200, 1).getBlue() / 255.0f), (float)200.0f);
                    GL11.glVertex3d((double)x2, (double)y, (double)z2);
                    GL11.glVertex3d((double)x1, (double)y, (double)z1);
                    GL11.glEnd();
                    GL11.glBegin((int)2);
                    GL11.glVertex3d((double)x2, (double)y, (double)z2);
                    GL11.glVertex3d((double)x1, (double)y, (double)z1);
                    GL11.glEnd();
                }
                GL11.glEnable((int)2884);
                GL11.glShadeModel((int)7424);
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glEnable((int)2929);
                GL11.glDisable((int)2848);
                GL11.glDisable((int)3042);
                GL11.glEnable((int)3553);
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public void onTick() {
        if (!this.rotate.getValue().booleanValue()) {
            this.doAura();
        }
        if (this.maxCps.getValue().floatValue() < this.minCps.getValue().floatValue()) {
            this.maxCps.setValue(this.minCps.getValue());
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue() && target != null) {
            float[] angle = MathUtil.calcAngle(Aura.mc.player.getPositionEyes(mc.getRenderPartialTicks()), target.getPositionEyes(mc.getRenderPartialTicks()));
            float[] newAngle = Managers.ROTATIONS.injectYawStep(angle, this.yawStep.getValue().floatValue());
            Managers.ROTATIONS.setRotations(newAngle[0], newAngle[1] + this.pitchAdd.getValue().floatValue() + (this.randomPitch.getValue() != false ? (float)Math.random() * this.amplitude.getValue().floatValue() : 0.0f));
        }
        this.doAura();
    }

    private void doAura() {
        int wait;
        if (this.weaponOnly.getValue().booleanValue() && !EntityUtil.isHoldingWeapon((EntityPlayer)Aura.mc.player)) {
            target = null;
            return;
        }
        int n = this.oneEight.getValue() != false || EntityUtil.isHolding32k((EntityPlayer)Aura.mc.player) && this.delay32k.getValue() == false ? (int)(MathUtil.randomBetween(this.minCps.getValue().floatValue(), this.maxCps.getValue().floatValue()) - (float)new Random().nextInt(10) + (float)(new Random().nextInt(10) * 100) * (this.tpsSync.getValue() != false ? Managers.SERVER.getTpsFactor() : 1.0f)) : (wait = (int)((float)EntityUtil.getHitCoolDown((EntityPlayer)Aura.mc.player) + (float)Math.random() * this.randomDelay.getValue().floatValue() * 100.0f * (this.tpsSync.getValue() != false ? Managers.SERVER.getTpsFactor() : 1.0f)));
        if (!this.timer.passedMs(wait) || !this.whileEating.getValue().booleanValue() && Aura.mc.player.isHandActive() && (!Aura.mc.player.getHeldItemOffhand().getItem().equals((Object)Items.SHIELD) || Aura.mc.player.getActiveHand() != EnumHand.OFF_HAND)) {
            return;
        }
        if (this.targetMode.getValue() != TargetMode.FOCUS || target == null || Aura.mc.player.getDistanceSq(target) >= MathUtil.square(this.range.getValue().floatValue()) && (!this.teleport.getValue().booleanValue() || Aura.mc.player.getDistanceSq(target) >= MathUtil.square(this.teleportRange.getValue().floatValue())) || !Aura.mc.player.canEntityBeSeen(target) && !EntityUtil.isFeetVisible(target) && Aura.mc.player.getDistanceSq(target) >= MathUtil.square(this.wallRange.getValue().floatValue()) && !this.teleport.getValue().booleanValue()) {
            target = this.getTarget();
        }
        if (target == null) {
            return;
        }
        if (this.teleport.getValue().booleanValue()) {
            Managers.POSITION.setPositionPacket(Aura.target.posX, EntityUtil.isFeetVisible(target) ? Aura.target.posY : Aura.target.posY + (double)target.getEyeHeight(), Aura.target.posZ, true, true, this.lagBack.getValue() == false);
        }
        if (EntityUtil.isHolding32k((EntityPlayer)Aura.mc.player) && !this.delay32k.getValue().booleanValue()) {
            if (this.multi32k.getValue().booleanValue()) {
                for (EntityPlayer player : Aura.mc.world.playerEntities) {
                    if (!EntityUtil.isValid((Entity)player, this.range.getValue().floatValue())) continue;
                    this.teekayAttack((Entity)player);
                }
            } else {
                this.teekayAttack(target);
            }
            this.timer.reset();
            return;
        }
        if (this.armorBreak.getValue().booleanValue()) {
            Aura.mc.playerController.windowClick(Aura.mc.player.inventoryContainer.windowId, 9, Aura.mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)Aura.mc.player);
            Managers.INTERACTIONS.attackEntity(target, this.packet.getValue(), this.swing.getValue());
            Aura.mc.playerController.windowClick(Aura.mc.player.inventoryContainer.windowId, 9, Aura.mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)Aura.mc.player);
            Managers.INTERACTIONS.attackEntity(target, this.packet.getValue(), this.swing.getValue());
        } else {
            boolean sneaking = Aura.mc.player.isSneaking();
            boolean sprinting = Aura.mc.player.isSprinting();
            if (this.sneak.getValue().booleanValue()) {
                if (sneaking) {
                    Aura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Aura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (sprinting) {
                    Aura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Aura.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
            Managers.INTERACTIONS.attackEntity(target, this.packet.getValue(), this.swing.getValue());
            if (this.sneak.getValue().booleanValue()) {
                if (sprinting) {
                    Aura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Aura.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                if (sneaking) {
                    Aura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Aura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
            }
            if (this.stopSprint.getValue().booleanValue()) {
                Aura.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Aura.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
        }
        this.timer.reset();
        if (this.rotate.getValue().booleanValue() && this.lookBack.getValue().booleanValue()) {
            Managers.ROTATIONS.resetRotations();
        }
    }

    private void teekayAttack(Entity entity) {
        for (int i = 0; i < this.packetAmount32k.getValue(); ++i) {
            this.startEntityAttackThread(entity, i * this.time32k.getValue());
        }
    }

    private void startEntityAttackThread(Entity entity, int time) {
        new Thread(() -> {
            Timer timer = new Timer();
            timer.reset();
            try {
                Thread.sleep(time);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            Managers.INTERACTIONS.attackEntity(entity, true, this.swing.getValue());
        }).start();
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.teleport.getValue() != false ? (double)this.teleportRange.getValue().floatValue() : (double)this.range.getValue().floatValue();
        double maxHealth = 36.0;
        for (Entity entity : Aura.mc.world.loadedEntityList) {
            if (!(this.players.getValue() != false && entity instanceof EntityPlayer || this.animals.getValue() != false && EntityUtil.isPassive(entity) || this.neutrals.getValue() != false && EntityUtil.isNeutralMob(entity) || this.hostiles.getValue() != false && EntityUtil.isMobAggressive(entity) || this.hostiles.getValue() != false && this.onlyGhasts.getValue() != false && entity instanceof EntityGhast || this.others.getValue() != false && EntityUtil.isVehicle(entity)) && (!this.projectiles.getValue().booleanValue() || !EntityUtil.isProjectile(entity)) || EntityUtil.isLiving(entity) && !EntityUtil.isValid(entity, distance) || !this.teleport.getValue().booleanValue() && !Aura.mc.player.canEntityBeSeen(entity) && !EntityUtil.isFeetVisible(entity) && Aura.mc.player.getDistanceSq(entity) > MathUtil.square(this.wallRange.getValue().floatValue()) || this.fovCheck.getValue().booleanValue() && !this.isInFov(entity, this.angle.getValue().intValue())) continue;
            if (target == null) {
                target = entity;
                distance = Aura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof EntityPlayer && EntityUtil.isArmorLow((EntityPlayer)entity, 15)) {
                target = entity;
                break;
            }
            if (this.targetMode.getValue() == TargetMode.SMART && EntityUtil.getHealth(entity) < this.targetHealth.getValue().floatValue()) {
                target = entity;
                break;
            }
            if (this.targetMode.getValue() != TargetMode.HEALTH && Aura.mc.player.getDistanceSq(entity) < distance) {
                target = entity;
                distance = Aura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
            if (this.targetMode.getValue() != TargetMode.HEALTH || (double)EntityUtil.getHealth(entity) >= maxHealth) continue;
            target = entity;
            distance = Aura.mc.player.getDistanceSq(entity);
            maxHealth = EntityUtil.getHealth(entity);
        }
        return target;
    }

    private boolean isInFov(Entity entity, float angle) {
        double x = entity.posX - Aura.mc.player.posX;
        double z = entity.posZ - Aura.mc.player.posZ;
        double yaw = Math.atan2(x, z) * 57.29577951308232;
        yaw = -yaw;
        angle = (float)((double)angle * 0.5);
        double angleDifference = (((double)Aura.mc.player.rotationYaw - yaw) % 360.0 + 540.0) % 360.0 - 180.0;
        return angleDifference > 0.0 && angleDifference < (double)angle || (double)(-angle) < angleDifference && angleDifference < 0.0;
    }

    private double easeInOutQuad(double x) {
        return x < 0.5 ? 2.0 * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 2.0) / 2.0;
    }

    private static enum TargetMode {
        FOCUS,
        HEALTH,
        SMART;

    }

    private static enum RenderMode {
        OLD,
        JELLO,
        OFF;

    }

    private static enum Page {
        GLOBAL,
        TARGETS,
        ADVANCED;

    }
}

