package me.adda.enhanced_falling_trees.neoforge.platform.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record NeoForgePayload(Identifier id, FriendlyByteBuf data) implements CustomPacketPayload {
    private static final Map<Identifier, Type<NeoForgePayload>> TYPES = new HashMap<>();

    public static Type<NeoForgePayload> getType(Identifier id) {
        return TYPES.computeIfAbsent(id, rid -> new Type<>(rid));
    }

    public static StreamCodec<FriendlyByteBuf, NeoForgePayload> codec(Identifier id) {
        return StreamCodec.of(
                (buf, payload) -> buf.writeBytes(payload.data.slice()),
                buf -> {
                    byte[] bytes = new byte[buf.readableBytes()];
                    buf.readBytes(bytes);
                    return new NeoForgePayload(id, new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes)));
                }
        );
    }

    @Override
    public Type<?> type() {
        return getType(id);
    }
}
