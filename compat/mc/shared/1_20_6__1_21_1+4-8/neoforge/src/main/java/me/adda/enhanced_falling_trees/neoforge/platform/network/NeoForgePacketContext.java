package me.adda.enhanced_falling_trees.neoforge.platform.network;

import me.adda.enhanced_falling_trees.api.platform.network.PacketContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class NeoForgePacketContext implements PacketContext {
    private final Player player;
    private final boolean isServer;

    public NeoForgePacketContext(Player player, boolean isServer) {
        this.player = player;
        this.isServer = isServer;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void execute(Runnable action) {
        if (isServer && player instanceof ServerPlayer sp && sp.getServer() != null) {
            sp.getServer().execute(action);
        } else {
            action.run();
        }
    }

    @Override
    public boolean isOnServer() {
        return isServer;
    }
}
