package cjminecraft.doubleslabs.common.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class DoubleSlabPlaceContext extends BlockPlaceContext {
    public DoubleSlabPlaceContext(Player player, InteractionHand hand, ItemStack stack, BlockHitResult result) {
        this(player.level, player, hand, stack, result);
    }

    protected DoubleSlabPlaceContext(Level worldIn, @Nullable Player playerIn, InteractionHand handIn, ItemStack stackIn, BlockHitResult rayTraceResultIn) {
        super(worldIn, playerIn, handIn, stackIn, rayTraceResultIn);
        this.replaceClicked = true; // Required because when trying to combine slabs we don't want to offset the block by default
    }
}
