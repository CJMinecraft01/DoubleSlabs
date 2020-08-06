package cjminecraft.doubleslabs.addons.conquestreforged;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ConquestReforgedVerticalSlabSupport implements ISlabSupport {

    private final Class<?> slab;

    public ConquestReforgedVerticalSlabSupport() {
        this("com.conquestreforged.common.blockmeta.BlockVerticalSlabMeta");
    }

    public ConquestReforgedVerticalSlabSupport(String blockClassName) {
        Class<?> slab;
        try {
            slab = Class.forName(blockClassName);
        } catch (ClassNotFoundException ignored) {
            slab = null;
        }
        this.slab = slab;
    }

    @Override
    public boolean isVerticalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass());
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return slab != null && (stack.getItem() instanceof ItemBlock) && slab.isAssignableFrom(((ItemBlock)stack.getItem()).getBlock().getClass());
    }

    @Override
    public IBlockState getStateForDirection(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        return slab == null ? state : state.withProperty(BlockHorizontal.FACING, direction);
    }

    @Override
    public EnumFacing getDirection(World world, BlockPos pos, IBlockState state) {
        if (slab == null)
            return ISlabSupport.super.getDirection(world, pos, state);
        return state.getValue(BlockHorizontal.FACING);
    }
}
