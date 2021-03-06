package cjminecraft.doubleslabs.api.support;

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

public class VerticalSlabSupport<T extends Enum<T> & IStringSerializable> implements IVerticalSlabSupport {

    protected final Class<?> verticalSlab;
    protected final EnumProperty<T> verticalSlabTypeProperty;
    protected final T[] verticalSlabTypes;

    public VerticalSlabSupport(String verticalSlabClassName, String verticalSlabPropertyName, String verticalSlabTypeEnumClassName) {
        Class<?> verticalSlab;
        EnumProperty<T> verticalSlabTypeProperty;
        T[] verticalSlabTypes;
        try {
            verticalSlab = Class.forName(verticalSlabClassName);
            verticalSlabTypeProperty = (EnumProperty<T>)verticalSlab.getField(verticalSlabPropertyName).get(null);
            Class<?> verticalSlabType = Class.forName(verticalSlabTypeEnumClassName);
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
        return verticalSlab != null && verticalSlab.isAssignableFrom(state.getBlock().getClass()) && !state.get(verticalSlabTypeProperty).equals(verticalSlabTypes[4]);
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return verticalSlab != null && (stack.getItem() instanceof BlockItem) && verticalSlab.isAssignableFrom(((BlockItem)stack.getItem()).getBlock().getClass());
    }

    @Override
    public BlockState getStateForDirection(World world, BlockPos pos, BlockState state, Direction direction) {
        return (verticalSlab == null) ? (state) : (state.with(verticalSlabTypeProperty, verticalSlabTypes[direction.getIndex() - 2]));
    }

    @Override
    public Direction getDirection(World world, BlockPos pos, BlockState state) {
        if (verticalSlab == null)
            // should never be called
            return null;
        Enum<T> type = state.get(verticalSlabTypeProperty);
        if (type.equals(verticalSlabTypes[0]))
            return Direction.NORTH;
        else if (type.equals(verticalSlabTypes[1]))
            return Direction.SOUTH;
        else if (type.equals(verticalSlabTypes[2]))
            return Direction.WEST;
        return Direction.EAST;
    }

}
