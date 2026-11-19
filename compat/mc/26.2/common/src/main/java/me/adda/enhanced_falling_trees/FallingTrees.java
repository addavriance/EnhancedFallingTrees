package me.adda.enhanced_falling_trees;

import me.adda.enhanced_falling_trees.api.platform.PlatformServices;
import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.event.EventHandler;
import me.adda.enhanced_falling_trees.network.PacketHandler;
import me.adda.enhanced_falling_trees.registry.EntityRegistry;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import me.adda.enhanced_falling_trees.registry.SoundRegistry;
import me.adda.enhanced_falling_trees.registry.TreeTypeRegistry;
import me.adda.enhanced_falling_trees.utils.BlockMapEntityData;
import net.minecraft.resources.Identifier;

public class FallingTrees {
	public static final String MOD_ID = "efallingtrees";
	public static final FallingTreesConfig CONFIG = new FallingTreesConfig();

	public static void init() {
		TreeTypeRegistry.register();
		SoundRegistry.SOUNDS.register();
		EntityRegistry.ENTITIES.register();
		PacketHandler.register();
		EventHandler.register();

		PlatformTest.testPlatform();

		PlatformServices.REGISTRATION.registerEntityDataSerializer(
				Identifier.fromNamespaceAndPath(MOD_ID, "block_map"), BlockMapEntityData.BLOCK_MAP);
	}
}
