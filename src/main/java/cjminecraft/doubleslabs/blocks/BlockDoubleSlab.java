package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IContainerSupport;
import cjminecraft.doubleslabs.blocks.properties.UnlistedPropertyBlockState;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.network.NetworkUtils;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.util.WorldWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.*;

public class BlockDoubleSlab extends Block {
    public static final UnlistedPropertyBlockState TOP = new UnlistedPropertyBlockState();
    public static final UnlistedPropertyBlockState BOTTOM = new UnlistedPropertyBlockState();

    public BlockDoubleSlab() {
        super(Material.ROCK);
        setRegistryName(DoubleSlabs.MODID, "double_slab");
    }

    public static Optional<TileEntityDoubleSlab> getTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return world.getBlockState(pos).getBlock() == Registrar.DOUBLE_SLAB && tile instanceof TileEntityDoubleSlab ? Optional.of((TileEntityDoubleSlab) tile) : Optional.empty();
    }

    public static Optional<IBlockState> getAvailableState(IBlockAccess world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> tile.getPositiveState() != null ? Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<IBlockState> getHalfState(IBlockAccess world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeState() == null && tile.getPositiveState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
                        Optional.of(tile.getPositiveState()) : Optional.of(tile.getNegativeState()));
    }

    public static Optional<Pair<IBlockState, WorldWrapper>> getHalfStateWithWorld(IBlockAccess world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeState() == null && tile.getPositiveState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeState() == null) && tile.getPositiveState() != null ?
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
        return getTileState(state).flatMap(s -> s.getValue(TOP) != null ? Optional.of(s.getValue(TOP)) : Optional.of(s.getValue(BOTTOM)));
    }

    public static Optional<IBlockState> getHalfState(IBlockState state, double y) {
        return getTileState(state).flatMap(s ->
                (y > 0.5 || s.getValue(BOTTOM) == null) && s.getValue(TOP) != null ?
                        Optional.of(s.getValue(TOP)) : Optional.of(s.getValue(BOTTOM)));
    }

    public static int min(IBlockState state, ToIntFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.min(s.getValue(TOP) != null ? converter.applyAsInt(s.getValue(TOP)) : Integer.MAX_VALUE, s.getValue(BOTTOM) != null ? converter.applyAsInt(s.getValue(BOTTOM)) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(IBlockState state, ToDoubleFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.min(s.getValue(TOP) != null ? converter.applyAsDouble(s.getValue(TOP)) : Integer.MAX_VALUE, s.getValue(BOTTOM) != null ? converter.applyAsDouble(s.getValue(BOTTOM)) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(IBlockState state, ToIntFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.max(s.getValue(TOP) != null ? converter.applyAsInt(s.getValue(TOP)) : 0, s.getValue(BOTTOM) != null ? converter.applyAsInt(s.getValue(BOTTOM)) : 0)).orElse(0);
    }

    public static float maxFloat(IBlockState state, ToDoubleFunction<IBlockState> converter) {
        return getTileState(state).map(s -> Math.max(s.getValue(TOP) != null ? converter.applyAsDouble(s.getValue(TOP)) : 0, s.getValue(BOTTOM) != null ? converter.applyAsDouble(s.getValue(BOTTOM)) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(IBlockState state, ToDoubleFunction<IBlockState> converter) {
        return getTileState(state).map(s -> (s.getValue(TOP) != null ? converter.applyAsDouble(s.getValue(TOP)) : 0) + (s.getValue(BOTTOM) != null ? converter.applyAsDouble(s.getValue(BOTTOM)) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(IBlockState state, Consumer<IBlockState> consumer) {
        getTileState(state).map(s -> {
            if (s.getValue(TOP) != null)
                consumer.accept(s.getValue(TOP));
            if (s.getValue(BOTTOM) != null)
                consumer.accept(s.getValue(BOTTOM));
            return null;
        });
    }

    public static boolean both(IBlockState state, Predicate<IBlockState> predicate) {
        return getTileState(state).map(s -> s.getValue(TOP) != null && s.getValue(BOTTOM) != null && predicate.test(s.getValue(TOP)) && predicate.test(s.getValue(BOTTOM))).orElse(false);
    }

    public static boolean either(IBlockState state, Predicate<IBlockState> predicate) {
        return getTileState(state).map(s -> (s.getValue(TOP) != null && predicate.test(s.getValue(TOP))) || (s.getValue(BOTTOM) != null && predicate.test(s.getValue(BOTTOM)))).orElse(false);
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    @Nonnull
//    public BlockRenderLayer getRenderLayer() {
//        return BlockRenderLayer.CUTOUT_MIPPED;
//    }

    public static boolean canHarvestBlock(@Nonnull Block block, @Nonnull EntityPlayer player, @Nonnull IBlockState state) {
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            String tool = block.getHarvestTool(state);
            if (!stack.isEmpty() && tool != null) {
                int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
                if (toolLevel < 0) {
                    return player.canHarvestBlock(state);
                } else {
                    return toolLevel >= block.getHarvestLevel(state);
                }
            } else {
                return player.canHarvestBlock(state);
            }
        }
    }

    public static float blockStrength(@Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F) {
            return 0.0F;
        } else {
            return !canHarvestBlock(state.getBlock(), player, state) ? player.getDigSpeed(state, pos) / hardness / 100.0F : player.getDigSpeed(state, pos) / hardness / 30.0F;
        }
    }

    private <T> T runOnDoubleSlab(IBlockState state, Function<Pair<IBlockState, IBlockState>, T> func, Supplier<T> orElse) {
        if (state.getBlock() != this)
            return orElse.get();
        if (state instanceof IExtendedBlockState) {
            IBlockState topState = ((IExtendedBlockState) state).getValue(TOP);
            IBlockState bottomState = ((IExtendedBlockState) state).getValue(BOTTOM);
            if (topState == null || bottomState == null)
                return orElse.get();
            return func.apply(Pair.of(topState, bottomState));
        }
        return orElse.get();
    }

    private <T> T runOnDoubleSlab(IBlockState state, IBlockAccess world, BlockPos pos, Function<Pair<IBlockState, IBlockState>, T> func, Supplier<T> orElse) {
        if (state.getBlock() != this)
            return orElse.get();
        IBlockState extendedState = getExtendedState(state, world, pos);
        return runOnDoubleSlab(extendedState, func, orElse);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return getTileState(state).map(s -> s.getValue(TOP).isTopSolid()).orElse(true);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return getTile(world, pos).map(tile -> {
            if (face == EnumFacing.UP)
                return tile.getTopState().getBlockFaceShape(world, pos, EnumFacing.UP);
            if (face == EnumFacing.DOWN)
                return tile.getBottomState().getBlockFaceShape(world, pos, EnumFacing.DOWN);
            BlockFaceShape topFace = tile.getTopState().getBlockFaceShape(world, pos, EnumFacing.UP);
            BlockFaceShape bottomFace = tile.getBottomState().getBlockFaceShape(world, pos, EnumFacing.DOWN);
            if (topFace == BlockFaceShape.SOLID && bottomFace == BlockFaceShape.SOLID)
                return BlockFaceShape.SOLID;
            return BlockFaceShape.UNDEFINED;
        }).orElse(BlockFaceShape.SOLID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState other = world.getBlockState(pos.offset(side));
        return getTile(world, pos).map(tile1 -> getTile(world, pos.offset(side)).map(tile2 -> {
            boolean selfTransparent = Utils.isTransparent(tile1.getTopState()) || Utils.isTransparent(tile1.getBottomState());
            boolean otherTransparent = Utils.isTransparent(tile2.getTopState()) || Utils.isTransparent(tile2.getBottomState());

            return selfTransparent ^ otherTransparent;
        }).orElse(super.shouldSideBeRendered(state, world, pos, side))).orElse(super.shouldSideBeRendered(state, world, pos, side));
//        return runOnDoubleSlab(state, world, pos, states -> runOnDoubleSlab(other, world, pos.offset(side), otherStates -> {
//            boolean selfTransparent = Utils.isTransparent(states.getLeft()) || Utils.isTransparent(states.getRight());
//            boolean otherTransparent = Utils.isTransparent(otherStates.getLeft()) || Utils.isTransparent(otherStates.getRight());
//
//            return selfTransparent ^ otherTransparent;
//        }, () -> super.shouldSideBeRendered(state, world, pos, side)), () -> super.shouldSideBeRendered(state, world, pos, side));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
//        return runOnDoubleSlab(state, world, pos, states -> states.getLeft().isNormalCube() && states.getRight().isNormalCube(), () -> true);
    }

    @Override
    public boolean canEntitySpawn(IBlockState state, Entity entity) {
        return both(state, s -> s.canEntitySpawn(entity));
//        return runOnDoubleSlab(state, states -> states.getLeft().canEntitySpawn(entity), () -> true);
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    @Nonnull
    public Material getMaterial(IBlockState state) {
        return getTileState(state).map(s -> s.getValue(TOP).getMaterial()).orElse(Material.ROCK);
//        return runOnDoubleSlab(state, states -> states.getLeft().getMaterial(), () -> Material.ROCK);
    }

    @Override
    @Nonnull
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (entity != null)
            return getHalfState(world, pos, entity.posY - pos.getY()).map(s -> s.getBlock().getSoundType(s, world, pos, entity)).orElse(super.getSoundType(state, world, pos, entity));
        return getAvailableState(world, pos).map(s -> s.getBlock().getSoundType(s, world, pos, null)).orElse(super.getSoundType(state, world, pos, null));
//        return runOnDoubleSlab(state, states -> states.getLeft().getBlock().getSoundType(state, world, pos, entity), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1.0F;
//        return runOnDoubleSlab(state, states -> Math.max(states.getLeft().getAmbientOcclusionLightValue(), states.getRight().getAmbientOcclusionLightValue()), () -> super.getAmbientOcclusionLightValue(state));
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return either(state, IBlockProperties::causesSuffocation);
//        return runOnDoubleSlab(state, states -> states.getLeft().causesSuffocation() || states.getRight().causesSuffocation(), () -> true);
    }

    @Override
    @Nullable
    public String getHarvestTool(@Nonnull IBlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        return either(world, pos, s -> canHarvestBlock(s.getBlock(), player, s));
//        return runOnDoubleSlab(world.getBlockState(pos), world, pos, states -> canHarvestBlock(states.getLeft().getBlock(), player, states.getLeft()) || canHarvestBlock(states.getRight().getBlock(), player, states.getRight()), () -> true);
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
        if (hitVec == null)
            return minFloat(world, pos, s -> s.getPlayerRelativeBlockHardness(player, world, pos));
        return getHalfState(world, pos, hitVec.y - pos.getY()).map(s -> blockStrength(s, player, world, pos)).orElse(super.getPlayerRelativeBlockHardness(state, player, world, pos));
//        return runOnDoubleSlab(state, world, pos, states -> {
//            RayTraceResult rayTraceResult = Utils.rayTrace(player);
//            Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
//            if (hitVec == null)
//                return Math.min(blockStrength(states.getLeft(), player, world, pos), blockStrength(states.getRight(), player, world, pos));
//            return (hitVec.y - pos.getY()) > 0.5 ? blockStrength(states.getLeft(), player, world, pos) : blockStrength(states.getRight(), player, world, pos);
//        }, () -> super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return max(world, pos, s -> s.getLightOpacity(world, pos));
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return maxFloat(world, pos, s -> s.getBlockHardness(world, pos));
//        return runOnDoubleSlab(state, world, pos, states -> Math.max(states.getLeft().getBlockHardness(world, pos), states.getRight().getBlockHardness(world, pos)), () -> super.getBlockHardness(state, world, pos));
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return maxFloat(world, pos, s -> s.getBlock().getExplosionResistance(world, pos, exploder, explosion));
//        return runOnDoubleSlab(world.getBlockState(pos), world, pos, states -> Math.min(states.getLeft().getBlock().getExplosionResistance(world, pos, exploder, explosion), states.getRight().getBlock().getExplosionResistance(world, pos, exploder, explosion)), () -> super.getExplosionResistance(world, pos, exploder, explosion));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return max(world, pos, s -> s.getLightValue(world, pos));
//        return runOnDoubleSlab(state, world, pos, states -> Math.max(states.getLeft().getLightValue(world, pos), states.getRight().getLightValue(world, pos)), () -> super.getLightValue(state, world, pos));
    }

//    @Override
//    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
//        return runOnDoubleSlab(state, world, pos, states -> Math.min(states.getLeft().getLightOpacity(world, pos), states.getRight().getLightOpacity(world, pos)), () -> super.getLightOpacity(state, world, pos));
//    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityDoubleSlab();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState.Builder(this).add(TOP, BOTTOM).build();
    }

    @Override
    @Nonnull
    public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState actualState = getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityDoubleSlab && actualState instanceof IExtendedBlockState)
            return ((IExtendedBlockState) actualState).withProperty(TOP, ((TileEntityDoubleSlab) tile).getTopState()).withProperty(BOTTOM, ((TileEntityDoubleSlab) tile).getBottomState());
        return state;
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return getHalfState(world, pos, target.hitVec.y - pos.getY()).map(s -> s.getBlock().getPickBlock(s, target, world, pos, player)).orElse(ItemStack.EMPTY);
//        return runOnDoubleSlab(state, world, pos, states -> target.hitVec.y - pos.getY() > 0.5 ? states.getLeft().getBlock().getPickBlock(states.getLeft(), target, world, pos, player) : states.getRight().getBlock().getPickBlock(states.getRight(), target, world, pos, player), () -> ItemStack.EMPTY);
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        if (willHarvest)
            return true;
        return super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) world.getTileEntity(pos);
        if (tile != null) {
            tile.getTopState().getBlock().getDrops(drops, world, pos, tile.getTopState(), fortune);
            tile.getBottomState().getBlock().getDrops(drops, world, pos, tile.getBottomState(), fortune);
        }
    }

    @Override
    public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
        if (hitVec == null || te == null) {
            super.harvestBlock(world, player, pos, state, te, stack);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            world.removeTileEntity(pos);
        } else {
            TileEntityDoubleSlab tile = (TileEntityDoubleSlab) te;

            double y = hitVec.y - (double) pos.getY();

            TileEntity remainingTile = y > 0.5 ? tile.getNegativeTile() : tile.getPositiveTile();
            IBlockState remainingState = y > 0.5 ? tile.getBottomState() : tile.getTopState();
            IBlockState stateToRemove = y > 0.5 ? tile.getTopState() : tile.getBottomState();

//            player.addStat(StatList.getBlockStats(stateToRemove.getBlock()));
//            player.addExhaustion(0.005F);
//            world.playEvent(2001, pos, Block.getStateId(stateToRemove));

            if (!world.isRemote)
                stateToRemove.getBlock().breakBlock(y > 0.5 ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, y > 0.5 ? tile.getPositiveState() : tile.getNegativeState());
            if (!player.isCreative())
                stateToRemove.getBlock().harvestBlock(y > 0.5 ? tile.getPositiveWorld() : tile.getNegativeWorld(), player, pos, stateToRemove, y > 0.5 ? tile.getPositiveTile() : tile.getNegativeTile(), stack);

//            stateToRemove.getBlock().harvestBlock(y > 0.5 ? tile.getPositiveWorld() : tile.getNegativeWorld(), player, pos, stateToRemove, y > 0.5 ? tile.getPositiveTile() : tile.getNegativeTile(), stack);

            world.setBlockState(pos, remainingState, 11);
            world.setTileEntity(pos, remainingTile);
        }
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        return getTile(world, pos).map(tile -> {
            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            world.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numOfParticles,
                    0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(tile.getTopState()));
            return true;
        }).orElse(false);
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            getTile(world, pos).ifPresent(tile -> {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                        entity.getEntityBoundingBox().minY + 0.1D,
                        entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                        -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, Block.getStateId(tile.getPositiveState()));
            });
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            return getHalfState(world, target.getBlockPos(), target.hitVec.y).map(s -> {
                BlockPos pos = target.getBlockPos();
                EnumFacing side = target.sideHit;

                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();

                AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
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
                ParticleDigging particle = (ParticleDigging) factory.createParticle(1, world, d0, d1, d2,
                        0.0D, 0.0D, 0.0D, Block.getStateId(s));
                if (particle == null)
                    return false;
                particle.setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
                manager.addEffect(particle);

                return true;
            }).orElse(false);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return getTile(world, pos).map(tile -> {
            ParticleDigging.Factory factory = new ParticleDigging.Factory();
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    for (int l = 0; l < 4; ++l) {
                        double d0 = ((double) j + 0.5D) / 4.0D;
                        double d1 = ((double) k + 0.5D) / 4.0D;
                        double d2 = ((double) l + 0.5D) / 4.0D;

                        if (tile.getPositiveState() != null)
                            createDestroyParticle(world, pos, manager, tile.getPositiveState(), factory, d0, d1, d2);
                        if (tile.getNegativeState() != null)
                            createDestroyParticle(world, pos, manager, tile.getNegativeState(), factory, d0, d1, d2);
                    }
                }
            }
            return true;
        }).orElse(false);
    }

    @SideOnly(Side.CLIENT)
    private void createDestroyParticle(World world, BlockPos pos, ParticleManager manager, IBlockState state, ParticleDigging.Factory factory, double d0, double d1, double d2) {
        ParticleDigging particle = (ParticleDigging) factory.createParticle(0, world,
                (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
                d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, Block.getStateId(state));
        if (particle == null)
            return;
        particle.setBlockPos(pos);
        manager.addEffect(particle);
    }

    @SideOnly(Side.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> getTile(world, pos).map(tile -> {
            if (tintIndex < DoubleSlabBakedModel.TINT_OFFSET)
                return Minecraft.getMinecraft().getBlockColors().colorMultiplier(tile.getTopState(), world, pos, tintIndex);
            return Minecraft.getMinecraft().getBlockColors().colorMultiplier(tile.getNegativeState(), world, pos, tintIndex - DoubleSlabBakedModel.TINT_OFFSET);
        }).orElse(-1);
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
        return getTile(world, pos).map(tile -> (tile.getPositiveState() != null && tile.getPositiveState().getBlock().canConnectRedstone(tile.getPositiveState(), world, pos, side)) || (tile.getNegativeState() != null && tile.getNegativeState().getBlock().canConnectRedstone(tile.getNegativeState(), world, pos, side))).orElse(false);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (side == null)
            return 0;
        return max(world, pos, s -> s.getWeakPower(world, pos, side));
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (side == null)
            return 0;
        return max(world, pos, s -> s.getStrongPower(world, pos, side));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return getTile(world, pos).map(tile -> tile.getPositiveState().getBlock().canCreatureSpawn(tile.getPositiveState(), world, pos, type)).orElse(true);
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
        return max(world, pos, s -> s.getBlock().getFireSpreadSpeed(world, pos, face));
    }


    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return max(world, pos, s -> s.getBlock().getFlammability(world, pos, face));
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
        return either(world, pos, s -> s.getBlock().isFireSource(world, pos, side));
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return either(world, pos, s -> s.getBlock().isFlammable(world, pos, face));
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
        return getHalfStateWithWorld(world, pos, hitY).map(pair -> {
            IContainerSupport support = ContainerSupport.getSupport(pair.getRight(), pos, pair.getLeft());
            if (support == null) {
                boolean result;
                try {
                    result = pair.getLeft().getBlock().onBlockActivated(pair.getRight(), pos, pair.getLeft(), player, hand, facing, hitX, hitY, hitZ);
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
            getHalfStateWithWorld(world, pos, result.hitVec.y - pos.getY()).ifPresent(pair -> pair.getLeft().getBlock().onBlockClicked(pair.getRight(), pos, player));
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
    public void onLanded(World world, Entity entity) {
        BlockPos pos = entity.getPosition().down();
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
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveState() != null) {
                tile.getPositiveState().getBlock().onEntityWalk(world, pos, entity);
                return true;
            }
            return false;
        }).orElse(null);
    }
}
