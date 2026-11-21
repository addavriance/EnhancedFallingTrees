package me.adda.enhanced_falling_trees.forge.platform.network;

import me.adda.enhanced_falling_trees.api.platform.network.NetworkService;
import me.adda.enhanced_falling_trees.api.platform.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ForgeNetworkService implements NetworkService {
    private static final int PROTOCOL_VERSION = 1;
    private final Map<ResourceLocation, SimpleChannel> channels = new HashMap<>();

    @Override
    public void registerClientToServerPacket(ResourceLocation id, BiConsumer<FriendlyByteBuf, PacketContext> handler) {
        SimpleChannel channel = getOrCreateChannel(id);

        System.out.println("c2s: " + id);

        channel.messageBuilder(C2SBufferWrapper.class)
                .encoder(BufferWrapper::writeToBuffer)
                .decoder(C2SBufferWrapper::new)
                .consumerNetworkThread((bufferWrapper, context) -> {
                    ServerPlayer sender = context.getSender();
                    ForgePacketContext packetContext = new ForgePacketContext(sender, true);
                    handler.accept(bufferWrapper.buffer, packetContext);
                })
                .add();
    }

    @Override
    public void registerServerToClientPacket(ResourceLocation id, BiConsumer<FriendlyByteBuf, PacketContext> handler) {
        SimpleChannel channel = getOrCreateChannel(id);

        System.out.println("s2c: " + id);

        channel.messageBuilder(S2CBufferWrapper.class)
                .encoder(BufferWrapper::writeToBuffer)
                .decoder(S2CBufferWrapper::new)
                .consumerNetworkThread((bufferWrapper, context) -> {
                    ForgePacketContext packetContext = new ForgePacketContext(null, false);
                    handler.accept(bufferWrapper.buffer, packetContext);
                })
                .add();
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        SimpleChannel channel = getOrCreateChannel(id);
        channel.send(new S2CBufferWrapper(buf), PacketDistributor.PLAYER.with(player));
    }

    @Override
    public void sendToServer(ResourceLocation id, FriendlyByteBuf buf) {
        SimpleChannel channel = getOrCreateChannel(id);
        channel.send(new C2SBufferWrapper(buf), PacketDistributor.SERVER.noArg());
    }

    @Override
    public void sendToAll(ResourceLocation id, FriendlyByteBuf buf) {
        SimpleChannel channel = getOrCreateChannel(id);
        channel.send(new S2CBufferWrapper(buf), PacketDistributor.ALL.noArg());
    }

    private SimpleChannel getOrCreateChannel(ResourceLocation id) {
        return channels.computeIfAbsent(id, resourceLocation ->
                ChannelBuilder.named(id)
                        .networkProtocolVersion(PROTOCOL_VERSION)
                        .clientAcceptedVersions((status, version) -> true)
                        .serverAcceptedVersions((status, version) -> true)
                        .simpleChannel()
        );
    }

    // Разные классы для разных типов пакетов
    public static class BufferWrapper {
        protected final FriendlyByteBuf buffer;

        public BufferWrapper(FriendlyByteBuf buffer) {
            this.buffer = buffer;
        }

        public void writeToBuffer(FriendlyByteBuf outBuffer) {
            outBuffer.writeBytes(buffer.copy());
        }
    }

    public static class C2SBufferWrapper extends BufferWrapper {
        public C2SBufferWrapper(FriendlyByteBuf buffer) {
            super(buffer);
        }
    }

    public static class S2CBufferWrapper extends BufferWrapper {
        public S2CBufferWrapper(FriendlyByteBuf buffer) {
            super(buffer);
        }
    }
}