package cjminecraft.doubleslabs.common.items;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public class VerticalSlabItem extends BlockItem {
    public VerticalSlabItem() {
        super(Services.REGISTRIES.getBlocks().getVerticalSlabBlock(), new Properties().tab(DoubleSlabs.TAB));
    }

    public static ItemStack setStack(ItemStack stack, ItemStack toSet) {
        ItemStack copy = toSet.copy();
        copy.setCount(1);
        stack.addTagElement("item", copy.save(new CompoundTag()));
        return stack;
    }

    public static ItemStack getStack(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTagElement("item");
        return ItemStack.of(nbt);
    }

    // todo: fill item category

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
//        if (this.allowedIn(tab)) {
//            ForgeRegistries.ITEMS.forEach(item -> {
//                if (SlabSupport.isHorizontalSlab(item)) {
//                    ItemStack stack = new ItemStack(this);
//                    items.add(setStack(stack, item.getDefaultInstance()));
//                }
//            });
//        }
//    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean result = super.placeBlock(context, state);
        if (context.getPlayer() != null) {
            VerticalSlabBlock.getSlab(context.getLevel(), context.getClickedPos()).ifPresent(tile -> {
                ItemStack slabStack = getStack(context.getItemInHand());
                IHorizontalSlabSupport support = SlabSupport.getHorizontalSlabSupport(slabStack, context.getPlayer(), InteractionHand.MAIN_HAND);
                if (support != null) {
                    boolean positive = tile.getPositiveBlockInfo().getBlockState() == null;
                    BlockState slabState = PlacementHandler.getStateFromSupport(context.getLevel(), context.getClickedPos(), context.getPlayer(), InteractionHand.MAIN_HAND, slabStack, positive ? SlabType.BOTTOM : SlabType.TOP, support);
                    if (positive)
                        tile.getPositiveBlockInfo().setBlockState(slabState);
                    else
                        tile.getNegativeBlockInfo().setBlockState(slabState);
                }
            });
        }
        return result;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return getStack(stack).getDescriptionId();
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.vertical_slab.prefix", Component.translatable(this.getDescriptionId(stack)));
    }

    // todo: item color

//    @OnlyIn(Dist.CLIENT)
//    public ItemColor getItemColor() {
//        return (stack, tintIndex) -> {
//            ItemStack actualStack = getStack(stack);
//            return Minecraft.getInstance().getItemColors().getColor(actualStack, tintIndex);
//        };
//    }
}
