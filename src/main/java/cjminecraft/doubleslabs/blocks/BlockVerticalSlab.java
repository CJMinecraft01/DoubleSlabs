package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IContainerSupport;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.network.NetworkUtils;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import cjminecraft.doubleslabs.util.WorldWrapper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.apache.commons.lang3.tuple.Pair;

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

public class BlockVerticalSlab extends Block implements IWaterLoggable {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockVerticalSlab() {
        super(Properties.create(Material.ROCK));
        setRegistryName(DoubleSlabs.MODID, "vertical_slab");
        setDefaultState(this.getStateContainer().getBaseState().getBlockState().with(FACING, Direction.NORTH).with(DOUBLE, false).with(WATERLOGGED, false));
    }

    public static Optional<TileEntityVerticalSlab> getTile(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return world.getBlockState(pos).getBlock() == Registrar.VERTICAL_SLAB && tile instanceof TileEntityVerticalSlab ? Optional.of((TileEntityVerticalSlab) tile) : Optional.empty();
    }

    public static Optional<BlockState> getAvailableState(IBlockReader world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> tile.getPositiveState() != null ? Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<BlockState> getHalfState(IBlockReader world, BlockPos pos, double x, double z) {
        BlockState state = world.getBlockState(pos);

        return getTile(world, pos).flatMap(tile -> tile.getPositiveState() == null && tile.getNegativeState() == null ? Optional.empty() :
                ((state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                        (state.get(FACING).getAxis() == Direction.Axis.X ? x : z) > 0.5 :
                        (state.get(FACING).getAxis() == Direction.Axis.X ? x : z) < 0.5)
                        || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
                        Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<Pair<BlockState, WorldWrapper>> getHalfStateWithWorld(IBlockReader world, BlockPos pos, double x, double z) {
        BlockState state = world.getBlockState(pos);

        return getTile(world, pos).flatMap(tile -> tile.getPositiveState() == null && tile.getNegativeState() == null ? Optional.empty() :
                ((state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                        (state.get(FACING).getAxis() == Direction.Axis.X ? x : z) > 0.5 :
                        (state.get(FACING).getAxis() == Direction.Axis.X ? x : z) < 0.5)
                        || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
                        Optional.of(Pair.of(tile.getPositiveState(), tile.getPositiveWorld())) : Optional.of(Pair.of(tile.getNegativeState(), tile.getNegativeWorld())));
    }

    public static int min(IBlockReader world, BlockPos pos, ToIntFunction<BlockState> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveState() != null ? converter.applyAsInt(tile.getPositiveState()) : Integer.MAX_VALUE, tile.getNegativeState() != null ? converter.applyAsInt(tile.getNegativeState()) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(IBlockReader world, BlockPos pos, ToDoubleFunction<BlockState> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveState() != null ? converter.applyAsDouble(tile.getPositiveState()) : Integer.MAX_VALUE, tile.getNegativeState() != null ? converter.applyAsDouble(tile.getNegativeState()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(IBlockReader world, BlockPos pos, ToIntFunction<BlockState> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveState() != null ? converter.applyAsInt(tile.getPositiveState()) : 0, tile.getNegativeState() != null ? converter.applyAsInt(tile.getNegativeState()) : 0)).orElse(0);
    }

    public static int maxWithWorld(IBlockReader world, BlockPos pos, ToIntFunction<Pair<BlockState, WorldWrapper>> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveState() != null ? converter.applyAsInt(Pair.of(tile.getPositiveState(), tile.getPositiveWorld())) : 0, tile.getNegativeState() != null ? converter.applyAsInt(Pair.of(tile.getNegativeState(), tile.getNegativeWorld())) : 0)).orElse(0);
    }

    public static float maxFloat(IBlockReader world, BlockPos pos, ToDoubleFunction<BlockState> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveState() != null ? converter.applyAsDouble(tile.getPositiveState()) : 0, tile.getNegativeState() != null ? converter.applyAsDouble(tile.getNegativeState()) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(IBlockReader world, BlockPos pos, ToDoubleFunction<BlockState> converter) {
        return getTile(world, pos).map(tile -> (tile.getPositiveState() != null ? converter.applyAsDouble(tile.getPositiveState()) : 0) + (tile.getNegativeState() != null ? converter.applyAsDouble(tile.getNegativeState()) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(IBlockReader world, BlockPos pos, Consumer<BlockState> consumer) {
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveState() != null)
                consumer.accept(tile.getPositiveState());
            if (tile.getNegativeState() != null)
                consumer.accept(tile.getNegativeState());
            return null;
        });
    }

    public static boolean both(IBlockReader world, BlockPos pos, Predicate<BlockState> predicate) {
        return getTile(world, pos).map(tile -> tile.getPositiveState() != null && tile.getNegativeState() != null && predicate.test(tile.getPositiveState()) && predicate.test(tile.getNegativeState())).orElse(false);
    }

    public static boolean either(IBlockReader world, BlockPos pos, Predicate<BlockState> predicate) {
        return getTile(world, pos).map(tile -> (tile.getPositiveState() != null && predicate.test(tile.getPositiveState())) || (tile.getNegativeState() != null && predicate.test(tile.getNegativeState()))).orElse(false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, DOUBLE, WATERLOGGED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registrar.TILE_VERTICAL_SLAB.create();
    }

    @Override
    public boolean func_220074_n(BlockState state) {
        return !state.get(DOUBLE);
    }

    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (state.get(DOUBLE))
            return VoxelShapes.fullCube();

        TileEntity tileEntity = world.getTileEntity(pos);

        double min = 0;
        double max = 8;
        if (state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min = 8;
            max = 16;
        }

        if (tileEntity instanceof TileEntityVerticalSlab) {
            TileEntityVerticalSlab tile = (TileEntityVerticalSlab) tileEntity;

            boolean positive = tile.getPositiveState() != null;
            boolean negative = tile.getNegativeState() != null;

            if ((state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE && positive) || (state.get(FACING).getAxisDirection() == Direction.AxisDirection.NEGATIVE && negative)) {
                min = 8;
                max = 16;
            } else {
                min = 0;
                max = 8;
            }
        }

        if (state.get(FACING).getAxis() == Direction.Axis.X)
            return Block.makeCuboidShape(min, 0, 0, max, 16, 16);
        else
            return Block.makeCuboidShape(0, 0, min, 16, 16, max);
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean receiveFluid(@Nonnull IWorld world, @Nonnull BlockPos pos, BlockState state, @Nonnull IFluidState fluidState) {
        return !state.get(DOUBLE) && IWaterLoggable.super.receiveFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.get(DOUBLE) && IWaterLoggable.super.canContainFluid(world, pos, state, fluid);
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

//    public <T> T runOnVerticalSlab(IBlockReader world, BlockPos pos, Function<Pair<BlockState, BlockState>, T> func, Supplier<T> orElse) {
//        TileEntity te = world.getTileEntity(pos);
//
//        if (te instanceof TileEntityVerticalSlab) {
//            BlockState positiveState = ((TileEntityVerticalSlab) te).getPositiveState();
//            BlockState negativeState = ((TileEntityVerticalSlab) te).getNegativeState();
//            return func.apply(Pair.of(positiveState, negativeState));
//        }
//
//        return orElse.get();
//    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
        // TODO optimisations
//        return adjacentBlockState.getBlock() == this;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
        return state.get(DOUBLE);
        //        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().isNormalCube(world, pos) && states.getRight().isNormalCube(world, pos), () -> true);
    }

    @Override
    public boolean canEntitySpawn(BlockState state, IBlockReader world, BlockPos pos, EntityType<?> type) {
        return getTile(world, pos).map(tile -> tile.getPositiveState() != null && tile.getPositiveState().canEntitySpawn(world, pos, type) && tile.getNegativeState() != null && tile.getNegativeState().canEntitySpawn(world, pos, type)).orElse(false);
//        return runOnVerticalSlab(world, pos, (states) -> states.getLeft() != null ? states.getLeft().canEntitySpawn(world, pos, type) : states.getRight().canEntitySpawn(world, pos, type), () -> true);
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        if (entity != null)
            return getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(BlockState::getSoundType).orElse(super.getSoundType(state, world, pos, entity));
        return getAvailableState(world, pos).map(BlockState::getSoundType).orElse(super.getSoundType(state, world, pos, null));
//        return runOnVerticalSlab(world, pos, (states) -> states.getLeft() != null ? states.getLeft().getSoundType() : states.getRight().getSoundType(), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return maxFloat(world, pos, s -> s.getExplosionResistance(world, pos, exploder, explosion));
//        return runOnVerticalSlab(world, pos, (states) -> Math.min(states.getLeft() != null ? states.getLeft().getExplosionResistance(world, pos, exploder, explosion) : Integer.MAX_VALUE, states.getRight() != null ? states.getRight().getExplosionResistance(world, pos, exploder, explosion) : Integer.MAX_VALUE), () -> super.getExplosionResistance(state, world, pos, exploder, explosion));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return getTile(world, pos).map(tile -> tile.getPositiveState() == null || tile.getPositiveState().propagatesSkylightDown(world, pos) || tile.getNegativeState() == null || tile.getNegativeState().propagatesSkylightDown(world, pos)).orElse(true);
//        return runOnVerticalSlab(world, pos, (states) -> states.getLeft() == null || states.getLeft().propagatesSkylightDown(world, pos) || states.getRight() == null || states.getRight().propagatesSkylightDown(world, pos), () -> false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 1.0F;
//        return runOnVerticalSlab(world, pos, (states) -> Math.max(states.getLeft() != null ? states.getLeft().getAmbientOcclusionLightValue(world, pos) : 0, states.getRight() != null ? states.getRight().getAmbientOcclusionLightValue(world, pos) : 0), () -> super.getAmbientOcclusionLightValue(state, world, pos));
    }

    @Override
    public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos) {
        return getTile(world, pos).map(tile -> !(tile.getPositiveState() == null || Utils.isTransparent(tile.getPositiveState()) || tile.getNegativeState() == null || Utils.isTransparent(tile.getNegativeState()))).orElse(true);
//        return runOnVerticalSlab(world, pos, (states) -> !(states.getLeft() == null || Utils.isTransparent(states.getLeft()) || states.getRight() == null || Utils.isTransparent(states.getRight())), () -> true);
//        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().with(SlabBlock.TYPE, SlabType.DOUBLE).isSuffocating(world, pos) || states.getRight().with(SlabBlock.TYPE, SlabType.DOUBLE).isSuffocating(world, pos), () -> true);
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        return max(world, pos, s -> s.getLightValue(world, pos));
//        return runOnVerticalSlab(world, pos, states -> Math.max(states.getLeft() != null ? states.getLeft().getLightValue(world, pos) : 0, states.getRight() != null ? states.getRight().getLightValue(world, pos) : 0), () -> 0);
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return getTile(world, pos)
                .map(tile -> (tile.getPositiveState() != null && tile.getPositiveState().canHarvestBlock(world, pos, player)) || (tile.getNegativeState() != null && tile.getNegativeState().canHarvestBlock(world, pos, player))).orElse(false);
//        return runOnVerticalSlab(world, pos, (states) -> (states.getLeft() != null && states.getLeft().canHarvestBlock(world, pos, player)) || (states.getRight() != null && states.getRight().canHarvestBlock(world, pos, player)), () -> false);
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vec3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
        if (hitVec == null)
            return minFloat(world, pos, s -> s.getPlayerRelativeBlockHardness(player, world, pos));
        return getHalfState(world, pos, hitVec.x - pos.getX(), hitVec.z - pos.getZ())
                .map(s -> s.getPlayerRelativeBlockHardness(player, world, pos))
                .orElse(super.getPlayerRelativeBlockHardness(state, player, world, pos));

//        return runOnVerticalSlab(world, pos, (states) -> {
//            RayTraceResult rayTraceResult = Utils.rayTrace(player);
//            Vec3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
//            if (hitVec == null)
//                return Math.min(states.getLeft() != null ? states.getLeft().getPlayerRelativeBlockHardness(player, world, pos) : Integer.MAX_VALUE, states.getRight() != null ? states.getRight().getPlayerRelativeBlockHardness(player, world, pos) : Integer.MAX_VALUE);
//            return ((state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? (state.get(FACING).getAxis() == Direction.Axis.X ? hitVec.x - pos.getX() : hitVec.z - pos.getZ()) > 0.5 : (state.get(FACING).getAxis() == Direction.Axis.X ? hitVec.x - pos.getX() : hitVec.z - pos.getZ()) < 0.5) || states.getRight() == null) && states.getLeft() != null ? states.getLeft().getPlayerRelativeBlockHardness(player, world, pos) : states.getRight().getPlayerRelativeBlockHardness(player, world, pos);
//        }, () -> super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return getHalfState(world, pos, target.getHitVec().x - pos.getX(), target.getHitVec().z - pos.getZ()).map(s -> s.getPickBlock(target, world, pos, player)).orElse(ItemStack.EMPTY);

//        return runOnVerticalSlab(world, pos, (states) -> ((state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? (state.get(FACING).getAxis() == Direction.Axis.X ? target.getHitVec().x - pos.getX() : target.getHitVec().z - pos.getZ()) > 0.5 : (state.get(FACING).getAxis() == Direction.Axis.X ? target.getHitVec().x - pos.getX() : target.getHitVec().z - pos.getZ()) < 0.5) || states.getRight() == null) && states.getLeft() != null ? states.getLeft().getPickBlock(target, world, pos, player) : states.getRight().getPickBlock(target, world, pos, player), () -> super.getPickBlock(state, target, world, pos, player));
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        if (willHarvest)
            return true;
        return super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntityVerticalSlab tile = (TileEntityVerticalSlab) builder.get(LootParameters.BLOCK_ENTITY);
        List<ItemStack> drops = new ArrayList<>();
        if (tile != null) {
            if (tile.getPositiveState() != null)
                drops.addAll(tile.getPositiveState().getDrops(builder));
            if (tile.getNegativeState() != null)
                drops.addAll(tile.getNegativeState().getDrops(builder));
        }
        return drops;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (player.abilities.isCreativeMode) {
            TileEntityVerticalSlab tile = (TileEntityVerticalSlab) world.getTileEntity(pos);
            if (tile != null) {
                if (tile.getPositiveState() != null)
                    tile.getPositiveState().onReplaced(tile.getPositiveWorld(), pos, Blocks.AIR.getDefaultState(), false);
                if (tile.getNegativeState() != null)
                    tile.getNegativeState().onReplaced(tile.getNegativeWorld(), pos, Blocks.AIR.getDefaultState(), false);
            }
            super.onBlockHarvested(world, pos, state, player);
        }
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vec3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
        if (hitVec == null || te == null) {
            super.harvestBlock(world, player, pos, state, te, stack);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else {
            if (state.get(DOUBLE)) {
                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) te;

                double distance = state.get(FACING).getAxis() == Direction.Axis.X ? hitVec.x - (double) pos.getX() : hitVec.z - (double) pos.getZ();

                boolean positive = state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? distance > 0.5 : distance < 0.5;

                BlockState stateToRemove = positive ? tile.getPositiveState() : tile.getNegativeState();

                player.addStat(Stats.BLOCK_MINED.get(stateToRemove.getBlock()));
                world.playEvent(2001, pos, Block.getStateId(stateToRemove));
                player.addExhaustion(0.005F);

                if (!player.abilities.isCreativeMode)
                    spawnDrops(stateToRemove, world, pos, null, player, stack);

                stateToRemove.onReplaced(positive ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, Blocks.AIR.getDefaultState(), false);

                if (positive)
                    tile.setPositiveState(null);
                else
                    tile.setNegativeState(null);

                world.setBlockState(pos, state.with(DOUBLE, false), 11);
                return;
            } else {
                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) te;
                BlockState remainingState = tile.getPositiveState() != null ? tile.getPositiveState() : tile.getNegativeState();
                player.addStat(Stats.BLOCK_MINED.get(remainingState.getBlock()));
                world.playEvent(2001, pos, Block.getStateId(remainingState));
                player.addExhaustion(0.005F);

                if (!player.abilities.isCreativeMode)
                    spawnDrops(remainingState, world, pos, null, player, stack);

                remainingState.onReplaced(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, Blocks.AIR.getDefaultState(), false);

                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        world.removeTileEntity(pos);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
        double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
        final int numOfParticles = state1.get(DOUBLE) ? (int) (75.0D * d0) : (int) (150.0D * d0);
//        if (state1.get(DOUBLE))
//            numOfParticles /= 2;
        runIfAvailable(worldserver, pos, s -> worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, s), entity.posX, entity.posY, entity.posZ, numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D));
        return true;
//        return runOnVerticalSlab(worldserver, pos, (states) -> {
//            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
//            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
//            int numOfParticles = (int) (150.0D * d0);
//            if (state1.get(DOUBLE))
//                numOfParticles /= 2;
//            if (states.getLeft() != null)
//                worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
//            if (states.getRight() != null)
//                worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getRight()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
//            return true;
//        }, () -> false);
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            runIfAvailable(world, pos, s -> world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, s),
                    entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                    entity.getBoundingBox().minY + 0.1D,
                    entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                    -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D));

//            runOnVerticalSlab(world, pos, (states) -> {
//                if (states.getLeft() != null)
//                    world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()),
//                            entity.getPosX() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
//                            entity.getBoundingBox().minY + 0.1D,
//                            entity.getPosZ() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
//                            -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D);
//                if (states.getRight() != null)
//                    world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getRight()),
//                            entity.getPosX() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
//                            entity.getBoundingBox().minY + 0.1D,
//                            entity.getPosZ() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
//                            -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D);
//                return null;
//            }, () -> null);
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult) target;
            return getHalfState(world, result.getPos(), target.getHitVec().x, target.getHitVec().z).map(s -> {
                BlockPos pos = result.getPos();
                Direction side = result.getFace();

                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();

                AxisAlignedBB axisalignedbb = state.getCollisionShape(world, pos).getBoundingBox();
                double d0 = (double) i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
                double d1 = (double) j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
                double d2 = (double) k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

                switch (side) {
                    case DOWN:
                        d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
                        break;
                    case UP:
                        d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
                        break;
                    case NORTH:
                        d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
                        break;
                    case SOUTH:
                        d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
                        break;
                    case WEST:
                        d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
                        break;
                    case EAST:
                        d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
                }

                DiggingParticle.Factory factory = new DiggingParticle.Factory();

                Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, s), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                if (particle != null) {
                    ((DiggingParticle) particle).setBlockPos(pos);
                    particle = particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
                    manager.addEffect(particle);
                    return true;
                }

                return false;
            }).orElse(false);

//            return runOnVerticalSlab(world, result.getPos(), (states) -> {
//                BlockPos pos = result.getPos();
//                Direction side = result.getFace();
//                int i = pos.getX();
//                int j = pos.getY();
//                int k = pos.getZ();
//
//                AxisAlignedBB axisalignedbb = state.getCollisionShape(world, pos).getBoundingBox();
//                double d0 = (double) i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
//                double d1 = (double) j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
//                double d2 = (double) k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;
//
//                switch (side) {
//                    case DOWN:
//                        d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
//                        break;
//                    case UP:
//                        d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
//                        break;
//                    case NORTH:
//                        d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
//                        break;
//                    case SOUTH:
//                        d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
//                        break;
//                    case WEST:
//                        d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
//                        break;
//                    case EAST:
//                        d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
//                }
//
//                DiggingParticle.Factory factory = new DiggingParticle.Factory();
//
//                BlockState state1 = ((state.get(FACING).getAxis() == Direction.Axis.X ? target.getHitVec().x : target.getHitVec().z) > 0.5 || states.getRight() == null) && states.getLeft() != null ? states.getLeft() : states.getRight();
//
//                if (state1 == null)
//                    return false;
//
//                Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, state1), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
//                if (particle != null) {
//                    ((DiggingParticle) particle).setBlockPos(pos);
//                    particle = particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
//                    manager.addEffect(particle);
//                    return true;
//                }
//
//                return false;
//            }, () -> false);
        }
        return false;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return getTile(world, pos).map(tile -> {
            DiggingParticle.Factory factory = new DiggingParticle.Factory();
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        double d0 = ((double) j + 0.5D) / 4.0D + pos.getX();
                        double d1 = ((double) k + 0.5D) / 4.0D + pos.getY();
                        double d2 = ((double) l + 0.5D) / 4.0D + pos.getZ();

                        if (tile.getPositiveState() != null) {
                            Particle particle1 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getPositiveState()), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            if (particle1 != null)
                                manager.addEffect(particle1);
                        }

                        if (tile.getNegativeState() != null) {
                            Particle particle2 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getNegativeState()), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            if (particle2 != null)
                                manager.addEffect(particle2);
                        }
                    }
                }
            }
            return true;
        }).orElse(false);
    }

    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> {
            if (world == null || pos == null)
                return -1;
            return getTile(world, pos).map(tile -> {
                if (tintIndex < DoubleSlabBakedModel.TINT_OFFSET)
                    return tile.getPositiveState() != null ? Minecraft.getInstance().getBlockColors().getColor(tile.getPositiveState(), world, pos, tintIndex) : -1;
                return tile.getNegativeState() != null ? Minecraft.getInstance().getBlockColors().getColor(tile.getNegativeState(), world, pos, tintIndex) : -1;
            }).orElse(-1);
        };
    }

    @Override
    public void randomTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        runIfAvailable(world, pos, s -> s.randomTick(world, pos, random));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        runIfAvailable(world, pos, s -> s.getBlock().animateTick(s, world, pos, rand));
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        if (side == null)
            return false;
        Direction face = Utils.rotateFace(side, state.get(FACING));
        return getTile(world, pos).map(tile -> (tile.getPositiveState() != null && tile.getPositiveState().canConnectRedstone(world, pos, face)) || (tile.getNegativeState() != null && tile.getNegativeState().canConnectRedstone(world, pos, face))).orElse(false);
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == null)
            return 0;
        Direction face = Utils.rotateFace(side, state.get(FACING));
        return max(world, pos, s -> s.getWeakPower(world, pos, face));
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == null)
            return 0;
        Direction face = Utils.rotateFace(side, state.get(FACING));
        return max(world, pos, s -> s.getStrongPower(world, pos, face));
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return both(world, pos, s -> s.getBlock().canCreatureSpawn(s, world, pos, type, entityType));
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        return maxWithWorld(world, pos, pair -> pair.getLeft().getComparatorInputOverride(pair.getRight(), pos));
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        return addFloat(world, pos, s -> s.getEnchantPowerBonus(world, pos));
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        Direction side = Utils.rotateFace(face, state.get(FACING));
        return max(world, pos, s -> s.getFireSpreadSpeed(world, pos, side));
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        Direction side = Utils.rotateFace(face, state.get(FACING));
        return max(world, pos, s -> s.getFlammability(world, pos, side));
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    @Override
    public boolean getWeakChanges(BlockState state, IWorldReader world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isBeaconBase(BlockState state, IWorldReader world, BlockPos pos, BlockPos beacon) {
        return either(world, pos, s -> s.isBeaconBase(world, pos, beacon));
    }

    @Override
    public boolean isBurning(BlockState state, IBlockReader world, BlockPos pos) {
        return either(world, pos, s -> s.isBurning(world, pos));
    }

    @Override
    public boolean isFertile(BlockState state, IBlockReader world, BlockPos pos) {
        return either(world, pos, s -> s.isFertile(world, pos));
    }

    @Override
    public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        Direction face = Utils.rotateFace(side, state.get(FACING));
        return either(world, pos, s -> s.isFireSource(world, pos, face));
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        Direction side = Utils.rotateFace(face, state.get(FACING));
        return either(world, pos, s -> s.isFlammable(world, pos, side));
    }

    @Override
    public boolean isFoliage(BlockState state, IWorldReader world, BlockPos pos) {
        return either(world, pos, s -> s.isFoliage(world, pos));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        runIfAvailable(world, pos, s -> s.neighborChanged(world, pos, block, fromPos, isMoving));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (state.getBlock() != this)
            return false;
        return getHalfStateWithWorld(world, pos, hit.getHitVec().x - pos.getX(), hit.getHitVec().z - pos.getZ()).map(pair -> {
            IContainerSupport support = ContainerSupport.getSupport(pair.getRight(), pos, pair.getLeft());
            if (support == null) {
                boolean result;
                try {
                    result = pair.getLeft().onBlockActivated(pair.getRight(), player, hand, hit);
                } catch (Exception e) {
                    result = false;
                }
                return result;
            } else {
                if (!world.isRemote) {
                    NetworkUtils.openGui((ServerPlayerEntity) player, support.getNamedContainerProvider(pair.getRight(), pos, pair.getLeft(), player, hand, hit), pos, pair.getRight().isPositive());
                    support.onClicked(pair.getRight(), pos, pair.getLeft(), player, hand, hit);
                }
                return true;
            }
        }).orElse(false);
//        return runOnVerticalSlab(world, pos, states -> ((state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? (state.get(FACING).getAxis() == Direction.Axis.X ? hit.getHitVec().x - pos.getX() : hit.getHitVec().z - pos.getZ()) > 0.5 : (state.get(FACING).getAxis() == Direction.Axis.X ? hit.getHitVec().x - pos.getX() : hit.getHitVec().z - pos.getZ()) < 0.5) || states.getRight() == null) && states.getLeft() != null ? states.getLeft().onBlockActivated(((TileEntityVerticalSlab) world.getTileEntity(pos)).getPositiveWorld(), player, hand, hit) : states.getRight().onBlockActivated(((TileEntityVerticalSlab) world.getTileEntity(pos)).getNegativeWorld(), player, hand, hit), () -> super.onBlockActivated(state, world, pos, player, hand, hit));
    }

    @Override
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        BlockRayTraceResult result = Utils.rayTrace(player);
        if (result.getHitVec() != null)
            getHalfStateWithWorld(world, pos, result.getHitVec().x - pos.getX(), result.getHitVec().z - pos.getZ())
                    .ifPresent(pair -> pair.getLeft().onBlockClicked(pair.getRight(), pos, player));
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        runIfAvailable(world, pos, s -> s.onNeighborChange(world, pos, neighbor));
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random rand) {
        runIfAvailable(world, pos, s -> s.tick(world, pos, rand));
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        if (!getHalfStateWithWorld(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(pair -> {
            pair.getLeft().getBlock().onFallenUpon(pair.getRight(), pos, entity, fallDistance);
            return true;
        }).orElse(false))
            super.onFallenUpon(world, pos, entity, fallDistance);
    }

    @Override
    public void onLanded(IBlockReader world, Entity entity) {
        BlockPos pos = entity.getPosition().down();
        if (world.getBlockState(pos).getBlock() == this)
            if (!getHalfStateWithWorld(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(pair -> {
                pair.getLeft().getBlock().onLanded(pair.getRight(), entity);
                return true;
            }).orElse(false))
                super.onLanded(world, entity);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!getHalfStateWithWorld(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(pair -> {
            pair.getLeft().getBlock().onEntityWalk(pair.getRight(), pos, entity);
            return true;
        }).orElse(false))
            super.onEntityWalk(world, pos, entity);
    }
}
