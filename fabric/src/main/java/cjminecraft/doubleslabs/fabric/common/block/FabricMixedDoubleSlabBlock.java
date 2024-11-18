package cjminecraft.doubleslabs.fabric.common.block;

import cjminecraft.doubleslabs.common.block.MixedDoubleSlabBlock;
import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class FabricMixedDoubleSlabBlock extends MixedDoubleSlabBlock implements BlockPickInteractionAware {
    public FabricMixedDoubleSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getPickedStack(BlockState state, BlockGetter view, BlockPos pos, Player player, HitResult result) {
        return view instanceof LevelReader levelReader ?
                MixedDoubleSlabBlockHooks.getCloneItemStack(levelReader, pos, result) : ItemStack.EMPTY;
    }
}
