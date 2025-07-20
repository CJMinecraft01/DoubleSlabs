package cjminecraft.doubleslabs.common.item;

import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.item.component.VerticalSlabContent;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class VerticalSlabItem extends BlockItem {
    public VerticalSlabItem() {
        super(DSBlocks.VERTICAL_SLAB.get(), new Properties().component(DSItems.VERTICAL_SLAB_CONTENT.get(), VerticalSlabContent.EMPTY));
    }

    public static ItemStack of(ItemStack slab) {
        return setContainedSlabItem(DSItems.VERTICAL_SLAB.get().getDefaultInstance(), slab);
    }

    public static ItemStack setContainedSlabItem(ItemStack stack, ItemStack slab) {
        final var copy = slab.copyWithCount(1);

        stack.applyComponents(DataComponentMap.builder().set(DSItems.VERTICAL_SLAB_CONTENT.get(), VerticalSlabContent.of(copy)).build());

        return stack;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        final var result = super.placeBlock(context, state);
        final var player = context.getPlayer();

        if (player == null) {
            return result;
        }

        final var level = context.getLevel();
        final var pos = context.getClickedPos();
        final var stack = context.getItemInHand();

        final var content = Objects.requireNonNull(stack.get(DSItems.VERTICAL_SLAB_CONTENT.get()));
        if (content.isEmpty()) {
            Constants.LOG.warn("Tried to place a vertical slab with no inner slab");
            return result;
        }

        final var type = state.getValue(VerticalSlabBlock.TYPE);

        final var helper = Internal.getSlabHelper().getHorizontalSlabHelper(content.getItem()).orElseThrow();

        final var stateFromSlabItem = Objects.requireNonNull(helper.getStateFromStack(content.getItem(), context));

        level.getBlockEntity(pos, DSBlockEntities.DYNAMIC_SLAB.get()).ifPresent(dynamicSlab -> {
            final var half = type == VerticalSlabType.DOUBLE ?
                    Objects.requireNonNull(dynamicSlab.getMissingHalf())
                    : type.getHalf();

            final var slabState = helper.getStateForHalf(level, pos, stateFromSlabItem, half);
            dynamicSlab.setBlockState(half, slabState);
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
