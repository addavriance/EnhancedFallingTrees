package me.adda.enhanced_falling_trees.client;

import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.entity.TreeEntity;
import me.adda.enhanced_falling_trees.registry.SoundRegistry;
import me.adda.enhanced_falling_trees.utils.GroundUtils;
import me.adda.enhanced_falling_trees.utils.LeavesUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class TreeEffects {
    public static void playTreeFallSound(TreeEntity entity) {
        if (!FallingTreesConfig.getClientConfig().soundSettings.enabled) return;

        entity.level().playLocalSound(
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundRegistry.TREE_FALL.get(),
                SoundSource.BLOCKS,
                FallingTreesConfig.getClientConfig().soundSettings.startVolume,
                1f,
                true
        );
    }

    public static void playTreeImpactSound(TreeEntity entity) {
        if (!FallingTreesConfig.getClientConfig().soundSettings.enabled) return;

        SoundEvent sound = GroundUtils.willBeInLiquid(entity) ?
                SoundEvents.PLAYER_SPLASH :
                SoundRegistry.TREE_IMPACT.get();

        entity.level().playLocalSound(
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                sound,
                SoundSource.BLOCKS,
                FallingTreesConfig.getClientConfig().soundSettings.endVolume,
                1f,
                true
        );
    }

    public static void spawnLeafParticles(Vec3[] positions, BlockState leavesState, BlockPos leavesPos, Level level) {
        if (leavesState == null || leavesPos == null) return;

        int particleCount = FallingTreesConfig.getCommonConfig().leafParticleCount;
        for (Vec3 pos : positions) {
            for (int i = 0; i < particleCount; i++) {
                LeavesUtils.trySpawnLeafParticle(
                        level,
                        pos,
                        leavesState,
                        leavesPos,
                        level.getRandom()
                );
            }
        }
    }
}