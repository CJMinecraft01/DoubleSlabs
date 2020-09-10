package cjminecraft.doubleslabs.api.support.craftingstation;

import cjminecraft.doubleslabs.api.containers.ContainerSupportProvider;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Consumer;

@ContainerSupportProvider(modid = "craftingstation")
public class CraftingStationContainerSupport implements IContainerSupport {

    @ObjectHolder("craftingstation:crafting_station_slab")
    public static final Block CRAFTING_STATION_SLAB = null;

    private static final ResourceLocation CONTAINER = new ResourceLocation("craftingstation", "crafting_station_container");

    @Override
    public boolean hasSupport(World world, BlockPos pos, BlockState state) {
        return state.getBlock() == CRAFTING_STATION_SLAB;
    }

    @Override
    public ContainerType<?> getContainer(World world, BlockPos pos, BlockState state) {
        return ForgeRegistries.CONTAINERS.getValue(CONTAINER);
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
