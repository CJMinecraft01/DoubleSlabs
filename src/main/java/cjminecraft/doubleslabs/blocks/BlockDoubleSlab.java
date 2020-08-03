package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IContainerSupport;
import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.network.NetworkUtils;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.util.WorldWrapper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
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

public class BlockDoubleSlab extends Block {

    public BlockDoubleSlab() {
        super(Properties.create(Material.ROCK).notSolid());
        setRegistryName(DoubleSlabs.MODID, "double_slab");
    }

    public static Optional<TileEntityDoubleSlab> getTile(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return world.getBlockState(pos).getBlock() == Registrar.DOUBLE_SLAB && tile instanceof TileEntityDoubleSlab ? Optional.of((TileEntityDoubleSlab) tile) : Optional.empty();
    }

    public static Optional<BlockState> getAvailableState(IBlockReader world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> tile.getPositiveState() != null ? Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<BlockState> getHalfState(IBlockReader world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeState() == null && tile.getPositiveState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
                        Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<Pair<BlockState, WorldWrapper>> getHalfStateWithWorld(IBlockReader world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeState() == null && tile.getPositiveState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
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
    @Nonnull
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


//    @Override
//    public boolean isSolid(BlockState state) {
//        return true;
//    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registrar.TILE_DOUBLE_SLAB.create();
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
        //        return adjacentBlockState.getBlock() == this;
    }

    //    @Override
//    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
//        return true;
//    }

//    @Override
//    public boolean canEntitySpawn(BlockState state, IBlockReader world, BlockPos pos, EntityType<?> type) {
//        return both(world, pos, s -> s.canEntitySpawn(world, pos, type));
//        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().canEntitySpawn(world, pos, type), () -> true);
//    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        if (entity != null)
            return getHalfState(world, pos, entity.getPosY() - pos.getY()).map(BlockState::getSoundType).orElse(super.getSoundType(state, world, pos, entity));
        return getAvailableState(world, pos).map(BlockState::getSoundType).orElse(super.getSoundType(state, world, pos, null));
//        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().getSoundType(), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        return maxFloat(world, pos, s -> s.getExplosionResistance(world, pos, explosion));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return both(world, pos, s -> s.propagatesSkylightDown(world, pos));
//        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().propagatesSkylightDown(world, pos) && states.getRight().propagatesSkylightDown(world, pos), () -> false);
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 1.0F;
//        return runOnDoubleSlab(world, pos, (states) -> Math.max(states.getLeft().getAmbientOcclusionLightValue(world, pos), states.getRight().getAmbientOcclusionLightValue(world, pos)), () -> super.getAmbientOcclusionLightValue(state, world, pos));
    }

    // TODO suffocation
//    @Override
//    public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos) {
//        return getTile(world, pos).map(tile -> !Utils.isTransparent(tile.getPositiveState()) || !Utils.isTransparent(tile.getNegativeState())).orElse(true);
//    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return max(world, pos, s -> s.getLightValue(world, pos));
//        return runOnDoubleSlab(world, pos, states -> Math.max(states.getLeft().getLightValue(world, pos), states.getRight().getLightValue(world, pos)), () -> 0);
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return either(world, pos, s -> s.canHarvestBlock(world, pos, player));
//        return getTile(world, pos).map(tile -> tile.getPositiveState().canHarvestBlock(world, pos, player) || tile.getNegativeState().canHarvestBlock(world, pos, player)).orElse(false);
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vector3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
        if (hitVec == null)
            return minFloat(world, pos, s -> s.getPlayerRelativeBlockHardness(player, world, pos));
        return getHalfState(world, pos, hitVec.y - pos.getY())
                .map(s -> s.getPlayerRelativeBlockHardness(player, world, pos))
                .orElse(super.getPlayerRelativeBlockHardness(state, player, world, pos));

//        return runOnDoubleSlab(world, pos, (states) -> {
//            RayTraceResult rayTraceResult = Utils.rayTrace(player);
//            Vec3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
//            if (hitVec == null)
//                return Math.min(states.getLeft().getPlayerRelativeBlockHardness(player, world, pos), states.getRight().getPlayerRelativeBlockHardness(player, world, pos));
//            return (hitVec.y - pos.getY()) > 0.5 ? states.getLeft().getPlayerRelativeBlockHardness(player, world, pos) : states.getRight().getPlayerRelativeBlockHardness(player, world, pos);
//        }, () -> super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return getHalfState(world, pos, target.getHitVec().y - pos.getY()).map(s -> s.getPickBlock(target, world, pos, player)).orElse(ItemStack.EMPTY);
//        return runOnDoubleSlab(world, pos, (states) -> target.getHitVec().y - pos.getY() > 0.5 ? states.getLeft().getPickBlock(target, world, pos, player) : states.getRight().getPickBlock(target, world, pos, player), () -> super.getPickBlock(state, target, world, pos, player));
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
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) builder.get(LootParameters.BLOCK_ENTITY);
        List<ItemStack> drops = new ArrayList<>();
        if (tile != null) {
            drops.addAll(tile.getTopState().getDrops(builder));
            drops.addAll(tile.getBottomState().getDrops(builder));
        }
        return drops;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (player.abilities.isCreativeMode) {
            TileEntityDoubleSlab tile = (TileEntityDoubleSlab) world.getTileEntity(pos);
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
        Vector3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
        if (hitVec == null || te == null) {
            super.harvestBlock(world, player, pos, state, te, stack);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            world.removeTileEntity(pos);
        } else {
            TileEntityDoubleSlab tile = (TileEntityDoubleSlab) te;

            double y = hitVec.y - (double) pos.getY();

            TileEntity remainingTile = y > 0.5 ? tile.getNegativeTile() : tile.getPositiveTile();
            BlockState remainingState = y > 0.5 ? tile.getBottomState() : tile.getTopState();
            BlockState stateToRemove = y > 0.5 ? tile.getTopState() : tile.getBottomState();

            player.addStat(Stats.BLOCK_MINED.get(stateToRemove.getBlock()));
            world.playEvent(2001, pos, Block.getStateId(stateToRemove));
            player.addExhaustion(0.005F);

            if (!player.abilities.isCreativeMode)
                spawnDrops(stateToRemove, world, pos, null, player, stack);

            stateToRemove.onReplaced(y > 0.5 ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, Blocks.AIR.getDefaultState(), false);

            world.setBlockState(pos, remainingState, Constants.BlockFlags.DEFAULT);
            world.setTileEntity(pos, remainingTile);
        }
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return getTile(worldserver, pos).map(tile -> {
            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getPositiveState()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            getTile(world, pos).ifPresent(tile -> {
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getPositiveState()),
                        entity.getPosX() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                        entity.getBoundingBox().minY + 0.1D,
                        entity.getPosZ() + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                        -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D);
            });
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult) target;
            return getHalfState(world, result.getPos(), target.getHitVec().y).map(s -> {
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

                Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, s), (ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                if (particle != null) {
                    ((DiggingParticle) particle).setBlockPos(pos);
                    particle = particle.multiplyVelocity(0.2F).multiplyParticleScaleBy(0.6F);
                    manager.addEffect(particle);
                    return true;
                }

                return false;
            }).orElse(false);
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
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
                            Particle particle1 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getPositiveState()), (ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            if (particle1 != null)
                                manager.addEffect(particle1);
                        }

                        if (tile.getNegativeState() != null) {
                            Particle particle2 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getNegativeState()), (ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
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
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
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
        return getTile(world, pos).map(tile -> (side == Direction.UP ? tile.getPositiveState().canConnectRedstone(world, pos, Direction.UP) : side == Direction.DOWN ? tile.getNegativeState().canConnectRedstone(world, pos, Direction.DOWN) : either(world, pos, s -> s.canConnectRedstone(world, pos, side)))).orElse(false);
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == null)
            return 0;
        return max(world, pos, s -> s.getWeakPower(world, pos, side));
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if (side == null)
            return 0;
        return max(world, pos, s -> s.getStrongPower(world, pos, side));
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return getTile(world, pos).map(tile -> tile.getPositiveState() != null && tile.getPositiveState().getBlock().canCreatureSpawn(tile.getPositiveState(), world, pos, type, entityType)).orElse(true);
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
        return max(world, pos, s -> s.getFireSpreadSpeed(world, pos, face));
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return max(world, pos, s -> s.getFlammability(world, pos, face));
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
    public boolean isBurning(BlockState state, IBlockReader world, BlockPos pos) {
        return either(world, pos, s -> s.isBurning(world, pos));
    }

    @Override
    public boolean isFertile(BlockState state, IBlockReader world, BlockPos pos) {
        return either(world, pos, s -> s.isFertile(world, pos));
    }

    @Override
    public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return either(world, pos, s -> s.isFireSource(world, pos, side));
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return either(world, pos, s -> s.isFlammable(world, pos, face));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        runIfAvailable(world, pos, s -> s.neighborChanged(world, pos, block, fromPos, isMoving));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (state.getBlock() != this)
            return ActionResultType.PASS;
        return getHalfStateWithWorld(world, pos, hit.getHitVec().y - pos.getY()).map(pair -> {
            IContainerSupport support = ContainerSupport.getSupport(pair.getRight(), pos, pair.getLeft());
            if (support == null) {
                ActionResultType result;
                ISlabSupport slabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, pair.getLeft());
                try {
                    result = slabSupport == null ? pair.getLeft().onBlockActivated(pair.getRight(), player, hand, hit) : slabSupport.onActivated(pair.getLeft(), pair.getRight(), pos, player, hand, hit);
                } catch (Exception e) {
                    result = ActionResultType.PASS;
                }
                return result;
            } else {
                if (!world.isRemote) {
                    NetworkUtils.openGui((ServerPlayerEntity) player, support.getNamedContainerProvider(pair.getRight(), pos, pair.getLeft(), player, hand, hit), pos, pair.getRight().isPositive());
                    support.onClicked(pair.getRight(), pos, pair.getLeft(), player, hand, hit);
                }
                return ActionResultType.SUCCESS;
            }
        }).orElse(ActionResultType.PASS);
