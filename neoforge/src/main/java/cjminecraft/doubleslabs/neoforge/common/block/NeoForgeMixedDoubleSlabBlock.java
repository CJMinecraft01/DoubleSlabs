package cjminecraft.doubleslabs.neoforge.common.block;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class NeoForgeMixedDoubleSlabBlock extends MixedDoubleSlabBlock {
    public NeoForgeMixedDoubleSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return MixedDoubleSlabBlockHooks.removeBlock(state, level, pos, player, fluid, willHarvest);
    }
}
