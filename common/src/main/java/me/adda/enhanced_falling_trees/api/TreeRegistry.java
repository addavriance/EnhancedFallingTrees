package me.adda.enhanced_falling_trees.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class TreeRegistry {
	private static final Map<ResourceLocation, TreeType> REGISTRIES = new ConcurrentHashMap<>();

	public static Supplier<TreeType> register(ResourceLocation resourceLocation, Supplier<TreeType> treeTypeSupplier) {
		REGISTRIES.put(resourceLocation, treeTypeSupplier.get());
		return treeTypeSupplier;
	}

	public static Optional<TreeType> getTreeType(BlockState blockState) {
		return REGISTRIES.values().stream().filter(treeType -> treeType.enabled() && treeType.mineableBlock(blockState)).findFirst();
	}

	public static Optional<TreeType> getTreeType(ResourceLocation resourceLocation) {
		return Optional.ofNullable(REGISTRIES.get(resourceLocation));
	}

	public static ResourceLocation getTreeTypeLocation(TreeType treeType) {
		for (Map.Entry<ResourceLocation, TreeType> entry : REGISTRIES.entrySet()) {
			if (entry.getValue() == treeType) return entry.getKey();
		}
		return null;
	}
}
