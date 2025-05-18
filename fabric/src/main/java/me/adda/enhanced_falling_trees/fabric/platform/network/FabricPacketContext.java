// fabric/src/main/java/me/adda/enhanced_falling_trees/fabric/platform/network/FabricPacketContext.java
package me.adda.enhanced_falling_trees.fabric.platform.network;

import me.adda.enhanced_falling_trees.api.platform.network.PacketContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class FabricPacketContext implements PacketContext {
    private final Player player;
    private final PacketSender responseSender;

    public FabricPacketContext(Player player, PacketSender responseSender) {
        this.player = player;
        this.responseSender = responseSender;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void execute(Runnable action) {
        // В новом Fabric API мы можем быть уверены, что уже находимся в правильном потоке
        action.run();
    }

    @Override
    public boolean isOnServer() {
        return player instanceof ServerPlayer;
    }
}