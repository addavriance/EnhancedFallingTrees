package me.adda.enhanced_falling_trees.registry;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.api.TreeRegistry;
import me.adda.enhanced_falling_trees.api.TreeType;
import me.adda.enhanced_falling_trees.trees.BambooTree;
import me.adda.enhanced_falling_trees.trees.CactusTree;
import me.adda.enhanced_falling_trees.trees.ChorusTree;
import me.adda.enhanced_falling_trees.trees.DefaultTree;
import me.adda.enhanced_falling_trees.trees.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class TreeTypeRegistry {

	public static final Supplier<TreeType> DEFAULT = TreeRegistry.register(new ResourceLocation(FallingTrees.MOD_ID, "default"), DefaultTree::new);
	public static final Supplier<TreeType> CACTUS = TreeRegistry.register(new ResourceLocation(FallingTrees.MOD_ID, "cactus"), CactusTree::new);
	public static final Supplier<TreeType> BAMBOO = TreeRegistry.register(new ResourceLocation(FallingTrees.MOD_ID, "bamboo"), BambooTree::new);
	public static final Supplier<TreeType> CHORUS = TreeRegistry.register(new ResourceLocation(FallingTrees.MOD_ID, "chorus"), ChorusTree::new);
	
	public static void register() {
	}
}
