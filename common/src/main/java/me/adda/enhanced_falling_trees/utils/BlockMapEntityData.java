package me.adda.enhanced_falling_trees.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockMapEntityData implements EntityDataSerializer<Map<BlockPos, BlockState>> {
	public static final EntityDataSerializer<Map<BlockPos, BlockState>> BLOCK_MAP = new BlockMapEntityData();

	@Override
	public void write(FriendlyByteBuf buffer, Map<BlockPos, BlockState> value) {
		buffer.writeMap(value, FriendlyByteBuf::writeBlockPos, (friendlyByteBuf, state) -> friendlyByteBuf.writeVarInt(Block.getId(state)));
	}

	@Override
	public Map<BlockPos, BlockState> read(FriendlyByteBuf buffer) {
		return buffer.readMap(FriendlyByteBuf::readBlockPos, buf -> Block.stateById(buf.readVarInt()));
	}

	@Override
	public Map<BlockPos, BlockState> copy(Map<BlockPos, BlockState> value) {
		return new ConcurrentHashMap<>(value);
	}
}
