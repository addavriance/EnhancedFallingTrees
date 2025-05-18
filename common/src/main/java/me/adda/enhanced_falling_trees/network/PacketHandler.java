package me.adda.enhanced_falling_trees.network;

import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.api.platform.EnvType;
import me.adda.enhanced_falling_trees.api.platform.PlatformServices;
import me.adda.enhanced_falling_trees.api.platform.network.NetworkServices;
import net.minecraft.resources.ResourceLocation;

public class PacketHandler {
	public static final ResourceLocation CONFIG_PACKET_ID = new ResourceLocation(FallingTrees.MOD_ID, "config_packet");

	public static void register() {
		if (PlatformServices.getPlatform().getEnvironmentType() == EnvType.CLIENT) {
			NetworkServices.getNetworkService().registerServerToClientPacket(CONFIG_PACKET_ID, ConfigPacket::clientReceiver);
		}
		NetworkServices.getNetworkService().registerClientToServerPacket(CONFIG_PACKET_ID, ConfigPacket::serverReceiver);
	}
}
