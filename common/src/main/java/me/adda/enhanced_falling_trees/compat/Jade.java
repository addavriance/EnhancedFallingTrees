package me.adda.enhanced_falling_trees.compat;

import me.adda.enhanced_falling_trees.registry.EntityRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class Jade implements IWailaPlugin {
    @Override
    @SuppressWarnings("unchecked")
    public void register(IWailaCommonRegistration registration) {
        ResourceKey<EntityType<?>> key = (ResourceKey<EntityType<?>>) (ResourceKey<?>) EntityRegistry.TREE.getKey();
        registration.entityTypeOperations().hide(key);
    }
}
