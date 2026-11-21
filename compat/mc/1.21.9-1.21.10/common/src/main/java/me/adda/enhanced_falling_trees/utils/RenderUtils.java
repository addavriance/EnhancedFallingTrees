package me.adda.enhanced_falling_trees.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class RenderUtils {
	private static final int DEFAULT_OVERLAY = OverlayTexture.NO_OVERLAY;
	private static float lightningMultiplier = 0.95f;

	private static BlockRenderDispatcher getBlockRenderDispatcher() {
		return Minecraft.getInstance().getBlockRenderer();
	}

	public static void renderBlock(
			PoseStack.Pose pose,
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

		renderBlockFaces(context, pose, vertexConsumer, faceRenderCondition);
		renderBlockGeneral(context, pose, vertexConsumer);
	}

	public static void setLightningMultiplier(float multiplier) {
		lightningMultiplier = Math.max(0.0f, Math.min(1.0f, multiplier));
	}

	public static float getLightningMultiplier() {
		return lightningMultiplier;
	}

	private static void renderBlockFaces(
			BlockRenderContext context,
			PoseStack.Pose pose,
			VertexConsumer vertexConsumer,
			FaceRenderCondition faceRenderCondition
	) {
		BlockPos.MutableBlockPos mutableBlockPos = context.blockPos.mutable();

		for (Direction direction : Direction.values()) {
			List<BakedQuad> quads = quadsForDirection(context.parts, direction);

			if (quads.isEmpty()) continue;

			mutableBlockPos.setWithOffset(context.blockPos, direction);
			if (!faceRenderCondition.shouldRenderFace(
					context.blockState,
					context.level,
					context.blockPos,
					direction,
					mutableBlockPos
			)) continue;

			renderFace(context, pose, vertexConsumer, quads);
		}
	}

	private static void renderBlockGeneral(
			BlockRenderContext context,
			PoseStack.Pose pose,
			VertexConsumer vertexConsumer
	) {
		List<BakedQuad> quads = quadsForDirection(context.parts, null);

		if (!quads.isEmpty()) {
			renderFace(context, pose, vertexConsumer, quads);
		}
	}

	private static List<BakedQuad> quadsForDirection(List<BlockModelPart> parts, Direction direction) {
		List<BakedQuad> quads = new ArrayList<>();
		for (BlockModelPart part : parts) {
			quads.addAll(part.getQuads(direction));
		}
		return quads;
	}

	private static void renderFace(
			BlockRenderContext context,
			PoseStack.Pose pose,
			VertexConsumer vertexConsumer,
			List<BakedQuad> quads
	) {
		int light = (int) (LevelRenderer.getLightColor(context.level, context.blockPos.above()) * lightningMultiplier);

		for (BakedQuad quad : quads) {
			float shade = context.level.getShade(quad.direction(), quad.shade());
			float r = shade, g = shade, b = shade;
			if (quad.isTinted()) {
				int color = Minecraft.getInstance().getBlockColors().getColor(context.blockState, context.level, context.blockPos, quad.tintIndex());
				r *= ((color >> 16) & 0xFF) / 255.0f;
				g *= ((color >> 8) & 0xFF) / 255.0f;
				b *= (color & 0xFF) / 255.0f;
			}
			vertexConsumer.putBulkData(pose, quad, r, g, b, 1.0f, light, DEFAULT_OVERLAY);
		}
	}

	public static void renderBoundingBox(PoseStack poseStack, AABB boundingBox, VertexConsumer buffer) {
		ShapeRenderer.renderLineBox(poseStack.last(), buffer, boundingBox, 1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static float getDeltaTime() {
		return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks() / 20;
	}

	public interface FaceRenderCondition {
		boolean shouldRenderFace(BlockState state, BlockGetter level, BlockPos offset, Direction face, BlockPos pos);
	}

	private static class BlockRenderContext {
		final BlockState blockState;
		final BlockPos blockPos;
		final Level level;
		final List<BlockModelPart> parts;

		BlockRenderContext(BlockRenderDispatcher dispatcher, BlockState blockState, BlockPos blockPos, Level level) {
			this.blockState = blockState;
			this.blockPos = blockPos;
			this.level = level;

			BlockStateModel model = dispatcher.getBlockModel(blockState);
			RandomSource random = RandomSource.create(blockState.getSeed(blockPos));
			this.parts = model.collectParts(random);
		}
	}
}