package me.adda.enhanced_falling_trees.api.platform.event;

import net.minecraft.client.Minecraft;

public interface ClientSetupCallback {
    void onClientSetup(Minecraft minecraft);
}