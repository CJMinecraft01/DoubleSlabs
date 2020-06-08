package cjminecraft.doubleslabs.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IContainerSupport {
    boolean isValid(World world, BlockPos pos, IBlockState state);

    Object getMod();

    int getGuiId(World world, BlockPos pos, IBlockState state);

    void onClicked(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing);
}
