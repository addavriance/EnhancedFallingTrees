// forge/src/main/java/me/adda/enhanced_falling_trees/forge/platform/network/ForgePacketContext.java
package me.adda.enhanced_falling_trees.forge.platform.network;

import me.adda.enhanced_falling_trees.api.platform.network.PacketContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ForgePacketContext implements PacketContext {
    private final ServerPlayer sender;
    private final boolean isServer;

    public ForgePacketContext(ServerPlayer sender, boolean isServer) {
        this.sender = sender;
        this.isServer = isServer;
    }

    @Override
    public Player getPlayer() {
        return sender;
    }

    @Override
    public void execute(Runnable action) {
        if (sender != null && sender.getServer() != null) {
            sender.getServer().execute(action);
        } else {
            action.run();
        }
    }

    @Override
    public boolean isOnServer() {
        return isServer;
    }
}