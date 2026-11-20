package me.adda.enhanced_falling_trees.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import me.adda.enhanced_falling_trees.FallingTrees;
import net.fabricmc.api.EnvType;
import net.minecraft.resources.ResourceLocation;

public class PacketHandler {
	public static final ResourceLocation CONFIG_PACKET_ID = new ResourceLocation(FallingTrees.MOD_ID, "config_packet");

	public static void register() {
		if (Platform.getEnv() == EnvType.CLIENT) {
			NetworkManager.registerReceiver(NetworkManager.serverToClient(), CONFIG_PACKET_ID, ConfigPacket::clientReceiver);
		}
		NetworkManager.registerReceiver(NetworkManager.clientToServer(), CONFIG_PACKET_ID, ConfigPacket::serverReceiver);
	}
}
