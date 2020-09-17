package cjminecraft.doubleslabs.api.support;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IHorizontalSlabSupport extends ISlabSupport {

    boolean isHorizontalSlab(IBlockAccess world, BlockPos pos, IBlockState state);

    default boolean isHorizontalSlab(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return isHorizontalSlab(stack.getItem());
    }

    boolean isHorizontalSlab(Item item);

    @Nullable
    BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state);

    IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, @Nullable BlockSlab.EnumBlockHalf half);

}
