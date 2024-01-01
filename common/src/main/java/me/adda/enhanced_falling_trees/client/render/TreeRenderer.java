package me.adda.enhanced_falling_trees.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.adda.enhanced_falling_trees.entity.TreeEntity;
import me.adda.enhanced_falling_trees.api.TreeType;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class TreeRenderer extends EntityRenderer<TreeEntity> {

	GroundUtils groundUtil = new GroundUtils(null, null);

	public TreeRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(TreeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		TreeType treeType = entity.getTreeType();
		Level treeLevel = entity.level();

		if (treeType == null) return;

		poseStack.pushPose();

		Map<BlockPos, BlockState> blocks = entity.getBlocks();

		float fallAnimLength = treeType.getFallAnimLength();

		float bounceHeight = treeType.getBounceAngleHeight();
		float bounceAnimLength = treeType.getBounceAnimLength();

		boolean hasBlockAtRoot = entity.getBlockStateOn().isAir() || treeLevel.getBlockState(entity.getOnPos().above().offset(entity.getDirection().getNormal())).isSolid();

		Integer[] groundIndexes = groundUtil.getGroundInfo(entity).indexes;

		float fallAngle = hasBlockAtRoot ? 15 : calculateFallAngle(groundIndexes);

		entity.setAngle(lerp(entity.getAngle(), fallAngle, 0.05f));

		float time = (float) (entity.getLifetime(partialTick) * (Math.PI / 2) / fallAnimLength);

		float fallAnim = bumpCos(time) * entity.getAngle();;
		float bounceAnim = bumpSin((float) ((time - Math.PI / 2) / (bounceAnimLength / (fallAnimLength * 2)))) * bounceHeight;

		float animation = (fallAnim + bounceAnim) - entity.getAngle();;

		Direction direction = entity.getDirection().getOpposite();
		int distance = getDistance(treeType, blocks, 0, direction.getOpposite());

		Vector3f pivot =  new Vector3f(0, 0, (.5f + distance) * treeType.fallAnimationEdgeDistance());
		pivot.rotateY(Math.toRadians(-direction.toYRot()));
		poseStack.translate(-pivot.x, 0, -pivot.z);

		Vector3f vector = new Vector3f(Math.toRadians(animation), 0, 0);
		vector.rotateY(Math.toRadians(-direction.toYRot()));
		Quaternionf quaternion = new Quaternionf().identity().rotateX(vector.x).rotateZ(vector.z);
		poseStack.mulPose(quaternion);

		poseStack.translate(pivot.x, 0, pivot.z);

		poseStack.translate(-.5, 0, -.5);
		VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());

		blocks.forEach((blockPos, blockState) -> {
			poseStack.pushPose();
			poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			RenderUtils.renderBlock(poseStack, blockState, blockPos.offset(entity.getOriginPos()), entity.level(), consumer,
					(state, level, offset, face, pos) -> {
						if (state.canOcclude()) {
							BlockPos facePos = blockPos.offset(face.getNormal());
							if (blocks.containsKey(facePos)) {
								return !state.is(blocks.get(facePos).getBlock());
							}
							return true;
						}
						return true;
					});
			poseStack.popPose();
		});
		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(TreeEntity entity) {
		return null;
	}

	private int getDistance(TreeType treeType, Map<BlockPos, BlockState> blocks, int distance, Direction direction) {
		BlockPos nextBlockPos = new BlockPos(direction.getNormal().multiply(distance + 1));
		if (blocks.containsKey(nextBlockPos) && treeType.baseBlockCheck(blocks.get(nextBlockPos)))
			return getDistance(treeType, blocks, distance + 1, direction);
		return distance;
	}

	private float bumpCos(float time) {
		return (float) Math.max(0, Math.cos(Math.clamp(-Math.PI, Math.PI, time)));
	}

	private float bumpSin(float time) {
		return (float) Math.max(0, Math.sin(Math.clamp(-Math.PI, Math.PI, time)));
	}

	private float calculateFallAngle(Integer[] groundIndexes) {
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, max_index;
		float angle;

		for (Integer index : groundIndexes) {
			if (index < min) min = index;
			if (index > max) max = index;
		}

		max_index = Arrays.asList(groundIndexes).indexOf(max);

		angle = (float) Math.toDegrees(java.lang.Math.atan((double) (max_index+1) / max));

		if (max <= 0) {

			groundIndexes = groundUtil.translateGroundIndexes(groundIndexes);

			for (Integer index : groundIndexes) {
				if (index > max) max = index;
			}

			max_index = Arrays.asList(groundIndexes).indexOf(max);

			angle = 90 + (float) Math.toDegrees(java.lang.Math.atan((double) (max_index + 1) / max));

			boolean allEqual = Arrays.stream(groundIndexes).distinct().count() <= 1;

			if (groundIndexes[0] > 1 || (allEqual && groundIndexes[0] != 0)) angle = 90;

		}

		return angle;
	}

	private float lerp(float a, float b, float f)
	{
		return (float) ((a * (1.0 - f)) + (b * f));
	}

}