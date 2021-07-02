package cjminecraft.doubleslabs.api.support.engineersdecor;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SlabSupportProvider(modid = "engineersdecor")
public class EngineersDecorSlabSupport implements IHorizontalSlabSupport {
    private final Class<?> slab;
    private final PropertyInteger parts;

    public EngineersDecorSlabSupport() {
        Class<?> slab;
        PropertyInteger parts;
        try {
            slab = Class.forName("wile.engineersdecor.blocks.BlockDecorSlab");
            parts = (PropertyInteger)slab.getField("PARTS").get(null);
        } catch(ClassNotFoundException|NoSuchFieldException|IllegalAccessException ignored) {
            slab = null;
            parts = null;
        }
        this.slab = slab;
        this.parts = parts;
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return (slab != null) && (block.getClass().equals(slab));
    }

    @Override
    public boolean isHorizontalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        return (slab != null) && (state.getBlock().getClass().equals(slab)) && (state.getValue(parts) < 2);
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return (slab != null) && (item instanceof ItemBlock) && (((ItemBlock)item).getBlock().getClass().equals(slab));
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return ((slab != null) && (state.getValue(parts) == 0)) ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return (slab == null) ? (state) : (state.withProperty(parts, half == BlockSlab.EnumBlockHalf.BOTTOM ? 0 : 1));
    }
}
