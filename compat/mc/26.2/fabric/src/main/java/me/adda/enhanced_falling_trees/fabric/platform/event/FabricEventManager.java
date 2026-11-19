package me.adda.enhanced_falling_trees.fabric.platform.event;

import me.adda.enhanced_falling_trees.api.platform.event.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FabricEventManager implements EventManager {

    @Override
    public void registerBlockBreakEvent(BlockBreakCallback callback) {
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof net.minecraft.server.level.ServerPlayer) {
                IntValue xp = new FabricIntValue(0);
                EventResult result = callback.onBlockBreak(
                        world,
                        pos,
                        state,
                        (net.minecraft.server.level.ServerPlayer) player,
                        xp
                );
                return result != EventResult.INTERRUPT_FALSE;
            }
            return true;
        });
    }

    @Override
    public void registerPlayerJoinEvent(PlayerJoinCallback callback) {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            callback.onPlayerJoin(handler.getPlayer());
        });
    }

    @Override
    public void registerClientSetupEvent(ClientSetupCallback callback) {
        ClientLifecycleEvents.CLIENT_STARTED.register(callback::onClientSetup);
    }

    @Override
    public void registerClientPlayerJoinEvent(ClientPlayerJoinCallback callback) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                callback.onClientPlayerJoin(client.player);
            }
        });
    }
}