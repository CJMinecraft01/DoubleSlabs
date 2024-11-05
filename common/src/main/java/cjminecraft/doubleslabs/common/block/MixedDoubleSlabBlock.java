package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class MixedDoubleSlabBlock extends DynamicSlabBlock {
    // Anything specific to horizontal double slabs should go here

    public MixedDoubleSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return MixedDoubleSlabBlockHooks.getDestroyProgress(player, level, pos)
                .orElseGet(() -> super.getDestroyProgress(state, player, level, pos));
    }
}
