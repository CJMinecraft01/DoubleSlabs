package cjminecraft.doubleslabs.api.containers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface IContainerSupport {
    boolean hasSupport(World world, BlockPos pos, IBlockState state);

    Object getModInstance();

    int getId(World world, BlockPos pos, IBlockState state, EntityPlayer player, RayTraceResult hit);

}
