package cjminecraft.doubleslabs.api.support.istations;

import cjminecraft.doubleslabs.api.containers.ContainerSupportProvider;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.minecraft.MinecraftSlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Consumer;

@ContainerSupportProvider(modid = "improved-stations")
@SlabSupportProvider(modid = "improved-stations")
public class ImprovedStationsContainerSupport extends MinecraftSlabSupport implements IContainerSupport {

    @ObjectHolder("improved-stations:crafting_station_slab")
    public static final Block CRAFTING_STATION_SLAB = null;

    private static final ResourceLocation CONTAINER = new ResourceLocation("improved-stations", "crafting_station");

    @Override
    public boolean hasSupport(World world, BlockPos pos, BlockState state) {
        return state.getBlock() == CRAFTING_STATION_SLAB;
    }

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
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

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote)
            player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        return ActionResultType.SUCCESS;
    }
}
