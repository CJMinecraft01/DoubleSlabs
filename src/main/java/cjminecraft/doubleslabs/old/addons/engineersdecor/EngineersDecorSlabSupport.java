package cjminecraft.doubleslabs.old.addons.engineersdecor;

import cjminecraft.doubleslabs.old.api.ISlabSupport;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EngineersDecorSlabSupport implements ISlabSupport {
    private final Class<?> slab;
    private final IntegerProperty parts;

    public EngineersDecorSlabSupport() {
        Class<?> slab;
        IntegerProperty parts;
        try {
            slab = Class.forName("wile.engineersdecor.blocks.BlockDecorSlab");
            parts = (IntegerProperty)slab.getField("PARTS").get(null);
        } catch(ClassNotFoundException|NoSuchFieldException|IllegalAccessException ignored) {
            slab = null;
            parts = null;
        }
        this.slab = slab;
        this.parts = parts;
    }

    @Override
    public boolean isValid(IBlockReader world, BlockPos pos, BlockState state) {
        return (slab != null) && (state.getBlock().getClass().equals(slab)) && (state.get(parts) < 2);
    }

    @Override
    public boolean isValid(ItemStack stack, PlayerEntity player, net.minecraft.util.Hand hand) {
        return (slab != null) && (stack.getItem() instanceof BlockItem) && (((BlockItem)stack.getItem()).getBlock().getClass().equals(slab));
    }

    @Override
    public SlabType getHalf(World world, BlockPos pos, BlockState state) {
        return ((slab != null) && (state.get(parts) == 0)) ? SlabType.BOTTOM : SlabType.TOP;
    }

    @Override
    public BlockState getStateForHalf(World world, BlockPos pos, ItemStack stack, BlockItemUseContext context, SlabType half) {
        BlockState state = net.minecraft.block.Block.getBlockFromItem(stack.getItem()).getStateForPlacement(context);
        return (slab == null) ? (state) : (state.with(parts, half == SlabType.BOTTOM ? 0 : 1));
    }

    @Override
    public boolean areSame(World world, BlockPos pos, BlockState state, ItemStack stack) {
        return ((BlockItem)stack.getItem()).getBlock() == state.getBlock();
    }
}
