package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.init.DSTiles;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class DynamicSlabBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DynamicSlabBlock() {
        super(Properties.of(Material.STONE).noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean crack(ClientLevel level, BlockState actualState, BlockState state, BlockPos pos, Direction direction, ParticleEngine manager) {
        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            float f = 0.1F;
            AABB aabb = actualState.getShape(level, pos).bounds();
            double d0 = (double)i + level.random.nextDouble() * (aabb.maxX - aabb.minX - (double)0.2F) + (double)0.1F + aabb.minX;
            double d1 = (double)j + level.random.nextDouble() * (aabb.maxY - aabb.minY - (double)0.2F) + (double)0.1F + aabb.minY;
            double d2 = (double)k + level.random.nextDouble() * (aabb.maxZ - aabb.minZ - (double)0.2F) + (double)0.1F + aabb.minZ;
            if (direction == Direction.DOWN) {
                d1 = (double)j + aabb.minY - (double)0.1F;
            }

            if (direction == Direction.UP) {
                d1 = (double)j + aabb.maxY + (double)0.1F;
            }

            if (direction == Direction.NORTH) {
                d2 = (double)k + aabb.minZ - (double)0.1F;
            }

            if (direction == Direction.SOUTH) {
                d2 = (double)k + aabb.maxZ + (double)0.1F;
            }

            if (direction == Direction.WEST) {
                d0 = (double)i + aabb.minX - (double)0.1F;
            }

            if (direction == Direction.EAST) {
                d0 = (double)i + aabb.maxX + (double)0.1F;
            }

            Particle particle = manager.createParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            if (particle != null) {
                particle = particle.setPower(0.2F).scale(0.6F);
                manager.add(particle);
                return true;
            }
        }
        return false;
    }

    public static Optional<SlabTileEntity> getTile(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof SlabTileEntity ? Optional.of((SlabTileEntity) tile) : Optional.empty();
    }

    public static Optional<IBlockInfo> getAvailable(BlockGetter world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> Optional.of(tile.getPositiveBlockInfo().getBlockState() != null ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo()));
    }

    public static int min(BlockGetter world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(BlockGetter world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : 0)).orElse(0);
    }

    public static float maxFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(BlockGetter world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0) + (tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(BlockGetter world, BlockPos pos, Consumer<IBlockInfo> consumer) {
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                consumer.accept(tile.getPositiveBlockInfo());
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                consumer.accept(tile.getNegativeBlockInfo());
            return null;
        });
    }

    public static boolean both(BlockGetter world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo()) && predicate.test(tile.getNegativeBlockInfo())).orElse(false);
    }

    public static boolean either(BlockGetter world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo())) || (tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getNegativeBlockInfo()))).orElse(false);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SlabTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, DSTiles.DYNAMIC_SLAB.get(), SlabTileEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        runIfAvailable(world, pos, i -> {
            if (i.getBlockState().getBlock() instanceof SimpleWaterloggedBlock)
                ((SimpleWaterloggedBlock) i.getBlockState().getBlock()).placeLiquid(i.getWorld(), pos, i.getBlockState(), fluidState);
        });
        return SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluidState);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return SimpleWaterloggedBlock.super.canPlaceLiquid(world, pos, state, fluid) && either(world, pos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getWorld(), pos, i.getBlockState()));
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = context.getLevel().getBlockState(blockpos);
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        if (blockstate.is(this)) {
            return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER && either(context.getLevel(), blockpos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getWorld(), i.getPos(), i.getBlockState())));
        } else {
            return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED))
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return type == PathComputationType.WATER && world.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        return maxFloat(world, pos, i -> i.getBlockState().getExplosionResistance(i.getWorld(), pos, explosion));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return max(world, pos, i -> i.getBlockState().getLightEmission(i.getWorld(), pos));
    }

    // test?
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1F;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return null;
    }

    //    @Override
