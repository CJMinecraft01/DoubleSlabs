package cjminecraft.doubleslabs.common.item;

import cjminecraft.doubleslabs.api.helpers.IVerticalSlabHelper;
import cjminecraft.doubleslabs.api.state.VerticalSlabState;
import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import javax.annotation.Nullable;

public class VerticalSlabItem extends BlockItem implements IVerticalSlabHelper {
    public VerticalSlabItem() {
        super(DSBlocks.VERTICAL_SLAB.get(), new Properties().component(DSItems.VERTICAL_SLAB_CONTENT.get(), VerticalSlabContent.EMPTY));
    }

    public static ItemStack of(ItemStack slab) {
        return setContainedSlabItem(DSItems.VERTICAL_SLAB.get().getDefaultInstance(), slab);
    }

    public static ItemStack setContainedSlabItem(ItemStack stack, ItemStack slab) {
        final var copy = slab.copyWithCount(1);

        stack.set(DSItems.VERTICAL_SLAB_CONTENT.get(), VerticalSlabContent.of(copy));

        return stack;
    }

    @Override
    public boolean isVerticalSlab(Item item) {
        return item == this;
    }

    @Override
    public boolean isVerticalSlab(Block block) {
        return block == DSBlocks.VERTICAL_SLAB.get();
    }

    @Override
    public VerticalSlabState getVerticalSlabState(BlockGetter level, BlockPos pos, BlockState state) {
        return new VerticalSlabState(state.getValue(VerticalSlabBlock.AXIS), state.getValue(VerticalSlabBlock.TYPE));
    }

    @Override
    public BlockState getStateForVerticalSlabState(BlockGetter level, BlockPos pos, BlockState state,
                                                  VerticalSlabState verticalSlabState) {
        // Our item stores a horizontal slab; the container supplies its vertical orientation.
        return Internal.getSlabHelper().getHorizontalSlabHelper(state).orElseThrow()
                .getStateForHalf(state, verticalSlabState.type().getHalf());
    }

    @Override
    public @Nullable BlockState getStateFromStack(ItemStack stack, BlockPlaceContext context) {
        final var content = stack.get(DSItems.VERTICAL_SLAB_CONTENT.get());
        if (content == null || content.isEmpty()) {
            return null;
        }
        return Internal.getSlabHelper().getHorizontalSlabHelper(content.getItem())
                .map(helper -> helper.getStateFromStack(content.getItem(), context)).orElse(null);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        final var level = context.getLevel();
        final var pos = context.getClickedPos();
        final var originalState = level.getBlockState(pos);

        final var result = super.placeBlock(context, state);
        final var player = context.getPlayer();

        if (player == null) {
            return result;
        }

        final var stack = context.getItemInHand();

        final var content = Objects.requireNonNull(stack.get(DSItems.VERTICAL_SLAB_CONTENT.get()));
        if (content.isEmpty()) {
            Constants.LOG.warn("Tried to place a vertical slab with no inner slab");
            return result;
        }

        final var newType = state.getValue(VerticalSlabBlock.TYPE);

        final var helper = Internal.getSlabHelper().getHorizontalSlabHelper(content.getItem()).orElseThrow();

        final var stateFromSlabItem = Objects.requireNonNull(helper.getStateFromStack(content.getItem(), context));

        level.getBlockEntity(pos, DSBlockEntities.DYNAMIC_SLAB.get()).ifPresent(dynamicSlab -> {
            final var half = newType == VerticalSlabType.DOUBLE ?
                    originalState.getValue(VerticalSlabBlock.TYPE).getHalf().getOpposite()
                    : newType.getHalf();

            final var slabState = helper.getStateForHalf(stateFromSlabItem, half);
            dynamicSlab.setBlockState(half, slabState);

            if (slabState.hasBlockEntity()) {
                final var slabToPlaceBlockEntity = ((EntityBlock) slabState.getBlock()).newBlockEntity(pos, slabState);
                dynamicSlab.setBlockEntity(half, slabToPlaceBlockEntity);
            }
        });

        return result;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        final var content = Objects.requireNonNull(stack.get(DSItems.VERTICAL_SLAB_CONTENT.get()));
        return content.getItem().getDescriptionId();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.vertical_slab.prefix", Component.translatable(getDescriptionId(stack)));
    }
}
