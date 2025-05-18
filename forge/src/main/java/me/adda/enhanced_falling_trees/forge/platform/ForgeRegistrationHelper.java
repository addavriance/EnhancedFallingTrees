package me.adda.enhanced_falling_trees.forge.platform;

import me.adda.enhanced_falling_trees.api.platform.RegistrationHelper;
import me.adda.enhanced_falling_trees.api.platform.registry.DeferredObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeRegistrationHelper implements RegistrationHelper {
    private final Map<DeferredObject<?>, Supplier<?>> entries = new HashMap<>();
    private final Map<ResourceLocation, PreparableReloadListener> reloadListeners = new HashMap<>();
    public ForgeRegistrationHelper() {
        // Регистрируем обработчик события RegisterEvent на шине модов
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onRegister);

        // Регистрируем обработчик на обычной шине событий Forge, а не на шине модов
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
    }

    @Override
    public <T> void register(DeferredObject<? extends T> deferredObject, Supplier<? extends T> supplier) {
        entries.put(deferredObject, supplier);
    }

    @Override
    public <T> void registerNewRegistry(Registry<T> registry) {
        // Forge управляет реестрами иначе, нам не нужно делать это вручную
    }

    @Override
    public void registerReloadListener(PackType packType, PreparableReloadListener listener, ResourceLocation id, List<ResourceLocation> dependencies) {
        // В Forge мы обрабатываем это через событие AddReloadListenerEvent
        if (packType == PackType.SERVER_DATA) {
            reloadListeners.put(id, listener);
        }
    }

    private void onAddReloadListener(AddReloadListenerEvent event) {
        reloadListeners.forEach((id, listener) -> event.addListener(listener));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void onRegister(RegisterEvent event) {
        entries.forEach((deferredObject, supplier) -> {
            if (event.getRegistryKey().equals(deferredObject.getRegistryKey())) {
                event.register((ResourceKey) deferredObject.getRegistryKey(), deferredObject.getId(), () -> supplier.get());
            }
        });
    }
}