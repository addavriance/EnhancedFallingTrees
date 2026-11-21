package me.adda.enhanced_falling_trees.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.LightCoordsUtil;

public class RenderUtils {
	private static float lightningMultiplier = 1.05f;

	public static void setLightningMultiplier(float multiplier) {
		lightningMultiplier = Math.max(0.0f, Math.min(2.0f, multiplier));
	}

	public static float getLightningMultiplier() {
		return lightningMultiplier;
	}

	public static int scaleLight(int light) {
		int blockLight = Math.clamp(Math.round(LightCoordsUtil.block(light) * lightningMultiplier), 0, 15);
		int skyLight = Math.clamp(Math.round(LightCoordsUtil.sky(light) * lightningMultiplier), 0, 15);
		return LightCoordsUtil.pack(blockLight, skyLight);
	}

	public static float getDeltaTime() {
		return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks() / 20;
	}
}
