package me.adda.enhanced_falling_trees.config.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class TreeBreakingSpeedConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableBreakingSpeedModification = true;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    @ConfigEntry.Gui.Tooltip
    public int axeSpeedMultiplier = 80;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    @ConfigEntry.Gui.Tooltip
    public int noAxeSpeedMultiplier = 30;
}