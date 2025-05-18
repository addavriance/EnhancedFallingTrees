package me.adda.enhanced_falling_trees.fabric.platform.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record FallingTreesPayload(ResourceLocation id, ByteBuf data) implements CustomPacketPayload {
    private static final Map<ResourceLocation, Type<?>> TYPES = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends FallingTreesPayload> Type<T> getType(ResourceLocation id) {
        return (Type<T>) TYPES.computeIfAbsent(id, resourceId ->
                CustomPacketPayload.createType(resourceId.toString())
        );
    }

    @Override
    public Type<?> type() {
        return getType(id);
    }

    public static StreamCodec<FriendlyByteBuf, FallingTreesPayload> codec(ResourceLocation id) {
        return StreamCodec.of(
                // Encoder - тут всё остаётся как есть
                (buf, payload) -> {
                    // Копируем все данные из payload.data в buf
                    buf.writeBytes(payload.data.slice());
                },
                // Decoder - здесь происходит проблема, исправляем
                buf -> {
                    // Прочитаем ВСЕ данные из буфера
                    byte[] data = new byte[buf.readableBytes()];
                    buf.readBytes(data);

                    // Создаем новый буфер с прочитанными данными
                    FriendlyByteBuf dataBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));

                    return new FallingTreesPayload(id, dataBuf);
                }
        );
    }
}