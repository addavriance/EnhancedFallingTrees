package me.adda.enhanced_falling_trees.neoforge.platform;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.api.platform.RegistrationHelper;
import me.adda.enhanced_falling_trees.api.platform.registry.DeferredObject;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeRegistrationHelper implements RegistrationHelper {
    private static IEventBus EVENT_BUS;
    private static final Map<DeferredObject<?>, Supplier<?>> ENTRIES = new HashMap<>();
    private static final Map<ResourceLocation, PreparableReloadListener> RELOAD_LISTENERS = new HashMap<>();
    private static DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS;

    public static void setEventBus(IEventBus bus) {
        EVENT_BUS = bus;
        DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, FallingTrees.MOD_ID);
        DATA_SERIALIZERS.register(bus);
        bus.addListener(NeoForgeRegistrationHelper::onRegister);
        NeoForge.EVENT_BUS.addListener(NeoForgeRegistrationHelper::onAddReloadListener);
    }

    @Override
    public void registerEntityDataSerializer(ResourceLocation id, EntityDataSerializer<?> serializer) {
        DATA_SERIALIZERS.register(id.getPath(), () -> serializer);
    }

    @Override
    public <T> void register(DeferredObject<? extends T> deferredObject, Supplier<? extends T> supplier) {
        ENTRIES.put(deferredObject, supplier);
    }

    @Override
    public <T> void registerNewRegistry(Registry<T> registry) {
        // NeoForge manages registries differently
    }

    @Override
    public void registerReloadListener(PackType packType, PreparableReloadListener listener, ResourceLocation id, List<ResourceLocation> dependencies) {
        if (packType == PackType.SERVER_DATA) {
            RELOAD_LISTENERS.put(id, listener);
        }
    }

    private static void onAddReloadListener(AddReloadListenerEvent event) {
        RELOAD_LISTENERS.forEach((id, listener) -> event.addListener(listener));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void onRegister(RegisterEvent event) {
        ENTRIES.forEach((deferredObject, supplier) -> {
            if (event.getRegistryKey().equals(deferredObject.getRegistryKey())) {
                event.register((ResourceKey) deferredObject.getRegistryKey(), deferredObject.getId(), supplier::get);
            }
        });
    }
}
