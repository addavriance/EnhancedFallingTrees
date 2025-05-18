package me.adda.enhanced_falling_trees.forge.platform;

import me.adda.enhanced_falling_trees.api.platform.EnvType;
import me.adda.enhanced_falling_trees.api.platform.PlatformHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ForgePlatformHelper implements PlatformHelper {
    @Override
    public EnvType getEnvironmentType() {
        return FMLEnvironment.dist == Dist.CLIENT ? EnvType.CLIENT : EnvType.SERVER;
    }

    @Override
    public boolean isFabric() {
        return false;
    }

    @Override
    public boolean isForge() {
        return true;
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }
}