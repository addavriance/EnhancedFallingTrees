package me.adda.enhanced_falling_trees.neoforge.platform.event;

import me.adda.enhanced_falling_trees.api.platform.event.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class NeoForgeEventManager implements EventManager {
    private static IEventBus MOD_EVENT_BUS;

    public static void setModEventBus(IEventBus bus) {
        MOD_EVENT_BUS = bus;
    }

    @Override
    public void registerBlockBreakEvent(BlockBreakCallback callback) {
        NeoForge.EVENT_BUS.addListener((BlockEvent.BreakEvent event) -> {
            EventResult result = callback.onBlockBreak(
                    (Level) event.getLevel(),
                    event.getPos(),
                    event.getState(),
                    event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer sp ? sp : null,
                    new NeoForgeIntValue()
            );
            if (result == EventResult.INTERRUPT_FALSE) {
                event.setCanceled(true);
            }
        });
    }

    @Override
    public void registerPlayerJoinEvent(PlayerJoinCallback callback) {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer sp) {
                callback.onPlayerJoin(sp);
            }
        });
    }

    @Override
    public void registerClientSetupEvent(ClientSetupCallback callback) {
        MOD_EVENT_BUS.addListener((FMLClientSetupEvent event) ->
                event.enqueueWork(() -> callback.onClientSetup(Minecraft.getInstance())));
    }

    @Override
    public void registerClientPlayerJoinEvent(ClientPlayerJoinCallback callback) {
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
            if (event.getPlayer() != null) {
                callback.onClientPlayerJoin(event.getPlayer());
            }
        });
    }
}
