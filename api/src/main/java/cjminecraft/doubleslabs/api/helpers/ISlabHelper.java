package cjminecraft.doubleslabs.api.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface ISlabHelper {

    @Nullable
    IHorizontalSlabHelper getHorizontalSlabHelper(BlockGetter level, BlockPos pos, BlockState state);

    @Nullable
    IHorizontalSlabHelper getHorizontalSlabHelper(ItemStack stack, Player player, InteractionHand hand);

    boolean isHorizontalSlab(Item item);

    boolean isHorizontalSlab(Block block);

}
