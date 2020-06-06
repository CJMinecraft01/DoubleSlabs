package cjminecraft.doubleslabs.addons.mubble;

import cjminecraft.doubleslabs.addons.minecraft.MinecraftSlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class MubbleSlabSupport<T extends Enum<T> & IStringSerializable> extends MinecraftSlabSupport {

    private final Class<?> verticalSlab;
    private final EnumProperty<T> verticalSlabTypeProperty;
    private final T[] verticalSlabTypes;

    public MubbleSlabSupport() {
        Class<?> verticalSlab;
        EnumProperty<T> verticalSlabTypeProperty;
        T[] verticalSlabTypes;
        try {
            verticalSlab = Class.forName("hugman.mubble.objects.block.VerticalSlabBlock");
            verticalSlabTypeProperty = (EnumProperty<T>)verticalSlab.getField("TYPE").get(null);
            Class<?> verticalSlabType = Class.forName("hugman.mubble.objects.block.block_state_property.VerticalSlabType");
            verticalSlabTypes = (T[]) verticalSlabType.getEnumConstants();
        } catch(ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            verticalSlab = null;
            verticalSlabTypeProperty = null;
            verticalSlabTypes = null;
        }
        this.verticalSlab = verticalSlab;
        this.verticalSlabTypeProperty = verticalSlabTypeProperty;
        this.verticalSlabTypes = verticalSlabTypes;
    }

    @Override
    public boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return verticalSlab != null && state.getBlock().getClass().equals(verticalSlab) && !state.get(verticalSlabTypeProperty).equals(verticalSlabTypes[4]);
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return verticalSlab != null && (stack.getItem() instanceof BlockItem) && (((BlockItem)stack.getItem()).getBlock().getClass().equals(verticalSlab));
    }

    @Override
    public BlockState getStateForDirection(World world, BlockPos pos, ItemStack stack, Direction direction) {
        BlockState state = net.minecraft.block.Block.getBlockFromItem(stack.getItem()).getDefaultState();
        return (verticalSlab == null) ? (state) : (state.with(verticalSlabTypeProperty, verticalSlabTypes[direction.getAxis() == Direction.Axis.X ? direction == Direction.WEST ? 3 : 2 : direction.getIndex() - 2]));
    }

    @Override
    public Direction getDirection(World world, BlockPos pos, BlockState state) {
        if (verticalSlab == null)
            return super.getDirection(world, pos, state);
        Enum<T> type = state.get(verticalSlabTypeProperty);
        if (type.equals(verticalSlabTypes[0]))
            return Direction.NORTH;
        else if (type.equals(verticalSlabTypes[1]))
            return Direction.SOUTH;
        else if (type.equals(verticalSlabTypes[2]))
            return Direction.EAST;
        return Direction.WEST;
    }
}

