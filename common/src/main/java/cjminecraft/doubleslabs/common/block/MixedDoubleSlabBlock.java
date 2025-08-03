package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MixedDoubleSlabBlock extends DynamicSlabBlock {
    // Anything specific to horizontal double slabs should go here

    public MixedDoubleSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return MixedDoubleSlabBlockHooks.getDrops(params);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return MixedDoubleSlabBlockHooks.getDestroyProgress(player, level, pos)
                .orElseGet(() -> super.getDestroyProgress(state, player, level, pos));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        MixedDoubleSlabBlockHooks.playerDestroy(player, level, pos, state, blockEntity, tool);
    }

    // TODO: Fix light propagating through
//    @Override
//    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
//        return MixedDoubleSlabBlockHooks.propagatesSkylightDown(level, pos);
//    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!MixedDoubleSlabBlockHooks.fallOn(level, pos, entity, fallDistance)) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (!MixedDoubleSlabBlockHooks.updateEntityAfterFallOn(level, entity)) {
            super.updateEntityAfterFallOn(level, entity);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!MixedDoubleSlabBlockHooks.stepOn(level, pos, entity)) {
            super.stepOn(level, pos, state, entity);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return MixedDoubleSlabBlockHooks.propagatesSkylightDown(level, pos);
    }

    // TODO: Fix collision shape
//    @Override
//    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
//        return MixedDoubleSlabBlockHooks.getCollisionShape(level, pos, context).orElseGet(() -> super.getCollisionShape(state, level, pos, context));
//    }
}
