package cjminecraft.doubleslabs.api.support;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IVerticalSlabSupport extends ISlabSupport {

    boolean isVerticalSlab(IBlockAccess world, BlockPos pos, IBlockState state);

    boolean isVerticalSlab(ItemStack stack, EntityPlayer player, EnumHand hand);

    EnumFacing getDirection(World world, BlockPos pos, IBlockState state);

    IBlockState getStateForDirection(World world, BlockPos pos, IBlockState state, EnumFacing direction);

    default boolean rotateModel(IBlockAccess world, BlockPos pos, IBlockState state) {
        return false;
    }

}
