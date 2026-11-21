package me.adda.enhanced_falling_trees.forge;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod(FallingTrees.MOD_ID)
public class FallingTreesForge {
    public FallingTreesForge() {
        ParticleRegistry.initialize();

        FallingTrees.init();
    }
}