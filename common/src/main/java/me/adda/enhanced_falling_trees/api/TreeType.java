package me.adda.enhanced_falling_trees.api;

import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.config.common.LimitationsConfig;
import me.adda.enhanced_falling_trees.entity.TreeEntity;
import me.adda.enhanced_falling_trees.network.ConfigPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Set;

public interface TreeType {
	boolean baseBlockCheck(BlockState blockState);

	Set<BlockPos> blockGatheringAlgorithm(BlockPos blockPos, LevelAccessor level);

	default boolean extraRequiredBlockCheck(BlockState blockState) {
		return true;
	}

	default boolean mineableBlock(BlockState blockState) {
		return baseBlockCheck(blockState);
	}

	default boolean allowedTool(ItemStack itemStack, BlockState blockState) {
		return true;
	}

	default void entityTick(TreeEntity entity) {
		Level level = entity.level();
		if (entity.tickCount >= entity.getMaxLifeTimeTick()) {
			ItemStack usedItem = entity.getUsedTool();
			for (Map.Entry<BlockPos, BlockState> entry : entity.getBlocks().entrySet()) {
				if (shouldDropItems(entry.getValue())) {
					BlockEntity blockEntity = null;
					if (entry.getValue().hasBlockEntity())
						blockEntity = level.getBlockEntity(entry.getKey().offset(entity.getOriginPos()));
					Block.dropResources(entry.getValue(), level, entity.getOriginPos(), blockEntity, entity.owner, usedItem);
				}
			}

			entity.remove(Entity.RemovalReason.DISCARDED);
		}
	}

	default boolean allowedToFall(Player player, LevelAccessor level, BlockPos blockPos, LimitationsConfig.FallRequirements fallRequirements) {
		ItemStack mainItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
		BlockState blockState = level.getBlockState(blockPos);
		if (fallRequirements.onlyRequiredTool && this.allowedTool(mainItem, blockState)) return false;

		Set<BlockPos> treeBlockPos = this.blockGatheringAlgorithm(blockPos, level);
		if (treeBlockPos.stream().noneMatch(blockPos1 -> this.extraRequiredBlockCheck(level.getBlockState(blockPos1)))) return false;

		long baseAmount = treeBlockPos.stream().filter(blockPos1 -> this.baseBlockCheck(level.getBlockState(blockPos1))).count();
		long treeHeight = treeBlockPos.stream().filter(blockPos1 -> blockPos1.getX() == blockPos.getX() && blockPos1.getZ() == blockPos.getZ() && this.baseBlockCheck(level.getBlockState(blockPos1))).count();

		if (treeHeight < fallRequirements.minTreeHeight) return false;

		switch (fallRequirements.maxAmountType) {
			case BLOCK_AMOUNT -> {
				if (treeBlockPos.size() > fallRequirements.maxAmount) return false;
			}
			case BASE_BLOCK_AMOUNT -> {
				if (baseAmount > fallRequirements.maxAmount) return false;
			}
		}

		return !(FallingTreesConfig.getCommonConfig().isCrouchMiningAllowed &&
				player.isCrouching() != ConfigPacket.getClientConfig(player).getBoolean("invertCrouchMining"));
	}

	default boolean shouldDropItems(BlockState blockState) {
		return true;
	}

	default float fallAnimationEdgeDistance() {
		return 1;
	}

	default boolean enabled() {
		return true;
	}

	default float getFallAnimLength() {
		return 0;
	}
	default float getBounceAngleHeight() {
		return 0;
	}

	default float getBounceAnimLength() {
		return 0;
	}
}
