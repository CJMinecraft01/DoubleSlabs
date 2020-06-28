package cjminecraft.doubleslabs.addons.stairwaytoaether;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class StairwayToAetherSlabSupport<T extends Enum<T> & IStringSerializable> implements ISlabSupport {

    private final Class<?> slab;
    private final EnumProperty<T> typeProperty;
    private final T[] slabTypes;

    public StairwayToAetherSlabSupport() {
        Class<?> slab;
        EnumProperty<T> typeProperty;
        T[] slabTypes;
        try {
            slab = Class.forName("mod.stairway.blocks.BlockSlabs");
            typeProperty = (EnumProperty<T>) slab.getField("TYPE").get(null);
            Class<?> slabType = Class.forName("mod.stairway.blocks.BlockSlabs$EnumBlockHalf");
            slabTypes = (T[]) slabType.getEnumConstants();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            slab = null;
            typeProperty = null;
            slabTypes = null;
        }
        this.slab = slab;
        this.typeProperty = typeProperty;
        this.slabTypes = slabTypes;
    }

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && (state.get(typeProperty).equals(slabTypes[0]) || state.get(typeProperty).equals(slabTypes[1]));
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return slab != null && (stack.getItem() instanceof BlockItem) && slab.isAssignableFrom(((BlockItem)stack.getItem()).getBlock().getClass());
    }

    @Override
    public boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && !(state.get(typeProperty).equals(slabTypes[0]) || state.get(typeProperty).equals(slabTypes[1])) && !state.get(typeProperty).equals(slabTypes[6]);
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return isHorizontalSlab(stack, player, hand);
    }

    @Override
    public BlockState getStateForDirection(World world, BlockPos pos, ItemStack stack, Direction direction) {
        BlockState state = net.minecraft.block.Block.getBlockFromItem(stack.getItem()).getDefaultState();
        return slab == null ? state : state.with(typeProperty, slabTypes[direction.getIndex()]);
    }

    @Override
    public Direction getDirection(World world, BlockPos pos, BlockState state) {
        if (slab == null)
            return ISlabSupport.super.getDirection(world, pos, state);
        Enum<T> type = state.get(typeProperty);
        if (type.equals(slabTypes[2]))
            return Direction.NORTH;
        else if (type.equals(slabTypes[3]))
            return Direction.SOUTH;
        else if (type.equals(slabTypes[4]))
            return Direction.EAST;
        return Direction.WEST;
    }

    @Override
    public SlabType getHalf(World world, BlockPos pos, BlockState state) {
        if (slab == null)
            return ISlabSupport.super.getHalf(world, pos, state);
        Enum<T> type = state.get(typeProperty);
        if (type.equals(slabTypes[0]))
            return SlabType.TOP;
        else if (type.equals(slabTypes[1]))
            return SlabType.BOTTOM;
        return SlabType.DOUBLE;
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, SlabType half) {
        BlockState state = net.minecraft.block.Block.getBlockFromItem(stack.getItem()).getDefaultState();
        return slab == null ? state : state.with(typeProperty, slabTypes[half == SlabType.TOP ? 0 : 1]);
    }
}
