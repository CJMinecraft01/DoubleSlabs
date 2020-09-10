package cjminecraft.doubleslabs.old.api;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface IContainerSupport {
    boolean isValid(World world, BlockPos pos, BlockState state);

    INamedContainerProvider getNamedContainerProvider(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit);

    void onClicked(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit);
}
