package me.adda.enhanced_falling_trees.api;

import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.config.common.LimitationsConfig;
import me.adda.enhanced_falling_trees.entity.TreeEntity;
import me.adda.enhanced_falling_trees.network.ConfigPacket;
import me.adda.enhanced_falling_trees.utils.GroundUtils;
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
import net.minecraft.world.phys.Vec3;

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

	default boolean allowedTool(ItemStack itemStack) {
		return true;
	}

	default void entityTick(TreeEntity entity) {
		handleDrops(entity);
		handleSpecialEffects(entity);
	}

	default void handleDrops(TreeEntity entity) {
		if (entity.tickCount >= entity.getMaxLifeTimeTick()) {
			Level level = entity.level();
			Vec3[] fallBlockLine = GroundUtils.getFallBlockLine(entity);
			int counter = 0;

			for (Map.Entry<BlockPos, BlockState> entry : entity.getBlocks().entrySet()) {
				if (shouldDropItems(entry.getValue())) {
					BlockEntity blockEntity = null;
					if (entry.getValue().hasBlockEntity()) {
						blockEntity = level.getBlockEntity(entry.getKey().offset(entity.getOriginPos()));
					}

					BlockPos pos = new BlockPos(
							(int) fallBlockLine[counter].x,
							(int) fallBlockLine[counter].y,
							(int) fallBlockLine[counter].z
					);

					Block.dropResources(
							entry.getValue(),
							level,
							pos,
							blockEntity,
							entity.owner,
							entity.getUsedTool()
					);

					counter = counter == fallBlockLine.length - 1 ? 0 : counter + 1;
				}
			}

			entity.remove(Entity.RemovalReason.DISCARDED);
		}
	}

	default void handleSpecialEffects(TreeEntity entity) {

	}

	default boolean allowedToFall(Player player, LevelAccessor level, BlockPos blockPos, LimitationsConfig.FallRequirements fallRequirements) {
		ItemStack mainItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
		if (fallRequirements.onlyRequiredTool && !this.allowedTool(mainItem)) return false;

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

		boolean invertCrouch = ConfigPacket.getClientConfig(player).getBoolean("invertCrouchMining");

		return !player.isCrouching() || (invertCrouch != FallingTreesConfig.getCommonConfig().isCrouchMiningAllowed);
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

	default boolean supportsParticles() {
		return false;
	}

	default BlockState getParticleBlockState(TreeEntity entity) {
		return null;
	}

	default BlockPos getParticleBlockPos(TreeEntity entity) {
		return null;
	}
}
