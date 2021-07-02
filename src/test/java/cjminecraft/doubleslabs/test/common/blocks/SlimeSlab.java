package cjminecraft.doubleslabs.test.common.blocks;

import cjminecraft.doubleslabs.test.common.init.DSTBlocks;
import cjminecraft.doubleslabs.test.common.util.DummyVariantProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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

public abstract class SlimeSlab extends BlockSlab {
    public SlimeSlab() {
        super(Material.CLAY, MapColor.GRASS);
        this.setDefaultSlipperiness(0.8f);
        this.setSoundType(SoundType.SLIME);

        IBlockState state = this.blockState.getBaseState();

        if (!this.isDouble())
            state = state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);

        this.setDefaultState(state.withProperty(DummyVariantProperty.DUMMY, DummyVariantProperty.DEFAULT));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState adjacentState = blockAccess.getBlockState(pos.offset(side));
        if (adjacentState.getBlock() == this) {
            EnumBlockHalf type = blockState.getValue(HALF);
            EnumBlockHalf otherType = adjacentState.getValue(HALF);
            BlockSlab otherSlab = (BlockSlab) adjacentState.getBlock();
            if (side == EnumFacing.UP)
                return !((this.isDouble() || type == EnumBlockHalf.TOP) && (otherSlab.isDouble() || otherType == EnumBlockHalf.BOTTOM));
            else if (side == EnumFacing.DOWN)
                return !((this.isDouble() || type == EnumBlockHalf.BOTTOM) && (otherSlab.isDouble() || otherType == EnumBlockHalf.TOP));
            return !(adjacentState.getValue(HALF).equals(blockState.getValue(HALF)) || otherSlab.isDouble());
        }
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(DSTBlocks.SLIME_SLAB);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(DSTBlocks.SLIME_SLAB);
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

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSneaking()) {
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        } else {
            entityIn.fall(fallDistance, 0.0F);
        }
    }

    @Override
    public void onLanded(World worldIn, Entity entity) {
        if (entity.isSneaking()) {
            super.onLanded(worldIn, entity);
        } else {
            if (entity.motionY < 0.0D) {
                double d0 = entity instanceof EntityLiving ? 1.0D : 0.8D;
                entity.motionY *= -d0;
            }
        }

    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entity) {
        double d0 = Math.abs(entity.motionY);
        if (d0 < 0.1D && !entity.isSneaking()) {
            double d1 = 0.4D + d0 * 0.2D;
            entity.motionX *= d1;
            entity.motionZ *= d1;
        }

        super.onEntityWalk(worldIn, pos, entity);
    }

    public static class Double extends SlimeSlab {
        public boolean isDouble() {
            return true;
        }
    }

    public static class Half extends SlimeSlab {
        public boolean isDouble() {
            return false;
        }
    }
}
