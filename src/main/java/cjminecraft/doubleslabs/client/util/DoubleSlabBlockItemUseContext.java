package cjminecraft.doubleslabs.client.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DoubleSlabBlockItemUseContext extends BlockItemUseContext {
    public DoubleSlabBlockItemUseContext(PlayerEntity player, Hand hand, ItemStack stack, BlockRayTraceResult result) {
        this(player.world, player, hand, stack, result);
    }

    protected DoubleSlabBlockItemUseContext(World worldIn, @Nullable PlayerEntity playerIn, Hand handIn, ItemStack stackIn, BlockRayTraceResult rayTraceResultIn) {
        super(worldIn, playerIn, handIn, stackIn, rayTraceResultIn);
        this.replaceClicked = true; // Required because when trying to combine slabs we don't want to offset the block by default
    }
}
