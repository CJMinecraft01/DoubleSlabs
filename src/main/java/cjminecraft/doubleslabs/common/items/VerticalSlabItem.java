package cjminecraft.doubleslabs.common.items;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.placement.PlacementHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class VerticalSlabItem extends BlockItem {
    public VerticalSlabItem() {
//        super(DSBlocks.VERTICAL_SLAB.get(), new Item.Properties().setISTER(() -> VerticalSlabItemStackTileEntityRenderer::new).group(DoubleSlabs.TAB));
        super(DSBlocks.VERTICAL_SLAB.get(), new Item.Properties().tab(DoubleSlabs.TAB));
    }

    public static ItemStack setStack(ItemStack stack, ItemStack toSet) {
        ItemStack copy = toSet.copy();
        copy.setCount(1);
        stack.addTagElement("item", copy.serializeNBT());
        return stack;
    }

    public static ItemStack getStack(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTagElement("item");
        return ItemStack.of(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            ForgeRegistries.ITEMS.forEach(item -> {
                if (SlabSupport.isHorizontalSlab(item)) {
                    ItemStack stack = new ItemStack(this);
                    items.add(setStack(stack, item.getDefaultInstance()));
                }
            });
        }
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        boolean result = super.placeBlock(context, state);
        if (context.getPlayer() != null) {
            VerticalSlabBlock.getTile(context.getLevel(), context.getClickedPos()).ifPresent(tile -> {
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
        return new TranslatableComponent("item.vertical_slab.prefix", new TranslatableComponent(this.getDescriptionId(stack)));
    }
}
