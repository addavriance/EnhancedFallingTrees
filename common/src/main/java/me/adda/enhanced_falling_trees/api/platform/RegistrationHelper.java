package me.adda.enhanced_falling_trees.api.platform;

import me.adda.enhanced_falling_trees.api.platform.registry.DeferredObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.List;
import java.util.function.Supplier;

public interface RegistrationHelper {
    <T> void register(DeferredObject<? extends T> deferredObject, Supplier<? extends T> supplier);
    <T> void registerNewRegistry(Registry<T> registry);
    void registerReloadListener(PackType packType, PreparableReloadListener listener, ResourceLocation id, List<ResourceLocation> dependencies);
}