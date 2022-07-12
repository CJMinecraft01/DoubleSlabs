package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
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

public class DynamicSlabBlock extends Block implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DynamicSlabBlock() {
        super(Properties.create(Material.ROCK).notSolid());
        setDefaultState(this.getStateContainer().getBaseState().with(WATERLOGGED, false));
    }

    public static Optional<SlabTileEntity> getTile(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof SlabTileEntity ? Optional.of((SlabTileEntity) tile) : Optional.empty();
    }

    public static Optional<IBlockInfo> getAvailable(IBlockReader world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> Optional.of(tile.getPositiveBlockInfo().getBlockState() != null ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo())).filter(i -> i.getBlockState() != null);
    }

    public static int min(IBlockReader world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(IBlockReader world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(IBlockReader world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : 0)).orElse(0);
    }

    public static float maxFloat(IBlockReader world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static float maxFloat(IBlockReader world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter, float defaultValue) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : defaultValue, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : defaultValue)).orElse((double) defaultValue).floatValue();
    }

    public static float addFloat(IBlockReader world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0) + (tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(IBlockReader world, BlockPos pos, Consumer<IBlockInfo> consumer) {
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                consumer.accept(tile.getPositiveBlockInfo());
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                consumer.accept(tile.getNegativeBlockInfo());
            return null;
        });
    }

    public static boolean both(IBlockReader world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo()) && predicate.test(tile.getNegativeBlockInfo())).orElse(false);
    }

    public static boolean either(IBlockReader world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo())) || (tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getNegativeBlockInfo()))).orElse(false);
    }

    public static boolean formsDoubleSlab(BlockState state1, BlockState state2) {
        return state1.getBlock() == state2.getBlock() && state2.getBlockState().isIn(state2.getBlock());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SlabTileEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(true) : super.getFluidState(state);
    }

    @Override
    public boolean receiveFluid(@Nonnull IWorld world, @Nonnull BlockPos pos, BlockState state, @Nonnull FluidState fluidState) {
        runIfAvailable(world, pos, i -> {
            if (i.getBlockState().getBlock() instanceof IWaterLoggable)
                ((IWaterLoggable) i.getBlockState().getBlock()).receiveFluid(i.getWorld(), pos, i.getBlockState(), fluidState);
        });
        return IWaterLoggable.super.receiveFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
        return IWaterLoggable.super.canContainFluid(world, pos, state, fluid) && either(world, pos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getWorld(), i.getPos(), i.getBlockState()));
    }

    @Override
    public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state) {
        runIfAvailable(world, pos, i -> {
            if (i.getBlockState().getBlock() instanceof IWaterLoggable)
                ((IWaterLoggable) i.getBlockState().getBlock()).pickupFluid(i.getWorld(), pos, i.getBlockState());
        });
        return IWaterLoggable.super.pickupFluid(world, pos, state);
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        BlockState blockstate = context.getWorld().getBlockState(blockpos);
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        if (blockstate.isIn(this)) {
            return blockstate.with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER && either(context.getWorld(), blockpos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getWorld(), i.getPos(), i.getBlockState())));
        } else {
            return this.getDefaultState().with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (state.get(WATERLOGGED))
            world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
        return type == PathType.WATER && world.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        return maxFloat(world, pos, i -> i.getBlockState().getExplosionResistance(i.getWorld(), pos, explosion));
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return max(world, pos, i -> i.getBlockState().getLightValue(i.getWorld(), pos));
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return null;
    }

