package cjminecraft.doubleslabs.test.common.blocks;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.test.common.container.ChestSlabContainer;
import cjminecraft.doubleslabs.test.common.init.DSTContainers;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ChestSlab extends SlabBlock implements IContainerSupport {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public ChestSlab() {
        super(Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ChestSlabTileEntity();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote)
            NetworkHooks.openGui((ServerPlayerEntity) player, getNamedContainerProvider(world, pos, state, player, hand, hit), pos);
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasSupport(World world, BlockPos pos, BlockState state) {
        return state.getBlock() == this;
    }

    @Override
    public ContainerType<?> getContainer(World world, BlockPos pos, BlockState state) {
        return DSTContainers.CHEST_SLAB.get();
    }

    @Override
    public Consumer<PacketBuffer> writeExtraData(World world, BlockPos pos, BlockState state) {
        return buffer -> buffer.writeBlockPos(pos);
    }

    @Override
    public INamedContainerProvider getNamedContainerProvider(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("gui.doubleslabstest.chest_slab");
            }

            @Nullable
            @Override
            public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
                return new ChestSlabContainer(windowId, playerInventory, (ChestSlabTileEntity) world.getTileEntity(pos));
            }
        };
    }
}
