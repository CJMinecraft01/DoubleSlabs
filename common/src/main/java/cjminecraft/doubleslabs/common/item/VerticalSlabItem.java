package cjminecraft.doubleslabs.common.item;

import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.Internal;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class VerticalSlabItem extends BlockItem {
    public VerticalSlabItem() {
        super(DSBlocks.VERTICAL_SLAB.get(), new Properties());
    }

    public static ItemStack of(ItemStack slab) {
        return setContainedSlabItem(DSItems.VERTICAL_SLAB.get().getDefaultInstance(), slab);
    }

    public static ItemStack setContainedSlabItem(ItemStack stack, ItemStack slab) {
        final var copy = slab.copyWithCount(1);

        stack.addTagElement("item", copy.save(new CompoundTag()));

        return stack;
    }

    public static ItemStack getContainedSlabItem(ItemStack stack) {
        final var slabTag = stack.getOrCreateTagElement("item");
        return ItemStack.of(slabTag);
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

        final var slabStack = getContainedSlabItem(stack);
        if (slabStack.isEmpty()) {
            Constants.LOG.warn("Tried to place a vertical slab with no inner slab");
            return result;
        }

        final var newType = state.getValue(VerticalSlabBlock.TYPE);

        final var helper = Internal.getSlabHelper().getHorizontalSlabHelper(slabStack).orElseThrow();

        final var stateFromSlabItem = Objects.requireNonNull(helper.getStateFromStack(slabStack, context));

        level.getBlockEntity(pos, DSBlockEntities.DYNAMIC_SLAB.get()).ifPresent(dynamicSlab -> {
            final var half = newType == VerticalSlabType.DOUBLE ?
                    originalState.getValue(VerticalSlabBlock.TYPE).getHalf().getOpposite()
                    : newType.getHalf();

            final var slabState = helper.getStateForHalf(level, pos, stateFromSlabItem, half);
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
        final var slabStack = getContainedSlabItem(stack);
        return slabStack.getDescriptionId();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.vertical_slab.prefix", Component.translatable(getDescriptionId(stack)));
    }
}
