package cjminecraft.doubleslabs.old.addons.worldofwonder;

import cjminecraft.doubleslabs.old.api.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class WorldOfWonderDirectionVerticalSlabSupport<T extends Enum<T> & IStringSerializable> implements ISlabSupport {

    private final Class<?> verticalSlab;
    private final EnumProperty<T> verticalSlabTypeProperty;
    private final DirectionProperty verticalSlabFacingProperty;
    private final T[] verticalSlabTypes;

    public WorldOfWonderDirectionVerticalSlabSupport() {
        Class<?> verticalSlab;
        EnumProperty<T> verticalSlabTypeProperty;
        DirectionProperty verticalSlabFacingProperty;
        T[] verticalSlabTypes;
        try {
            verticalSlab = Class.forName("net.msrandom.worldofwonder.block.DirectionalVerticalSlabBlock");
            verticalSlabTypeProperty = (EnumProperty<T>)verticalSlab.getField("TYPE").get(null);
            verticalSlabFacingProperty = (DirectionProperty) verticalSlab.getField("FACING").get(null);
            Class<?> verticalSlabType = Class.forName("net.msrandom.worldofwonder.block.DirectionalVerticalSlabBlock$VerticalSlabType");
            verticalSlabTypes = (T[]) verticalSlabType.getEnumConstants();
        } catch(ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            verticalSlab = null;
            verticalSlabTypeProperty = null;
            verticalSlabFacingProperty = null;
            verticalSlabTypes = null;
        }
        this.verticalSlab = verticalSlab;
        this.verticalSlabTypeProperty = verticalSlabTypeProperty;
        this.verticalSlabFacingProperty = verticalSlabFacingProperty;
        this.verticalSlabTypes = verticalSlabTypes;
    }

    @Override
    public boolean isVerticalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return verticalSlab != null && state.getBlock().getClass().equals(verticalSlab) && !state.get(verticalSlabTypeProperty).equals(verticalSlabTypes[1]);
    }

    @Override
    public boolean isVerticalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return verticalSlab != null && (stack.getItem() instanceof BlockItem) && (((BlockItem)stack.getItem()).getBlock().getClass().equals(verticalSlab));
    }

    @Override
    public BlockState getStateForDirection(World world, BlockPos pos, ItemStack stack, BlockItemUseContext context, Direction direction) {
        BlockState state = net.minecraft.block.Block.getBlockFromItem(stack.getItem()).getStateForPlacement(context);
        return (verticalSlab == null) ? (state) : (state.with(verticalSlabFacingProperty, direction));
    }

    @Override
    public Direction getDirection(World world, BlockPos pos, BlockState state) {
        if (verticalSlab == null)
            return ISlabSupport.super.getDirection(world, pos, state);
        return state.get(verticalSlabFacingProperty);
    }

}
