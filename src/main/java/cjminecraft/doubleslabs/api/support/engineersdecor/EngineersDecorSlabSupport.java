package cjminecraft.doubleslabs.api.support.engineersdecor;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@SlabSupportProvider(modid = "engineersdecor")
public class EngineersDecorSlabSupport implements IHorizontalSlabSupport {
    private final Class<?> slab;
    private final IntegerProperty parts;

    public EngineersDecorSlabSupport() {
        Class<?> slab;
        IntegerProperty parts;
        try {
            slab = Class.forName("wile.engineersdecor.blocks.BlockDecorSlab");
            parts = (IntegerProperty) slab.getField("PARTS").get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            slab = null;
            parts = null;
        }
        this.slab = slab;
        this.parts = parts;
    }

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return (slab != null) && (state.getBlock().getClass().equals(slab)) && (state.get(parts) < 2);
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return (slab != null) && (item instanceof BlockItem) && (((BlockItem) item).getBlock().getClass().equals(slab));
    }

    @Override
    public SlabType getHalf(World world, BlockPos pos, BlockState state) {
        return ((slab != null) && (state.get(parts) == 0)) ? SlabType.BOTTOM : SlabType.TOP;
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, BlockState state, SlabType half) {
        return (slab == null) ? (state) : (state.with(parts, half == SlabType.BOTTOM ? 0 : 1));
    }
}
