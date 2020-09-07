package cjminecraft.doubleslabs.client.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DoubleSlabBlockItemUseContext extends BlockItemUseContext {

    private BlockPos pos;

    public DoubleSlabBlockItemUseContext(PlayerEntity player, Hand hand, ItemStack stack, BlockRayTraceResult result, BlockPos pos) {
        this(player.world, player, hand, stack, result);
        this.pos = pos;
    }

    protected DoubleSlabBlockItemUseContext(World worldIn, @Nullable PlayerEntity playerIn, Hand handIn, ItemStack stackIn, BlockRayTraceResult rayTraceResultIn) {
        super(worldIn, playerIn, handIn, stackIn, rayTraceResultIn);
        this.replaceClicked = true; // Required because when trying to combine slabs we don't want to offset the block by default
    }

    @Override
    public BlockPos getPos() {
        return this.pos == null ? super.getPos() : this.pos;
    }
}
