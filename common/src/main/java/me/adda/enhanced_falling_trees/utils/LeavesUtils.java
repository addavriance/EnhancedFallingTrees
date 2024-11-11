package me.adda.enhanced_falling_trees.utils;

import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LeavesUtils {
    private static final Minecraft client = Minecraft.getInstance();
    private static final float MIN_LIGHT_LEVEL = 0.3f;

    public static void trySpawnLeafParticle(Level world, Vec3 pos, BlockState leavesState, BlockPos leavesPos, RandomSource random) {
        if (leavesState == null || leavesPos == null) return;

        SimpleParticleType simpleParticle = ParticleRegistry.LEAVES.get();
        if (simpleParticle == null) return;

        double x = pos.x + random.nextDouble();
        double y = pos.y - (random.nextDouble() / 3);
        double z = pos.z + random.nextDouble();

        double xV = random.nextGaussian() * 0.09D;
        double yV = random.nextFloat() * 0.1D;
        double zV = random.nextGaussian() * 0.03D;

        Particle particle = client.particleEngine.createParticle(simpleParticle, x, y, z, xV, yV, zV);
        if (particle == null) return;

        int leafColor = getLeafColor(world, leavesState, leavesPos);

        BakedModel model = client.getModelManager().getBlockModelShaper().getBlockModel(leavesState);
        List<BakedQuad> quads = model.getQuads(leavesState, Direction.DOWN, random);
        TextureAtlasSprite sprite = quads.isEmpty() ? model.getParticleIcon() : quads.get(0).getSprite();
        boolean shouldColor = quads.isEmpty() || quads.stream().anyMatch(BakedQuad::isTinted);

        ResourceLocation texture = spriteToTexture(sprite);
        double[] leaves_rgb = calculateLeafColor(texture, shouldColor, leafColor);

        float lightLevel = calculateLightLevel((ClientLevel) world);

        float red = (float) leaves_rgb[0] * lightLevel;
        float green = (float) leaves_rgb[1] * lightLevel;
        float blue = (float) leaves_rgb[2] * lightLevel;

        particle.setColor(red, green, blue);
    }

    private static int getLeafColor(Level world, BlockState leavesState, BlockPos leavesPos) {
        try {
            return client.getBlockColors().getColor(leavesState, world, leavesPos, 0);
        } catch (Exception e) {
            return leavesState.getMapColor(world, leavesPos).col;
        }
    }

    private static float calculateLightLevel(ClientLevel level) {
        long timeOfDay = level.getDayTime() % 24000;

        final long NOON = 6000;
        final long SUNSET = 12000;
        final long NIGHT = 13000;
        final long SUNRISE = 23000;

        float lightLevel;

        if (timeOfDay >= NOON && timeOfDay < SUNSET) {
            lightLevel = 1.0f;
        } else if (timeOfDay >= SUNSET && timeOfDay < NIGHT) {
            float progress = (float)(timeOfDay - SUNSET) / (NIGHT - SUNSET);
            lightLevel = 1.0f - (progress * 0.5f);
        } else if (timeOfDay >= NIGHT && timeOfDay < SUNRISE) {
            lightLevel = 0.5f;
        } else {
            float progress;
            if (timeOfDay >= SUNRISE) {
                progress = (float)(timeOfDay - SUNRISE) / (24000 - SUNRISE);
            } else {
                progress = (float)(timeOfDay + (24000 - SUNRISE)) / (24000 - SUNRISE);
            }
            lightLevel = 0.5f + (progress * 0.5f);
        }

        float rainLevel = level.getRainLevel(client.getFrameTime());
        float thunderLevel = level.getThunderLevel(client.getFrameTime());

        lightLevel *= (1.0F - rainLevel * 0.4F);
        lightLevel *= (1.0F - thunderLevel * 0.4F);

        return Math.max(MIN_LIGHT_LEVEL, lightLevel);
    }

    private static double[] calculateLeafColor(ResourceLocation texture, boolean shouldColor, int blockColor) {
        Resource res = client.getResourceManager().getResource(texture).orElse(null);
        if (res == null) return new double[] {1, 1, 1};

        String resourcePack = res.sourcePackId();
        TextureCache.Data cache = TextureCache.INST.get(texture);
        double[] textureColor;

        if (cache != null && resourcePack.equals(cache.resourcePack)) {
            textureColor = cache.getColor();
        } else {
            try (InputStream is = res.open()) {
                textureColor = averageColor(ImageIO.read(is));
                TextureCache.INST.put(texture, new TextureCache.Data(textureColor, resourcePack));
            } catch (IOException e) {
                return new double[] {1, 1, 1};
            }
        }

        if (shouldColor && blockColor != -1) {
            textureColor[0] *= (blockColor >> 16 & 255) / 255.0;
            textureColor[1] *= (blockColor >> 8 & 255) / 255.0;
            textureColor[2] *= (blockColor & 255) / 255.0;
        }

        return textureColor;
    }

    public static double[] averageColor(BufferedImage image) {
        double r = 0;
        double g = 0;
        double b = 0;
        int n = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y), true);
                if (c.getAlpha() == 255) {
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                    n++;
                }
            }
        }

        return new double[] {
                (r / n) / 255.0,
                (g / n) / 255.0,
                (b / n) / 255.0
        };
    }

    public static ResourceLocation spriteToTexture(TextureAtlasSprite sprite) {
        String texture = sprite.contents().name().getPath();
        return new ResourceLocation(sprite.contents().name().getNamespace(), "textures/" + texture + ".png");
    }
}