/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemAppleGold
 *  net.minecraft.item.ItemFood
 *  net.minecraftforge.event.entity.living.LivingEntityUseItemEvent$Finish
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.mioclient.mod.modules.impl.player;

import java.text.DecimalFormat;
import java.util.Random;
import me.mioclient.api.events.impl.BreakBlockEvent;
import me.mioclient.api.util.interact.BlockUtil;
import me.mioclient.api.util.math.MathUtil;
import me.mioclient.api.util.math.Timer;
import me.mioclient.mod.modules.Category;
import me.mioclient.mod.modules.Module;
import me.mioclient.mod.modules.settings.Setting;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Announcer
extends Module {
    private final Setting<Boolean> move = this.add(new Setting<Boolean>("Move", true));
    private final Setting<Boolean> breakBlock = this.add(new Setting<Boolean>("Break", true));
    private final Setting<Boolean> eat = this.add(new Setting<Boolean>("Eat", true));
    private final Setting<Double> delay = this.add(new Setting<Double>("Delay", 10.0, 1.0, 30.0));
    private double lastPositionX;
    private double lastPositionY;
    private double lastPositionZ;
    private int eaten;
    private int broken;
    private final Timer delayTimer = new Timer();

    public Announcer() {
        super("Announcer", "announces yo shit", Category.PLAYER, true);
    }

    @Override
    public void onEnable() {
        this.eaten = 0;
        this.broken = 0;
        this.delayTimer.reset();
    }

    @Override
    public void onUpdate() {
        if (Announcer.fullNullCheck() || !Announcer.spawnCheck()) {
            return;
        }
        double traveledX = this.lastPositionX - Announcer.mc.player.lastTickPosX;
        double traveledY = this.lastPositionY - Announcer.mc.player.lastTickPosY;
        double traveledZ = this.lastPositionZ - Announcer.mc.player.lastTickPosZ;
        double traveledDistance = Math.sqrt(traveledX * traveledX + traveledY * traveledY + traveledZ * traveledZ);
        if (this.move.getValue().booleanValue() && traveledDistance >= 1.0 && traveledDistance <= 1000.0 && this.delayTimer.passedS(this.delay.getValue())) {
            Announcer.mc.player.sendChatMessage(this.getWalkMessage().replace("{blocks}", new DecimalFormat("0.00").format(traveledDistance)));
            this.lastPositionX = Announcer.mc.player.lastTickPosX;
            this.lastPositionY = Announcer.mc.player.lastTickPosY;
            this.lastPositionZ = Announcer.mc.player.lastTickPosZ;
            this.delayTimer.reset();
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent.Finish event) {
        if (Announcer.fullNullCheck() || !Announcer.spawnCheck()) {
            return;
        }
        int random = MathUtil.randomBetween(1, 6);
        if (this.eat.getValue().booleanValue() && event.getEntity() == Announcer.mc.player && event.getItem().getItem() instanceof ItemFood || event.getItem().getItem() instanceof ItemAppleGold) {
            ++this.eaten;
            if (this.eaten >= random && this.delayTimer.passedS(this.delay.getValue())) {
                Announcer.mc.player.sendChatMessage(this.getEatMessage().replace("{amount}", "" + this.eaten).replace("{name}", "" + event.getItem().getDisplayName()));
                this.eaten = 0;
                this.delayTimer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onBreakBlock(BreakBlockEvent event) {
        if (Announcer.fullNullCheck() || !Announcer.spawnCheck()) {
            return;
        }
        int random = MathUtil.randomBetween(1, 6);
        ++this.broken;
        if (this.breakBlock.getValue().booleanValue() && this.broken >= random && this.delayTimer.passedS(this.delay.getValue())) {
            Announcer.mc.player.sendChatMessage(this.getBreakMessage().replace("{amount}", "" + this.broken).replace("{name}", "" + BlockUtil.getBlock(event.getPos()).getLocalizedName()));
            this.broken = 0;
            this.delayTimer.reset();
        }
    }

    private String getWalkMessage() {
        String[] walkMessage = new String[]{"I just flew over {blocks} blocks thanks to mioclient.me!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u043f\u0440\u043e\u043b\u0435\u0442\u0435\u043b \u043d\u0430\u0434 {blocks} \u0431\u043b\u043e\u043a\u0430\u043c\u0438 \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e mioclient.me!", "mioclient.me sayesinde {blocks} blok u\u00e7tum!", "\u6211\u521a\u521a\u7528 mioclient.me \u8d70\u4e86 {blocks} \u7c73!", "Dank mioclient.me bin ich gerade \u00fcber {blocks} Bl\u00f6cke geflogen!", "Jag hoppade precis \u00f6ver {blocks} blocks tack vare mioclient.me!", "W\u0142a\u015bnie przelecia\u0142em ponad {blocks} bloki dzi\u0119ki mioclient.me!", "Es tikko nolidoju {blocks} blokus, paldies mioclient.me!", "\u042f \u0449\u043e\u0439\u043d\u043e \u043f\u0440\u043e\u043b\u0435\u0442\u0456\u0432 \u043d\u0430\u0434 {blocks} \u0431\u043b\u043e\u043a\u0430\u043c\u0438 \u0437\u0430\u0432\u0434\u044f\u043a\u0438 mioclient.me!", "I just fwew ovew {blocks} bwoccs thanks to miocwient.me! :3", "Ho appena camminato per {blocks} blocchi grazie a mioclient.me!", "\u05e2\u05db\u05e9\u05d9\u05d5 \u05e2\u05e4\u05ea\u05d9 {blocks} \u05d4\u05d5\u05d3\u05d5\u05ea \u05dc mioclient.me!", "Pr\u00e1v\u011b jsem prolet\u011bl {blocks} blok\u016f d\u00edky mioclient.me!"};
        return walkMessage[new Random().nextInt(walkMessage.length)];
    }

    private String getBreakMessage() {
        String[] breakMessage = new String[]{"I just destroyed {amount} {name} with the power of mioclient.me!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0440\u0430\u0437\u0440\u0443\u0448\u0438\u043b {amount} {name} \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e mioclient.me!", "Az \u00f6nce {amount} tane {name} k\u0131rd\u0131m. Te\u015eekk\u00fcrler mioclient.me!", "\u6211\u521a\u521a\u7528 mioclient.me \u7834\u574f\u4e86 {amount} {name}!", "Ich habe gerade {amount} {name} mit der Kraft von mioclient.me zerst\u00f6rt!", "Jag f\u00f6rst\u00f6rde precis {amount} {name} tack vare mioclient.me!", "W\u0142a\u015bnie zniszczy\u0142em {amount} {name} za pomoc\u0105 mioclient.me", "Es tikko salauzu {amount} {name} ar sp\u0113ku mioclient.me!", "\u042f \u0449\u043e\u0439\u043d\u043e \u0437\u043d\u0438\u0449\u0438\u0432 {amount} {name} \u0437\u0430 \u0434\u043e\u043f\u043e\u043c\u043e\u0433\u043e\u044e mioclient.me!", "I just destwoyed {amount} {name} with the powew of miocwient.me! :3", "Ho appena distrutto {amount} {name} grazie al potere di mioclient.me!", "\u05d1\u05d3\u05d9\u05d5\u05e7 \u05d7\u05e6\u05d1\u05ea\u05d9 {amount} {name} \u05d1\u05e2\u05d6\u05e8\u05ea \u05d4\u05db\u05d5\u05d7 \u05e9\u05dc mioclient.me!", "Pr\u00e1v\u011b jsem zni\u010dil {amount} {name} s moc\u00ed mioclient.me!"};
        return breakMessage[new Random().nextInt(breakMessage.length)];
    }

    private String getEatMessage() {
        String[] eatMessage = new String[]{"I just ate {amount} {name} thanks to mioclient.me!", "\u042f \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0441\u044a\u0435\u043b {amount} {name} \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e mioclient.me!", "Tam olarak {amount} tane {name} yedim. Te\u015eekk\u00fcrler mioclient.me", "\u6211\u521a\u7528 mioclient.me \u5403\u4e86 {amount} \u4e2a {name}!", "Ich habe gerade {amount} {name} dank mioclient.me gegessen!", "Jag \u00e5t precis {amount} {name} tack vare mioclient.me", "W\u0142a\u015bnie zjad\u0142em {amount} {name} dzi\u0119ki mioclient.me", "Es tikko ap\u0113du {amount} {name} paldies mioclient.me", "\u042f \u0449\u043e\u0439\u043d\u043e \u0437\u2019\u0457\u0432 {amount} {name} \u0437\u0430\u0432\u0434\u044f\u043a\u0438 mioclient.me!", "I just ate {amount} {name} thanks to miocwient.me! ^-^", "Ho appena mangiato {amount} {name} grazie a mioclient.me!", "\u05db\u05e8\u05d2\u05e2 \u05d0\u05db\u05dc\u05ea\u05d9 {amount} {name} \u05d4\u05d5\u05d3\u05d5\u05ea \u05dcmioclient.me!", "Pr\u00e1v\u011b jsem sn\u011bdl {amount} {name} d\u00edky mioclient.me"};
        return eatMessage[new Random().nextInt(eatMessage.length)];
    }
}

