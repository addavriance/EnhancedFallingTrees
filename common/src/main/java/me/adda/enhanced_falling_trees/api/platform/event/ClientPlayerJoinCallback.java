package me.adda.enhanced_falling_trees.api.platform.event;

import net.minecraft.client.player.LocalPlayer;

public interface ClientPlayerJoinCallback {
    void onClientPlayerJoin(LocalPlayer player);
}