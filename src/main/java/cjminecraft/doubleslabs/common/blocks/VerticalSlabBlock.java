package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IBlockRenderProperties;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class VerticalSlabBlock extends DynamicSlabBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");

    public VerticalSlabBlock() {
        super();
        registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(DOUBLE, false).setValue(FACING, Direction.NORTH));
    }

    public static Optional<IBlockInfo> getHalfState(BlockGetter world, BlockPos pos, double x, double z) {
        BlockState state = world.getBlockState(pos);

        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() == null
                && tile.getNegativeBlockInfo().getBlockState() == null ? null :
                ((state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                        (state.getValue(FACING).getAxis() == Direction.Axis.X ? x : z) > 0.5 :
                        (state.getValue(FACING).getAxis() == Direction.Axis.X ? x : z) < 0.5)
                        || tile.getNegativeBlockInfo().getBlockState() == null) && tile.getPositiveBlockInfo().getBlockState() != null ?
                        tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo())
                .flatMap(block -> block == null || block.getBlockState() == null ? Optional.empty() : Optional.of(block));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
        consumer.accept(new IBlockRenderProperties() {
            @Override
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                if (target.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult result = (BlockHitResult) target;
                    return getHalfState(level, result.getBlockPos(), result.getLocation().x - result.getBlockPos().getX(), result.getLocation().z - result.getBlockPos().getZ()).map(i -> crack((ClientLevel) level, state, i.getBlockState(), result.getBlockPos(), result.getDirection(), manager)).orElse(false);
                }
                return false;
            }

            @Override
            public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
                AtomicBoolean result = new AtomicBoolean(false);
                runIfAvailable(level, pos, i -> {
                    destroy(manager, level, pos, state, i.getBlockState());
                    result.set(true);
                });
                return result.get();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void destroy(ParticleEngine manager, Level level, BlockPos pos, BlockState actualState, BlockState state) {
        if (!state.isAir() && !net.minecraftforge.client.RenderProperties.get(state).addDestroyEffects(state, level, pos, manager)) {
            VoxelShape voxelshape = actualState.getShape(level, pos);
            double d0 = 0.25D;
            voxelshape.forAllBoxes((p_172273_, p_172274_, p_172275_, p_172276_, p_172277_, p_172278_) -> {
                double d1 = Math.min(1.0D, p_172276_ - p_172273_);
                double d2 = Math.min(1.0D, p_172277_ - p_172274_);
                double d3 = Math.min(1.0D, p_172278_ - p_172275_);
                int i = Math.max(2, Mth.ceil(d1 / 0.25D));
                int j = Math.max(2, Mth.ceil(d2 / 0.25D));
                int k = Math.max(2, Mth.ceil(d3 / 0.25D));

                for(int l = 0; l < i; ++l) {
                    for(int i1 = 0; i1 < j; ++i1) {
                        for(int j1 = 0; j1 < k; ++j1) {
                            double d4 = ((double)l + 0.5D) / (double)i;
                            double d5 = ((double)i1 + 0.5D) / (double)j;
                            double d6 = ((double)j1 + 0.5D) / (double)k;
                            double d7 = d4 * d1 + p_172273_;
                            double d8 = d5 * d2 + p_172274_;
                            double d9 = d6 * d3 + p_172275_;
                            manager.add(new TerrainParticle((ClientLevel) level, (double)pos.getX() + d7, (double)pos.getY() + d8, (double)pos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, state, pos));
                        }
                    }
                }

            });
        }
    }

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
                return state.setValue(DOUBLE, true).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER && either(context.getLevel(), pos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getWorld(), i.getPos(), i.getBlockState())));
            }
            return state;
        }
        BlockState newState = super.getStateForPlacement(context);
        if (context.getClickedFace().getAxis().isVertical()) {
//            double value = context.getPlacementHorizontalFacing().getAxis() == Direction.Axis.X ? context.getHitVec().x - (double)context.getPos().getX() : context.getHitVec().z - (double)context.getPos().getZ();
//            boolean positive = context.getPlacementHorizontalFacing().getAxisDirection() == Direction.AxisDirection.POSITIVE ?  value > 0.5d : value < 0.5d;
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
    public Item asItem() {
        return DSItems.VERTICAL_SLAB.get();
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        ItemStack stack = useContext.getItemInHand();
        if (!state.getValue(DOUBLE) && stack.getItem() == this.asItem()) {
            if (useContext.replacingClickedOnBlock()) {
                Direction direction = state.getValue(FACING);
                return getTile(useContext.getLevel(), useContext.getClickedPos()).map(tile -> {
//                    boolean positiveX = useContext.getHitVec().x - (double) useContext.getPos().getX() > 0.5d;
//                    boolean positiveZ = useContext.getHitVec().z - (double) useContext.getPos().getZ() > 0.5d;
                    boolean positive = tile.getPositiveBlockInfo().getBlockState() != null;
                    return (useContext.getClickedFace() == direction.getOpposite() && positive) || (useContext.getClickedFace() == direction && !positive);
                }).orElse(false);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(DOUBLE))
            return Shapes.block();

        double min = 0;
        double max = 0.5;
        if (state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min = 0.5;
            max = 1;
        }

        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof SlabTileEntity) {
            SlabTileEntity tile = (SlabTileEntity) entity;

            boolean positive = tile.getPositiveBlockInfo().getBlockState() != null;
            boolean negative = tile.getNegativeBlockInfo().getBlockState() != null;

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
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() == null || tile.getPositiveBlockInfo().getBlockState().propagatesSkylightDown(tile.getPositiveBlockInfo().getWorld(), pos) || tile.getNegativeBlockInfo().getBlockState() == null || tile.getNegativeBlockInfo().getBlockState().propagatesSkylightDown(tile.getNegativeBlockInfo().getWorld(), pos)).orElse(false);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        BlockHitResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3 hitVec = rayTraceResult.getType() == BlockHitResult.Type.BLOCK ? rayTraceResult.getLocation() : null;
        if (hitVec == null)
            return minFloat(world, pos, i -> i.getBlockState().getDestroyProgress(player, i.getWorld(), pos));
        return getHalfState(world, pos, hitVec.x - pos.getX(), hitVec.z - pos.getZ())
                .map(i -> i.getBlockState().getDestroyProgress(player, i.getWorld(), pos))
                .orElseGet(() -> super.getDestroyProgress(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return getHalfState(world, pos, target.getLocation().x - pos.getX(), target.getLocation().z - pos.getZ()).map(i -> i.getBlockState().getPickBlock(target, i.getWorld(), pos, player)).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (willHarvest)
            return true;
        if (player.isCreative() && player.isCrouching() && state.getValue(DOUBLE)) {
            playerDestroy(world, player, pos, state, world.getBlockEntity(pos), ItemStack.EMPTY);
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        BlockHitResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3 hitVec = rayTraceResult.getLocation();
        return getHalfState(world, pos, hitVec.x - pos.getX(), hitVec.z - pos.getZ()).map(i -> i.getBlockState().canHarvestBlock(i.getWorld(), i.getPos(), player)).orElse(false);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        BlockHitResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3 hitVec = rayTraceResult.getType() == BlockHitResult.Type.BLOCK ? rayTraceResult.getLocation() : null;
        if (hitVec == null || te == null) {
            super.playerDestroy(world, player, pos, state, te, stack);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.DEFAULT);
            world.removeBlockEntity(pos);
        } else {
            if (state.getValue(DOUBLE)) {
                SlabTileEntity tile = (SlabTileEntity) te;

                double distance = state.getValue(FACING).getAxis() == Direction.Axis.X ? hitVec.x - (double) pos.getX() : hitVec.z - (double) pos.getZ();

                boolean positive = state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? distance > 0.5 : distance < 0.5;

                IBlockInfo blockToRemove = positive ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo();
//                BlockState stateToRemove = positive ? tile.getPositiveBlockInfo().getBlockState() : tile.getNegativeBlockInfo().getBlockState();

                player.awardStat(Stats.BLOCK_MINED.get(blockToRemove.getBlockState().getBlock()));
                world.levelEvent(2001, pos, Block.getId(blockToRemove.getBlockState()));
                player.causeFoodExhaustion(0.005F);

                if (!player.isCreative())
                    dropResources(blockToRemove.getBlockState(), world, pos, null, player, stack);

                blockToRemove.getBlockState().onRemove(blockToRemove.getWorld(), pos, Blocks.AIR.defaultBlockState(), false);

                blockToRemove.setBlockState(null);
//                if (positive)
//                    tile.getPositiveBlockInfo().setBlockState(null);
//                else
//                    tile.getNegativeBlockInfo().setBlockState(null);

                world.setBlock(pos, state.setValue(DOUBLE, false), Constants.BlockFlags.DEFAULT);
            } else {
                SlabTileEntity tile = (SlabTileEntity) te;
                boolean positive = tile.getPositiveBlockInfo().getBlockState() != null;
                IBlockInfo blockToRemove = positive ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo();
//                BlockState remainingState = positive ? tile.getPositiveBlockInfo().getBlockState() : tile.getNegativeBlockInfo().getBlockState();
                player.awardStat(Stats.BLOCK_MINED.get(blockToRemove.getBlockState().getBlock()));
                world.levelEvent(2001, pos, Block.getId(blockToRemove.getBlockState()));
                player.causeFoodExhaustion(0.005F);

                if (!player.isCreative())
                    dropResources(blockToRemove.getBlockState(), world, pos, blockToRemove.getBlockEntity(), player, stack);

                blockToRemove.getBlockState().onRemove(blockToRemove.getWorld(), pos, Blocks.AIR.defaultBlockState(), false);

                world.setBlock(pos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.DEFAULT);
                world.removeBlockEntity(pos);
            }
        }
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        float f = (float) Math.ceil(entity.fallDistance - 3.0F);
        double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
        final int numOfParticles = state1.getValue(DOUBLE) ? (int) (75.0D * d0) : (int) (150.0D * d0);
        runIfAvailable(worldserver, pos, i -> worldserver.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, i.getBlockState()), entity.getX(), entity.getY(), entity.getZ(), numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D));
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide()) {
            runIfAvailable(world, pos, i -> world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, i.getBlockState()),
                    entity.getX() + ((double) world.random.nextFloat() - 0.5D) * (double) entity.getBbWidth(),
                    entity.getBoundingBox().minY + 0.1D,
                    entity.getZ() + ((double) world.random.nextFloat() - 0.5D) * (double) entity.getBbWidth(),
                    -entity.getDeltaMovement().x * 4.0D, 1.5D, -entity.getDeltaMovement().z * 4.0D));
        }
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getBlock() != this)
            return InteractionResult.PASS;
        return getHalfState(world, pos, hit.getLocation().x - pos.getX(), hit.getLocation().z - pos.getZ()).map(i -> {
            IContainerSupport containerSupport = ContainerSupport.getSupport(i.getWorld(), pos, i.getBlockState());
            ISlabSupport slabSupport = SlabSupport.getSlabSupport(i.getWorld(), pos, i.getBlockState());
            if (containerSupport != null) {
                if (!world.isClientSide()) {
                    MenuProvider provider = containerSupport.getNamedContainerProvider(i.getWorld(), pos, i.getBlockState(), player, hand, hit);
                    NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return provider.getDisplayName();
                        }

                        @Nullable
                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                            return new WrappedContainer(windowId, inv, player, provider, i);
                        }
                    }, buffer -> {
                        buffer.writeBlockPos(i.getPos());
                        buffer.writeBoolean(i.isPositive());
                        buffer.writeResourceLocation(containerSupport.getContainer(i.getWorld(), pos, state).getRegistryName());
                        containerSupport.writeExtraData(world, pos, state).accept(buffer);
                    });
                }
                return InteractionResult.SUCCESS;
            } else {
                try {
                    return slabSupport == null ? i.getBlockState().use(i.getWorld(), player, hand, hit) : slabSupport.onBlockActivated(i.getBlockState(), i.getWorld(), pos, player, hand, hit);
                } catch (Exception e) {
                    return InteractionResult.PASS;
                }
            }
        }).orElse(InteractionResult.PASS);
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        if (result.getLocation() != null)
            getHalfState(world, pos, result.getLocation().x - pos.getX(), result.getLocation().z - pos.getZ())
                    .ifPresent(i -> i.getBlockState().attack(i.getWorld(), pos, player));
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!getHalfState(world, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ()).map(i -> {
            i.getBlockState().getBlock().fallOn(i.getWorld(), i.getBlockState(), pos, entity, fallDistance);
            return true;
        }).orElse(false))
            super.fallOn(world, state, pos, entity, fallDistance);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter world, Entity entity) {
        BlockPos pos = new BlockPos(entity.position()).below();
        if (world.getBlockState(pos).getBlock() == this)
            if (!getHalfState(world, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ()).map(i -> {
                i.getBlockState().getBlock().updateEntityAfterFallOn(i.getWorld(), entity);
                return true;
            }).orElse(false))
                super.updateEntityAfterFallOn(world, entity);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!getHalfState(world, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ()).map(i -> {
            i.getBlockState().getBlock().stepOn(i.getWorld(), pos, i.getBlockState(), entity);
            return true;
        }).orElse(false))
            super.stepOn(world, pos, state, entity);
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
        return state.getValue(DOUBLE);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        getHalfState(world, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ()).ifPresent(i -> i.getBlockState().entityInside(i.getWorld(), pos, entity));
    }

    @Override
    public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
        getHalfState(world, hit.getBlockPos(), hit.getLocation().x, hit.getLocation().z).ifPresent(i -> i.getBlockState().onProjectileHit(i.getWorld(), i.getBlockState(), hit, projectile));
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        if (entity != null)
            return getHalfState(world, pos, entity.getX() - pos.getX(), entity.getZ() - pos.getZ()).map(i -> i.getBlockState().getSoundType(i.getWorld(), pos, entity)).orElseGet(() -> super.getSoundType(state, world, pos, entity));
        return getAvailable(world, pos).map(i -> i.getBlockState().getSoundType(i.getWorld(), pos, null)).orElseGet(() -> super.getSoundType(state, world, pos, null));
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().handlePrecipitation(i.getBlockState(), i.getWorld(), pos, precipitation));
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return both(world, pos, i -> i.getBlockState().canSustainPlant(i.getWorld(), pos, facing, plantable));
    }
}
