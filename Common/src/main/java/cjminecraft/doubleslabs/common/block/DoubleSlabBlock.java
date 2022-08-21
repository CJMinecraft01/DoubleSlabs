package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class DoubleSlabBlock extends DynamicSlabBlock {

    public static Optional<IBlockInfo> getHalfState(BlockGetter level, BlockPos pos, double y) {
        return getSlab(level, pos).flatMap(slab -> slab.getNegativeBlockInfo().getBlockState() == null && slab.getPositiveBlockInfo().getBlockState() == null ? Optional.empty() :
                (y > 0.5 || slab.getNegativeBlockInfo().getBlockState() == null) && slab.getPositiveBlockInfo().getBlockState() != null ?
                        Optional.of(slab.getPositiveBlockInfo()) : Optional.of(slab.getNegativeBlockInfo()));
    }

    // todo: initialise client
    // todo: can harvest block

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return both(level, pos, i -> i.blockState().map(s -> s.propagatesSkylightDown(i.getLevel(), pos)).orElse(true));
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        if (result.getType() == HitResult.Type.MISS)
            return minFloat(level, pos, i -> i.blockState().map(s -> s.getDestroyProgress(player, i.getLevel(), pos)).orElse(0.0f));
        return getHalfState(level, pos, result.getLocation().y - pos.getY())
                .flatMap(i -> i.blockState().map(s -> s.getDestroyProgress(player, i.getLevel(), pos)))
                .orElseGet(() -> super.getDestroyProgress(state, player, level, pos));
    }

    // todo: get clone item stack

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack stack) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        SlabBlockEntity<?> slab = (SlabBlockEntity<?>) blockEntity;
        if (result.getType() == HitResult.Type.MISS || blockEntity == null || slab.getPositiveBlockInfo().getBlockState() == null || slab.getNegativeBlockInfo().getBlockState() == null) {
            super.playerDestroy(level, player, pos, state, blockEntity, stack);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            level.removeBlockEntity(pos);
        } else {
            double y = result.getLocation().y - pos.getY();

            IBlockInfo remainingBlock = y > 0.5 ? slab.getNegativeBlockInfo() : slab.getPositiveBlockInfo();
            IBlockInfo blockToRemove = y > 0.5 ? slab.getPositiveBlockInfo() : slab.getNegativeBlockInfo();

            assert remainingBlock.getBlockState() != null;
            assert blockToRemove.getBlockState() != null;

            player.awardStat(Stats.BLOCK_MINED.get(blockToRemove.getBlockState().getBlock()));
            level.levelEvent(2001, pos, Block.getId(blockToRemove.getBlockState()));
            player.causeFoodExhaustion(0.005F);

            if (!player.isCreative())
                dropResources(blockToRemove.getBlockState(), level, pos, null, player, stack);

            blockToRemove.getBlockState().onRemove(blockToRemove.getLevel(), pos, Blocks.AIR.defaultBlockState(), false);

            level.setBlock(pos, remainingBlock.getBlockState(), 3);
            if (remainingBlock.getBlockEntity() != null)
                level.setBlockEntity(remainingBlock.getBlockEntity());
            else
                level.removeBlockEntity(pos);
        }
    }

    // todo: add landing effects
    // todo: add running effects


    @Override
    public @NotNull InteractionResult use(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (state.getBlock() != this)
            return InteractionResult.PASS;
        return getHalfState(level, pos, hit.getLocation().y - pos.getY()).flatMap(i -> i.blockState().map(s -> {
            IContainerSupport containerSupport = ContainerSupport.getSupport(i.getLevel(), pos, s);
            ISlabSupport slabSupport = SlabSupport.getSlabSupport(i.getLevel(), pos, s);
            if (containerSupport != null) {
                if (!level.isClientSide()) {
                    MenuProvider provider = containerSupport.getNamedContainerProvider(i.getLevel(), pos, s, player, hand, hit);
                    Services.PLATFORM.openScreen(player, new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return provider.getDisplayName();
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                            return new WrappedMenu(windowId, inventory, player, provider, i);
                        }
                    }, buffer -> {
                        buffer.writeBlockPos(i.getPos());
                        buffer.writeBoolean(i.isPositive());
                        buffer.writeResourceLocation(Objects.requireNonNull(Services.PLATFORM.getMenuTypeName(containerSupport.getContainer(i.getLevel(), pos, s))));
                        containerSupport.writeExtraData(i.getLevel(), pos, s).accept(buffer);
                    });
                }
                return InteractionResult.SUCCESS;
            } else {
                try {
                    return slabSupport == null ? s.use(i.getLevel(), player, hand, hit) : slabSupport.onBlockActivated(s, i.getLevel(), pos, player, hand, hit);
                } catch (Exception e) {
                    return InteractionResult.PASS;
                }
            }
        })).orElse(InteractionResult.PASS);
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        getHalfState(level, pos, result.getLocation().y - pos.getY())
                .ifPresent(i -> i.blockState().ifPresent(s -> s.attack(i.getLevel(), pos, player)));
    }

    @Override
    public void fallOn(@NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull Entity entity, float fallDistance) {
        getSlab(level, pos).map(SlabBlockEntity::getPositiveBlockInfo)
                .filter(i -> i.getBlockState() != null)
                .ifPresentOrElse(i -> i.getBlockState().getBlock().fallOn(i.getLevel(), i.getBlockState(), pos, entity, fallDistance),
                        () -> super.fallOn(level, state, pos, entity, fallDistance));
    }

    @Override
    public void updateEntityAfterFallOn(@NotNull BlockGetter level, Entity entity) {
        BlockPos pos = new BlockPos(entity.position()).below();
        if (level.getBlockState(pos).getBlock() == this) {
            getSlab(level, pos).map(SlabBlockEntity::getPositiveBlockInfo)
                    .filter(i -> i.getBlockState() != null)
                    .ifPresentOrElse(i -> i.getBlockState().getBlock().updateEntityAfterFallOn(i.getLevel(), entity),
                            () -> super.updateEntityAfterFallOn(level, entity));
        }
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        getSlab(level, pos).map(SlabBlockEntity::getPositiveBlockInfo)
                .filter(i -> i.getBlockState() != null)
                .ifPresent(i -> i.getBlockState().getBlock().stepOn(i.getLevel(), pos, i.getBlockState(), entity));
    }

    // todo: should display fluid overlay


    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        getSlab(level, pos).ifPresent(slab -> {
            slab.getPositiveBlockInfo().blockState()
                    .ifPresent(s -> s.entityInside(slab.getPositiveBlockInfo().getLevel(), pos, entity));
            slab.getNegativeBlockInfo().blockState()
                    .ifPresent(s -> s.entityInside(slab.getNegativeBlockInfo().getLevel(), pos, entity));
        });
    }

    @Override
    public void onProjectileHit(@NotNull Level level, @NotNull BlockState state, BlockHitResult hit, @NotNull Projectile projectile) {
        getHalfState(level, hit.getBlockPos(), hit.getLocation().y).ifPresent(i -> i.blockState().ifPresent(s -> s.onProjectileHit(i.getLevel(), s, hit, projectile)));
    }

    // todo: get sound type
}
