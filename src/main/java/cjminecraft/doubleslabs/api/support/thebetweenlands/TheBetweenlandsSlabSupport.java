package cjminecraft.doubleslabs.api.support.thebetweenlands;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SlabSupportProvider(modid = "thebetweenlands")
public class TheBetweenlandsSlabSupport<T extends Enum<T> & IStringSerializable> implements IHorizontalSlabSupport {

    private final Class<?> slab;
    private final PropertyEnum<T> slabTypeProperty;
    private final T[] slabTypes;

    public TheBetweenlandsSlabSupport() {
        Class<?> slab;
        PropertyEnum<T> slabTypeProperty;
        T[] slabTypes;
        try {
            slab = Class.forName("thebetweenlands.common.block.structure.BlockSlabBetweenlands");
            slabTypeProperty = (PropertyEnum<T>) slab.getField("HALF").get(null);
            Class<?> slabType = Class.forName("thebetweenlands.common.block.structure.BlockSlabBetweenlands$EnumBlockHalfBL");
            slabTypes = (T[]) slabType.getEnumConstants();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            slab = null;
            slabTypeProperty = null;
            slabTypes = null;
        }
        this.slab = slab;
        this.slabTypeProperty = slabTypeProperty;
        this.slabTypes = slabTypes;
    }

    @Override
    public boolean isHorizontalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return slab != null && slab.isAssignableFrom(state.getBlock().getClass()) && state.getValue(slabTypeProperty) != slabTypes[2];
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return slab != null && item instanceof ItemBlock && slab.isAssignableFrom(((ItemBlock) item).getBlock().getClass());
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return slab != null && state.getValue(slabTypeProperty) == slabTypes[1] ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return slab != null ? state.withProperty(slabTypeProperty, half == null ? slabTypes[2] : half == BlockSlab.EnumBlockHalf.BOTTOM ? slabTypes[1] : slabTypes[0]) : state;
    }

}