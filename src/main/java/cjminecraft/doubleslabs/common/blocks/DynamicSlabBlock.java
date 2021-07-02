package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import cjminecraft.doubleslabs.client.ClientConstants;
import cjminecraft.doubleslabs.common.blocks.properties.UnlistedPropertyBlockInfo;
import cjminecraft.doubleslabs.common.blocks.properties.UnlistedPropertyBoolean;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

public class DynamicSlabBlock extends Block {

    public static final UnlistedPropertyBlockInfo POSITIVE_BLOCK = new UnlistedPropertyBlockInfo();
    public static final UnlistedPropertyBlockInfo NEGATIVE_BLOCK = new UnlistedPropertyBlockInfo();
    public static final UnlistedPropertyBoolean RENDER_POSITIVE = new UnlistedPropertyBoolean();

    public DynamicSlabBlock() {
        super(Material.ROCK);
    }

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

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState state) {
        return either(state, i -> i.getBlockState().isTranslucent());
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
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(POSITIVE_BLOCK, NEGATIVE_BLOCK, RENDER_POSITIVE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState actualState = getActualState(state, world, pos);
        return actualState instanceof IExtendedBlockState ? getTile(world, pos)
                .map(tile -> ((IExtendedBlockState) actualState)
                        .withProperty(POSITIVE_BLOCK, tile.getPositiveBlockInfo())
                        .withProperty(NEGATIVE_BLOCK, tile.getNegativeBlockInfo()))
                .orElse((IExtendedBlockState) actualState) : actualState;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true;
    }

    public static Optional<SlabTileEntity> getTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof SlabTileEntity ? Optional.of((SlabTileEntity) tile) : Optional.empty();
    }

    public static Optional<IBlockInfo> getAvailable(IBlockAccess world, BlockPos pos) {
        return getTile(world, pos).flatMap(tile -> Optional.of(tile.getPositiveBlockInfo().getBlockState() != null ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo()));
    }

    public static int min(IBlockAccess world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0);
    }

    public static float minFloat(IBlockAccess world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.min(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : Integer.MAX_VALUE, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : Integer.MAX_VALUE)).orElse(0D).floatValue();
    }

    public static int max(IBlockAccess world, BlockPos pos, ToIntFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsInt(tile.getNegativeBlockInfo()) : 0)).orElse(0);
    }

    public static float maxFloat(IBlockAccess world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> Math.max(tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0, tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static float addFloat(IBlockAccess world, BlockPos pos, ToDoubleFunction<IBlockInfo> converter) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getPositiveBlockInfo()) : 0) + (tile.getNegativeBlockInfo().getBlockState() != null ? converter.applyAsDouble(tile.getNegativeBlockInfo()) : 0)).orElse(0D).floatValue();
    }

    public static void runIfAvailable(IBlockAccess world, BlockPos pos, Consumer<IBlockInfo> consumer) {
        getTile(world, pos).map(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                consumer.accept(tile.getPositiveBlockInfo());
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                consumer.accept(tile.getNegativeBlockInfo());
            return null;
        });
    }

    public static boolean both(IBlockAccess world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo()) && predicate.test(tile.getNegativeBlockInfo())).orElse(false);
    }

    public static boolean either(IBlockAccess world, BlockPos pos, Predicate<IBlockInfo> predicate) {
        return getTile(world, pos).map(tile -> (tile.getPositiveBlockInfo().getBlockState() != null && predicate.test(tile.getPositiveBlockInfo())) || (tile.getNegativeBlockInfo().getBlockState() != null && predicate.test(tile.getNegativeBlockInfo()))).orElse(false);
    }

    public static Optional<IStateContainer> getStateContainer(IBlockState state) {
        if (!(state.getBlock() instanceof DynamicSlabBlock))
            return Optional.empty();
        if (state instanceof IExtendedBlockState) {
            IBlockInfo positiveBlock = ((IExtendedBlockState) state).getValue(POSITIVE_BLOCK);
            IBlockInfo negativeBlock = ((IExtendedBlockState) state).getValue(NEGATIVE_BLOCK);
            if (negativeBlock == null || positiveBlock == null)
                return Optional.empty();
            return Optional.of(new IStateContainer() {
                @Override
                public IBlockInfo getPositiveBlockInfo() {
                    return positiveBlock;
                }

                @Override
                public IBlockInfo getNegativeBlockInfo() {
                    return negativeBlock;
                }
            });
        }
        return Optional.empty();
    }

    public static boolean either(IBlockState state, Predicate<IBlockInfo> predicate) {
        return getStateContainer(state).map(c -> (c.getPositiveBlockInfo().getBlockState() != null && predicate.test(c.getPositiveBlockInfo()) || (c.getNegativeBlockInfo().getBlockState() != null && predicate.test(c.getNegativeBlockInfo())))).orElse(false);
    }

    public static boolean both(IBlockState state, Predicate<IBlockInfo> predicate) {
        return getStateContainer(state).map(c -> c.getPositiveBlockInfo().getBlockState() != null && c.getNegativeBlockInfo().getBlockState() != null && predicate.test(c.getPositiveBlockInfo()) && predicate.test(c.getNegativeBlockInfo())).orElse(false);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new SlabTileEntity();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return max(world, pos, i -> i.getBlockState().getBlock().getLightOpacity(i.getBlockState(), i.getWorld(), i.getPos()));
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return maxFloat(world, pos, i -> i.getBlockState().getBlock().getExplosionResistance(i.getWorld(), i.getPos(), exploder, explosion));
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1.0F;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return max(world, pos, i -> i.getBlockState().getLightValue(i.getWorld(), i.getPos()));
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return null;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().getDrops(drops, i.getWorld(), i.getPos(), i.getBlockState(), fortune));
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
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.isCreative()) {
            runIfAvailable(world, pos, i -> i.getBlockState().getBlock().breakBlock(i.getWorld(), pos, i.getBlockState()));
            super.onBlockHarvested(world, pos, state, player);
        }
    }

    @SideOnly(Side.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> {
            if (world == null || pos == null)
                return -1;
            return getTile(world, pos).map(tile -> {
                if (tintIndex >= ClientConstants.TINT_OFFSET)
                    return tile.getPositiveBlockInfo().getBlockState() != null ? Minecraft.getMinecraft().getBlockColors().colorMultiplier(tile.getPositiveBlockInfo().getBlockState(), tile.getPositiveBlockInfo().getWorld(), pos, tintIndex - ClientConstants.TINT_OFFSET) : -1;
                return tile.getNegativeBlockInfo().getBlockState() != null ? Minecraft.getMinecraft().getBlockColors().colorMultiplier(tile.getNegativeBlockInfo().getBlockState(), tile.getNegativeBlockInfo().getWorld(), pos, tintIndex) : -1;
            }).orElse(-1);
        };
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        super.randomTick(world, pos, state, random);
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().randomTick(i.getWorld(), i.getPos(), i.getBlockState(), random));
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return either(world, pos, i -> i.getBlockState().getBlock().canConnectRedstone(i.getBlockState(), i.getWorld(), i.getPos(), side));
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return max(world, pos, i -> i.getBlockState().getWeakPower(i.getWorld(), pos, side));
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return max(world, pos, i -> i.getBlockState().getStrongPower(i.getWorld(), pos, side));
    }


    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return max(world, pos, i -> i.getBlockState().getComparatorInputOverride(i.getWorld(), pos));
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return addFloat(world, pos, i -> i.getBlockState().getBlock().getEnchantPowerBonus(i.getWorld(), i.getPos()));
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        try {
            return max(world, pos, i -> i.getBlockState().getBlock().getFireSpreadSpeed(i.getWorld(), i.getPos(), face));
        } catch (IllegalArgumentException e) { // Likely BoP crash
            return Blocks.PLANKS.getFireSpreadSpeed(world, pos, face);
        }
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        try {
            return max(world, pos, i -> i.getBlockState().getBlock().getFlammability(i.getWorld(), i.getPos(), face));
        } catch (IllegalArgumentException e) { // Likely BoP crash
            return Blocks.PLANKS.getFlammability(world, pos, face);
        }
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().getBlock().getWeakChanges(i.getWorld(), i.getPos()));
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().getBlock().isBurning(i.getWorld(), i.getPos()));
    }

    @Override
    public boolean isFertile(World world, BlockPos pos) {
        return either(world, pos, i -> i.getBlockState().getBlock().isFertile(i.getWorld(), i.getPos()));
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return either(world, pos, i -> i.getBlockState().getBlock().isFireSource(i.getWorld(), i.getPos(), side));
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        try {
            return either(world, pos, i -> i.getBlockState().getBlock().isFlammable(i.getWorld(), i.getPos(), face));
        } catch (IllegalArgumentException e) { // Likely BoP crash
            return Blocks.PLANKS.isFlammable(world, pos, face);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
        runIfAvailable(world, pos, i -> i.getBlockState().neighborChanged(i.getWorld(), i.getPos(), i.getBlockState().getBlock(), fromPos));
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().onNeighborChange(i.getWorld(), i.getPos(), neighbor));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().updateTick(i.getWorld(), i.getPos(), i.getBlockState(), rand));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().randomDisplayTick(i.getBlockState(), i.getWorld(), i.getPos(), rand));
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
    public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity entity) {
        return maxFloat(world, pos, i -> i.getBlockState().getBlock().getSlipperiness(i.getBlockState(), i.getWorld(), i.getPos(), entity));
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null && tile.getPositiveBlockInfo().getBlockState().getBlock().canSustainPlant(tile.getPositiveBlockInfo().getBlockState(), tile.getPositiveBlockInfo().getWorld(), pos, direction, plantable)).orElse(false);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().onBlockAdded(i.getWorld(), i.getPos(), i.getBlockState()));
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        return either(world, pos, i -> i.getBlockState().onBlockEventReceived(i.getWorld(), pos, id, param));
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return either(world, pos, i -> i.getBlockState().getBlock().isLadder(i.getBlockState(), i.getWorld(), i.getPos(), entity));
    }

    @Override
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
        return either(world, pos, i -> i.getBlockState().getBlock().isBed(i.getBlockState(), i.getWorld(), i.getPos(), player));
    }

    @Override
    public void onPlantGrow(IBlockState state, World world, BlockPos pos, BlockPos source) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().onPlantGrow(i.getBlockState(), i.getWorld(), i.getPos(), source));
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        return max(world, pos, i -> i.getBlockState().getBlock().getExpDrop(i.getBlockState(), i.getWorld(), i.getPos(), fortune));
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (entity == null)
            return getAvailable(world, pos).map(i -> i.getBlockState().getBlock().getSoundType(i.getBlockState(), i.getWorld(), pos, null)).orElse(super.getSoundType(state, world, pos, null));
        return super.getSoundType(state, world, pos, entity);
    }

    @Nullable
    @Override
    public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
        List<Float> result = new ArrayList<>();
        getTile(world, pos).ifPresent(tile -> {
            float[] positiveBlockColours = null;
            float[] negativeBlockColours = null;
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                positiveBlockColours = tile.getPositiveBlockInfo().getBlockState().getBlock().getBeaconColorMultiplier(tile.getPositiveBlockInfo().getBlockState(), tile.getPositiveBlockInfo().getWorld(), pos, beaconPos);
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                negativeBlockColours = tile.getNegativeBlockInfo().getBlockState().getBlock().getBeaconColorMultiplier(tile.getNegativeBlockInfo().getBlockState(), tile.getNegativeBlockInfo().getWorld(), pos, beaconPos);
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
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityLiving entity) {
        return getTile(world, pos).map(tile -> {
            PathNodeType positiveBlockNodeType = null;
            PathNodeType negativeBlockNodeType = null;
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                positiveBlockNodeType = tile.getPositiveBlockInfo().getBlockState().getBlock().getAiPathNodeType(tile.getPositiveBlockInfo().getBlockState(), tile.getPositiveBlockInfo().getWorld(), pos, entity);
            if (positiveBlockNodeType != null)
                return positiveBlockNodeType;
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                negativeBlockNodeType = tile.getNegativeBlockInfo().getBlockState().getBlock().getAiPathNodeType(tile.getNegativeBlockInfo().getBlockState(), tile.getNegativeBlockInfo().getWorld(), pos, entity);
            return negativeBlockNodeType;
        }).orElse(super.getAiPathNodeType(state, world, pos, entity));
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return both(world, pos, i -> i.getBlockState().getBlock().canEntityDestroy(i.getBlockState(), i.getWorld(), i.getPos(), entity));
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().onBlockExploded(i.getWorld(), i.getPos(), explosion));
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    public boolean getTickRandomly() {
        return true;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return either(state, i -> i.getBlockState().causesSuffocation());
    }
}
