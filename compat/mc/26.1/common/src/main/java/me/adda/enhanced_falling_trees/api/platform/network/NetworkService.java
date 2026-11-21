package me.adda.enhanced_falling_trees.api.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public interface NetworkService {
    /**
     * Регистрирует обработчик пакетов от клиента к серверу
     */
    void registerClientToServerPacket(Identifier id, BiConsumer<FriendlyByteBuf, PacketContext> handler);

    /**
     * Регистрирует обработчик пакетов от сервера к клиенту
     */
    void registerServerToClientPacket(Identifier id, BiConsumer<FriendlyByteBuf, PacketContext> handler);

    /**
     * Отправляет пакет конкретному игроку
     */
    void sendToPlayer(ServerPlayer player, Identifier id, FriendlyByteBuf buf);

    /**
     * Отправляет пакет на сервер
     */
    void sendToServer(Identifier id, FriendlyByteBuf buf);

    /**
     * Отправляет пакет всем игрокам
     */
    void sendToAll(Identifier id, FriendlyByteBuf buf);
}