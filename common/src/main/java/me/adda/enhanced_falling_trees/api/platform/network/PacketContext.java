package me.adda.enhanced_falling_trees.api.platform.network;

import net.minecraft.world.entity.player.Player;

public interface PacketContext {
    /**
     * Получает игрока, связанного с этим пакетом
     */
    Player getPlayer();

    /**
     * Выполняет действие в правильном потоке
     */
    void execute(Runnable action);

    /**
     * Проверяет, находимся ли мы на сервере
     */
    boolean isOnServer();
}