//        return runOnVerticalSlab(world, pos, states -> ((state.get(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? (state.get(FACING).getAxis() == Direction.Axis.X ? hit.getHitVec().x - pos.getX() : hit.getHitVec().z - pos.getZ()) > 0.5 : (state.get(FACING).getAxis() == Direction.Axis.X ? hit.getHitVec().x - pos.getX() : hit.getHitVec().z - pos.getZ()) < 0.5) || states.getRight() == null) && states.getLeft() != null ? states.getLeft().onBlockActivated(((TileEntityVerticalSlab) world.getTileEntity(pos)).getPositiveWorld(), player, hand, hit) : states.getRight().onBlockActivated(((TileEntityVerticalSlab) world.getTileEntity(pos)).getNegativeWorld(), player, hand, hit), () -> super.onBlockActivated(state, world, pos, player, hand, hit));
    }

    @Override
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        BlockRayTraceResult result = Utils.rayTrace(player);
        if (result.getHitVec() != null)
            getHalfStateWithWorld(world, pos, result.getHitVec().y - pos.getY())
                    .ifPresent(pair -> pair.getLeft().onBlockClicked(pair.getRight(), pos, player));
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        runIfAvailable(world, pos, s -> s.onNeighborChange(world, pos, neighbor));
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        runIfAvailable(world, pos, s -> s.tick(world, pos, rand));
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        if (!getTile(world, pos).map(tile -> {
            if (tile.getPositiveState() != null) {
                tile.getPositiveState().getBlock().onFallenUpon(tile.getPositiveWorld(), pos, entity, fallDistance);
                return true;
            }
            return false;
        }).orElse(false)) {
            super.onFallenUpon(world, pos, entity, fallDistance);
        }
    }

    @Override
    public void onLanded(IBlockReader world, Entity entity) {
        BlockPos pos = new BlockPos(entity.getPositionVec()).down();
        if (world.getBlockState(pos).getBlock() == this) {
            if (!getTile(world, pos).map(tile -> {
                if (tile.getPositiveState() != null) {
                    tile.getPositiveState().getBlock().onLanded(tile.getPositiveWorld(), entity);
                    return true;
                }
                return false;
            }).orElse(false)) {
                super.onLanded(world, entity);
            }
        }
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveState() != null)
                tile.getPositiveState().getBlock().onEntityWalk(world, pos, entity);
        });
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveState() != null)
                tile.getPositiveState().onEntityCollision(world, pos, entity);
        });
    }

    @Override
    public void onProjectileCollision(World world, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {
        getHalfState(world, hit.getPos(), hit.getHitVec().y).ifPresent(s -> s.onProjectileCollision(world, s, hit, projectile));
    }
}
