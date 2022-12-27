/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketBlockBreakAnim
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.render;

import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import me.mioclient.api.events.impl.DamageBlockEvent;
import me.mioclient.api.events.impl.PacketEvent;
import me.mioclient.api.events.impl.Render3DEvent;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.InterpolationUtil;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BreakingESP
extends Module {
    private final Setting<Boolean> showSelf = this.add(new Setting<Boolean>("ShowSelf", true));
    private final Setting<Mode> mode = this.add(new Setting<Mode>("Mode", Mode.OUT));
    private final Setting<Boolean> box = this.add(new Setting<Boolean>("Box", true));
    private final Setting<Boolean> line = this.add(new Setting<Boolean>("Line", true));
    private final Setting<Float> lineWidth = this.add(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    private final Setting<Double> range = this.add(new Setting<Double>("Range", 20.0, 1.0, 50.0));
    private final Setting<ColorMode> colorMode = this.add(new Setting<ColorMode>("ColorMode", ColorMode.PROGRESS));
    private final Setting<Color> color = this.add(new Setting<Color>("Color", new Color(-2005041707, true), v -> this.colorMode.getValue() != ColorMode.PROGRESS));
    private final Map<BlockPos, Integer> blocks = Maps.newHashMap();
    ArrayList<ArrayList<Object>> packets = new ArrayList();

    public BreakingESP() {
        super("BreakingESP", "Highlights the blocks being broken around you.", Category.RENDER, true);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        this.blocks.forEach((pos, progress) -> {
            if (pos != null && progress != null) {
                if (BlockUtil.getBlock(pos) == Blocks.AIR) {
                    return;
                }
                if (pos.getDistance((int)BreakingESP.mc.player.posX, (int)BreakingESP.mc.player.posY, (int)BreakingESP.mc.player.posZ) <= this.range.getValue()) {
                    float preDamage = BreakingESP.mc.playerController.curBlockDamageMP;
                    float damage = InterpolationUtil.getInterpolatedFloat(preDamage, BreakingESP.mc.playerController.curBlockDamageMP, event.getPartialTicks());
                    this.drawESP((BlockPos)pos, damage);
                }
            }
        });
        for (int i = 0; i < this.packets.size(); ++i) {
            BlockPos pos2 = (BlockPos)this.packets.get(i).get(0);
            int ticks = (Integer)this.packets.get(i).get(1);
            if (BlockUtil.getBlock(pos2) instanceof BlockAir) {
                this.packets.remove(i);
                --i;
                continue;
            }
            if (!this.blocks.containsKey((Object)pos2)) {
                if (pos2.getDistance((int)BreakingESP.mc.player.posX, (int)BreakingESP.mc.player.posY, (int)BreakingESP.mc.player.posZ) <= this.range.getValue()) {
                    this.drawESP(pos2, (float)Math.min(ticks, 140) / 140.0f);
                }
            } else {
                this.packets.get(i).set(1, ++ticks);
            }
            if (++ticks > 140) {
                this.packets.remove(i);
                --i;
                continue;
            }
            this.packets.get(i).set(1, ticks);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockChange && this.blocks.containsKey((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition()) && ((SPacketBlockChange)event.getPacket()).getBlockState().getBlock() != Blocks.AIR) {
            this.blocks.remove((Object)((SPacketBlockChange)event.getPacket()).getBlockPosition());
        }
        if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            final SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim)event.getPacket();
            BlockPos pos = packet.getPosition();
            Block block = BlockUtil.getBlock(pos);
            if (!this.renderingPos(pos) && block != Blocks.BEDROCK && block != Blocks.BARRIER && block != Blocks.AIR) {
                if (!this.showSelf.getValue().booleanValue() && BreakingESP.mc.world.getEntityByID(packet.getBreakerId()) == BreakingESP.mc.player) {
                    return;
                }
                this.packets.add(new ArrayList<Object>(){
                    {
                        this.add(packet.getPosition());
                        this.add(0);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onDamageBlock(DamageBlockEvent event) {
        if (BreakingESP.fullNullCheck() || BreakingESP.mc.player.getDistanceSq(event.getPosition()) > this.range.getValue()) {
            return;
        }
        if (!this.showSelf.getValue().booleanValue() && BreakingESP.mc.world.getEntityByID(event.getBreakerId()) == BreakingESP.mc.player) {
            return;
        }
        if (event.getProgress() > 0 && event.getProgress() < 9) {
            this.blocks.putIfAbsent(event.getPosition(), event.getProgress());
        } else {
            this.blocks.remove((Object)event.getPosition(), event.getProgress());
        }
    }

    private void drawESP(BlockPos pos, float damage) {
        AxisAlignedBB bb = BreakingESP.mc.world.getBlockState(pos).getSelectedBoundingBox((World)BreakingESP.mc.world, pos);
        double x = bb.minX + (bb.maxX - bb.minX) / 2.0;
        double y = bb.minY + (bb.maxY - bb.minY) / 2.0;
        double z = bb.minZ + (bb.maxZ - bb.minZ) / 2.0;
        double sizeX = (double)damage * (bb.maxX - x);
        double sizeY = (double)damage * (bb.maxY - y);
        double sizeZ = (double)damage * (bb.maxZ - z);
        Color color = this.colorMode.getValue() == ColorMode.PROGRESS ? new Color(damage <= 0.75f ? 200 : 0, damage >= 0.751f ? 200 : 0, 0, this.color.getValue().getAlpha()) : this.color.getValue();
        AxisAlignedBB inBB = bb.shrink((double)damage * bb.getAverageEdgeLength() * 0.5);
        AxisAlignedBB outBB = new AxisAlignedBB(x - sizeX, y - sizeY, z - sizeZ, x + sizeX, y + sizeY, z + sizeZ);
        RenderUtil.drawBoxESP(this.mode.getValue() == Mode.IN ? inBB : outBB, color, false, new Color(-1), this.lineWidth.getValue().floatValue(), this.line.getValue(), this.box.getValue(), color.getAlpha(), false);
    }

    private boolean renderingPos(BlockPos pos) {
        for (ArrayList<Object> part : this.packets) {
            BlockPos temp = (BlockPos)part.get(0);
            if (temp.getX() != pos.getX() || temp.getY() != pos.getY() || temp.getZ() != pos.getZ()) continue;
            return true;
        }
        return false;
    }

    private static enum ColorMode {
        PROGRESS,
        CUSTOM;

    }

    private static enum Mode {
        IN,
        OUT;

    }
}

