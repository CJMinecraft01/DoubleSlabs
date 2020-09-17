package cjminecraft.doubleslabs.api.support.conquestreforged;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SlabSupportProvider(modid = "conquest")
public class ConquestReforgedSlabSupport implements IHorizontalSlabSupport {

    private final Class<?> slab;
    private final PropertyEnum<BlockTrapDoor.DoorHalf> slabHalfProperty;

    public ConquestReforgedSlabSupport() {
        Class<?> slab;
        PropertyEnum<BlockTrapDoor.DoorHalf> slabHalfProperty;
        try {
            slab = Class.forName("com.conquestreforged.common.block.SlabDirectionalBlock");
            slabHalfProperty = (PropertyEnum<BlockTrapDoor.DoorHalf>) slab.getField("HALF").get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            slab = null;
            slabHalfProperty = null;
        }
        this.slab = slab;
        this.slabHalfProperty = slabHalfProperty;
    }

    @Override
    public boolean isHorizontalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass());
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return slab != null && item instanceof ItemBlock && slab.isAssignableFrom(((ItemBlock) item).getBlock().getClass());
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return slab != null && state.getValue(slabHalfProperty) == BlockTrapDoor.DoorHalf.BOTTOM ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return slab != null ? state.withProperty(slabHalfProperty, half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockTrapDoor.DoorHalf.BOTTOM : BlockTrapDoor.DoorHalf.TOP) : state;
    }

    @Override
    public boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        return state.getBlock() == ((ItemBlock) stack.getItem()).getBlock();
    }
}
