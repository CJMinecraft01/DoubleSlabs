package cjminecraft.doubleslabs.api.support;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface ISlabSupport {

    default boolean areSame(World world, BlockPos pos, IBlockState state, ItemStack stack) {
        return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == state.getBlock();
    }

    default boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return state.getBlock().onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    default IBlockState getStateFromStack(ItemStack stack, World world, BlockPos pos, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
        return stack.getItem() instanceof ItemBlock ? ((ItemBlock) stack.getItem()).getBlock().getStateForPlacement(world, pos, hit.sideHit, (float) hit.hitVec.x, (float) hit.hitVec.y, (float) hit.hitVec.z, stack.getMetadata(), player, hand) : Blocks.AIR.getDefaultState();
    }

    default boolean useDoubleSlabModel(IBlockState state) {
        return true;
    }

}
