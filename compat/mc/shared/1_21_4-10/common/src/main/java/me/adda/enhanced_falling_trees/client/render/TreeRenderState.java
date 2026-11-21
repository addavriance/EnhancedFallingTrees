package me.adda.enhanced_falling_trees.client.render;

import me.adda.enhanced_falling_trees.api.TreeType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class TreeRenderState extends EntityRenderState {
    public TreeType treeType;
    public Map<BlockPos, BlockState> blocks;
    public BlockPos originPos;
    public Level level;
    public Direction direction;
    public float totalAnimation;
}
