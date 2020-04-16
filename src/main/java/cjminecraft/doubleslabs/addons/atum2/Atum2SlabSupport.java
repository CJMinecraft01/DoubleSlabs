package cjminecraft.doubleslabs.addons.atum2;

import cjminecraft.doubleslabs.api.ISlabSupport;
import com.teammetallurgy.atum.blocks.base.BlockAtumSlab;
import com.teammetallurgy.atum.items.ItemAtumSlab;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Atum2SlabSupport implements ISlabSupport {
    @Override
    public boolean isValid(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() instanceof BlockAtumSlab && state.getValue(BlockAtumSlab.TYPE) != BlockAtumSlab.Type.DOUBLE;
    }

    @Override
    public boolean isValid(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return stack.getItem() instanceof ItemAtumSlab;
    }

    @Override
    public BlockSlab.EnumBlockHalf getHalf(World world, BlockPos pos, IBlockState state) {
        return state.getValue(BlockAtumSlab.TYPE) == BlockAtumSlab.Type.BOTTOM ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public IBlockState getStateForHalf(World world, BlockPos pos, IBlockState state, BlockSlab.EnumBlockHalf half) {
        return state.withProperty(BlockAtumSlab.TYPE, half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockAtumSlab.Type.BOTTOM : BlockAtumSlab.Type.TOP);
    }

    @Override
    public boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        return state.getBlock() == ((ItemBlock) stack.getItem()).getBlock();
    }
}
