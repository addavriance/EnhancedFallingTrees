package me.adda.enhanced_falling_trees.utils;

import me.adda.enhanced_falling_trees.entity.TreeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;

import java.util.Arrays;

public class GroundUtils {

    public static Integer[] getGroundIndexes(TreeEntity entity) {
        int treeHeight = entity.getTreeHeight();
        int offset = 0;

        Integer[] indexes = new Integer[treeHeight];

        for (int i = 0; i < treeHeight; i++) {
            BlockPos blockPos = entity.getOnPos().offset(entity.getDirection().getNormal()).relative(entity.getDirection(), offset + i);

            int groundIndex = 0;

            for (int j = 1; j <= treeHeight; j++) {
                BlockPos checkPos = blockPos.above(j);
                BlockState block = entity.level().getBlockState(checkPos);
                if (block.isSolid() && !block.getTags().toList().contains(BlockTags.LEAVES)) {
                    groundIndex = j;
                }
            }
            if (groundIndex == 0)
                for (int k = 1; k <= treeHeight; k++) {
                    BlockPos checkPos = blockPos.above().below(k);
                    BlockState block = entity.level().getBlockState(checkPos);
                    if (!block.isSolid() || block.getTags().toList().contains(BlockTags.LEAVES)) {
                        groundIndex = -k;
                    } else {
                        break;
                    }
                }

            indexes[i] = groundIndex;
        }
        return indexes;
    }

//    public static BlockPos[] getGroundBlocksPoses(TreeEntity entity, Integer[] indexes) {
//        int treeHeight = entity.getTreeHeight();
//        BlockPos[] blocksPoses = new BlockPos[treeHeight];
//
//        for (int i = 0; i < treeHeight; i++) {
//            BlockPos blockPos = entity.getOnPos().offset(entity.getDirection().getNormal()).relative(entity.getDirection(), i);
//
//            blocksPoses[i] = (blockPos.offset(0, indexes[i], 0));
//        }
//
//        return blocksPoses;
//    }

    public static Integer[] translateGroundIndexes(Integer[] groundIndexes) {
        int length = groundIndexes.length;
        Integer[] translatedArray = new Integer[length];
        Arrays.fill(translatedArray, 0);

        for (int i = 0; i < length; i++) {
            int n = length + groundIndexes[i];
            replaceFromEnd(translatedArray, n, i + 1);
        }

        return translatedArray;
    }

    private static void replaceFromEnd(Integer[] array, int n, int value) {
        for (int i = array.length - n; i < array.length; i++) {
            array[i] = value;
        }
    }

    public static Vec3[] getFallBlockLine(TreeEntity entity) {
        Integer[] indexes = getGroundIndexes(entity);

        int treeHeight = entity.getTreeHeight();

        BlockPos originPos = entity.getOriginPos().relative(entity.getDirection(), 1);

        BlockPos endPos = calculateEndPos(originPos, entity.getDirection(), calculateFallAngle(indexes), treeHeight);

        return calculateBlockLine(originPos, endPos);
    }

    public static Vec3[] calculateBlockLine(BlockPos startBlock, BlockPos endBlock) {
        int length = startBlock.distManhattan(endBlock);

        Vec3[] lineCoordinates = new Vec3[length];

        for (int i = 0; i < length; i++) {
            double t = (double) i / (length - 1);
            double x = (startBlock.getX() + (endBlock.getX() - startBlock.getX()) * t);
            double y = (startBlock.getY() + (endBlock.getY() - startBlock.getY()) * t);
            double z = (startBlock.getZ() + (endBlock.getZ() - startBlock.getZ()) * t);

            lineCoordinates[i] = new Vec3(x, y, z);
        }

        return lineCoordinates;
    }

    public static BlockPos calculateEndPos(BlockPos firstPoint, Direction fallDirection, double angleDegrees, int distance) {
        Vec3i lineVector = fallDirection.getNormal();

        double angleRadians = Math.toRadians(angleDegrees);

        double sin = angleDegrees == 180 ? 0 : Math.sin(angleRadians);
        double cos = angleDegrees == 90 ? 0 : Math.cos(angleRadians);

        int newX = (int) Math.round(firstPoint.getX() + distance * sin * lineVector.getX());
        int newY = (int) Math.round(firstPoint.getY() + distance * cos);
        int newZ = (int) Math.round(firstPoint.getZ() + distance * sin * lineVector.getZ());

        return new BlockPos(newX, newY, newZ);
    }

    public static float calculateFallAngle(Integer[] groundIndexes) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, max_index;
        float angle;

        for (Integer index : groundIndexes) {
            if (index < min) min = index;
            if (index > max) max = index;
        }

        max_index = Arrays.asList(groundIndexes).indexOf(max);

        angle = (float) Math.toDegrees(java.lang.Math.atan((double) (max_index+1) / max));

        if (max <= 0) {

            groundIndexes = GroundUtils.translateGroundIndexes(groundIndexes);

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
}
