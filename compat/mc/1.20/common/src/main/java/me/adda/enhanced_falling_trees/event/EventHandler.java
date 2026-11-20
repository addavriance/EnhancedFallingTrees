package me.adda.enhanced_falling_trees.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.utils.value.IntValue;
import me.adda.enhanced_falling_trees.api.TreeRegistry;
import me.adda.enhanced_falling_trees.api.TreeType;
import me.adda.enhanced_falling_trees.client.render.TreeRenderer;
import me.adda.enhanced_falling_trees.config.CommonConfig;
import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.entity.TreeEntity;
import me.adda.enhanced_falling_trees.network.ConfigPacket;
import me.adda.enhanced_falling_trees.registry.EntityRegistry;
import me.adda.enhanced_falling_trees.trees.DefaultTree;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Set;

public class EventHandler {
	public static void register() {
		if (Platform.getEnv() == EnvType.CLIENT) {
			if (Platform.isFabric()) ClientLifecycleEvent.CLIENT_SETUP.register(EventHandler::onClientSetup);
			if (Platform.isForge()) onClientSetup(Minecraft.getInstance());
		}
		BlockEvent.BREAK.register(EventHandler::onBlockBreak);
		PlayerEvent.PLAYER_JOIN.register(EventHandler::onPlayerJoin);
	}

	private static void onClientSetup(Minecraft minecraft) {
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(EventHandler::onClientPlayerJoin);

		EntityRendererRegistry.register(EntityRegistry.TREE, TreeRenderer::new);
	}

	private static EventResult onBlockBreak(Level level, BlockPos blockPos, BlockState blockState, ServerPlayer serverPlayer, IntValue intValue) {
		if (serverPlayer != null && makeTreeFall(blockPos, level, serverPlayer)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static void onPlayerJoin(ServerPlayer serverPlayer) {
		ConfigPacket.sendToPlayer(serverPlayer);
	}
	private static void onClientPlayerJoin(LocalPlayer localPlayer) {
		ConfigPacket.sendToServer();
	}

	public static boolean makeTreeFall(BlockPos blockPos, LevelAccessor level, Player player) {
		Optional<TreeType> treeTypeOptional = TreeRegistry.getTreeType(level.getBlockState(blockPos));
		return treeTypeOptional.filter(treeType -> makeTreeFall(treeType, blockPos, level, player)).isPresent();
	}

	public static boolean makeTreeFall(TreeType treeType, BlockPos blockPos, LevelAccessor level, Player player) {
		ItemStack mainItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
		BlockState blockState = level.getBlockState(blockPos);
		CommonConfig commonConfig = FallingTreesConfig.getCommonConfig();

		if (treeType.baseBlockCheck(Blocks.OAK_LOG.defaultBlockState())) {
			if (!treeType.allowedToFall(player, level, blockPos, commonConfig.limitations.treeFallRequirements)) return false;
		} else if (treeType.baseBlockCheck(Blocks.CACTUS.defaultBlockState())) {
			if (!treeType.allowedToFall(player, level, blockPos, commonConfig.limitations.cactusFallRequirements)) return false;
		} else if (treeType.baseBlockCheck(Blocks.BAMBOO.defaultBlockState())) {
			if (!treeType.allowedToFall(player, level, blockPos, commonConfig.limitations.bambooFallRequirements)) return false;
		} else if (treeType.baseBlockCheck(Blocks.CHORUS_PLANT.defaultBlockState())) {
			if (!treeType.allowedToFall(player, level, blockPos, commonConfig.limitations.chorusFallRequirements)) return false;
		}

		Set<BlockPos> treeBlockPos = treeType.blockGatheringAlgorithm(blockPos, level);
		long baseAmount = treeBlockPos.stream().filter(blockPos1 -> treeType.baseBlockCheck(level.getBlockState(blockPos1))).count();

		if (treeType instanceof DefaultTree)
			if (!mainItem.isEmpty() && treeType.allowedTool(mainItem)) {
				mainItem.hurtAndBreak(commonConfig.multiplyToolDamage ? (int) baseAmount : 1, player, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			} else {
				player.causeFoodExhaustion(2F * (commonConfig.multiplyFoodExhaustion ? treeBlockPos.toArray().length : 1));
			}
		else
			if (!mainItem.isEmpty() && treeType.allowedTool(mainItem)) {
				mainItem.hurtAndBreak(1, player, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			} else {
				player.causeFoodExhaustion(2);
			}

		player.awardStat(Stats.BLOCK_MINED.get(blockState.getBlock()), (int) baseAmount);

		TreeEntity.destroyTree(treeBlockPos, blockPos, level, treeType, player);
		return true;
	}
}
