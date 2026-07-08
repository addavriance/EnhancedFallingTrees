package me.adda.enhanced_falling_trees.network;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import me.adda.enhanced_falling_trees.FallingTrees;
import me.adda.enhanced_falling_trees.api.platform.network.NetworkService;
import me.adda.enhanced_falling_trees.api.platform.network.NetworkServices;
import me.adda.enhanced_falling_trees.api.platform.network.PacketContext;
import me.adda.enhanced_falling_trees.config.ClientConfig;
import me.adda.enhanced_falling_trees.config.CommonConfig;
import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigPacket {
	private static final Map<UUID, Boolean> INVERT_CROUCH_MINING = new ConcurrentHashMap<>();

	public static void clientReceiver(FriendlyByteBuf buf, PacketContext context) {
		byte[] configBytes = buf.readByteArray();
		FallingTrees.CONFIG.setCommonConfig(new Gson().fromJson(new String(configBytes), CommonConfig.class));
	}

	public static void sendToPlayer(ServerPlayer player) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeByteArray(new Gson().toJson(FallingTrees.CONFIG.commonConfigHolder.getConfig()).getBytes());
		System.out.println(buf.readableBytes());
		NetworkServices.getNetworkService().sendToPlayer(player, PacketHandler.CONFIG_PACKET_ID, buf);
	}

	public static void serverReceiver(FriendlyByteBuf buf, PacketContext context) {
		ClientConfig clientConfig = new Gson().fromJson(new String(buf.readByteArray()), ClientConfig.class);
		INVERT_CROUCH_MINING.put(context.getPlayer().getUUID(), clientConfig.invertCrouchMining);
	}

	public static void sendToServer() {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeByteArray(new Gson().toJson(FallingTreesConfig.getClientConfig()).getBytes());
		NetworkServices.getNetworkService().sendToServer(PacketHandler.CONFIG_PACKET_ID, buf);
	}

	public static boolean getClientConfig(Player player) {
		return INVERT_CROUCH_MINING.getOrDefault(player.getUUID(), false);
	}
}
