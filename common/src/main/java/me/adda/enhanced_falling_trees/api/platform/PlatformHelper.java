package me.adda.enhanced_falling_trees.api.platform;

public interface PlatformHelper {
    boolean isClient();

    boolean isFabric();

    boolean isForge();

    default boolean isNeoForge() { return false; }

    default boolean isQuilt() { return false; }

    String getPlatformName();
}