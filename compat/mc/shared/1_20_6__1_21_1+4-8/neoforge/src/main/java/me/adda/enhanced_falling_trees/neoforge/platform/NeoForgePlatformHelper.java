package me.adda.enhanced_falling_trees.neoforge.platform;

import me.adda.enhanced_falling_trees.api.platform.PlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isClient() {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    @Override
    public boolean isFabric() {
        return false;
    }

    @Override
    public boolean isForge() {
        return false;
    }

    @Override
    public boolean isNeoForge() {
        return true;
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }
}
