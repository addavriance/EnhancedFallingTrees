package me.adda.enhanced_falling_trees.fabric;

import me.adda.enhanced_falling_trees.FallingTrees;
import net.fabricmc.api.ModInitializer;

public class FallingTreesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FallingTrees.init();
    }
}