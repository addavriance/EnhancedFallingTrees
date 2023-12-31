package me.adda.enhanced_falling_trees.config;

import me.adda.enhanced_falling_trees.config.client.AnimationConfig;
import me.adda.enhanced_falling_trees.config.client.SoundSettingsConfig;
import me.adda.enhanced_falling_trees.FallingTrees;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = FallingTrees.MOD_ID + "_client")
public class ClientConfig implements ConfigData {
	public boolean invertCrouchMining = false;

	@ConfigEntry.Category("sound_settings")
	@ConfigEntry.Gui.TransitiveObject
	public SoundSettingsConfig soundSettings = new SoundSettingsConfig();

	@ConfigEntry.Category("animation")
	@ConfigEntry.Gui.TransitiveObject
	public AnimationConfig animation = new AnimationConfig();
}
