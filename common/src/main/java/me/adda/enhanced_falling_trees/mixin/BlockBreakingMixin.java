package me.adda.enhanced_falling_trees.mixin;

import me.adda.enhanced_falling_trees.utils.TreeBreakingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBreakingMixin {
    @Inject(method = "getDestroyProgress", at = @At("RETURN"), cancellable = true)
    private void onGetDestroySpeed(Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = (BlockState) (Object) this;
        if (!state.is(BlockTags.LOGS)) return;

        TreeBreakingUtils.getCachedMultiplier(state, player, player.level(), pos)
                .ifPresent(multiplier -> cir.setReturnValue(cir.getReturnValue() * multiplier));
    }
}