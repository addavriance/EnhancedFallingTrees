package me.adda.enhanced_falling_trees.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.particles.CustomParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(FallingTrees.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> LEAVES = register("leaves",
            () -> new CustomParticleType(false));

    public static void bootstrap() {
        PARTICLE_TYPES.register();
    }

    public static <T extends SimpleParticleType> RegistrySupplier<T> register(String name, Supplier<T> type) {
        return PARTICLE_TYPES.register(new ResourceLocation(FallingTrees.MOD_ID, name), type);
    }
}