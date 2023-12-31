package me.adda.enhanced_falling_trees.utils;

import me.adda.enhanced_falling_trees.entity.TreeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroundUtils {
    public final Integer[] indexes;
    public final List<BlockPos> blockData;

    public GroundUtils(Integer[] indexes, List<BlockPos> blockData) {
        this.indexes = indexes;
        this.blockData = blockData;
    }

    public GroundUtils getGroundInfo(TreeEntity entity) {
        int treeHeight = entity.getTreeHeight();
        int offset = 0;

        Integer[] indexes = new Integer[treeHeight];
        List<BlockPos> blockData = new ArrayList<>();

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

            blockData.add(blockPos.offset(0, groundIndex, 0));
            indexes[i] = groundIndex;
        }

        return new GroundUtils(indexes, blockData);
    }

    public Integer[] translateGroundIndexes(Integer[] groundIndexes) {

        int length = groundIndexes.length;
        Integer[] translatedArray = new Integer[length];

        Arrays.fill(translatedArray, 0);

        for (int i = 0; i < length; i++) {
            int n = length + groundIndexes[i];

            replaceFromEnd(translatedArray, n, i + 1);
        }

        return translatedArray;

    }

    public void replaceFromEnd(Integer[] array, int n, int value) {
        for (int i = array.length - n; i < array.length; i++) {
            array[i] = value;
        }
    }

}