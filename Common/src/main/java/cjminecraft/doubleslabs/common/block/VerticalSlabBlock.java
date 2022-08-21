package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class VerticalSlabBlock extends DynamicSlabBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");

    public VerticalSlabBlock() {
        super();
        registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(DOUBLE, false).setValue(FACING, Direction.NORTH));
    }

    public static Optional<IBlockInfo> getHalfState(BlockGetter world, BlockPos pos, double x, double z) {
        BlockState state = world.getBlockState(pos);

        return getSlab(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() == null
                        && tile.getNegativeBlockInfo().getBlockState() == null ? null :
                        ((state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                                (state.getValue(FACING).getAxis() == Direction.Axis.X ? x : z) > 0.5 :
                                (state.getValue(FACING).getAxis() == Direction.Axis.X ? x : z) < 0.5)
                                || tile.getNegativeBlockInfo().getBlockState() == null) && tile.getPositiveBlockInfo().getBlockState() != null ?
                                tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo())
                .flatMap(block -> block == null || block.getBlockState() == null ? Optional.empty() : Optional.of(block));
    }

    // todo: initialiseClient


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, DOUBLE);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return !state.getValue(DOUBLE) || super.canPlaceLiquid(world, pos, state, fluid);
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        if (state.is(this)) {
            // same block as this meaning we are trying to combine slabs
            if (canBeReplaced(state, context)) {
                FluidState fluidstate = context.getLevel().getFluidState(pos);
                return state.setValue(DOUBLE, true).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER && either(context.getLevel(), pos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getLevel(), i.getPos(), i.getBlockState())));
            }
            return state;
        }
        BlockState newState = super.getStateForPlacement(context);
        assert newState != null;
        if (context.getClickedFace().getAxis().isVertical()) {
            Vec3 vec = context.getClickLocation().subtract(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            double angle = Math.atan2(vec.x, vec.z) * -180.0 / Math.PI;
            Direction direction = Direction.fromYRot(angle);
            return newState.setValue(FACING, direction);
        }
        double value = context.getHorizontalDirection().getAxis() == Direction.Axis.Z ? context.getClickLocation().x - (double)pos.getX() : context.getClickLocation().z - (double)pos.getZ();
        if (value > 0.25d && value < 0.75d)
            return newState.setValue(FACING, context.getHorizontalDirection());
        boolean positive = context.getHorizontalDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE ? value > 0.5d : value < 0.5d;
        if (context.getHorizontalDirection().getAxis() == Direction.Axis.Z)
            positive = !positive;
        return newState.setValue(FACING, positive ? context.getHorizontalDirection().getClockWise() : context.getHorizontalDirection().getCounterClockWise());
    }

    @Override
    public @NotNull Item asItem() {
        return DSItems.VERTICAL_SLAB.get();
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        ItemStack stack = useContext.getItemInHand();
        if (!state.getValue(DOUBLE) && stack.getItem() == this.asItem()) {
            if (useContext.replacingClickedOnBlock()) {
                Direction direction = state.getValue(FACING);
                return getSlab(useContext.getLevel(), useContext.getClickedPos()).map(slab -> {
                    boolean positive = slab.getPositiveBlockInfo().getBlockState() != null;
                    return (useContext.getClickedFace() == direction.getOpposite() && positive) || (useContext.getClickedFace() == direction && !positive);
                }).orElse(false);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue(DOUBLE))
            return Shapes.block();

        double min = 0;
        double max = 0.5;
        if (state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min = 0.5;
            max = 1;
        }

        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof SlabBlockEntity<?> slab) {
            boolean positive = slab.getPositiveBlockInfo().getBlockState() != null;
            boolean negative = slab.getNegativeBlockInfo().getBlockState() != null;

            if ((state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE && positive) || (state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.NEGATIVE && negative)) {
                min = 0.5;
                max = 1;
            } else {
                min = 0;
                max = 0.5;
            }
        }

        if (state.getValue(FACING).getAxis() == Direction.Axis.X)
            return Shapes.box(min, 0, 0, max, 1, 1);
        else
            return Shapes.box(0, 0, min, 1, 1, max);
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return getSlab(world, pos).map(slab -> slab.getPositiveBlockInfo().getBlockState() == null || slab.getPositiveBlockInfo().getBlockState().propagatesSkylightDown(slab.getPositiveBlockInfo().getLevel(), pos) || slab.getNegativeBlockInfo().getBlockState() == null || slab.getNegativeBlockInfo().getBlockState().propagatesSkylightDown(slab.getNegativeBlockInfo().getLevel(), pos)).orElse(false);
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        if (result.getType() != HitResult.Type.BLOCK)
            return minFloat(level, pos, i -> i.blockState().map(s -> s.getDestroyProgress(player, i.getLevel(), pos)).orElse(100f));
        return getHalfState(level, pos, result.getLocation().x - pos.getX(), result.getLocation().z - pos.getZ())
                .flatMap(i -> i.blockState().map(s -> s.getDestroyProgress(player, i.getLevel(), pos)))
                        .orElseGet(() -> super.getDestroyProgress(state, player, level, pos));
    }

    // todo: get clone item stack
    // todo: on destroyed by player
    // todo: can harvest block


    @Override
    public void playerDestroy(@NotNull Level world, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity te, @NotNull ItemStack stack) {
        BlockHitResult rayTraceResult = RayTraceUtil.rayTrace(player);
        if (rayTraceResult.getType() != BlockHitResult.Type.BLOCK || te == null) {
            super.playerDestroy(world, player, pos, state, te, stack);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            world.removeBlockEntity(pos);
        } else {
            if (state.getValue(DOUBLE)) {
                SlabBlockEntity<?> slab = (SlabBlockEntity<?>) te;

                double distance = state.getValue(FACING).getAxis() == Direction.Axis.X ? rayTraceResult.getLocation().x - (double) pos.getX() : rayTraceResult.getLocation().z - (double) pos.getZ();

                boolean positive = state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? distance > 0.5 : distance < 0.5;

                IBlockInfo blockToRemove = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

                player.awardStat(Stats.BLOCK_MINED.get(blockToRemove.getBlockState().getBlock()));
                world.levelEvent(2001, pos, Block.getId(blockToRemove.getBlockState()));
                player.causeFoodExhaustion(0.005F);

                if (!player.isCreative())
                    dropResources(blockToRemove.getBlockState(), world, pos, null, player, stack);

                blockToRemove.getBlockState().onRemove(blockToRemove.getLevel(), pos, Blocks.AIR.defaultBlockState(), false);

                blockToRemove.setBlockState(null);

                world.setBlock(pos, state.setValue(DOUBLE, false), 3);
            } else {
                SlabBlockEntity<?> slab = (SlabBlockEntity<?>) te;
                boolean positive = slab.getPositiveBlockInfo().getBlockState() != null;
                IBlockInfo blockToRemove = positive ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();
                player.awardStat(Stats.BLOCK_MINED.get(blockToRemove.getBlockState().getBlock()));
                world.levelEvent(2001, pos, Block.getId(blockToRemove.getBlockState()));
                player.causeFoodExhaustion(0.005F);

                if (!player.isCreative())
                    dropResources(blockToRemove.getBlockState(), world, pos, blockToRemove.getBlockEntity(), player, stack);

                blockToRemove.getBlockState().onRemove(blockToRemove.getLevel(), pos, Blocks.AIR.defaultBlockState(), false);

                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                world.removeBlockEntity(pos);
            }
        }
    }

    // todo: add landing effects
    // todo: add running effects

    @Override
    public @NotNull InteractionResult use(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (state.getBlock() != this)
            return InteractionResult.PASS;
        return getHalfState(level, pos, hit.getLocation().x - pos.getX(), hit.getLocation().z - pos.getZ()).flatMap(i -> i.blockState().map(s -> {
            IContainerSupport containerSupport = ContainerSupport.getSupport(i.getLevel(), pos, s);
            ISlabSupport slabSupport = SlabSupport.getSlabSupport(i.getLevel(), pos, s);
            if (containerSupport != null) {
                if (!level.isClientSide()) {
                    MenuProvider provider = containerSupport.getNamedContainerProvider(i.getLevel(), pos, s, player, hand, hit);
                    Services.PLATFORM.openScreen(player, new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return provider.getDisplayName();
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                            return new WrappedContainer(windowId, inventory, player, provider, i);
                        }
                    }, buffer -> {
                        buffer.writeBlockPos(i.getPos());
                        buffer.writeBoolean(i.isPositive());
                        buffer.writeResourceLocation(Objects.requireNonNull(Services.PLATFORM.getMenuTypeName(containerSupport.getContainer(i.getLevel(), pos, s))));
                        containerSupport.writeExtraData(i.getLevel(), pos, s).accept(buffer);
                    });
                }
                return InteractionResult.SUCCESS;
            } else {
                try {
                    return slabSupport == null ? s.use(i.getLevel(), player, hand, hit) : slabSupport.onBlockActivated(s, i.getLevel(), pos, player, hand, hit);
                } catch (Exception e) {
                    return InteractionResult.PASS;
                }
            }
        })).orElse(InteractionResult.PASS);
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        getHalfState(level, pos, result.getLocation().x - pos.getX(), result.getLocation().z - pos.getZ())
                .ifPresent(i -> i.blockState().ifPresent(s -> s.attack(i.getLevel(), pos, player)));
    }

    @Override
    public void fallOn(@NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull Entity entity, float fallDistance) {
        getHalfState(level, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ())
                .filter(i -> i.getBlockState() != null)
                .ifPresentOrElse(i -> i.getBlockState().getBlock().fallOn(i.getLevel(), i.getBlockState(), pos, entity, fallDistance),
                        () -> super.fallOn(level, state, pos, entity, fallDistance));
    }

    @Override
    public void updateEntityAfterFallOn(@NotNull BlockGetter level, Entity entity) {
        BlockPos pos = new BlockPos(entity.position()).below();
        if (level.getBlockState(pos).getBlock() == this) {
            getHalfState(level, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ())
                    .filter(i -> i.getBlockState() != null)
                    .ifPresentOrElse(i -> i.getBlockState().getBlock().updateEntityAfterFallOn(i.getLevel(), entity),
                            () -> super.updateEntityAfterFallOn(level, entity));
        }
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        getHalfState(level, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ())
                .filter(i -> i.getBlockState() != null)
                .ifPresent(i -> i.getBlockState().getBlock().stepOn(i.getLevel(), pos, i.getBlockState(), entity));
    }

    // todo: should display fluid overlay


    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        getHalfState(level, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ())
                .ifPresent(i -> i.blockState().ifPresent(s -> s.entityInside(i.getLevel(), pos, entity)));
    }

    @Override
    public void onProjectileHit(@NotNull Level level, @NotNull BlockState state, BlockHitResult hit, @NotNull Projectile projectile) {
        getHalfState(level, hit.getBlockPos(), hit.getLocation().x, hit.getLocation().z)
                .ifPresent(i -> i.blockState().ifPresent(s -> s.onProjectileHit(i.getLevel(), s, hit, projectile)));
    }

    // todo: get sound type


    @Override
    public void handlePrecipitation(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Biome.@NotNull Precipitation precipitation) {
        runIfAvailable(level, pos, i -> i.blockState().ifPresent(s -> s.getBlock().handlePrecipitation(s, i.getLevel(), pos, precipitation)));
    }

    // todo: can sustain plant
}
