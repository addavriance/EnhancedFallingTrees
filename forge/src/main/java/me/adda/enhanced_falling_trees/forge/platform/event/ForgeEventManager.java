package me.adda.enhanced_falling_trees.forge.platform.event;

import me.adda.enhanced_falling_trees.api.platform.event.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeEventManager implements EventManager {

    @Override
    public void registerBlockBreakEvent(BlockBreakCallback callback) {
        BlockEvent.BreakEvent.BUS.addListener(event -> {
            EventResult result = callback.onBlockBreak(
                    (Level) event.getLevel(),
                    event.getPos(),
                    event.getState(),
                    event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer ?
                            (net.minecraft.server.level.ServerPlayer) event.getPlayer() : null,
                    new ForgeIntValue(event)
            );

            return result == EventResult.INTERRUPT_FALSE;
        });
    }

    @Override
    public void registerPlayerJoinEvent(PlayerJoinCallback callback) {
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(event -> {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer) {
                callback.onPlayerJoin((net.minecraft.server.level.ServerPlayer) event.getEntity());
            }
        });
    }

    @Override
    public void registerClientSetupEvent(ClientSetupCallback callback) {
        FMLClientSetupEvent.getBus(FMLJavaModLoadingContext.get().getModBusGroup()).addListener(event -> {
            event.enqueueWork(() -> callback.onClientSetup(Minecraft.getInstance()));
        });
    }

    @Override
    public void registerClientPlayerJoinEvent(ClientPlayerJoinCallback callback) {
        ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(event -> {
            if (event.getPlayer() != null) {
                callback.onClientPlayerJoin(event.getPlayer());
            }
        });
    }
}