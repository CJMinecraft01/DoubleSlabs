package cjminecraft.doubleslabs.api.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;

public interface IContainerSupport {
    boolean hasSupport(Level world, BlockPos pos, BlockState state);

    MenuType<?> getContainer(Level world, BlockPos pos, BlockState state);

    Consumer<FriendlyByteBuf> writeExtraData(Level world, BlockPos pos, BlockState state);

    MenuProvider getNamedContainerProvider(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit);
}
