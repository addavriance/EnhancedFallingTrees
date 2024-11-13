package me.adda.enhanced_falling_trees.forge;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.config.screen.ConfigScreen;
import me.adda.enhanced_falling_trees.particles.LeavesParticles;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FallingTrees.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FallingTreesClientForge {
	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
				new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen)));
	}

	@SubscribeEvent
	public static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
		Minecraft.getInstance().particleEngine.register(
				ParticleRegistry.LEAVES.get(),
				LeavesParticles.Factory::new
		);
	}
}