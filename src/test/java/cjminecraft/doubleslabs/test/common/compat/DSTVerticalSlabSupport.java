package cjminecraft.doubleslabs.test.common.compat;

import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.blocks.VerticalSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SlabSupportProvider(modid = DoubleSlabsTest.MODID)
public class DSTVerticalSlabSupport implements IVerticalSlabSupport {
    @Override
    public boolean isVerticalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof VerticalSlab;
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof VerticalSlab;
    }

    @Override
    public EnumFacing getDirection(World world, BlockPos pos, IBlockState state) {
        return state.getValue(VerticalSlab.TYPE).direction;
    }

    @Override
    public IBlockState getStateForDirection(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        VerticalSlab.VerticalSlabType type = VerticalSlab.VerticalSlabType.fromDirection(direction);
        return state.withProperty(VerticalSlab.TYPE, type != null ? type : VerticalSlab.VerticalSlabType.NORTH);
    }
}
