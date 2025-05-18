package me.adda.enhanced_falling_trees.network;

import com.google.gson.Gson;
import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.config.CommonConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ConfigPayload(String configData) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(FallingTrees.MOD_ID, "config_packet");
    public static final Type<ConfigPayload> TYPE = CustomPacketPayload.createType(ID.toString());

    @Override
    public Type<?> type() {
        return TYPE;
    }

    // Преобразование конфигурации в ConfigPayload
    public static ConfigPayload fromConfig(CommonConfig config) {
        return new ConfigPayload(new Gson().toJson(config));
    }

    // Получение конфигурации из ConfigPayload
    public CommonConfig toConfig() {
        return new Gson().fromJson(configData, CommonConfig.class);
    }

    // Кодек для сериализации/десериализации
    public static final StreamCodec<FriendlyByteBuf, ConfigPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeUtf(payload.configData, 32767), // Увеличиваем лимит до максимально допустимого
            buf -> new ConfigPayload(buf.readUtf(32767))
    );
}