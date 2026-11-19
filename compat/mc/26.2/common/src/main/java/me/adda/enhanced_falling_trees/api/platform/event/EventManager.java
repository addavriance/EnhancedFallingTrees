package me.adda.enhanced_falling_trees.api.platform.event;

public interface EventManager {
    void registerBlockBreakEvent(BlockBreakCallback callback);
    void registerPlayerJoinEvent(PlayerJoinCallback callback);
    void registerClientSetupEvent(ClientSetupCallback callback);
    void registerClientPlayerJoinEvent(ClientPlayerJoinCallback callback);
}