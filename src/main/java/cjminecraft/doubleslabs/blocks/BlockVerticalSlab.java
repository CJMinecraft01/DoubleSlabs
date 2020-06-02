package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
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
import net.minecraft.util.Direction;
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
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockVerticalSlab extends Block implements IWaterLoggable {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockVerticalSlab() {
        super(Properties.create(Material.ROCK).notSolid());
        setRegistryName(DoubleSlabs.MODID, "vertical_slab");
        setDefaultState(this.getStateContainer().getBaseState().getBlockState().with(FACING, Direction.NORTH).with(DOUBLE, false).with(WATERLOGGED, false));
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
    public boolean isTransparent(BlockState state) {
        return !state.get(DOUBLE);
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

        if (tileEntity != null) {
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

    public <T> T runOnVerticalSlab(IBlockReader world, BlockPos pos, Function<Pair<BlockState, BlockState>, T> func, Supplier<T> orElse) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityVerticalSlab) {
            BlockState positiveState = ((TileEntityVerticalSlab) te).getPositiveState();
            BlockState negativeState = ((TileEntityVerticalSlab) te).getNegativeState();
            return func.apply(Pair.of(positiveState, negativeState));
        }

        return orElse.get();
    }

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
        return runOnVerticalSlab(world, pos, (states) -> states.getLeft() != null ? states.getLeft().canEntitySpawn(world, pos, type) : states.getRight().canEntitySpawn(world, pos, type), () -> true);
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return runOnVerticalSlab(world, pos, (states) -> states.getLeft() != null ? states.getLeft().getSoundType() : states.getRight().getSoundType(), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return runOnVerticalSlab(world, pos, (states) -> Math.min(states.getLeft() != null ? states.getLeft().getExplosionResistance(world, pos, exploder, explosion) : Integer.MAX_VALUE, states.getRight() != null ? states.getRight().getExplosionResistance(world, pos, exploder, explosion) : Integer.MAX_VALUE), () -> super.getExplosionResistance(state, world, pos, exploder, explosion));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnVerticalSlab(world, pos, (states) -> states.getLeft() == null || states.getLeft().propagatesSkylightDown(world, pos) || states.getRight() == null || states.getRight().propagatesSkylightDown(world, pos), () -> false);
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnVerticalSlab(world, pos, (states) -> Math.max(states.getLeft() != null ? states.getLeft().getAmbientOcclusionLightValue(world, pos) : 0, states.getRight() != null ? states.getRight().getAmbientOcclusionLightValue(world, pos) : 0), () -> super.getAmbientOcclusionLightValue(state, world, pos));
    }

    @Override
    public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnVerticalSlab(world, pos, (states) -> !(states.getLeft() == null || Utils.isTransparent(states.getLeft()) || states.getRight() == null || Utils.isTransparent(states.getRight())), () -> true);
//        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().with(SlabBlock.TYPE, SlabType.DOUBLE).isSuffocating(world, pos) || states.getRight().with(SlabBlock.TYPE, SlabType.DOUBLE).isSuffocating(world, pos), () -> true);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnVerticalSlab(world, pos, states -> Math.max(states.getLeft() != null ? states.getLeft().getLightValue(world, pos) : 0, states.getRight() != null ? states.getRight().getLightValue(world, pos) : 0), () -> 0);
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return runOnVerticalSlab(world, pos, (states) -> (states.getLeft() != null && states.getLeft().canHarvestBlock(world, pos, player)) || (states.getRight() != null && states.getRight().canHarvestBlock(world, pos, player)), () -> false);
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        return runOnVerticalSlab(world, pos, (states) -> {
            RayTraceResult rayTraceResult = Utils.rayTrace(player);
            Vec3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
            if (hitVec == null)
                return Math.min(states.getLeft() != null ? states.getLeft().getPlayerRelativeBlockHardness(player, world, pos) : Integer.MAX_VALUE, states.getRight() != null ? states.getRight().getPlayerRelativeBlockHardness(player, world, pos) : Integer.MAX_VALUE);
            return ((state.get(FACING).getAxis() == Direction.Axis.X ? hitVec.x - pos.getX() : hitVec.z - pos.getZ()) > 0.5 || states.getRight() == null) && states.getLeft() != null ? states.getLeft().getPlayerRelativeBlockHardness(player, world, pos) : states.getRight().getPlayerRelativeBlockHardness(player, world, pos);
        }, () -> super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return runOnVerticalSlab(world, pos, (states) -> ((state.get(FACING).getAxis() == Direction.Axis.X ? target.getHitVec().x : target.getHitVec().z) > 0.5 || states.getRight() == null) && states.getLeft() != null ? states.getLeft().getPickBlock(target, world, pos, player) : states.getRight().getPickBlock(target, world, pos, player), () -> super.getPickBlock(state, target, world, pos, player));
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
        if (player.abilities.isCreativeMode)
            super.onBlockHarvested(world, pos, state, player);
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

                double distance = state.get(FACING).getAxis() == Direction.Axis.X ? hitVec.x - (double)pos.getX() : hitVec.z - (double)pos.getZ();

                boolean distanceTest = state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? distance > 0.5 : distance < 0.5;

                BlockState stateToRemove = distanceTest ? tile.getPositiveState() : tile.getNegativeState();

                player.addStat(Stats.BLOCK_MINED.get(stateToRemove.getBlock()));
                world.playEvent(2001, pos, Block.getStateId(stateToRemove));
                player.addExhaustion(0.005F);

                if (!player.abilities.isCreativeMode)
                    spawnDrops(stateToRemove, world, pos, null, player, stack);

                if (distanceTest)
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
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        world.removeTileEntity(pos);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return runOnVerticalSlab(worldserver, pos, (states) -> {
            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            if (state1.get(DOUBLE))
                numOfParticles /= 2;
            if (states.getLeft() != null)
                worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            if (states.getRight() != null)
                worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getRight()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            return true;
        }, () -> false);
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            runOnVerticalSlab(world, pos, (states) -> {
                if (states.getLeft() != null)
                    world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()),
                            entity.getPosX() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                            entity.getBoundingBox().minY + 0.1D,
                            entity.getPosZ() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                            -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D);
                if (states.getRight() != null)
                    world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getRight()),
                            entity.getPosX() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                            entity.getBoundingBox().minY + 0.1D,
                            entity.getPosZ() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                            -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D);
                return null;
            }, () -> null);
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult) target;
            return runOnVerticalSlab(world, result.getPos(), (states) -> {
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

                BlockState state1 = ((state.get(FACING).getAxis() == Direction.Axis.X ? target.getHitVec().x : target.getHitVec().z) > 0.5 || states.getRight() == null) && states.getLeft() != null ? states.getLeft() : states.getRight();

                if (state1 == null)
                    return false;

                Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, state1), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                if (particle != null) {
                    ((DiggingParticle) particle).setBlockPos(pos);
                    particle = particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
                    manager.addEffect(particle);
                    return true;
                }

                return false;
            }, () -> false);
        }
        return false;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return runOnVerticalSlab(world, pos, (states) -> {
            DiggingParticle.Factory factory = new DiggingParticle.Factory();
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        double d0 = ((double) j + 0.5D) / 4.0D + pos.getX();
                        double d1 = ((double) k + 0.5D) / 4.0D + pos.getY();
                        double d2 = ((double) l + 0.5D) / 4.0D + pos.getZ();

                        if (states.getLeft() != null) {
                            Particle particle1 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            if (particle1 != null)
                                manager.addEffect(particle1);
                        }

                        if (states.getRight() != null) {
                            Particle particle2 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getRight()), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            if (particle2 != null)
                                manager.addEffect(particle2);
                        }
                    }
                }
            }
            return true;
        }, () -> false);
    }

    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> {
            if (world == null || pos == null)
                return -1;
            return runOnVerticalSlab(world, pos, (states) -> {
                if (tintIndex < DoubleSlabBakedModel.TINT_OFFSET)
                    return states.getLeft() != null ? Minecraft.getInstance().getBlockColors().getColor(states.getLeft(), world, pos, tintIndex) : -1;
                return states.getRight() != null ? Minecraft.getInstance().getBlockColors().getColor(states.getRight(), world, pos, tintIndex) : -1;
            }, () -> -1);
        };
    }

}
