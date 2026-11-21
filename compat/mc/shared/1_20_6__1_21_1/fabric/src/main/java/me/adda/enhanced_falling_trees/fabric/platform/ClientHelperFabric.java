package me.adda.enhanced_falling_trees.fabric.platform;

import me.adda.enhanced_falling_trees.api.platform.ClientHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Function;

public class ClientHelperFabric implements ClientHelper {
    @Override
    public <T extends Entity> void registerEntityRenderer(
            EntityType<T> entityType,
            Function<EntityRendererProvider.Context, EntityRenderer<T>> rendererFactory) {
        System.out.println("Регистерим рендерер для фабрика");
        EntityRendererRegistry.register(entityType, rendererFactory::apply);
    }
}