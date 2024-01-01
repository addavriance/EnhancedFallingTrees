package me.adda.enhanced_falling_trees.forge;

import dev.architectury.platform.forge.EventBuses;
import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FallingTrees.MOD_ID)
public class FallingTreesForge {
    public FallingTreesForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FallingTrees.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        ParticleRegistry.bootstrap();

        FallingTrees.init();
    }
}