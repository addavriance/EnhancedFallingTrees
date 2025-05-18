package me.adda.enhanced_falling_trees.fabric.platform;


import me.adda.enhanced_falling_trees.api.platform.EnvType;
import me.adda.enhanced_falling_trees.api.platform.PlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public EnvType getEnvironmentType() {
        return FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT ? EnvType.CLIENT : EnvType.SERVER;
    }

    @Override
    public boolean isFabric() {
        return true;
    }

    @Override
    public boolean isForge() {
        return false;
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }
}