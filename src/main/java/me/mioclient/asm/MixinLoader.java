/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin$MCVersion
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin$Name
 */
package me.mioclient.asm;

import java.util.Map;
import me.mioclient.Mio;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

@IFMLLoadingPlugin.Name(value="Mio")
@IFMLLoadingPlugin.MCVersion(value="1.12.2")
public class MixinLoader
implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment;

    public MixinLoader() {
        Mio.LOGGER.info("Loading mio mixins...\n");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.mio.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("name");
        Mio.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

