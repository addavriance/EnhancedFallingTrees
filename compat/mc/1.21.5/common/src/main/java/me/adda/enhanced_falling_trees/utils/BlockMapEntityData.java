package me.adda.enhanced_falling_trees.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BlockMapEntityData implements EntityDataSerializer<Map<BlockPos, BlockState>> {
	public static final EntityDataSerializer<Map<BlockPos, BlockState>> BLOCK_MAP = new BlockMapEntityData();

	@Override
	public Map<BlockPos, BlockState> copy(Map<BlockPos, BlockState> value) {
		return new ConcurrentHashMap<>(value);
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, Map<BlockPos, BlockState>> codec() {
		return StreamCodec.of(
				// Encoder (write)
				(buffer, value) -> {
					// Ограничение количества блоков (например, до 1000)
					Map<BlockPos, BlockState> limitedMap = value;
					if (value.size() > 1000) {
						limitedMap = value.entrySet().stream()
								.limit(1000)
								.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
										(a, b) -> a, ConcurrentHashMap::new));
					}

					buffer.writeMap(limitedMap,
							(buf, pos) -> buf.writeBlockPos(pos),
							(buf, state) -> buf.writeVarInt(Block.getId(state))
					);
				},
				// Decoder
				buffer -> buffer.readMap(
						buf -> buf.readBlockPos(),
						buf -> Block.stateById(buf.readVarInt())
				)
		);
	}
}