//    @Override
//    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
//        return both(world, pos, i -> i.getBlockState().canHarvestBlock(i.getWorld(), pos, player));
//    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntity tileEntity = builder.get(LootParameters.BLOCK_ENTITY);
        List<ItemStack> drops = new ArrayList<>();
        if (tileEntity instanceof SlabTileEntity) {
            SlabTileEntity tile = (SlabTileEntity) tileEntity;
            if (tile.getPositiveBlockInfo().getBlockState() != null) {
                LootContext.Builder newBuilder = builder.withParameter(LootParameters.BLOCK_STATE, tile.getPositiveBlockInfo().getBlockState());
                if (tile.getPositiveBlockInfo().getTileEntity() != null)
                    newBuilder = newBuilder.withParameter(LootParameters.BLOCK_ENTITY, tile.getPositiveBlockInfo().getTileEntity());
                drops.addAll(tile.getPositiveBlockInfo().getBlockState().getDrops(newBuilder));
            }
            if (tile.getNegativeBlockInfo().getBlockState() != null) {
                LootContext.Builder newBuilder = builder.withParameter(LootParameters.BLOCK_STATE, tile.getNegativeBlockInfo().getBlockState());
                if (tile.getNegativeBlockInfo().getTileEntity() != null)
                    newBuilder = newBuilder.withParameter(LootParameters.BLOCK_ENTITY, tile.getNegativeBlockInfo().getTileEntity());
                drops.addAll(tile.getNegativeBlockInfo().getBlockState().getDrops(newBuilder));
            }
        }
        return drops;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (willHarvest)
            return true;
        if (player.isCreative() && player.isSneaking()) {
            harvestBlock(world, player, pos, state, world.getTileEntity(pos), ItemStack.EMPTY);
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (player.isCreative()) {
            runIfAvailable(world, pos, i -> i.getBlockState().onReplaced(i.getWorld(), pos, Blocks.AIR.getDefaultState(), false));
            super.onBlockHarvested(world, pos, state, player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
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
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        runIfAvailable(world, pos, i -> i.getBlockState().randomTick(world, pos, random));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().animateTick(i.getBlockState(), i.getWorld(), pos, rand));
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return either(world, pos, i -> i.getBlockState().canConnectRedstone(i.getWorld(), pos, side));
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return max(world, pos, i -> i.getBlockState().getWeakPower(i.getWorld(), pos, side));
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return max(world, pos, i -> i.getBlockState().getStrongPower(i.getWorld(), pos, side));
    }


    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        return max(world, pos, i -> i.getBlockState().getComparatorInputOverride(i.getWorld(), pos));
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        return addFloat(world, pos, i -> i.getBlockState().getEnchantPowerBonus(i.getWorld(), pos));
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return max(world, pos, i -> i.getBlockState().getFireSpreadSpeed(i.getWorld(), pos, face));
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return max(world, pos, i -> i.getBlockState().getFlammability(i.getWorld(), pos, face));
    }

    @Override
    public boolean getWeakChanges(BlockState state, IWorldReader world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isBurning(BlockState state, IBlockReader world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().isBurning(i.getWorld(), pos));
    }

    @Override
    public boolean isFertile(BlockState state, IBlockReader world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().isFertile(i.getWorld(), pos));
    }

    @Override
    public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return either(world, pos, i -> i.getBlockState().isFireSource(i.getWorld(), pos, side));
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return either(world, pos, i -> i.getBlockState().isFlammable(i.getWorld(), pos, face));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        runIfAvailable(world, pos, i -> i.getBlockState().neighborChanged(i.getWorld(), pos, i.getBlockState().getBlock(), fromPos, isMoving));
    }


    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        runIfAvailable(world, pos, i -> i.getBlockState().onNeighborChange(i.getWorld(), pos, neighbor));
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        runIfAvailable(world, pos, i -> i.getBlockState().tick(world, pos, rand));
    }

    @Override
    public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().onExplosionDestroy(i.getWorld(), pos, explosion));
    }

    @Override
    public void fillWithRain(World world, BlockPos pos) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                tile.getPositiveBlockInfo().getBlockState().getBlock().fillWithRain(tile.getPositiveBlockInfo().getWorld(), pos);
        });
    }

    @Override
    public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return maxFloat(world, pos, i -> i.getBlockState().getSlipperiness(i.getWorld(), pos, entity), 0.6f);
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getPositiveBlockInfo().getBlockState().canSustainPlant(tile.getPositiveBlockInfo().getWorld(), pos, facing, plantable)).orElse(false);
    }

    @Override
    public void updateDiagonalNeighbors(BlockState state, IWorld world, BlockPos pos, int flags, int recursionLeft) {
        runIfAvailable(world, pos, i -> i.getBlockState().updateDiagonalNeighbors(i.getWorld(), pos, flags, recursionLeft));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        runIfAvailable(world, pos, i -> i.getBlockState().onBlockAdded(i.getWorld(), pos, oldState, isMoving));
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
        return either(world, pos, i -> i.getBlockState().receiveBlockEvent(i.getWorld(), pos, id, param));
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
        return either(world, pos, i -> i.getBlockState().isLadder(i.getWorld(), pos, entity));
    }

    @Override
    public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, @Nullable Entity player) {
        return either(world, pos, i -> i.getBlockState().isBed(i.getWorld(), pos, (LivingEntity) player));
    }

    @Override
    public void onPlantGrow(BlockState state, IWorld world, BlockPos pos, BlockPos source) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().onPlantGrow(i.getBlockState(), i.getWorld(), pos, source));
    }

    @Override
    public boolean isConduitFrame(BlockState state, IWorldReader world, BlockPos pos, BlockPos conduit) {
        return either(world, pos, i -> i.getBlockState().isConduitFrame(i.getWorld(), pos, conduit));
    }

    @Override
    public boolean isPortalFrame(BlockState state, IBlockReader world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().isPortalFrame(i.getWorld(), pos));
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
        return max(world, pos, i -> i.getBlockState().getExpDrop(i.getWorld(), pos, fortune, silktouch));
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        if (entity == null)
            return getAvailable(world, pos).map(i -> i.getBlockState().getSoundType(i.getWorld(), pos, null)).orElse(super.getSoundType(state, world, pos, entity));
        return super.getSoundType(state, world, pos, entity);
    }

    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
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
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return getTile(world, pos).map(tile -> {
            PathNodeType positiveBlockNodeType = null;
            PathNodeType negativeBlockNodeType = null;
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
    public void catchFire(BlockState state, World world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        runIfAvailable(world, pos, i -> i.getBlockState().catchFire(i.getWorld(), pos, face, igniter));
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
        return both(world, pos, i -> i.getBlockState().canEntityDestroy(i.getWorld(), pos, entity));
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        runIfAvailable(world, pos, i -> i.getBlockState().onBlockExploded(i.getWorld(), pos, explosion));
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }
}
