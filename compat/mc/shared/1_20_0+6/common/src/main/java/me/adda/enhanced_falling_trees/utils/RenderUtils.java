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
	private static final int DEFAULT_OVERLAY = OverlayTexture.NO_OVERLAY;
	private static float lightningMultiplier = 0.95f;

	private static BlockRenderDispatcher getBlockRenderDispatcher() {
		return Minecraft.getInstance().getBlockRenderer();
	}

	public static void renderBlock(
			PoseStack poseStack,
			BlockState blockState,
			BlockPos blockPos,
			Level level,
			VertexConsumer vertexConsumer,
			FaceRenderCondition faceRenderCondition
	) {
		BlockRenderContext context = new BlockRenderContext(
				getBlockRenderDispatcher(),
				blockState,
				blockPos,
				level
		);

		renderBlockFaces(context, poseStack, vertexConsumer, faceRenderCondition);
		renderBlockGeneral(context, poseStack, vertexConsumer);
	}

	public static void setLightningMultiplier(float multiplier) {
		lightningMultiplier = Math.max(0.0f, Math.min(1.0f, multiplier));
	}

	public static float getLightningMultiplier() {
		return lightningMultiplier;
	}

	private static void renderBlockFaces(
			BlockRenderContext context,
			PoseStack poseStack,
			VertexConsumer vertexConsumer,
			FaceRenderCondition faceRenderCondition
	) {
		BitSet bitSet = new BitSet(3);
		BlockPos.MutableBlockPos mutableBlockPos = context.blockPos.mutable();

		for (Direction direction : Direction.values()) {
			context.random.setSeed(context.seed);
			List<BakedQuad> quads = context.model.getQuads(context.blockState, direction, context.random);

			if (quads.isEmpty()) continue;

			mutableBlockPos.setWithOffset(context.blockPos, direction);
			if (!faceRenderCondition.shouldRenderFace(
					context.blockState,
					context.level,
					context.blockPos,
					direction,
					mutableBlockPos
			)) continue;

			renderFace(context, poseStack, vertexConsumer, quads, bitSet, false);
		}
	}

	private static void renderBlockGeneral(
			BlockRenderContext context,
			PoseStack poseStack,
			VertexConsumer vertexConsumer
	) {
		context.random.setSeed(context.seed);
		List<BakedQuad> quads = context.model.getQuads(context.blockState, null, context.random);

		if (!quads.isEmpty()) {
			renderFace(context, poseStack, vertexConsumer, quads, new BitSet(3), true);
		}
	}

	private static void renderFace(
			BlockRenderContext context,
			PoseStack poseStack,
			VertexConsumer vertexConsumer,
			List<BakedQuad> quads,
			BitSet bitSet,
			boolean isGeneral
	) {
		int light = isGeneral ? -1 : LevelRenderer.getLightColor(context.level, context.blockState, context.blockPos.above());
		light = (int) (light * lightningMultiplier);

		context.modelRenderer.renderModelFaceFlat(
				context.level,
				context.blockState,
				context.blockPos,
				light,
				DEFAULT_OVERLAY,
				isGeneral,
				poseStack,
				vertexConsumer,
				quads,
				bitSet
		);
	}

	public static void renderBoundingBox(PoseStack poseStack, AABB boundingBox, VertexConsumer buffer) {
		LevelRenderer.renderLineBox(poseStack, buffer, boundingBox, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static float getDeltaTime() {
		return Minecraft.getInstance().getDeltaFrameTime() / 20;
	}

	public interface FaceRenderCondition {
		boolean shouldRenderFace(BlockState state, BlockGetter level, BlockPos offset, Direction face, BlockPos pos);
	}

	private static class BlockRenderContext {
		final BlockRenderDispatcher dispatcher;
		final ModelBlockRenderer modelRenderer;
		final BlockState blockState;
		final BlockPos blockPos;
		final Level level;
		final RandomSource random;
		final long seed;
		final BakedModel model;

		BlockRenderContext(BlockRenderDispatcher dispatcher, BlockState blockState, BlockPos blockPos, Level level) {
			this.dispatcher = dispatcher;
			this.modelRenderer = dispatcher.getModelRenderer();
			this.blockState = blockState;
			this.blockPos = blockPos;
			this.level = level;
			this.random = level.getRandom();
			this.seed = blockState.getSeed(blockPos);
			this.model = dispatcher.getBlockModel(blockState);
		}
	}
}