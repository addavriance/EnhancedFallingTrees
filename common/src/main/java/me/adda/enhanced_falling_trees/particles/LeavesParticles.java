package me.adda.enhanced_falling_trees.particles;

import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class LeavesParticles extends SimpleAnimatedParticle {
    protected final float maxRotateSpeed;
    protected final int maxRotateTime;
    protected int rotateTime = 0;

    public LeavesParticles(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites, 0f);

        this.xd = random.nextGaussian() * 0.09D;
        this.yd = random.nextFloat() * 0.1D;
        this.zd = random.nextGaussian() * 0.03D;

        this.gravity = 0.08f + random.nextFloat() * 0.04f;
        this.quadSize = 0.125f;

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

        this.setSprite(sprites.get(random));
        this.setSize(0.02F, 0.02F);

        this.roll = (float) (random.nextFloat() * (2 * Math.PI));

        this.maxRotateTime = (3 + random.nextInt(4 + 1)) * 20;
        this.maxRotateSpeed = (float) ((random.nextBoolean() ? -1 : 1) * (0.1f + 2.4f * random.nextFloat()) * (random.nextFloat() * (2 * Math.PI)) / 20f);

        this.lifetime = (int) (FallingTreesConfig.getCommonConfig().leafParticleLifeTimeLength * 20);

    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        rotateTime = Math.min(rotateTime + 1, maxRotateTime);
        this.roll += (rotateTime / (float) maxRotateTime) * maxRotateSpeed;

        fadeOut();
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)lifetime) * age + 1);
    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet sprites) {
            this.spriteProvider = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double Xv, double Yv, double Zv) {
            LeavesParticles leaves = new LeavesParticles(world, x, y, z, spriteProvider);
            leaves.pickSprite(this.spriteProvider);
            return leaves;
        }
    }
}

