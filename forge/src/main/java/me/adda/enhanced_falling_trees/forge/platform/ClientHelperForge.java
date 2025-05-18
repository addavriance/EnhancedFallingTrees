// forge/src/main/java/me/adda/enhanced_falling_trees/forge/platform/ClientHelperImpl.java
package me.adda.enhanced_falling_trees.forge.platform;

import me.adda.enhanced_falling_trees.api.platform.ClientHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ClientHelperForge implements ClientHelper {
    // Используем типизированный класс для хранения информации о рендерере
    private static class RendererEntry<T extends Entity> {
        final EntityType<T> entityType;
        final Function<EntityRendererProvider.Context, EntityRenderer<T>> factory;

        RendererEntry(EntityType<T> entityType, Function<EntityRendererProvider.Context, EntityRenderer<T>> factory) {
            this.entityType = entityType;
            this.factory = factory;
        }
    }

    private final List<RendererEntry<?>> renderers = new ArrayList<>();

    public ClientHelperForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onRegisterRenderers);
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(
            EntityType<T> entityType,
            Function<EntityRendererProvider.Context, EntityRenderer<T>> rendererFactory) {
        renderers.add(new RendererEntry<>(entityType, rendererFactory));
    }

    // Метод для безопасной регистрации каждого рендерера с сохранением типизации
    private <T extends Entity> void registerRenderer(
            EntityRenderersEvent.RegisterRenderers event,
            RendererEntry<T> entry) {
        event.registerEntityRenderer(entry.entityType, entry.factory::apply);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Используем generic метод для сохранения типизации
        for (RendererEntry<?> entry : renderers) {
            registerRendererInternal(event, entry);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> void registerRendererInternal(
            EntityRenderersEvent.RegisterRenderers event,
            RendererEntry<?> rawEntry) {
        RendererEntry<T> entry = (RendererEntry<T>) rawEntry;
        registerRenderer(event, entry);
    }
}