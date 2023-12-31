package me.adda.enhanced_falling_trees.utils;

import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.gametest.FabricGameTestHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class LeavesUtils {

    public static void trySpawnLeafParticle(Level world, BlockPos pos, BlockState leavesState, BlockPos leavesPos, RandomSource random) {
        Minecraft client = Minecraft.getInstance();

        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() - (random.nextDouble() / 3);
        double z = pos.getZ() + random.nextDouble();

        double xV = random.nextGaussian() * 0.09D;
        double yV = random.nextFloat() * 0.1D;
        double zV = random.nextGaussian() * 0.03D;


        SimpleParticleType simpleParticle = ParticleRegistry.LEAVES.get();

        Particle particle = client.particleEngine.createParticle(simpleParticle, x, y, z, xV, yV, zV);

        int leafColor = client.getBlockColors().getColor(leavesState, world, leavesPos);

        BakedModel model = client.getBlockRenderer().getBlockModel(leavesState);

        ResourceLocation texture = spriteToTexture(model.getParticleIcon());

//        double[] leaves_rgb = mergeColorIncludeBiome(world.getBiome(leavesPos).value(), calculateLeafColor(texture, true, leafColor, client));

        double[] leaves_rgb = calculateLeafColor(texture, leafColor, client);

        float red = (float) leaves_rgb[0];
        float green = (float) leaves_rgb[1];
        float blue = (float) leaves_rgb[2];

        if (particle != null)
            particle.setColor(red, green, blue);

    }

    private static double[] calculateLeafColor(ResourceLocation texture, int blockColor, Minecraft client) {
       Resource res = client.getResourceManager().getResource(texture).orElse(null);
       double[] textureColor = {0, 0, 0};

        if (res != null) {
            String resourcePack = res.source().getNamespaces(PackType.CLIENT_RESOURCES).toString();
            TextureCache.Data cache = TextureCache.INST.get(texture);

            if (cache != null && resourcePack.equals(cache.resourcePack)) {
                textureColor = cache.getColor();
            } else {
                try (InputStream is = res.open()) {
                    textureColor = averageColor(ImageIO.read(is));
                    TextureCache.INST.put(texture, new TextureCache.Data(textureColor, resourcePack));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {

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
                ((r / n) / 255.0),
                ((g / n) / 255.0),
                ((b / n) / 255.0)
        };
    }

//    public static double[] mergeColorIncludeBiome(Biome biome, double[] leafColor) {
//        // Wrong
//        int biomeColor = biome.getFoliageColor();
//
//        double red = ((float) (biomeColor >> 16 & 255) / 255) * leafColor[0];
//        double green = ((float) (biomeColor >> 8 & 255) / 255) * leafColor[1];
//        double blue = ((float) (biomeColor & 255) / 255) * leafColor[2];
//
//        return new double[]{red, green, blue};
//    }

    public static ResourceLocation spriteToTexture(TextureAtlasSprite sprite) {
        String texture = sprite.contents().name().getPath();
        return new ResourceLocation(sprite.contents().name().getNamespace(), "textures/" + texture + ".png");
    }

}
