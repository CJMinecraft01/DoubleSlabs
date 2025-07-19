package cjminecraft.doubleslabs.neoforge.common.block;

import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;

public class NeoForgeVerticalSlabBlock extends VerticalSlabBlock {
    public NeoForgeVerticalSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        final var usePlayerDestroy = VerticalSlabBlockHooks.removeBlock(state, level, pos, player, willHarvest);
        return usePlayerDestroy || super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return VerticalSlabBlockHooks.getCloneItemStack(level, pos, state, target);
    }
}
