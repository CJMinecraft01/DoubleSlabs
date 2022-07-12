package cjminecraft.doubleslabs.api.support.betternether;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.minecraft.MinecraftSlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Objects;

@SlabSupportProvider(modid = "betternether", priority = 1)
public class BetterNetherSlabSupport extends MinecraftSlabSupport {

    private static final String MODID = "betternether";

    @Override
    public boolean isHorizontalSlab(Item item) {
        return MODID.equals(Objects.requireNonNull(item.getRegistryName()).getNamespace()) && super.isHorizontalSlab(item);
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return MODID.equals(Objects.requireNonNull(block.getRegistryName()).getNamespace()) && super.isHorizontalSlab(block);
    }

    @Override
    public boolean isHorizontalSlab(IBlockReader world, BlockPos pos, BlockState state) {
        return MODID.equals(Objects.requireNonNull(state.getBlock().getRegistryName()).getNamespace()) && super.isHorizontalSlab(world, pos, state);
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, PlayerEntity player, Hand hand) {
        return MODID.equals(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace()) && super.isHorizontalSlab(stack, player, hand);
    }

    @Override
    public boolean flipModelWhenVertical() {
        return true;
    }
}
