package me.adda.enhanced_falling_trees.mixin;

import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleParticleType.class)
public interface MixinDefaultParticleType {
    @Invoker("<init>")
    static SimpleParticleType init(boolean alwaysShow) {
        return null;
    }
}
