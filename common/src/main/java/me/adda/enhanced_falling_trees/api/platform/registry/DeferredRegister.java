// common/src/main/java/me/adda/enhanced_falling_trees/api/platform/registry/DeferredRegister.java
package me.adda.enhanced_falling_trees.api.platform.registry;

import me.adda.enhanced_falling_trees.api.platform.PlatformServices;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class DeferredRegister<T> {
    private final String namespace;
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final Map<DeferredObject<? extends T>, Supplier<? extends T>> entries = new ConcurrentHashMap<>();

    public static <T> DeferredRegister<T> create(String namespace, ResourceLocation registryLocation) {
        return create(namespace, ResourceKey.createRegistryKey(registryLocation));
    }

    public static <T> DeferredRegister<T> create(String namespace, Registry<T> registry) {
        return create(namespace, registry.key());
    }

    public static <T> DeferredRegister<T> create(String namespace, ResourceKey<? extends Registry<T>> registryKey) {
        return new DeferredRegister<>(namespace, registryKey);
    }

    private DeferredRegister(String namespace, ResourceKey<? extends Registry<T>> registryKey) {
        this.namespace = namespace;
        this.registryKey = registryKey;
    }

    public <R extends T> DeferredObject<R> register(String name, Function<ResourceKey<T>, R> registryFunc) {
        return register(new ResourceLocation(namespace, name), registryFunc);
    }

    public <R extends T> DeferredObject<R> register(String name, Supplier<R> registrySup) {
        return register(new ResourceLocation(namespace, name), registrySup);
    }

    public <R extends T> DeferredObject<R> register(ResourceLocation name, Function<ResourceKey<T>, R> registryFunc) {
        ResourceKey<T> key = ResourceKey.create(registryKey, name);
        return register(key, () -> registryFunc.apply(key));
    }

    public <R extends T> DeferredObject<R> register(ResourceLocation name, Supplier<R> registrySup) {
        return register(ResourceKey.create(registryKey, name), registrySup);
    }

    private <R extends T> DeferredObject<R> register(ResourceKey<T> resourceKey, Supplier<R> registrySup) {
        DeferredObject<R> deferredObject = new DeferredObject<>(resourceKey);
        entries.put(deferredObject, registrySup);
        return deferredObject;
    }

    public void register() {
        entries.forEach(PlatformServices.REGISTRATION::register);
    }
}