package me.adda.enhanced_falling_trees.neoforge;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.neoforge.platform.ClientHelperNeoForge;
import me.adda.enhanced_falling_trees.neoforge.platform.NeoForgeRegistrationHelper;
import me.adda.enhanced_falling_trees.neoforge.platform.event.NeoForgeEventManager;
import me.adda.enhanced_falling_trees.neoforge.platform.network.NeoForgeNetworkService;
import me.adda.enhanced_falling_trees.registry.ParticleRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(FallingTrees.MOD_ID)
public class FallingTreesNeoForge {
    public FallingTreesNeoForge(IEventBus modEventBus) {
        NeoForgeRegistrationHelper.setEventBus(modEventBus);
        NeoForgeEventManager.setModEventBus(modEventBus);
        NeoForgeNetworkService.setModEventBus(modEventBus);
        ClientHelperNeoForge.setModEventBus(modEventBus);

        ParticleRegistry.initialize();
        FallingTrees.init();

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(FallingTreesClientNeoForge::clientInit);
            modEventBus.addListener(FallingTreesClientNeoForge::onParticleFactory);
            modEventBus.addListener(FallingTreesClientNeoForge::onRegisterRenderers);
        }
    }
}
