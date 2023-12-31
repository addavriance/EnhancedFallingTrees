package me.adda.enhanced_falling_trees.trees;

import me.adda.enhanced_falling_trees.config.CommonConfig;
import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.api.TreeType;
import me.adda.enhanced_falling_trees.network.ConfigPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class ChorusTree implements TreeType {
	@Override
	public boolean baseBlockCheck(BlockState blockState) {
		return blockState.is(Blocks.CHORUS_PLANT);
	}

	@Override
	public boolean extraRequiredBlockCheck(BlockState blockState) {
		return blockState.is(Blocks.CHORUS_FLOWER);
	}

	@Override
	public Set<BlockPos> blockGatheringAlgorithm(BlockPos blockPos, LevelAccessor level) {
		Set<BlockPos> blocks = new HashSet<>();
		Set<BlockPos> loopedBlocks = new HashSet<>();

		loopBlocks(level, blockPos, blocks, loopedBlocks);
		return blocks;
	}

	public void loopBlocks(LevelAccessor level, BlockPos originPos, Set<BlockPos> blocks, Set<BlockPos> loopedBlocks) {
		if (loopedBlocks.contains(originPos))
			return;

		loopedBlocks.add(originPos);

		BlockState blockState = level.getBlockState(originPos);
		if (this.baseBlockCheck(blockState) || this.extraRequiredBlockCheck(blockState)) {
			blocks.add(originPos);

			if (this.baseBlockCheck(blockState)) {
				for (Direction direction : Direction.values()) {
					if (blockState.getValue(ChorusPlantBlock.PROPERTY_BY_DIRECTION.get(direction))) {
						BlockPos neighborPos = originPos.offset(direction.getNormal());
						loopBlocks(level, neighborPos, blocks, loopedBlocks);
					}
				}
			}
		}
	}

	@Override
	public float fallAnimationEdgeDistance() {
		return 6f / 16f;
	}

	@Override
	public boolean shouldDropItems(BlockState blockState) {
		return blockState.is(Blocks.CHORUS_FLOWER);
	}

	@Override
	public boolean enabled() {
		return !FallingTreesConfig.getCommonConfig().features.disableChorusTrees;
	}

	@Override
	public float getFallAnimLength() {
		return FallingTreesConfig.getClientConfig().animation.chorusProperties.fallAnimLength;
	}
	@Override
	public float getBounceAngleHeight() {
		return FallingTreesConfig.getClientConfig().animation.chorusProperties.bounceAngleHeight;
	}
	@Override
	public float getBounceAnimLength() {
		return FallingTreesConfig.getClientConfig().animation.chorusProperties.bounceAnimLength;
	}
}
