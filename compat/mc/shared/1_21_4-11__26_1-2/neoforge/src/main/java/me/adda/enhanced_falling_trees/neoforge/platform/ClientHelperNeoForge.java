package me.adda.enhanced_falling_trees.neoforge.platform;

import me.adda.enhanced_falling_trees.api.platform.ClientHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ClientHelperNeoForge implements ClientHelper {
    private static final List<RendererEntry<?>> RENDERERS = new ArrayList<>();

    public static void setModEventBus(IEventBus bus) {
        bus.addListener(ClientHelperNeoForge::onRegisterRenderers);
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(
            EntityType<T> entityType,
            Function<EntityRendererProvider.Context, EntityRenderer<T, ?>> rendererFactory) {
        RENDERERS.add(new RendererEntry<>(entityType, rendererFactory));
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (RendererEntry<?> entry : RENDERERS) {
            registerInternal(event, entry);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void registerInternal(
            EntityRenderersEvent.RegisterRenderers event, RendererEntry<?> raw) {
        RendererEntry<T> entry = (RendererEntry<T>) raw;
        event.registerEntityRenderer(entry.entityType, entry.factory::apply);
    }

    private static class RendererEntry<T extends Entity> {
        final EntityType<T> entityType;
        final Function<EntityRendererProvider.Context, EntityRenderer<T, ?>> factory;

        RendererEntry(EntityType<T> entityType, Function<EntityRendererProvider.Context, EntityRenderer<T, ?>> factory) {
            this.entityType = entityType;
            this.factory = factory;
        }
    }
}
