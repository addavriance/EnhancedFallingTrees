// fabric/src/main/java/me/adda/enhanced_falling_trees/fabric/platform/network/FabricNetworkService.java
package me.adda.enhanced_falling_trees.fabric.platform.network;

import me.adda.enhanced_falling_trees.api.platform.network.NetworkService;
import me.adda.enhanced_falling_trees.api.platform.network.PacketContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class FabricNetworkService implements NetworkService {
    private final Map<ResourceLocation, BiConsumer<FriendlyByteBuf, PacketContext>> serverHandlers = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, BiConsumer<FriendlyByteBuf, PacketContext>> clientHandlers = new ConcurrentHashMap<>();

    private static MinecraftServer SERVER;

    public static void setServer(MinecraftServer server) {
        SERVER = server;
    }

    // Получение сервера
    private MinecraftServer getServer() {
        return SERVER;
    }

    @Override
    public void registerClientToServerPacket(ResourceLocation id, BiConsumer<FriendlyByteBuf, PacketContext> handler) {
        CustomPacketPayload.Type<FallingTreesPayload> type = FallingTreesPayload.getType(id);

        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            serverHandlers.put(id, handler);
            FabricPacketContext packetContext = new FabricPacketContext(context.player(), context.responseSender());
            handler.accept((FriendlyByteBuf) payload.data(), packetContext);
        });
    }

    @Override
    public void registerServerToClientPacket(ResourceLocation id, BiConsumer<FriendlyByteBuf, PacketContext> handler) {
        CustomPacketPayload.Type<FallingTreesPayload> type = FallingTreesPayload.getType(id);

        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            clientHandlers.put(id, handler);
            FabricPacketContext packetContext = new FabricPacketContext(context.player(), context.responseSender());
            handler.accept((FriendlyByteBuf) payload.data(), packetContext);
        });
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        CustomPacketPayload.Type<FallingTreesPayload> type = FallingTreesPayload.getType(id);
        FallingTreesPayload payload = new FallingTreesPayload(id, buf.copy());
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToServer(ResourceLocation id, FriendlyByteBuf buf) {
        CustomPacketPayload.Type<FallingTreesPayload> type = FallingTreesPayload.getType(id);
        FallingTreesPayload payload = new FallingTreesPayload(id, buf.copy());
        ClientPlayNetworking.send(payload);
    }

    @Override
    public void sendToAll(ResourceLocation id, FriendlyByteBuf buf) {
        CustomPacketPayload.Type<FallingTreesPayload> type = FallingTreesPayload.getType(id);
        FallingTreesPayload payload = new FallingTreesPayload(id, buf.copy());

        for (ServerPlayer player : PlayerLookup.all(getServer())) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}