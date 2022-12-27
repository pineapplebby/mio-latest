/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Util
 *  net.minecraft.util.Util$EnumOS
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.Mod$Instance
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.Display
 */
package me.mioclient;

import java.io.InputStream;
import java.nio.ByteBuffer;
import me.mioclient.api.managers.Managers;
import me.mioclient.api.util.render.RenderUtil;
import me.mioclient.mod.gui.screen.MioClickGui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid="mioclient.me", name="Mio", version="v0.6.9-alpha")
public class Mio {
    @Mod.Instance
    public static Mio INSTANCE;
    public static final String MODID = "mioclient.me";
    public static final String MODVER = "v0.6.9-alpha";
    public static final String VERHASH;
    public static final Logger LOGGER;

    public static void load() {
        LOGGER.info("Loading Mio...");
        Managers.load();
        if (MioClickGui.INSTANCE == null) {
            MioClickGui.INSTANCE = new MioClickGui();
        }
        LOGGER.info("Mio successfully loaded!\n");
    }

    public static void unload(boolean force) {
        LOGGER.info("Unloading Mio...");
        Managers.unload(force);
        LOGGER.info("Mio successfully unloaded!\n");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Display.setTitle((String)"mioclient.me: Loading...");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle((String)MODID);
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/minecraft/textures/mio/constant/icon16x.png");
                 InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/minecraft/textures/mio/constant/icon32x.png");){
                ByteBuffer[] icons = new ByteBuffer[]{RenderUtil.readImageToBuffer(inputStream16x), RenderUtil.readImageToBuffer(inputStream32x)};
                Display.setIcon((ByteBuffer[])icons);
            }
            catch (Exception e) {
                LOGGER.error("Mio couldn't set the window icon!", (Throwable)e);
            }
        }
        Mio.load();
    }

    static {
        VERHASH = "e4e9564ed7caaac078a61bbef88fa1b91ccac41c".substring(0, 12);
        LOGGER = LogManager.getLogger((String)"Mio");
    }
}

