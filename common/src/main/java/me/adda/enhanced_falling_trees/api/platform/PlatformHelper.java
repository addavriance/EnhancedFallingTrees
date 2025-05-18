package me.adda.enhanced_falling_trees.api.platform;

public interface PlatformHelper {
    EnvType getEnvironmentType();

    boolean isFabric();

    boolean isForge();

    String getPlatformName();
}