package me.adda.enhanced_falling_trees.config.client;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class AnimationConfig {

	@ConfigEntry.Gui.CollapsibleObject
	public AnimationConfig.TreeProperties treeProperties = new AnimationConfig.TreeProperties();
	@ConfigEntry.Gui.CollapsibleObject
	public AnimationConfig.BambooProperties bambooProperties = new AnimationConfig.BambooProperties();
	@ConfigEntry.Gui.CollapsibleObject
	public AnimationConfig.CactusProperties cactusProperties = new AnimationConfig.CactusProperties();
	@ConfigEntry.Gui.CollapsibleObject
	public AnimationConfig.ChorusProperties chorusProperties = new AnimationConfig.ChorusProperties();

	public static class TreeProperties {
		public float fallAnimLength = 2.5f;
		public float bounceAngleHeight = 10;
		public float bounceAnimLength = 1;
	}

	public static class BambooProperties {
		public float fallAnimLength = 2.5f;
		public float bounceAngleHeight = 10;
		public float bounceAnimLength = 1;
	}

	public static class CactusProperties {
		public float fallAnimLength = 1.5f;
		public float bounceAngleHeight = 8;
		public float bounceAnimLength = 0.5f;
	}

	public static class ChorusProperties {
		public float fallAnimLength = 2f;
		public float bounceAngleHeight = 10;
		public float bounceAnimLength = 1;
	}
}
