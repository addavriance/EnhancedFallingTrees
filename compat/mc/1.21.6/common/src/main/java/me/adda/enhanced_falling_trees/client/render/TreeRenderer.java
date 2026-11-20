package me.adda.enhanced_falling_trees.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.adda.enhanced_falling_trees.api.TreeType;
import me.adda.enhanced_falling_trees.entity.TreeEntity;
import me.adda.enhanced_falling_trees.utils.GroundUtils;
import me.adda.enhanced_falling_trees.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class TreeRenderer extends EntityRenderer<TreeEntity, TreeRenderState> {
	private static final float PI = (float) Math.PI;
	private static final float HALF_PI = PI / 2;
	private static final float TWO_PI = PI * 2;
	private static final float WATER_LERP_FACTOR = 0.01f;
	private static final float NORMAL_LERP_FACTOR = 0.05f;
	private static final float MIN_BOUNCE_TRIGGER_ANGLE = 10f;
	private static final int MAX_TREE_RADIUS = 3;

	public TreeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public TreeRenderState createRenderState() {
		return new TreeRenderState();
	}

	@Override
	public void extractRenderState(TreeEntity entity, TreeRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);

		TreeType treeType = entity.getTreeType();
		state.treeType = treeType;
		state.blocks = entity.getBlocks();
		state.originPos = entity.getOriginPos();
		state.level = entity.level();

		if (treeType != null && !state.blocks.isEmpty()) {
			AnimationParameters params = calculateAnimationParameters(entity, partialTick, treeType);
			state.direction = entity.getDirection().getOpposite();
			state.totalAnimation = params.totalAnimation();
		}
	}

	@Override
	protected boolean affectedByCulling(TreeEntity entity) {
		return false;
	}

	@Override
	public void render(TreeRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		if (state.treeType == null || state.blocks == null || state.blocks.isEmpty()) return;

		poseStack.pushPose();
		try {
			renderTree(state, poseStack, buffer);
		} finally {
			poseStack.popPose();
		}
	}

	private void renderTree(TreeRenderState state, PoseStack poseStack, MultiBufferSource buffer) {
		applyTreeTransformations(poseStack, state.blocks, state.treeType, state.direction, state.totalAnimation);

		renderTreeBlocks(state, poseStack, buffer);
	}

	private record AnimationParameters(float fallAnim, float bounceAnim, float totalAnimation, float targetAngle) {
	}

	private AnimationParameters calculateAnimationParameters(TreeEntity entity, float partialTick, TreeType treeType) {
		float fallAnimLength = treeType.getFallAnimLength();
		float time = entity.getLifetime(partialTick) * HALF_PI / fallAnimLength;

		boolean isInLiquid = GroundUtils.willBeInLiquid(entity);
		float targetAngle = calculateTargetAngle(entity, time, isInLiquid);
		entity.setTargetAngle(targetAngle);

		float fallAnim = calculateFallAnimation(time, targetAngle);

		float bounceHeight = calculateBounceHeight(entity, treeType);
		float bounceAnim = calculateBounceAnimation(time, treeType, bounceHeight, isInLiquid);

		float totalAnimation = (fallAnim + bounceAnim) - targetAngle;

		return new AnimationParameters(fallAnim, bounceAnim, totalAnimation, targetAngle);
	}

	private float calculateTargetAngle(TreeEntity entity, float time, boolean isInLiquid) {
		Integer[] groundIndexes = GroundUtils.getGroundIndexes(entity, false);
		Integer[] waterGroundIndexes = GroundUtils.getGroundIndexes(entity, true);

		float currentAngle = entity.getTargetAngle();
		float targetAngle;

		if (bumpCos(time) * currentAngle <= MIN_BOUNCE_TRIGGER_ANGLE && isInLiquid) {
			float waterHeight = GroundUtils.calculateAverageWaterHeight(entity, waterGroundIndexes);
			float waterAngle = GroundUtils.calculateFallAngle(waterGroundIndexes);
			targetAngle = waterAngle + (waterAngle - waterAngle * waterHeight);
		} else {
			targetAngle = GroundUtils.calculateFallAngle(groundIndexes);
		}

		float lerpFactor = (bumpCos(time) * currentAngle <= 1 && isInLiquid) ?
				WATER_LERP_FACTOR : NORMAL_LERP_FACTOR;

		return Math.lerp(currentAngle, targetAngle, lerpFactor);
	}

	private float calculateFallAnimation(float time, float targetAngle) {
		return bumpCos(time) * targetAngle;
	}

	private float calculateBounceHeight(TreeEntity entity, TreeType treeType) {
		float maxBounceHeight = treeType.getBounceAngleHeight();
		return Math.clamp(0, maxBounceHeight, Math.round(entity.getTargetAngle() / 10));
	}

	private float calculateBounceAnimation(float time, TreeType treeType,
										   float bounceHeight, boolean isInLiquid) {
		float bounceAnimLength = treeType.getBounceAnimLength();

		if (isInLiquid) {
			return bumpSinLiquid((time + bounceAnimLength) * treeType.getBounceAngleHeight());
		} else {
			float adjustedTime = (time - HALF_PI) / (bounceAnimLength / (treeType.getFallAnimLength() * 2));
			return bumpSin(adjustedTime) * bounceHeight;
		}
	}

	private void applyTreeTransformations(PoseStack poseStack, Map<BlockPos, BlockState> blocks,
										  TreeType treeType, Direction direction, float totalAnimation) {
		int distance = calculateTreeDistance(blocks, direction.getOpposite(), treeType);

		Vector3f pivot = calculatePivotPoint(direction, distance, treeType.fallAnimationEdgeDistance());
		poseStack.translate(-pivot.x, 0, -pivot.z);

		applyRotation(poseStack, totalAnimation, direction);

		poseStack.translate(pivot.x, 0, pivot.z);

		poseStack.translate(-.5, 0, -.5);
	}

	private Vector3f calculatePivotPoint(Direction direction, int distance, float edgeDistance) {
		Vector3f pivot = new Vector3f(0, 0, (.5f + distance) * edgeDistance);
		pivot.rotateY(Math.toRadians(-direction.toYRot()));
		return pivot;
	}

	private void applyRotation(PoseStack poseStack, float totalAnimation, Direction direction) {
		Vector3f rotationVector = new Vector3f(Math.toRadians(totalAnimation), 0, 0);
		rotationVector.rotateY(Math.toRadians(-direction.toYRot()));

		Quaternionf rotation = new Quaternionf().identity()
				.rotateX(rotationVector.x)
				.rotateZ(rotationVector.z);

		poseStack.mulPose(rotation);
	}

	private void renderTreeBlocks(TreeRenderState state, PoseStack poseStack, MultiBufferSource buffer) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());

		state.blocks.forEach((blockPos, blockState) -> renderBlock(state, poseStack, consumer, blockPos, blockState));
	}

	private void renderBlock(TreeRenderState state, PoseStack poseStack, VertexConsumer consumer,
							 BlockPos blockPos, BlockState blockState) {
		poseStack.pushPose();
		try {
			poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

			RenderUtils.renderBlock(poseStack, blockState, blockPos.offset(state.originPos),
					state.level, consumer, (s, level, offset, face, pos) ->
							shouldRenderFace(s, state.blocks, blockPos, face));
		} finally {
			poseStack.popPose();
		}
	}

	private boolean shouldRenderFace(BlockState state, Map<BlockPos, BlockState> blocks,
									 BlockPos pos, Direction face) {
		if (!state.canOcclude()) return true;

		BlockPos facePos = pos.offset(face.getUnitVec3i());
		if (!blocks.containsKey(facePos)) return true;

		return !state.is(blocks.get(facePos).getBlock());
	}

	private int calculateTreeDistance(Map<BlockPos, BlockState> blocks, Direction direction, TreeType treeType) {
		int distance = 0;
		while (shouldContinueDistance(blocks, distance, direction, treeType)) {
			distance++;
		}
		return distance;
	}

	private boolean shouldContinueDistance(Map<BlockPos, BlockState> blocks, int distance, Direction direction, TreeType treeType) {
		int currentWidth = getWidthAtDistance(blocks, distance, direction, treeType);
		int nextWidth = getWidthAtDistance(blocks, distance + 1, direction, treeType);

		return nextWidth >= currentWidth * 0.7 &&
				blocks.containsKey(new BlockPos(direction.getUnitVec3i().multiply(distance + 1)));
	}

	private int getWidthAtDistance(Map<BlockPos, BlockState> blocks, int distance, Direction direction, TreeType treeType) {
		BlockPos center = new BlockPos(direction.getUnitVec3i().multiply(distance));
		int width = 0;

		for (int x = -MAX_TREE_RADIUS; x <= MAX_TREE_RADIUS; x++) {
			for (int y = -MAX_TREE_RADIUS; y <= MAX_TREE_RADIUS; y++) {
				BlockPos checkPos = center.offset(direction.getClockWise().getUnitVec3i().multiply(x)).above(y);
				if (blocks.containsKey(checkPos) && treeType.baseBlockCheck(blocks.get(checkPos))) {
					width++;
				}
			}
		}

		return width;
	}

	private float bumpCos(float time) {
		return Math.max(0, Math.cos(Math.clamp(-PI, PI, time)));
	}

	private float bumpSin(float time) {
		return Math.max(0, Math.sin(Math.clamp(-PI, PI, time)));
	}

	private float bumpSinLiquid(float time) {
		return Math.sin(time);
	}
}
