package me.adda.enhanced_falling_trees.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class TextureCache {
    public static final class Data {
        private final double[] color;
        public final String resourcePack;

        public Data(double[] color, String resourcePack) {
            if (color.length != 3)
                throw new IllegalArgumentException("texture color should have 3 components");

            this.color = new double[3];
            System.arraycopy(color, 0, this.color, 0, 3);
            this.resourcePack = resourcePack;
        }

        public double[] getColor() {
            double[] copy = new double[3];
            System.arraycopy(color, 0, copy, 0, 3);
            return copy;
        }
    }

    public static final ConcurrentHashMap<ResourceLocation, Data> INST = new ConcurrentHashMap<>();

    private TextureCache() {}

}
