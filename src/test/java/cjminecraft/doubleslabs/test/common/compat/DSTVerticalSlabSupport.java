package cjminecraft.doubleslabs.test.common.compat;

import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.blocks.VerticalSlab;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SlabSupportProvider(modid = DoubleSlabsTest.MODID)
public class DSTVerticalSlabSupport implements IVerticalSlabSupport {
    @Override
    public boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof VerticalSlab;
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof VerticalSlab;
    }

    @Override
    public Direction getDirection(IBlockReader world, BlockPos pos, BlockState state) {
        return state.get(VerticalSlab.TYPE).direction;
    }

    @Override
    public BlockState getStateForDirection(IBlockReader world, BlockPos pos, BlockState state, Direction direction) {
        VerticalSlab.VerticalSlabType type = VerticalSlab.VerticalSlabType.fromDirection(direction);
        return state.with(VerticalSlab.TYPE, type != null ? type : VerticalSlab.VerticalSlabType.NORTH);
    }
}
