package cjminecraft.doubleslabs.api.containers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.function.Consumer;

public interface IContainerSupport {
    boolean hasSupport(World world, BlockPos pos, BlockState state);

    ContainerType<?> getContainer(World world, BlockPos pos, BlockState state);

    Consumer<PacketBuffer> writeExtraData(World world, BlockPos pos, BlockState state);

    INamedContainerProvider getNamedContainerProvider(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit);
}
