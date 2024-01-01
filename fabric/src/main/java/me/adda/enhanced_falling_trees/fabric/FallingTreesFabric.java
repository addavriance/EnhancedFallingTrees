package me.adda.enhanced_falling_trees.fabric;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.particles.LeavesParticles;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.fabricmc.api.ModInitializer;

public class FallingTreesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ParticleRegistry.bootstrap();

        ParticleProviderRegistry.register(ParticleRegistry.LEAVES.get(), LeavesParticles.Factory::new);

        FallingTrees.init();

    }
}