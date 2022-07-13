package cjminecraft.doubleslabs.test.common.blocks;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.test.common.container.ChestSlabContainer;
import cjminecraft.doubleslabs.test.common.init.DSTContainers;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ChestSlab extends SlabBlock implements IContainerSupport, EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ChestSlab() {
        super(Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ChestSlabTileEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide())
            NetworkHooks.openScreen((ServerPlayer) player, getNamedContainerProvider(world, pos, state, player, hand, hit), pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasSupport(Level world, BlockPos pos, BlockState state) {
        return state.is(this);
    }

    @Override
    public MenuType<?> getContainer(Level world, BlockPos pos, BlockState state) {
        return DSTContainers.CHEST_SLAB.get();
    }

    @Override
    public Consumer<FriendlyByteBuf> writeExtraData(Level world, BlockPos pos, BlockState state) {
        return buffer -> buffer.writeBlockPos(pos);
    }

    @Override
    public MenuProvider getNamedContainerProvider(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("gui.doubleslabstest.chest_slab");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
                return new ChestSlabContainer(windowId, playerInventory, (ChestSlabTileEntity) world.getBlockEntity(pos));
            }
        };
    }
}
