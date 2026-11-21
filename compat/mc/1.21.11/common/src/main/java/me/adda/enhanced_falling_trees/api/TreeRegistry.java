package me.adda.enhanced_falling_trees.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class TreeRegistry {
	private static final Map<Identifier, TreeType> REGISTRIES = new ConcurrentHashMap<>();

	public static Supplier<TreeType> register(Identifier resourceLocation, Supplier<TreeType> treeTypeSupplier) {
		REGISTRIES.put(resourceLocation, treeTypeSupplier.get());
		return treeTypeSupplier;
	}

	public static Optional<TreeType> getTreeType(BlockState blockState) {
		return REGISTRIES.values().stream().filter(treeType -> treeType.enabled() && treeType.mineableBlock(blockState)).findFirst();
	}

	public static Optional<TreeType> getTreeType(Identifier resourceLocation) {
		return Optional.ofNullable(REGISTRIES.get(resourceLocation));
	}

	public static Identifier getTreeTypeLocation(TreeType treeType) {
		for (Map.Entry<Identifier, TreeType> entry : REGISTRIES.entrySet()) {
			if (entry.getValue() == treeType) return entry.getKey();
		}
		return null;
	}
}
