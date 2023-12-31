package me.adda.enhanced_falling_trees.config.common;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class LimitationsConfig {
	public int maxLeavesDistance = 7;

	@ConfigEntry.Gui.CollapsibleObject
	public FallRequirements treeFallRequirements = new FallRequirements();
	@ConfigEntry.Gui.CollapsibleObject
	public FallRequirements bambooFallRequirements = new FallRequirements();
	@ConfigEntry.Gui.CollapsibleObject
	public FallRequirements chorusFallRequirements = new FallRequirements();
	@ConfigEntry.Gui.CollapsibleObject
	public FallRequirements cactusFallRequirements = new FallRequirements();


	public static class FallRequirements {
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		public RequiredBlockType maxAmountType = RequiredBlockType.BASE_BLOCK_AMOUNT;
		public int maxAmount = 200;
		public int minTreeHeight = 2;
		public boolean onlyRequiredTool = false;

		public enum RequiredBlockType {
			BASE_BLOCK_AMOUNT,
			BLOCK_AMOUNT
		}
	}
}