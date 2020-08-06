package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IContainerSupport;
import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.blocks.properties.UnlistedPropertyBlockState;
import cjminecraft.doubleslabs.blocks.properties.UnlistedPropertyBoolean;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.network.NetworkUtils;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import cjminecraft.doubleslabs.util.WorldWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class BlockVerticalSlab extends Block {

    public static final UnlistedPropertyBlockState POSITIVE = new UnlistedPropertyBlockState();
    public static final UnlistedPropertyBlockState NEGATIVE = new UnlistedPropertyBlockState();
    public static final UnlistedPropertyBoolean ROTATE_POSITIVE = new UnlistedPropertyBoolean();
    public static final UnlistedPropertyBoolean ROTATE_NEGATIVE = new UnlistedPropertyBoolean();
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool DOUBLE = PropertyBool.create("double");

    public BlockVerticalSlab() {
        super(Material.ROCK);
        setRegistryName(DoubleSlabs.MODID, "vertical_slab");
        setDefaultState(this.getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DOUBLE, false));
    }

    public static Optional<TileEntityVerticalSlab> getTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return world.getBlockState(pos).getBlock() == Registrar.VERTICAL_SLAB && tile instanceof TileEntityVerticalSlab ? Optional.of((TileEntityVerticalSlab) tile) : Optional.empty();
    }

    public static Optional<IBlockState> getAvailableState(IBlockAccess world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> tile.getPositiveState() != null ? Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<IBlockState> getHalfState(IBlockAccess world, BlockPos pos, double x, double z) {
        IBlockState state = world.getBlockState(pos);

        return getTile(world, pos).flatMap(tile ->
                ((state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ?
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) > 0.5 :
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) < 0.5)
                        || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
                        Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<Pair<IBlockState, WorldWrapper>> getHalfStateWithWorld(IBlockAccess world, BlockPos pos, double x, double z) {
        IBlockState state = world.getBlockState(pos);

        return getTile(world, pos).flatMap(tile ->
                ((state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ?
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) > 0.5 :
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) < 0.5)
                        || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
                        Optional.of(Pair.of(tile.getPositiveState(), tile.getPositiveWorld())) : Optional.of(Pair.of(tile.getNegativeState(), tile.getNegativeWorld())));
    }

    public static int min(IBlockAccess world, BlockPos pos, ToIntFunction<IBlockState> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveState() != null ? converter.applyAsInt(tile.getPositiveState()) : Integer.MAX_VALUE, tile.getNegativeState() != null ? converter.applyAsInt(tile.getNegativeState()) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(IBlockAccess world, BlockPos pos, ToDoubleFunction<IBlockState> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveState() != null ? converter.applyAsDouble(tile.getPositiveState()) : Integer.MAX_VALUE, tile.getNegativeState() != null ? converter.applyAsDouble(tile.getNegativeState()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(IBlockAccess world, BlockPos pos, ToIntFunction<IBlockState> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveState() != null ? converter.applyAsInt(tile.getPositiveState()) : 0, tile.getNegativeState() != null ? converter.applyAsInt(tile.getNegativeState()) : 0)).orElse(0);
    }

    public static int maxWithWorld(IBlockAccess world, BlockPos pos, ToIntFunction<Pair<IBlockState, WorldWrapper>> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveState() != null ? converter.applyAsInt(Pair.of(tile.getPositiveState(), tile.getPositiveWorld())) : 0, tile.getNegativeState() != null ? converter.applyAsInt(Pair.of(tile.getNegativeState(), tile.getNegativeWorld())) : 0)).orElse(0);
    }

    public static float maxFloat(IBlockAccess world, BlockPos pos, ToDoubleFunction<IBlockState> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveState() != null ? converter.applyAsDouble(tile.getPositiveState()) : 0, tile.getNegativeState() != null ? converter.applyAsDouble(tile.getNegativeState()) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(IBlockAccess world, BlockPos pos, ToDoubleFunction<IBlockState> converter) {
        return getTile(world, pos).map(tile -> (tile.getPositiveState() != null ? converter.applyAsDouble(tile.getPositiveState()) : 0) + (tile.getNegativeState() != null ? converter.applyAsDouble(tile.getNegativeState()) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(IBlockAccess world, BlockPos pos, Consumer<IBlockState> consumer) {
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveState() != null)
                consumer.accept(tile.getPositiveState());
            if (tile.getNegativeState() != null)
                consumer.accept(tile.getNegativeState());
            return null;
        });
    }

    public static boolean both(IBlockAccess world, BlockPos pos, Predicate<IBlockState> predicate) {
        return getTile(world, pos).map(tile -> tile.getPositiveState() != null && tile.getNegativeState() != null && predicate.test(tile.getPositiveState()) && predicate.test(tile.getNegativeState())).orElse(false);
    }

    public static boolean either(IBlockAccess world, BlockPos pos, Predicate<IBlockState> predicate) {
        return getTile(world, pos).map(tile -> (tile.getPositiveState() != null && predicate.test(tile.getPositiveState())) || (tile.getNegativeState() != null && predicate.test(tile.getNegativeState()))).orElse(false);
    }

    public static Optional<IExtendedBlockState> getTileState(IBlockState state) {
        if (!(state.getBlock() instanceof BlockVerticalSlab))
            return Optional.empty();
        if (state instanceof IExtendedBlockState) {
            return Optional.of((IExtendedBlockState) state);
        }
        return Optional.empty();
    }

    public static Optional<IBlockState> getAvailableState(IBlockState state) {
        return getTileState(state).flatMap(s -> s.getValue(POSITIVE) != null ? Optional.of(s.getValue(POSITIVE)) : Optional.of(s.getValue(NEGATIVE)));
    }

    public static Optional<IBlockState> getHalfState(IBlockState state, double x, double z) {
        return getTileState(state).flatMap(s ->
                ((state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ?
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) > 0.5 :
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) < 0.5)
                        || s.getValue(NEGATIVE) == null) && s.getValue(POSITIVE) != null ?
                        Optional.of(s.getValue(POSITIVE)) : Optional.of(s.getValue(NEGATIVE)));
    }

    public static int min(IBlockState state, ToIntFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.min(s.getValue(POSITIVE) != null ? converter.applyAsInt(s.getValue(POSITIVE)) : Integer.MAX_VALUE, s.getValue(NEGATIVE) != null ? converter.applyAsInt(s.getValue(NEGATIVE)) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(IBlockState state, ToDoubleFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.min(s.getValue(POSITIVE) != null ? converter.applyAsDouble(s.getValue(POSITIVE)) : Integer.MAX_VALUE, s.getValue(NEGATIVE) != null ? converter.applyAsDouble(s.getValue(NEGATIVE)) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(IBlockState state, ToIntFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.max(s.getValue(POSITIVE) != null ? converter.applyAsInt(s.getValue(POSITIVE)) : 0, s.getValue(NEGATIVE) != null ? converter.applyAsInt(s.getValue(NEGATIVE)) : 0)).orElse(0);
    }

    public static float maxFloat(IBlockState state, ToDoubleFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.max(s.getValue(POSITIVE) != null ? converter.applyAsDouble(s.getValue(POSITIVE)) : 0, s.getValue(NEGATIVE) != null ? converter.applyAsDouble(s.getValue(NEGATIVE)) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(IBlockState state, ToDoubleFunction<IBlockState> converter) {
        return getTileState(state).map(s -> (s.getValue(POSITIVE) != null ? converter.applyAsDouble(s.getValue(POSITIVE)) : 0) + (s.getValue(NEGATIVE) != null ? converter.applyAsDouble(s.getValue(NEGATIVE)) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(IBlockState state, Consumer<IBlockState> consumer) {
        getTileState(state).map(s -> {
            if (s.getValue(POSITIVE) != null)
                consumer.accept(s.getValue(POSITIVE));
            if (s.getValue(NEGATIVE) != null)
                consumer.accept(s.getValue(NEGATIVE));
            return null;
        });
    }

    public static boolean both(IBlockState state, Predicate<IBlockState> predicate) {
        return getTileState(state).map(s -> s.getValue(POSITIVE) != null && s.getValue(NEGATIVE) != null && predicate.test(s.getValue(POSITIVE)) && predicate.test(s.getValue(NEGATIVE))).orElse(false);
    }

    public static boolean either(IBlockState state, Predicate<IBlockState> predicate) {
        return getTileState(state).map(s -> (s.getValue(POSITIVE) != null && predicate.test(s.getValue(POSITIVE))) || (s.getValue(NEGATIVE) != null && predicate.test(s.getValue(NEGATIVE)))).orElse(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true;
    }

    // TODO should side be rendered


    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState.Builder(this).add(POSITIVE, NEGATIVE, ROTATE_POSITIVE, ROTATE_NEGATIVE).add(FACING, DOUBLE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState actualState = getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityVerticalSlab && actualState instanceof IExtendedBlockState) {
            TileEntityVerticalSlab te = (TileEntityVerticalSlab) tile;
            boolean rotatePositive = true;
            boolean rotateNegative = true;
            if (te.getPositiveState() != null && SlabSupport.getVerticalSlabSupport(world, pos, te.getPositiveState()) != null)
                rotatePositive = false;
            if (te.getNegativeState() != null && SlabSupport.getVerticalSlabSupport(world, pos, te.getNegativeState()) != null)
                rotateNegative = false;
            return ((IExtendedBlockState) actualState).withProperty(POSITIVE, te.getPositiveState()).withProperty(NEGATIVE, te.getNegativeState()).withProperty(ROTATE_POSITIVE, rotatePositive).withProperty(ROTATE_NEGATIVE, rotateNegative);
        }
        return state;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityVerticalSlab();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return state.getValue(DOUBLE);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(DOUBLE);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(DOUBLE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(DOUBLE))
            return FULL_BLOCK_AABB;

        TileEntity tileEntity = world.getTileEntity(pos);

        double min = 0;
        double max = 8;
        if (state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
            min = 8;
            max = 16;
        }

        if (tileEntity != null) {
            TileEntityVerticalSlab tile = (TileEntityVerticalSlab) tileEntity;

            boolean positive = tile.getPositiveState() != null;
            boolean negative = tile.getNegativeState() != null;

            if ((state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE && positive) || (state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && negative)) {
                min = 8;
                max = 16;
            } else {
                min = 0;
                max = 8;
            }
        }

        if (state.getValue(FACING).getAxis() == EnumFacing.Axis.X)
            return new AxisAlignedBB(min / 16, 0, 0, max / 16, 1, 1);
        else
            return new AxisAlignedBB(0, 0, min / 16, 1, 1, max / 16);
    }

//    public <T> T runOnVerticalSlab(IBlockAccess world, BlockPos pos, Function<Pair<BlockState, BlockState>, T> func, Supplier<T> orElse) {
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        boolean positive = getTile(world, pos).map(tile -> tile.getPositiveState() != null).orElse(true);
        return !state.getValue(DOUBLE) && (face == (positive ? state.getValue(FACING).getOpposite() : state.getValue(FACING))) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @Override
    public boolean canEntitySpawn(IBlockState state, Entity entity) {
        return getTileState(state).map(s -> s.getValue(POSITIVE) != null && s.getValue(POSITIVE).canEntitySpawn(entity) && s.getValue(NEGATIVE) != null && s.getValue(NEGATIVE).canEntitySpawn(entity)).orElse(false);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (entity != null)
            return getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(s -> s.getBlock().getSoundType(s, world, pos, entity)).orElse(super.getSoundType(state, world, pos, entity));
        return getAvailableState(world, pos).map(s -> s.getBlock().getSoundType(s, world, pos, entity)).orElse(super.getSoundType(state, world, pos, null));
//        return runOnVerticalSlab(world, pos, (states) -> states.getLeft() != null ? states.getLeft().getSoundType() : states.getRight().getSoundType(), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return maxFloat(world, pos, s -> s.getBlock().getExplosionResistance(world, pos, exploder, explosion));
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1.0F;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return getTileState(state).map(s -> !(s.getValue(POSITIVE) == null || Utils.isTransparent(s.getValue(POSITIVE)) || s.getValue(NEGATIVE) == null || Utils.isTransparent(s.getValue(NEGATIVE)))).orElse(true);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getLightOpacity(state, world, pos);
//        return min(world, pos, s -> s.getLightOpacity(world, pos));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return max(world, pos, s -> s.getLightValue(world, pos));
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return either(world, pos, s -> BlockDoubleSlab.canHarvestBlock(s.getBlock(), player, s));
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
        if (hitVec == null)
            return minFloat(world, pos, s -> BlockDoubleSlab.blockStrength(s, player, world, pos));
        return getHalfState(world, pos, hitVec.x - pos.getX(), hitVec.z - pos.getZ())
                .map(s -> BlockDoubleSlab.blockStrength(s, player, world, pos))
                .orElse(super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getHalfState(world, pos, target.hitVec.x - pos.getX(), target.hitVec.z - pos.getZ())
                .map(s -> s.getBlock().getPickBlock(s, target, world, pos, player)).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest)
            return true;
        if (player.isCreative() && player.isSneaking()) {
            harvestBlock(world, player, pos, state, world.getTileEntity(pos), ItemStack.EMPTY);
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        runIfAvailable(world, pos, s -> s.getBlock().getDrops(drops, world, pos, s, fortune));
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.isCreative()) {
            TileEntityVerticalSlab tile = (TileEntityVerticalSlab) world.getTileEntity(pos);
            if (tile != null) {
                if (tile.getPositiveState() != null)
                    tile.getPositiveState().getBlock().breakBlock(tile.getPositiveWorld(), pos, Blocks.AIR.getDefaultState());
                if (tile.getNegativeState() != null)
                    tile.getNegativeState().getBlock().breakBlock(tile.getNegativeWorld(), pos, Blocks.AIR.getDefaultState());
            }
            super.onBlockHarvested(world, pos, state, player);
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
        if (hitVec == null || te == null) {
            super.harvestBlock(world, player, pos, state, te, stack);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else {
            if (state.getValue(DOUBLE)) {
                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) te;

                double distance = state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? hitVec.x - (double) pos.getX() : hitVec.z - (double) pos.getZ();

                boolean positive = state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? distance > 0.5 : distance < 0.5;

                IBlockState stateToRemove = positive ? tile.getPositiveState() : tile.getNegativeState();

//                player.addStat(StatList.getBlockStats(stateToRemove.getBlock()));
//                world.playEvent(2001, pos, Block.getStateId(stateToRemove));
//                player.addExhaustion(0.005F);

                if (!player.isCreative())
                    stateToRemove.getBlock().harvestBlock(positive ? tile.getPositiveWorld() : tile.getNegativeWorld(), player, pos, stateToRemove, positive ? tile.getPositiveTile() : tile.getNegativeTile(), stack);
                if (!world.isRemote)
                    stateToRemove.getBlock().breakBlock(positive ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, stateToRemove);

                if (positive)
                    tile.setPositiveState(null);
                else
                    tile.setNegativeState(null);

                world.setBlockState(pos, getExtendedState(state.withProperty(DOUBLE, false), world, pos), Constants.BlockFlags.DEFAULT);
                return;
            } else {
                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) te;
                IBlockState remainingState = tile.getPositiveState() != null ? tile.getPositiveState() : tile.getNegativeState();
//                player.addStat(StatList.getBlockStats(remainingState.getBlock()));
//                world.playEvent(2001, pos, Block.getStateId(remainingState));
//                player.addExhaustion(0.005F);

                if (!player.isCreative())
                    remainingState.getBlock().harvestBlock(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), player, pos, remainingState, tile.getPositiveState() != null ? tile.getPositiveTile() : tile.getNegativeTile(), stack);

                if (!world.isRemote)
                    remainingState.getBlock().breakBlock(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, remainingState);

                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        world.removeTileEntity(pos);
    }

    @Override
    public boolean addLandingEffects(IBlockState state1, WorldServer worldserver, BlockPos pos, IBlockState state2, EntityLivingBase entity, int numberOfParticles) {
        float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
        double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
        final int numOfParticles = state1.getValue(DOUBLE) ? (int) (75.0D * d0) : (int) (150.0D * d0);
        runIfAvailable(worldserver, pos, s -> worldserver.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(s)));
        return true;
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            runIfAvailable(world, pos, s -> world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                    entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                    entity.getEntityBoundingBox().minY + 0.1D,
                    entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                    -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, Block.getStateId(s)));
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            return getHalfState(world, target.getBlockPos(), target.hitVec.x, target.hitVec.z).map(s -> {
                BlockPos pos = target.getBlockPos();
                EnumFacing side = target.sideHit;

                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();

                AxisAlignedBB axisalignedbb = state.getCollisionBoundingBox(world, pos);
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

                ParticleDigging.Factory factory = new ParticleDigging.Factory();

                Particle particle = factory.createParticle(1, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(s));
                if (particle != null) {
                    ((ParticleDigging) particle).setBlockPos(pos);
                    particle = particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
                    manager.addEffect(particle);
                    return true;
                }

                return false;
            }).orElse(false);
        }
        return false;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return getTile(world, pos).map(tile -> {
            ParticleDigging.Factory factory = new ParticleDigging.Factory();
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        double d0 = ((double) j + 0.5D) / 4.0D + pos.getX();
                        double d1 = ((double) k + 0.5D) / 4.0D + pos.getY();
                        double d2 = ((double) l + 0.5D) / 4.0D + pos.getZ();

                        if (tile.getPositiveState() != null) {
                            Particle particle1 = factory.createParticle(1, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(tile.getPositiveState()));
                            if (particle1 != null)
                                manager.addEffect(particle1);
                        }

                        if (tile.getNegativeState() != null) {
                            Particle particle2 = factory.createParticle(1, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(tile.getNegativeState()));
                            if (particle2 != null)
                                manager.addEffect(particle2);
                        }
                    }
                }
            }
            return true;
        }).orElse(false);
    }

    @SideOnly(Side.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> {
            if (world == null || pos == null)
                return -1;
            return getTile(world, pos).map(tile -> {
                if (tintIndex < DoubleSlabBakedModel.TINT_OFFSET)
                    return tile.getPositiveState() != null ? Minecraft.getMinecraft().getBlockColors().colorMultiplier(tile.getPositiveState(), world, pos, tintIndex) : -1;
                return tile.getNegativeState() != null ? Minecraft.getMinecraft().getBlockColors().colorMultiplier(tile.getNegativeState(), world, pos, tintIndex) : -1;
            }).orElse(-1);
        };
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        super.randomTick(world, pos, state, random);
        runIfAvailable(world, pos, s -> s.getBlock().randomTick(world, pos, s, random));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        runIfAvailable(world, pos, s -> s.getBlock().randomDisplayTick(s, world, pos, rand));
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        if (side == null)
            return false;
        EnumFacing face = Utils.rotateFace(side, state.getValue(FACING));
        return getTile(world, pos).map(tile -> (tile.getPositiveState() != null && tile.getPositiveState().getBlock().canConnectRedstone(tile.getPositiveState(), world, pos, face)) || (tile.getNegativeState() != null && tile.getNegativeState().getBlock().canConnectRedstone(tile.getNegativeState(), world, pos, face))).orElse(false);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (side == null)
            return 0;
        EnumFacing face = Utils.rotateFace(side, state.getValue(FACING));
        return max(world, pos, s -> s.getWeakPower(world, pos, face));
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (side == null)
            return 0;
        EnumFacing face = Utils.rotateFace(side, state.getValue(FACING));
        return max(world, pos, s -> s.getStrongPower(world, pos, face));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return both(world, pos, s -> s.getBlock().canCreatureSpawn(s, world, pos, type));
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return maxWithWorld(world, pos, pair -> pair.getLeft().getComparatorInputOverride(pair.getRight(), pos));
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return addFloat(world, pos, s -> s.getBlock().getEnchantPowerBonus(world, pos));
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        EnumFacing side = Utils.rotateFace(face, world.getBlockState(pos).getValue(FACING));
        return max(world, pos, s -> s.getBlock().getFireSpreadSpeed(world, pos, side));
    }


    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        EnumFacing side = Utils.rotateFace(face, world.getBlockState(pos).getValue(FACING));
        return max(world, pos, s -> s.getBlock().getFlammability(world, pos, side));
    }

    @Override
    public boolean getTickRandomly() {
        return true;
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beacon) {
        return either(world, pos, s -> s.getBlock().isBeaconBase(world, pos, beacon));
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos) {
        return either(world, pos, s -> s.getBlock().isBurning(world, pos));
    }

    @Override
    public boolean isFertile(World world, BlockPos pos) {
        return either(world, pos, s -> s.getBlock().isFertile(world, pos));
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        EnumFacing face = Utils.rotateFace(side, world.getBlockState(pos).getValue(FACING));
        return either(world, pos, s -> s.getBlock().isFireSource(world, pos, face));
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        EnumFacing side = Utils.rotateFace(face, world.getBlockState(pos).getValue(FACING));
        return either(world, pos, s -> s.getBlock().isFlammable(world, pos, side));
    }

    @Override
    public boolean isFoliage(IBlockAccess world, BlockPos pos) {
        return either(world, pos, s -> s.getBlock().isFoliage(world, pos));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, block, fromPos);
        runIfAvailable(world, pos, s -> s.neighborChanged(world, pos, block, fromPos));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getBlock() != this)
            return false;
        return getHalfStateWithWorld(world, pos, hitX, hitZ).map(pair -> {
            IContainerSupport support = ContainerSupport.getSupport(pair.getRight(), pos, pair.getLeft());
            if (support == null) {
                boolean result;
                ISlabSupport slabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, pair.getLeft());
                if (slabSupport == null)
                    slabSupport = SlabSupport.getVerticalSlabSupport(world, pos, pair.getLeft());
                try {
                    result = slabSupport == null ? pair.getLeft().getBlock().onBlockActivated(pair.getRight(), pos, pair.getLeft(), player, hand, facing, hitX, hitY, hitZ) : slabSupport.onActivated(pair.getLeft(), pair.getRight(), pos, player, hand, facing, hitX, hitY, hitZ);
                } catch (Exception e) {
                    result = false;
                }
                return result;
            } else {
                if (!world.isRemote) {
                    NetworkUtils.openGui(player, support.getMod(), support.getGuiId(pair.getRight(), pos, pair.getLeft()), pair.getRight(), pos.getX(), pos.getY(), pos.getZ(), pair.getRight().isPositive());
                    support.onClicked(pair.getRight(), pos, pair.getLeft(), player, hand, facing);
                }
                return true;
            }
        }).orElse(false);
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        RayTraceResult result = Utils.rayTrace(player);
        if (result.hitVec != null)
            getHalfStateWithWorld(world, pos, result.hitVec.x - pos.getX(), result.hitVec.z - pos.getZ()).ifPresent(pair -> pair.getLeft().getBlock().onBlockClicked(pair.getRight(), pos, player));
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        runIfAvailable(world, pos, s -> s.getBlock().onNeighborChange(world, pos, neighbor));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        runIfAvailable(world, pos, s -> s.getBlock().updateTick(world, pos, s, rand));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < 4 ? this.getDefaultState().withProperty(DOUBLE, false).withProperty(FACING, EnumFacing.byHorizontalIndex(meta)) : this.getDefaultState().withProperty(DOUBLE, true).withProperty(FACING, EnumFacing.byHorizontalIndex(meta - 4));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() + (state.getValue(DOUBLE) ? 4 : 0);
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
    public void onLanded(World world, Entity entity) {
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

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        boolean positive = getTile(world, pos).map(tile -> tile.getPositiveState() != null).orElse(true);
        return state.getValue(DOUBLE) || facing != (positive ? state.getValue(FACING).getOpposite() : state.getValue(FACING));
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).ifPresent(s -> s.getBlock().onEntityCollision(world, pos, s, entity));
    }
}
