package me.adda.enhanced_falling_trees.registry;

import me.adda.enhanced_falling_trees.FallingTrees;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(FallingTrees.MOD_ID, Registries.SOUND_EVENT);

	public static final RegistrySupplier<SoundEvent> TREE_FALL = SOUNDS.register("tree_fall", () ->
			SoundEvent.createFixedRangeEvent(new ResourceLocation(FallingTrees.MOD_ID, "tree_fall"), 16));
	public static final RegistrySupplier<SoundEvent> TREE_IMPACT = SOUNDS.register("tree_impact", () ->
			SoundEvent.createFixedRangeEvent(new ResourceLocation(FallingTrees.MOD_ID, "tree_impact"), 16));
	public static final RegistrySupplier<SoundEvent> CACTUS_FALL = SOUNDS.register("cactus_fall", () ->
			SoundEvent.createFixedRangeEvent(new ResourceLocation(FallingTrees.MOD_ID, "cactus_fall"), 16));
	public static final RegistrySupplier<SoundEvent> CACTUS_IMPACT = SOUNDS.register("cactus_impact", () ->
			SoundEvent.createFixedRangeEvent(new ResourceLocation(FallingTrees.MOD_ID, "cactus_impact"), 16));
	public static final RegistrySupplier<SoundEvent> BAMBOO_IMPACT = SOUNDS.register("bamboo_impact", () ->
			SoundEvent.createFixedRangeEvent(new ResourceLocation(FallingTrees.MOD_ID, "bamboo_impact"), 16));
	public static final RegistrySupplier<SoundEvent> BAMBOO_FALL = SOUNDS.register("bamboo_fall", () ->
			SoundEvent.createFixedRangeEvent(new ResourceLocation(FallingTrees.MOD_ID, "bamboo_fall"), 16));
}
