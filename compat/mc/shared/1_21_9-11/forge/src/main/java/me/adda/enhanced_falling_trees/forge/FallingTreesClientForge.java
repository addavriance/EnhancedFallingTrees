package me.adda.enhanced_falling_trees.forge;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.client.render.TreeRenderer;
import me.adda.enhanced_falling_trees.config.screen.ConfigScreen;
import me.adda.enhanced_falling_trees.particles.LeavesParticles;
import me.adda.enhanced_falling_trees.registry.EntityRegistry;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = FallingTrees.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FallingTreesClientForge {
	static {
		// EntityRenderersEvent.RegisterRenderers and RegisterParticleProvidersEvent are no longer
		// IModBusEvent as of 1.21.9 forge, so @SubscribeEvent annotation scanning can't pick them up anymore.
		RegisterParticleProvidersEvent.BUS.addListener(FallingTreesClientForge::onParticleFactoryRegistration);
		EntityRenderersEvent.RegisterRenderers.getBus(FMLJavaModLoadingContext.get().getModBusGroup())
				.addListener(FallingTreesClientForge::onRegisterRenderers);
	}

	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
				new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen)));
	}

	private static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ParticleRegistry.LEAVES.get(), LeavesParticles.Factory::new);
	}

	private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityRegistry.TREE.get(), TreeRenderer::new);
	}
}