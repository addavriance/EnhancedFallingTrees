package me.adda.enhanced_falling_trees.fabric;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import me.adda.enhanced_falling_trees.particles.LeavesParticles;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FallingTreesClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.register(ParticleRegistry.LEAVES.get(), LeavesParticles.Factory::new);
    }
}