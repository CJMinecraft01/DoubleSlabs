package cjminecraft.doubleslabs.addons.minecraft;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MinecraftSlabSupport implements ISlabSupport {

    private boolean isValid(Block block) {
        return (block instanceof BlockSlab && !((BlockSlab) block).isDouble() && hasEnumHalfProperty(block)) || hasEnumHalfProperty(block);
    }

    private boolean hasEnumHalfProperty(Block block) {
        return block.getDefaultState().getPropertyKeys().contains(BlockSlab.HALF);
    }

    @Override
    public boolean isValid(IBlockAccess world, BlockPos pos, IBlockState state) {
        return isValid(state.getBlock());
    }

    @Override
    public boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return stack.getItem() instanceof ItemBlock && isValid(((ItemBlock) stack.getItem()).getBlock());
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return state.getValue(BlockSlab.HALF);
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return state.withProperty(BlockSlab.HALF, half);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        Block block1 = ((ItemBlock) stack.getItem()).getBlock();
        Block block2 = state.getBlock();
        boolean sameVariant = true;
        if (block1 instanceof BlockSlab && block2 instanceof BlockSlab) {
            BlockSlab slab1 = (BlockSlab) block1;
            BlockSlab slab2 = (BlockSlab) block2;
            try {
                //noinspection ConstantConditions
                if (slab1.getVariantProperty() != null && slab2.getVariantProperty() != null)
                    sameVariant = block1.getStateFromMeta(stack.getMetadata()).getValue(slab1.getVariantProperty()) == state.getValue(slab2.getVariantProperty());
            } catch (Exception e) {
                // Just in case some mods throw erros when using getVariantProperty *cough* Midnight *cough*
                sameVariant = true;
            }
        }
        return block1 == block2 && sameVariant;
    }
}