//    public boolean canHarvestBlock(BlockState state, LevelReader world, BlockPos pos, PlayerEntity player) {
//        return both(world, pos, i -> i.getBlockState().canHarvestBlock(i.getWorld(), pos, player));
//    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity tileEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        List<ItemStack> drops = new ArrayList<>();
        if (tileEntity instanceof SlabTileEntity) {
            SlabTileEntity tile = (SlabTileEntity) tileEntity;
            if (tile.getPositiveBlockInfo().getBlockState() != null) {
                LootContext.Builder newBuilder = builder.withParameter(LootContextParams.BLOCK_STATE, tile.getPositiveBlockInfo().getBlockState());
                if (tile.getPositiveBlockInfo().getBlockEntity() != null)
                    newBuilder = newBuilder.withParameter(LootContextParams.BLOCK_ENTITY, tile.getPositiveBlockInfo().getBlockEntity());
                drops.addAll(tile.getPositiveBlockInfo().getBlockState().getDrops(newBuilder));
            }
            if (tile.getNegativeBlockInfo().getBlockState() != null) {
                LootContext.Builder newBuilder = builder.withParameter(LootContextParams.BLOCK_STATE, tile.getNegativeBlockInfo().getBlockState());
                if (tile.getNegativeBlockInfo().getBlockEntity() != null)
                    newBuilder = newBuilder.withParameter(LootContextParams.BLOCK_ENTITY, tile.getNegativeBlockInfo().getBlockEntity());
                drops.addAll(tile.getNegativeBlockInfo().getBlockState().getDrops(newBuilder));
            }
        }
        return drops;
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (willHarvest)
            return true;
        if (player.isCreative() && player.isCrouching()) {
            playerDestroy(world, player, pos, state, world.getBlockEntity(pos), ItemStack.EMPTY);
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative()) {
            runIfAvailable(world, pos, i -> i.getBlockState().onRemove(i.getWorld(), pos, Blocks.AIR.defaultBlockState(), false));
            super.playerWillDestroy(world, pos, state, player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public BlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> {
            if (world == null || pos == null)
                return -1;
            return getTile(world, pos).map(tile -> {
                if (tintIndex >= ClientConstants.TINT_OFFSET)
                    return tile.getPositiveBlockInfo().getBlockState() != null ? Minecraft.getInstance().getBlockColors().getColor(tile.getPositiveBlockInfo().getBlockState(), world, pos, tintIndex - ClientConstants.TINT_OFFSET) : -1;
                return tile.getNegativeBlockInfo().getBlockState() != null ? Minecraft.getInstance().getBlockColors().getColor(tile.getNegativeBlockInfo().getBlockState(), world, pos, tintIndex) : -1;
            }).orElse(-1);
        };
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        runIfAvailable(world, pos, i -> {
            if (i.getBlockState().isRandomlyTicking())
                i.getBlockState().randomTick((ServerLevel) i.getWorld(), pos, random);
        });
        // client isn't updated when the slab changes type for copper slabs
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().animateTick(i.getBlockState(), i.getWorld(), pos, random));
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
        return addFloat(world, pos, i -> i.getBlockState().getEnchantPowerBonus(i.getWorld(), pos));
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return max(world, pos, i -> i.getBlockState().getFireSpreadSpeed(i.getWorld(), pos, face));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return max(world, pos, i -> i.getBlockState().getFlammability(i.getWorld(), pos, face));
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().isBurning(i.getWorld(), pos));
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().isFertile(i.getWorld(), pos));
    }

    @Override
    public boolean isFireSource(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        return either(world, pos, i -> i.getBlockState().isFireSource(i.getWorld(), pos, side));
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return either(world, pos, i -> i.getBlockState().isFlammable(i.getWorld(), pos, face));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        runIfAvailable(world, pos, i -> i.getBlockState().neighborChanged(i.getWorld(), pos, i.getBlockState().getBlock(), fromPos, isMoving));
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
        runIfAvailable(world, pos, i -> i.getBlockState().onNeighborChange(i.getWorld(), pos, neighbor));
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        runIfAvailable(world, pos, i -> i.getBlockState().tick(world, pos, random));
    }

    @Override
    public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().wasExploded(i.getWorld(), pos, explosion));
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                tile.getPositiveBlockInfo().getBlockState().getBlock().handlePrecipitation(tile.getPositiveBlockInfo().getBlockState(), tile.getPositiveBlockInfo().getWorld(), pos, precipitation);
        });
    }

    @Override
    public float getFriction(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        return getTile(world, pos)
                .filter(tile -> tile.getPositiveBlockInfo().getBlockState() != null)
                .map(tile -> tile.getPositiveBlockInfo().getBlockState().getFriction(tile.getPositiveBlockInfo().getWorld(), pos, entity))
                .orElseGet(() -> super.getFriction(state, world, pos, entity));
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getPositiveBlockInfo().getBlockState().canSustainPlant(tile.getPositiveBlockInfo().getWorld(), pos, facing, plantable)).orElse(false);
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState state, LevelAccessor world, BlockPos pos, int flags, int recursionLeft) {
        runIfAvailable(world, pos, i -> i.getBlockState().updateIndirectNeighbourShapes(i.getWorld(), pos, flags, recursionLeft));
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return either(world, pos, i -> i.getBlockState().isLadder(i.getWorld(), pos, entity));
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter world, BlockPos pos, @Nullable Entity player) {
        return either(world, pos, i -> i.getBlockState().isBed(i.getWorld(), pos, (LivingEntity) player));
    }

    @Override
    public boolean isConduitFrame(BlockState state, LevelReader world, BlockPos pos, BlockPos conduit) {
        return either(world, pos, i -> i.getBlockState().isConduitFrame(i.getWorld(), pos, conduit));
    }

    @Override
    public boolean isPortalFrame(BlockState state, BlockGetter world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().isPortalFrame(i.getWorld(), pos));
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch) {
        return max(world, pos, i -> i.getBlockState().getExpDrop(i.getWorld(), pos, fortune, silktouch));
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        if (entity == null)
            return getAvailable(world, pos).map(i -> i.getBlockState().getSoundType(i.getWorld(), pos, null)).orElse(super.getSoundType(state, world, pos, entity));
        return super.getSoundType(state, world, pos, entity);
    }

    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
        List<Float> result = new ArrayList<>();
        getTile(world, pos).ifPresent(tile -> {
            float[] positiveBlockColours = null;
            float[] negativeBlockColours = null;
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                positiveBlockColours = tile.getPositiveBlockInfo().getBlockState().getBeaconColorMultiplier(tile.getPositiveBlockInfo().getWorld(), pos, beaconPos);
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                negativeBlockColours = tile.getNegativeBlockInfo().getBlockState().getBeaconColorMultiplier(tile.getNegativeBlockInfo().getWorld(), pos, beaconPos);
            if (positiveBlockColours != null)
                for (float colour : positiveBlockColours)
                    result.add(colour);
            if (negativeBlockColours != null)
                for (float colour : negativeBlockColours)
                    result.add(colour);
        });
        if (result.size() == 0)
            return null;
        float[] colours = new float[result.size()];
        for (int i = 0; i < result.size(); i++)
            colours[i] = result.get(i);
        return colours;
    }

    @Nullable
    @Override
    public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return getTile(world, pos).map(tile -> {
            BlockPathTypes positiveBlockNodeType = null;
            BlockPathTypes negativeBlockNodeType = null;
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                positiveBlockNodeType = tile.getPositiveBlockInfo().getBlockState().getAiPathNodeType(tile.getPositiveBlockInfo().getWorld(), pos, entity);
            if (positiveBlockNodeType != null)
                return positiveBlockNodeType;
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                negativeBlockNodeType = tile.getNegativeBlockInfo().getBlockState().getAiPathNodeType(tile.getNegativeBlockInfo().getWorld(), pos, entity);
            return negativeBlockNodeType;
        }).orElse(super.getAiPathNodeType(state, world, pos, entity));
    }

    @Override
    public void catchFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        runIfAvailable(world, pos, i -> i.getBlockState().catchFire(i.getWorld(), pos, face, igniter));
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
        return both(world, pos, i -> i.getBlockState().canEntityDestroy(i.getWorld(), pos, entity));
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        runIfAvailable(world, pos, i -> i.getBlockState().onBlockExploded(i.getWorld(), pos, explosion));
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

}
