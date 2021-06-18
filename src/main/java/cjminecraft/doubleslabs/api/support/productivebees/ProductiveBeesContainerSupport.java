package cjminecraft.doubleslabs.api.support.productivebees;

import cjminecraft.doubleslabs.api.containers.ContainerSupportProvider;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Consumer;

@ContainerSupportProvider(modid = "productivebees")
public class ProductiveBeesContainerSupport implements IContainerSupport {

    @ObjectHolder("productivebees:feeder")
    public static final Block FEEDER = null;

    @ObjectHolder("productivebees:feeder")
    public static final ContainerType<?> FEEDER_CONTAINER = null;

    @Override
    public boolean hasSupport(World world, BlockPos pos, BlockState state) {
        //noinspection ConstantConditions
        return FEEDER != null && FEEDER_CONTAINER != null && state.getBlock().equals(FEEDER);
    }

    @Override
    public ContainerType<?> getContainer(World world, BlockPos pos, BlockState state) {
        return FEEDER_CONTAINER;
    }

    @Override
    public Consumer<PacketBuffer> writeExtraData(World world, BlockPos pos, BlockState state) {
        return buffer -> buffer.writeBlockPos(pos);
    }

    @Override
    public INamedContainerProvider getNamedContainerProvider(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return (INamedContainerProvider) world.getTileEntity(pos);
    }
}
