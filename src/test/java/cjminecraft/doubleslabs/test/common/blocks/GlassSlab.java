package cjminecraft.doubleslabs.test.common.blocks;

import cjminecraft.doubleslabs.test.common.init.DSTBlocks;
import cjminecraft.doubleslabs.test.common.util.DummyVariantProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class GlassSlab extends BlockSlab {

    public GlassSlab() {
        super(Material.GLASS);
        this.setHardness(0.3f);
        this.setResistance(0.3f);
        this.setSoundType(SoundType.GLASS);
        IBlockState state = this.blockState.getBaseState();

        if (!this.isDouble())
            state = state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);

        this.setDefaultState(state.withProperty(DummyVariantProperty.DUMMY, DummyVariantProperty.DEFAULT));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState state = blockAccess.getBlockState(pos.offset(side));
        Block block = state.getBlock();

        return block == this;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(DSTBlocks.GLASS_SLAB);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(DSTBlocks.GLASS_SLAB);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.isDouble() ? new BlockStateContainer(this, DummyVariantProperty.DUMMY) : new BlockStateContainer(this, HALF, DummyVariantProperty.DUMMY);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(DummyVariantProperty.DUMMY, DummyVariantProperty.DEFAULT);

        if (!this.isDouble())
            state = state.withProperty(HALF, meta == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);

        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (!this.isDouble())
            return state.getValue(HALF) == EnumBlockHalf.TOP ? 1 : 0;
        return 0;
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

    public static class Double extends GlassSlab {
        public boolean isDouble() {
            return true;
        }
    }

    public static class Half extends GlassSlab {
        public boolean isDouble() {
            return false;
        }
    }

}
