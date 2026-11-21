package me.adda.enhanced_falling_trees.neoforge.platform.network;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.api.platform.network.NetworkService;
import me.adda.enhanced_falling_trees.api.platform.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class NeoForgeNetworkService implements NetworkService {
    private static final Map<ResourceLocation, BiConsumer<FriendlyByteBuf, PacketContext>> C2S = new LinkedHashMap<>();
    private static final Map<ResourceLocation, BiConsumer<FriendlyByteBuf, PacketContext>> S2C = new LinkedHashMap<>();

    public static void setModEventBus(IEventBus bus) {
        bus.addListener(NeoForgeNetworkService::onRegisterPayloads);
    }

    @Override
    public void registerClientToServerPacket(ResourceLocation id, BiConsumer<FriendlyByteBuf, PacketContext> handler) {
        C2S.put(id, handler);
    }

    @Override
    public void registerServerToClientPacket(ResourceLocation id, BiConsumer<FriendlyByteBuf, PacketContext> handler) {
        S2C.put(id, handler);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        PacketDistributor.sendToPlayer(player, new NeoForgePayload(id, new FriendlyByteBuf(buf.copy())));
    }

    @Override
    public void sendToServer(ResourceLocation id, FriendlyByteBuf buf) {
        ClientPacketDistributor.sendToServer(new NeoForgePayload(id, new FriendlyByteBuf(buf.copy())));
    }

    @Override
    public void sendToAll(ResourceLocation id, FriendlyByteBuf buf) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        NeoForgePayload payload = new NeoForgePayload(id, new FriendlyByteBuf(buf.copy()));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(FallingTrees.MOD_ID);

        Set<ResourceLocation> allIds = new HashSet<>(C2S.keySet());
        allIds.addAll(S2C.keySet());

        allIds.forEach(id -> {
            BiConsumer<FriendlyByteBuf, PacketContext> c2sHandler = C2S.get(id);
            BiConsumer<FriendlyByteBuf, PacketContext> s2cHandler = S2C.get(id);
            registrar.playBidirectional(
                    NeoForgePayload.getType(id),
                    NeoForgePayload.codec(id),
                    (payload, context) -> context.enqueueWork(() -> {
                        if (c2sHandler != null)
                            c2sHandler.accept(payload.data(), new NeoForgePacketContext(context.player(), true));
                    }),
                    (payload, context) -> context.enqueueWork(() -> {
                        if (s2cHandler != null)
                            s2cHandler.accept(payload.data(), new NeoForgePacketContext(context.player(), false));
                    })
            );
        });
    }
}
