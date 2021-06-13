package cjminecraft.doubleslabs.api.support.stairwaytoaether;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SlabSupportProvider(modid = "stairway")
public class StairwayToAetherSlabSupport<T extends Enum<T> & IStringSerializable> implements IHorizontalSlabSupport, IVerticalSlabSupport {

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
    public boolean isHorizontalSlab(Block block) {
        return slab != null && slab.isAssignableFrom(block.getClass());
    }

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return isHorizontalSlab(state.getBlock()) && (state.get(typeProperty).equals(slabTypes[0]) || state.get(typeProperty).equals(slabTypes[1]));
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return isHorizontalSlab(stack.getItem());
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return slab != null && (item instanceof BlockItem) && slab.isAssignableFrom(((BlockItem)item).getBlock().getClass());
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
    public BlockState getStateForDirection(World world, BlockPos pos, BlockState state, Direction direction) {
        return slab == null ? state : state.with(typeProperty, slabTypes[direction.getIndex()]);
    }

    @Override
    public Direction getDirection(World world, BlockPos pos, BlockState state) {
        if (slab == null)
            // shouldn't ever be called if this is the case
            return null;
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
            // shouldn't ever be called if this is the case
            return null;
        Enum<T> type = state.get(typeProperty);
        if (type.equals(slabTypes[0]))
            return SlabType.TOP;
        else if (type.equals(slabTypes[1]))
            return SlabType.BOTTOM;
        return SlabType.DOUBLE;
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, BlockState state, SlabType half) {
        return slab == null ? state : state.with(typeProperty, slabTypes[half == SlabType.TOP ? 0 : 1]);
    }
}