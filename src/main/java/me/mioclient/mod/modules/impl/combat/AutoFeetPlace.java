/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityExpBottle
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.network.play.server.SPacketMultiBlockChange
 *  net.minecraft.network.play.server.SPacketMultiBlockChange$BlockUpdateData
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.events.impl.StepEvent;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.entity.EntityUtil;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.math.Timer;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFeetPlace
extends Module {
    private final Setting<Timing> timing = this.add(new Setting<Timing>("Timing", Timing.VANILLA));
    private final Setting<Integer> tickDelay = this.add(new Setting<Integer>("TickDelay", 50, 0, 250));
    private final Setting<Integer> blocksPerTick = this.add(new Setting<Integer>("BPT", 2, 1, 30));
    private final Setting<Boolean> rotate = this.add(new Setting<Boolean>("Rotate", true));
    private final Setting<Boolean> packet = this.add(new Setting<Boolean>("Packet", true));
    private final Setting<Boolean> noLag = this.add(new Setting<Boolean>("NoLag", true));
    private final Setting<Boolean> clean = this.add(new Setting<Boolean>("Clean", true));
    private final Setting<Boolean> jumpDisable = this.add(new Setting<Boolean>("JumpDisable", true));
    private final Setting<Swap> swap = this.add(new Setting<Swap>("Swap", Swap.NORMAL));
    private final Setting<Center> center = this.add(new Setting<Center>("Center", Center.NONE));
    private final Setting<Boolean> render = this.add(new Setting<Boolean>("Render", true).setParent());
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true, v -> this.render.isOpen()));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true, v -> this.render.isOpen()));
    private final Map<BlockPos, Long> renderBlocks = new ConcurrentHashMap<BlockPos, Long>();
    private final List<BlockPos> activeBlocks = new ArrayList<BlockPos>();
    private final Timer renderTimer = new Timer();
    private final Timer lagTimer = new Timer();
    private final Timer delayTimer = new Timer();
    private final Timer hitTimer = new Timer();
    private boolean isSneaking;
    private EntityPlayer interceptedBy;

    public AutoFeetPlace() {
        super("AutoFeetPlace", "Surrounds your feet with obby.", Category.COMBAT, true);
    }

    @Override
    public String getInfo() {
        return String.valueOf(this.renderBlocks.size());
    }

    @Override
    public void onEnable() {
        if (AutoFeetPlace.fullNullCheck() || AutoFeetPlace.nullCheck()) {
            return;
        }
        this.interceptedBy = null;
        if (this.center.getValue() != Center.NONE) {
            double centerX = Math.floor(AutoFeetPlace.mc.player.posX) + 0.5;
            double centerZ = Math.floor(AutoFeetPlace.mc.player.posZ) + 0.5;
            switch (this.center.getValue()) {
                default: {
                    break;
                }
                case MOTION: {
                    AutoFeetPlace.mc.player.motionX = (centerX - AutoFeetPlace.mc.player.posX) / 2.0;
                    AutoFeetPlace.mc.player.motionZ = (centerZ - AutoFeetPlace.mc.player.posZ) / 2.0;
                    break;
                }
                case TELEPORT: {
                    AutoFeetPlace.mc.player.setPosition(centerX, AutoFeetPlace.mc.player.posY, centerZ);
                    AutoFeetPlace.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(centerX, AutoFeetPlace.mc.player.posY, centerZ, AutoFeetPlace.mc.player.onGround));
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (AutoFeetPlace.nullCheck()) {
            return;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }

    @Override
    public void onUpdate() {
        if (!AutoFeetPlace.fullNullCheck()) {
            this.doFeetPlace();
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.render.getValue().booleanValue()) {
            this.renderTimer.reset();
            this.renderBlocks.forEach((pos, time) -> {
                int lineA = 255;
                int fillA = 80;
                if (System.currentTimeMillis() - time > 400L) {
                    this.renderTimer.reset();
                    this.renderBlocks.remove(pos);
                } else {
                    long endTime = System.currentTimeMillis() - time - 100L;
                    double normal = MathUtil.normalize(endTime, 0.0, 500.0);
                    normal = MathHelper.clamp((double)normal, (double)0.0, (double)1.0);
                    normal = -normal + 1.0;
                    int firstAl = (int)(normal * (double)lineA);
                    int secondAl = (int)(normal * (double)fillA);
                    RenderUtil.drawBoxESP(new BlockPos((Vec3i)pos), Managers.COLORS.getCurrent(), true, new Color(255, 255, 255, firstAl), 0.7f, this.line.getValue(), this.box.getValue(), secondAl, true, 0.0);
                }
            });
        }
    }

    @SubscribeEvent
    public void onStep(StepEvent event) {
        if (event.getStage() == 0 && this.jumpDisable.getValue().booleanValue() && AutoFeetPlace.mc.player.stepHeight > 1.0f) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSoundEffect packet;
        if (AutoFeetPlace.fullNullCheck() || AutoFeetPlace.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.lagTimer.reset();
        }
        if (event.getPacket() instanceof SPacketBlockChange) {
            if (this.renderBlocks.containsKey((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition())) {
                this.renderTimer.reset();
                if (((SPacketBlockChange)event.getPacket()).getBlockState().getBlock() != Blocks.AIR && this.renderTimer.passedMs(400L)) {
                    this.renderBlocks.remove((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition());
                }
            }
            if (this.timing.getValue() == Timing.SEQUENTIAL) {
                if (!this.lagTimer.passedMs(500L) && this.noLag.getValue().booleanValue()) {
                    return;
                }
                SPacketMultiBlockChange.BlockUpdateData[] changePos = ((SPacketBlockChange)event.getPacket()).getBlockPosition();
                if (((SPacketBlockChange)event.getPacket()).getBlockState().getMaterial().isReplaceable() && AutoFeetPlace.mc.world.getEntitiesWithinAABB(EntityPlayerSP.class, new AxisAlignedBB((BlockPos)changePos)).isEmpty() && this.getOffsets().contains(changePos)) {
                    this.activeBlocks.clear();
                    int oldSlot = AutoFeetPlace.mc.player.inventory.currentItem;
                    int blockSlot = this.getSlot();
                    this.activeBlocks.add((BlockPos)changePos);
                    this.renderBlocks.put((BlockPos)changePos, System.currentTimeMillis());
                    this.placeBlock((BlockPos)changePos, blockSlot, oldSlot);
                }
            }
            if (event.getPacket() instanceof SPacketMultiBlockChange && this.timing.getValue() == Timing.SEQUENTIAL) {
                if (!this.lagTimer.passedMs(500L) && this.noLag.getValue().booleanValue()) {
                    return;
                }
                for (SPacketMultiBlockChange.BlockUpdateData blockUpdateData : ((SPacketMultiBlockChange)event.getPacket()).getChangedBlocks()) {
                    BlockPos changePos = blockUpdateData.getPos();
                    if (!blockUpdateData.getBlockState().getMaterial().isReplaceable() || !this.getOffsets().contains((Object)changePos)) continue;
                    this.activeBlocks.clear();
                    int oldSlot = AutoFeetPlace.mc.player.inventory.currentItem;
                    int blockSlot = this.getSlot();
                    this.activeBlocks.add(changePos);
                    this.renderBlocks.put(changePos, System.currentTimeMillis());
                    this.placeBlock(changePos, blockSlot, oldSlot);
                }
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect && this.timing.getValue() == Timing.SEQUENTIAL && this.clean.getValue().booleanValue() && (packet = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            List<BlockPos> offsets = this.getOffsets();
            for (BlockPos pos : offsets) {
                for (EntityEnderCrystal crystal : AutoFeetPlace.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos))) {
                    if (!offsets.contains((Object)crystal.getPosition())) continue;
                    crystal.setDead();
                }
            }
        }
        if (event.getPacket() instanceof SPacketSpawnObject && this.timing.getValue() == Timing.SEQUENTIAL && this.clean.getValue().booleanValue()) {
            packet = (SPacketSpawnObject)event.getPacket();
            List<BlockPos> offsets = this.getOffsets();
            block3: for (BlockPos pos : offsets) {
                for (EntityEnderCrystal crystal : AutoFeetPlace.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos))) {
                    if (packet.getEntityID() != crystal.getEntityId() || !this.hitTimer.passedMs(150L)) continue;
                    AutoFeetPlace.mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)crystal));
                    AutoFeetPlace.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                    this.hitTimer.reset();
                    continue block3;
                }
            }
        }
    }

    private void doFeetPlace() {
        if (AutoFeetPlace.mc.player.motionY > 0.0 && this.jumpDisable.getValue().booleanValue()) {
            this.disable();
            return;
        }
        if (!this.lagTimer.passedMs(500L) && this.noLag.getValue().booleanValue()) {
            return;
        }
        this.interceptedBy = null;
        if (this.delayTimer.passedMs(this.tickDelay.getValue().intValue())) {
            this.activeBlocks.clear();
            int oldSlot = AutoFeetPlace.mc.player.inventory.currentItem;
            int blockSlot = this.getSlot();
            if (blockSlot == -1) {
                this.disable();
                return;
            }
            int blocksInTick = 0;
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            for (int i = 0; i < 1; ++i) {
                List<BlockPos> offsets = this.getOffsets();
                for (BlockPos pos : offsets) {
                    if (blocksInTick > this.blocksPerTick.getValue() || !this.isPlaceable(pos)) continue;
                    this.activeBlocks.add(pos);
                    boolean intercepted = false;
                    for (Entity entity : AutoFeetPlace.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                        if (entity instanceof EntityEnderCrystal && this.clean.getValue().booleanValue() && this.hitTimer.passedMs(150L)) {
                            AutoFeetPlace.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
                            AutoFeetPlace.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                            this.hitTimer.reset();
                            break;
                        }
                        if (!(entity instanceof EntityPlayer) || entity == AutoFeetPlace.mc.player) continue;
                        this.interceptedBy = (EntityPlayer)entity;
                        intercepted = true;
                    }
                    if (intercepted) continue;
                    this.renderBlocks.put(pos, System.currentTimeMillis());
                    this.placeBlock(pos, blockSlot, oldSlot);
                    ++blocksInTick;
                }
                if (this.interceptedBy == null) continue;
                List<BlockPos> enemyOffsets = this.getEnemyOffsets(this.interceptedBy);
                int maxStep = enemyOffsets.size();
                int offsetStep = 0;
                while (blocksInTick <= this.blocksPerTick.getValue() && offsetStep < maxStep) {
                    BlockPos newPos = enemyOffsets.get(offsetStep++);
                    boolean foundSomeone = false;
                    for (Object entity : AutoFeetPlace.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(newPos))) {
                        if (!(entity instanceof EntityPlayer)) continue;
                        foundSomeone = true;
                        break;
                    }
                    if (foundSomeone || !AutoFeetPlace.mc.world.getBlockState(newPos).getMaterial().isReplaceable() || !this.isPlaceable(newPos)) continue;
                    this.activeBlocks.add(newPos);
                    boolean interceptedByCrystal = false;
                    for (EntityEnderCrystal crystal : AutoFeetPlace.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(newPos))) {
                        interceptedByCrystal = true;
                        if (!this.hitTimer.passedMs(150L) || !this.clean.getValue().booleanValue()) continue;
                        AutoFeetPlace.mc.player.connection.sendPacket((Packet)new CPacketUseEntity((Entity)crystal));
                        AutoFeetPlace.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                        this.hitTimer.reset();
                        break;
                    }
                    if (interceptedByCrystal) continue;
                    this.renderBlocks.put(newPos, System.currentTimeMillis());
                    this.placeBlock(newPos, blockSlot, oldSlot);
                    ++blocksInTick;
                }
            }
            this.delayTimer.reset();
        }
    }

    private void doSwap(int slot) {
        if (this.swap.getValue() == Swap.NORMAL) {
            AutoFeetPlace.mc.player.inventory.currentItem = slot;
        } else {
            mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(slot));
        }
        AutoFeetPlace.mc.playerController.updateController();
    }

    private void placeBlock(BlockPos pos, int blockSlot, int oldSlot) {
        if (BlockUtil.checkForEntities(pos) || blockSlot == -1) {
            return;
        }
        this.doSwap(blockSlot);
        Managers.INTERACTIONS.placeBlock(pos, this.rotate.getValue(), this.packet.getValue(), this.clean.getValue());
        this.doSwap(oldSlot);
    }

    private int getSlot() {
        int slot = -1;
        slot = this.getHotbarItemSlot(Item.getItemFromBlock((Block)Blocks.OBSIDIAN));
        if (slot == -1) {
            slot = this.getHotbarItemSlot(Item.getItemFromBlock((Block)Blocks.ENDER_CHEST));
        }
        return slot;
    }

    private int getHotbarItemSlot(Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (!AutoFeetPlace.mc.player.inventory.getStackInSlot(i).getItem().equals((Object)item)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    private boolean isInterceptedByOther(BlockPos pos) {
        for (Entity entity : AutoFeetPlace.mc.world.loadedEntityList) {
            if (entity instanceof EntityOtherPlayerMP || entity instanceof EntityItem || entity instanceof EntityEnderCrystal || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityArrow || !new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) continue;
            return true;
        }
        return false;
    }

    private boolean isPlaceable(BlockPos pos) {
        boolean placeable = AutoFeetPlace.mc.world.getBlockState(pos).getMaterial().isReplaceable();
        if (this.isInterceptedByOther(pos)) {
            placeable = false;
        }
        return placeable;
    }

    private List<BlockPos> getOffsets() {
        int z;
        int x;
        double calcPosX = AutoFeetPlace.mc.player.posX;
        double calcPosZ = AutoFeetPlace.mc.player.posZ;
        BlockPos playerPos = this.getPlayerPos();
        ArrayList<BlockPos> offsets = new ArrayList<BlockPos>();
        double decimalX = Math.abs(calcPosX) - Math.floor(Math.abs(calcPosX));
        double decimalZ = Math.abs(calcPosZ) - Math.floor(Math.abs(calcPosZ));
        int lengthXPos = this.calcLength(decimalX, false);
        int lengthXNeg = this.calcLength(decimalX, true);
        int lengthZPos = this.calcLength(decimalZ, false);
        int lengthZNeg = this.calcLength(decimalZ, true);
        ArrayList<BlockPos> tempOffsets = new ArrayList<BlockPos>();
        offsets.addAll(this.getOverlapPos());
        for (x = 1; x < lengthXPos + 1; ++x) {
            tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, 1 + lengthZPos));
            tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, -(1 + lengthZNeg)));
        }
        for (x = 0; x <= lengthXNeg; ++x) {
            tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, 1 + lengthZPos));
            tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, -(1 + lengthZNeg)));
        }
        for (z = 1; z < lengthZPos + 1; ++z) {
            tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, z));
            tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, z));
        }
        for (z = 0; z <= lengthZNeg; ++z) {
            tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, -z));
            tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, -z));
        }
        for (BlockPos pos2 : tempOffsets) {
            if (!this.isSurrounded(pos2)) {
                offsets.add(pos2.add(0, -1, 0));
            }
            offsets.add(pos2);
        }
        return offsets;
    }

    private List<BlockPos> getOverlapPos() {
        double calcPosX = AutoFeetPlace.mc.player.posX;
        double calcPosZ = AutoFeetPlace.mc.player.posZ;
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        double decimalX = calcPosX - Math.floor(calcPosX);
        double decimalZ = calcPosZ - Math.floor(calcPosZ);
        int offX = this.calcOffset(decimalX);
        int offZ = this.calcOffset(decimalZ);
        positions.add(this.getPlayerPos());
        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;
                positions.add(this.getPlayerPos().add(properX, -1, properZ));
            }
        }
        return positions;
    }

    private BlockPos getPlayerPos() {
        double calcPosX = AutoFeetPlace.mc.player.posX;
        double calcPosY = AutoFeetPlace.mc.player.posY;
        double calcPosZ = AutoFeetPlace.mc.player.posZ;
        double decimalPoint = calcPosY - Math.floor(calcPosY);
        return new BlockPos(calcPosX, decimalPoint > 0.8 ? Math.floor(calcPosY) + 1.0 : Math.floor(calcPosY), calcPosZ);
    }

    private List<BlockPos> getEnemyOffsets(EntityPlayer e) {
        int z;
        int x;
        if (e == AutoFeetPlace.mc.player) {
            return null;
        }
        BlockPos playerPos = this.getEnemyPos(e);
        ArrayList<BlockPos> offsets = new ArrayList<BlockPos>();
        double decimalX = Math.abs(e.posX) - Math.floor(Math.abs(e.posX));
        double decimalZ = Math.abs(e.posZ) - Math.floor(Math.abs(e.posZ));
        int lengthXPos = this.calcLength(decimalX, false);
        int lengthXNeg = this.calcLength(decimalX, true);
        int lengthZPos = this.calcLength(decimalZ, false);
        int lengthZNeg = this.calcLength(decimalZ, true);
        ArrayList<BlockPos> tempOffsets = new ArrayList<BlockPos>();
        offsets.addAll(this.getEnemyOverlapPos(e));
        for (x = 1; x < lengthXPos + 1; ++x) {
            tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, 1 + lengthZPos));
            tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, -(1 + lengthZNeg)));
        }
        for (x = 0; x <= lengthXNeg; ++x) {
            tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, 1 + lengthZPos));
            tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, -(1 + lengthZNeg)));
        }
        for (z = 1; z < lengthZPos + 1; ++z) {
            tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, z));
            tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, z));
        }
        for (z = 0; z <= lengthZNeg; ++z) {
            tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, -z));
            tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, -z));
        }
        offsets.addAll(tempOffsets);
        return offsets;
    }

    private List<BlockPos> getEnemyOverlapPos(EntityPlayer e) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        double decimalX = e.posX - Math.floor(e.posX);
        double decimalZ = e.posZ - Math.floor(e.posZ);
        int offX = this.calcOffset(decimalX);
        int offZ = this.calcOffset(decimalZ);
        positions.add(this.getEnemyPos(e));
        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;
                positions.add(this.getEnemyPos(e).add(properX, -1, properZ));
            }
        }
        return positions;
    }

    private BlockPos getEnemyPos(EntityPlayer e) {
        double decimalPoint = AutoFeetPlace.mc.player.posY - Math.floor(AutoFeetPlace.mc.player.posY);
        return new BlockPos(e.posX, decimalPoint > 0.8 ? Math.floor(AutoFeetPlace.mc.player.posY) + 1.0 : Math.floor(AutoFeetPlace.mc.player.posY), e.posZ);
    }

    private boolean isSurrounded(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (AutoFeetPlace.mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.AIR) continue;
            return true;
        }
        return false;
    }

    private BlockPos addToPlayer(BlockPos playerPos, double x, double y, double z) {
        if (playerPos.getX() < 0) {
            x = -x;
        }
        if (playerPos.getY() < 0) {
            y = -y;
        }
        if (playerPos.getZ() < 0) {
            z = -z;
        }
        return playerPos.add(x, y, z);
    }

    private int calcLength(double decimal, boolean negative) {
        if (negative) {
            return decimal <= 0.3 ? 1 : 0;
        }
        return decimal >= 0.7 ? 1 : 0;
    }

    private int calcOffset(double dec) {
        return dec >= 0.7 ? 1 : (dec <= 0.3 ? -1 : 0);
    }

    private static enum Center {
        NONE,
        MOTION,
        TELEPORT;

    }

    private static enum Swap {
        PACKET,
        NORMAL;

    }

    private static enum Timing {
        VANILLA,
        SEQUENTIAL;

    }
}

