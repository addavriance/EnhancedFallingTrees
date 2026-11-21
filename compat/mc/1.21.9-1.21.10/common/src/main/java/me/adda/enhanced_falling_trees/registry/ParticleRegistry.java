package me.adda.enhanced_falling_trees.registry;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.api.platform.registry.DeferredObject;
import me.adda.enhanced_falling_trees.api.platform.registry.DeferredRegister;
import me.adda.enhanced_falling_trees.particles.CustomParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(FallingTrees.MOD_ID, Registries.PARTICLE_TYPE);

    public static final DeferredObject<SimpleParticleType> LEAVES = register("leaves",
            () -> new CustomParticleType(false));

    public static void initialize() {
        PARTICLE_TYPES.register();
    }

    public static <T extends SimpleParticleType> DeferredObject<T> register(String name, Supplier<T> type) {
        return PARTICLE_TYPES.register(name, type);
    }
}