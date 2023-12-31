package me.adda.enhanced_falling_trees.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.BitSet;
import java.util.List;

public class RenderUtils {
	public static BlockRenderDispatcher getBlockRenderDispatcher() {
		return Minecraft.getInstance().getBlockRenderer();
	}

	public static void renderBlock(PoseStack poseStack, BlockState blockState, BlockPos blockPos,
								   Level level, VertexConsumer vertexConsumer, FaceRenderCondition faceRenderCondition) {
		ModelBlockRenderer blockRenderer = getBlockRenderDispatcher().getModelRenderer();
		RandomSource random = level.getRandom();
		long seed = blockState.getSeed(blockPos);
		BakedModel model = getBlockRenderDispatcher().getBlockModel(blockState);
		int packedOverlay = OverlayTexture.NO_OVERLAY;

		BitSet bitSet = new BitSet(3);
		BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
		for (Direction direction : Direction.values()) {
			random.setSeed(seed);
			List<BakedQuad> list = model.getQuads(blockState, direction, random);
			if (list.isEmpty()) continue;
			mutableBlockPos.setWithOffset(blockPos, direction);
			if (!faceRenderCondition.shouldRenderFace(blockState, level, blockPos, direction, mutableBlockPos)) continue;
			int i = LevelRenderer.getLightColor(level, blockState, blockPos);
			blockRenderer.renderModelFaceFlat(level, blockState, blockPos, i, packedOverlay, false, poseStack, vertexConsumer, list, bitSet);
		}
		random.setSeed(seed);
		List<BakedQuad> list2 = model.getQuads(blockState, null, random);
		if (!list2.isEmpty()) {
			blockRenderer.renderModelFaceFlat(level, blockState, blockPos, -1, packedOverlay, true,
					poseStack, vertexConsumer, list2, bitSet);
		}
	}

	public static void renderBoundingBox(PoseStack poseStack, AABB boundingBox, VertexConsumer buffer) {
		LevelRenderer.renderLineBox(poseStack, buffer, boundingBox, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	public interface FaceRenderCondition {
		boolean shouldRenderFace(BlockState state, BlockGetter level, BlockPos offset, Direction face, BlockPos pos);
	}

	public static float getDeltaTime() {
		return Minecraft.getInstance().getDeltaFrameTime() / 20;
	}
}
