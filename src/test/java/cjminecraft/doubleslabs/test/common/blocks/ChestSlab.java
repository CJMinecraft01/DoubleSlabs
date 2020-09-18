package cjminecraft.doubleslabs.test.common.blocks;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.test.common.container.GuiHandler;
import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.init.DSTBlocks;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import cjminecraft.doubleslabs.test.common.util.DummyVariantProperty;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class ChestSlab extends BlockSlab implements IContainerSupport {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public ChestSlab() {
        super(Material.WOOD);
        this.setHardness(2.5f);
        this.setResistance(2.5f);
        this.setSoundType(SoundType.WOOD);
        IBlockState state = this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH);

        if (!this.isDouble())
            state = state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);

        this.setDefaultState(state.withProperty(DummyVariantProperty.DUMMY, DummyVariantProperty.DEFAULT));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(DSTBlocks.CHEST_SLAB);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(DSTBlocks.CHEST_SLAB);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.isDouble() ? new BlockStateContainer(this, DummyVariantProperty.DUMMY, FACING) : new BlockStateContainer(this, HALF, DummyVariantProperty.DUMMY, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(DummyVariantProperty.DUMMY, DummyVariantProperty.DEFAULT);

        if (!this.isDouble())
            state = state.withProperty(HALF, meta < 4 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP)
                    .withProperty(FACING, EnumFacing.byHorizontalIndex(meta % 4));

        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (!this.isDouble())
            return state.getValue(FACING).getHorizontalIndex() + (state.getValue(HALF) == EnumBlockHalf.TOP ? 4 : 0);
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ChestSlabTileEntity();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getAdjustedHorizontalFacing());
    }

    @Override
    public String getTranslationKey(int meta) {
        return super.getTranslationKey();
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return DummyVariantProperty.DUMMY;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return DummyVariantProperty.DEFAULT;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote)
            player.openGui(DoubleSlabsTest.instance, GuiHandler.CHEST_SLAB, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public boolean hasSupport(World world, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public Object getModInstance() {
        return DoubleSlabsTest.instance;
    }

    @Override
    public int getId(World world, BlockPos pos, IBlockState state, EntityPlayer player, RayTraceResult hit) {
        return GuiHandler.CHEST_SLAB;
    }

    public static class Double extends ChestSlab {
        public boolean isDouble() {
            return true;
        }
    }

    public static class Half extends ChestSlab {
        public boolean isDouble() {
            return false;
        }
    }
}
