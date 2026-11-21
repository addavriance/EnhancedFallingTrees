package me.adda.enhanced_falling_trees.api.platform;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Function;

public interface ClientHelper {
    <T extends Entity> void registerEntityRenderer(
            EntityType<T> entityType,
            Function<EntityRendererProvider.Context, EntityRenderer<T>> rendererFactory
    );
}