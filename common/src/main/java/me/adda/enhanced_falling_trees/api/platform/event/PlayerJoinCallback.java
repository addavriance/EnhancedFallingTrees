package me.adda.enhanced_falling_trees.api.platform.event;

import net.minecraft.server.level.ServerPlayer;

public interface PlayerJoinCallback {
    void onPlayerJoin(ServerPlayer player);
}