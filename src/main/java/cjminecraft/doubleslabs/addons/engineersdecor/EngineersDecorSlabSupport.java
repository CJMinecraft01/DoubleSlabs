package cjminecraft.doubleslabs.addons.engineersdecor;

import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EngineersDecorSlabSupport implements ISlabSupport {
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
    public boolean isValid(World world, BlockPos pos, IBlockState state) {
        return (slab != null) && (state.getBlock().getClass().equals(slab)) && (state.getValue(parts) < 2);
    }

    @Override
    public boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return (slab != null) && (stack.getItem() instanceof ItemBlock) && (((ItemBlock)stack.getItem()).getBlock().getClass().equals(slab));
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return ((slab != null) && (state.getValue(parts) == 0)) ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return (slab == null) ? (state) : (state.withProperty(parts, half == BlockSlab.EnumBlockHalf.BOTTOM ? 0 : 1));
    }

    @Override
    public boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        return ((ItemBlock)stack.getItem()).getBlock() == state.getBlock();
    }
}
