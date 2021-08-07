package cjminecraft.doubleslabs.test.common.compat;

import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.test.common.DoubleSlabsTest;
import cjminecraft.doubleslabs.test.common.blocks.VerticalSlab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@SlabSupportProvider(modid = DoubleSlabsTest.MODID)
public class DSTVerticalSlabSupport implements IVerticalSlabSupport {

    @Override
    public boolean isVerticalSlab(BlockGetter world, BlockPos pos, BlockState state) {
        return state.getBlock() instanceof VerticalSlab;
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, Player player, InteractionHand hand) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof VerticalSlab;
    }

    @Override
    public Direction getDirection(BlockGetter world, BlockPos pos, BlockState state) {
        return state.getValue(VerticalSlab.TYPE).direction;
    }

    @Override
    public BlockState getStateForDirection(BlockGetter world, BlockPos pos, BlockState state, Direction direction) {
        VerticalSlab.VerticalSlabType type = VerticalSlab.VerticalSlabType.fromDirection(direction);
        return state.setValue(VerticalSlab.TYPE, type != null ? type : VerticalSlab.VerticalSlabType.NORTH);
    }
}
