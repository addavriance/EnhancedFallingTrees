package me.adda.enhanced_falling_trees;

import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.event.EventHandler;
import me.adda.enhanced_falling_trees.network.PacketHandler;
import me.adda.enhanced_falling_trees.registry.EntityRegistry;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import me.adda.enhanced_falling_trees.registry.SoundRegistry;
import me.adda.enhanced_falling_trees.registry.TreeTypeRegistry;
import me.adda.enhanced_falling_trees.utils.BlockMapEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

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

		EntityDataSerializers.registerSerializer(BlockMapEntityData.BLOCK_MAP);
	}

	public static final EntityDataAccessor<CompoundTag> PLAYER_CLIENT_CONFIG =
			SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
}
