package me.adda.enhanced_falling_trees.entity;

import me.adda.enhanced_falling_trees.api.TreeRegistry;
import me.adda.enhanced_falling_trees.api.TreeType;
import me.adda.enhanced_falling_trees.config.FallingTreesConfig;
import me.adda.enhanced_falling_trees.registry.EntityRegistry;
import me.adda.enhanced_falling_trees.registry.TreeTypeRegistry;
import me.adda.enhanced_falling_trees.utils.BlockMapEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TreeEntity extends Entity {
	public static final EntityDataAccessor<Map<BlockPos, BlockState>> BLOCKS = SynchedEntityData.defineId(TreeEntity.class, BlockMapEntityData.BLOCK_MAP);
	public static final EntityDataAccessor<BlockPos> ORIGIN_POS = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.BLOCK_POS);
	public static final EntityDataAccessor<Float> ANGLE = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Float> TARGET_ANGLE = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Boolean> LEAVES_DROPPED = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<ItemStack> USED_TOOL = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.ITEM_STACK);
	public static final EntityDataAccessor<Direction> FALL_DIRECTION = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.DIRECTION);
	public static final EntityDataAccessor<String> TREE_TYPE_LOCATION = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.STRING);

	public Entity owner = null;
	public TreeType treeType = null;

	public TreeEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
		this.noCulling = true;
	}

	public static void destroyTree(Set<BlockPos> blockPosList, BlockPos blockPos, LevelAccessor levelAccessor, TreeType treeType, Player player) {
		if (levelAccessor instanceof Level level) {
			TreeEntity treeEntity = new TreeEntity(EntityRegistry.TREE.get(), level);
			treeEntity.setPos(blockPos.getCenter().add(0, -.5, 0));
			treeEntity.setData(blockPosList, blockPos, treeType, player, player.getItemBySlot(EquipmentSlot.MAINHAND));
			level.addFreshEntity(treeEntity);


			for (BlockPos pos : blockPosList) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
			}
			for (Map.Entry<BlockPos, BlockState> entry : treeEntity.getBlocks().entrySet()) {
				level.sendBlockUpdated(entry.getKey().offset(blockPos), entry.getValue(), Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	public void setData(Set<BlockPos> blockPosList, BlockPos originBlock, TreeType treeType, Entity owner, ItemStack itemStack) {
		this.owner = owner;
		this.treeType = treeType;

		Map<BlockPos, BlockState> blockPosMap = new ConcurrentHashMap<>();

		for (BlockPos pos : blockPosList) {
			blockPosMap.put(pos.immutable().subtract(originBlock), level().getBlockState(pos));
		}

		this.getEntityData().set(ORIGIN_POS, originBlock);
		this.getEntityData().set(BLOCKS, blockPosMap);
		this.getEntityData().set(USED_TOOL, itemStack);
		this.getEntityData().set(ANGLE, 0f);
		this.getEntityData().set(TARGET_ANGLE, 0f);
		ResourceLocation treeTypeLocation = TreeRegistry.getTreeTypeLocation(treeType);
		if (treeTypeLocation != null)
			this.getEntityData().set(TREE_TYPE_LOCATION, treeTypeLocation.toString());
		this.getEntityData().set(FALL_DIRECTION, Direction.fromYRot(
				-Math.toDegrees(Math.atan2(owner.getX() - originBlock.getX(), owner.getZ() - originBlock.getZ()))
		).getOpposite());
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(BLOCKS, new ConcurrentHashMap<>());
		this.getEntityData().define(ANGLE, 0f);
		this.getEntityData().define(TARGET_ANGLE, 0f);
		this.getEntityData().define(LEAVES_DROPPED, false);
		this.getEntityData().define(ORIGIN_POS, new BlockPos(0, 0, 0));
		this.getEntityData().define(USED_TOOL, ItemStack.EMPTY);
		this.getEntityData().define(FALL_DIRECTION, Direction.NORTH);
		this.getEntityData().define(TREE_TYPE_LOCATION, "");
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	public void tick() {
		super.tick();

		if (!this.isNoGravity()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
		}
		this.move(MoverType.SELF, this.getDeltaMovement());
		if (this.onGround()) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1, -0.5, 1));
		}

		this.getTreeType().entityTick(this);
	}

	public Map<BlockPos, BlockState> getBlocks() {
		return this.getEntityData().get(BLOCKS);
	}

	public int getMaxLifeTimeTick() {
		TreeType treeType = this.getTreeType();

		if (treeType.getClass().equals(TreeTypeRegistry.CACTUS.get().getClass())) {
			return (int) (FallingTreesConfig.getCommonConfig().cactusLifetimeLength * 20);
		} else if (treeType.getClass().equals(TreeTypeRegistry.BAMBOO.get().getClass())) {
			return (int) (FallingTreesConfig.getCommonConfig().bambooLifetimeLength * 20);
		} else if (treeType.getClass().equals(TreeTypeRegistry.CHORUS.get().getClass())) {
			return (int) (FallingTreesConfig.getCommonConfig().chorusLifetimeLength * 20);
		}

		return (int) (FallingTreesConfig.getCommonConfig().treeLifetimeLength * 20);
	}

	public float getLifetime(float partialTick) {
		return (this.tickCount + partialTick) / 20;
	}

	public boolean isLarge() {
		return this.getTreeHeight() > 10;
	}

	public BlockPos getOriginPos() {
		return this.getEntityData().get(ORIGIN_POS);
	}

	public ItemStack getUsedTool() {
		return this.getEntityData().get(USED_TOOL);
	}

	public void setTargetAngle(float angle) {
		this.getEntityData().set(TARGET_ANGLE, angle);
	}

	public float getTargetAngle() {
		return this.getEntityData().get(TARGET_ANGLE);
	}

	public void setAngle(float angle) {
		this.getEntityData().set(ANGLE, angle);
	}

	public float getAngle() {
		return this.getEntityData().get(ANGLE);
	}

	public void setLeavesDropped() {
		this.getEntityData().set(LEAVES_DROPPED, true);
	}

	public boolean getLeavesDropped() {
		return this.getEntityData().get(LEAVES_DROPPED);
	}

	public @NotNull Direction getDirection() {
		return this.getEntityData().get(FALL_DIRECTION);
	}

	public TreeType getTreeType() {
		Optional<TreeType> treeTypeOptional = TreeRegistry.getTreeType(new ResourceLocation(this.getEntityData().get(TREE_TYPE_LOCATION)));
		return treeTypeOptional.orElse(null);
	}

	public int getTreeHeight() {
		Map<BlockPos, BlockState> blocks = this.getBlocks();

		return (int) blocks.entrySet().stream()
			.filter(entry -> {
				BlockPos pos = entry.getKey();
				BlockState state = entry.getValue().getBlock().defaultBlockState();
				return pos.getX() == 0 && pos.getZ() == 0 && this.getTreeType().baseBlockCheck(state);
			})
			.count();
	}
}