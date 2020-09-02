package cjminecraft.doubleslabs.common.items;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.placement.PlacementHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class VerticalSlabItem extends BlockItem {
    public VerticalSlabItem() {
//        super(DSBlocks.VERTICAL_SLAB.get(), new Item.Properties().setISTER(() -> VerticalSlabItemStackTileEntityRenderer::new).group(DoubleSlabs.TAB));
        super(DSBlocks.VERTICAL_SLAB.get(), new Item.Properties().group(DoubleSlabs.TAB));
    }
    
//    public static BlockState getState(ItemStack stack) {
//        CompoundNBT nbt = stack.getOrCreateChildTag("state");
//        return NBTUtil.readBlockState(nbt);
//    }

    public static ItemStack setStack(ItemStack stack, ItemStack toSet) {
        stack.setTagInfo("item", toSet.write(new CompoundNBT()));
        return stack;
    }

    public static ItemStack getStack(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateChildTag("item");
        return ItemStack.read(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ForgeRegistries.ITEMS.forEach(item -> {
                if (SlabSupport.addVerticalSlabItem(item)) {
                    ItemStack stack = new ItemStack(this);
//                    stack.setTagInfo("item", item.getDefaultInstance().write(new CompoundNBT()));
//                    stack.setTagInfo("state", NBTUtil.writeBlockState(PlacementHandler.getStateFromSupport(Minecraft.getInstance().world, BlockPos.ZERO, Minecraft.getInstance().player, Hand.MAIN_HAND, stack, SlabType.BOTTOM, support)));
                    items.add(setStack(stack, item.getDefaultInstance()));
                }
            });
//            ForgeRegistries.BLOCKS.forEach(block -> {
//                BlockState state = block.getDefaultState();
//                IHorizontalSlabSupport support = SlabSupport.getHorizontalSlabSupport(Minecraft.getInstance().world, BlockPos.ZERO, state);
//                if (support != null) {
//                    state = support.getStateForHalf(Minecraft.getInstance().world, BlockPos.ZERO, state, SlabType.BOTTOM);
//                    ItemStack stack = new ItemStack(this);
//                    stack.setTagInfo("state", NBTUtil.writeBlockState(state));
//                    items.add(stack);
//                }
//            });
        }
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        boolean result = super.onBlockPlaced(pos, world, player, stack, state);
        if (player != null) {
            VerticalSlabBlock.getTile(world, pos).ifPresent(tile -> {
                ItemStack slabStack = getStack(stack);
                IHorizontalSlabSupport support = SlabSupport.isHorizontalSlab(slabStack, player, Hand.MAIN_HAND);
                if (support != null) {
                    boolean positive = tile.getPositiveBlockInfo().getBlockState() == null;
                    BlockState slabState = PlacementHandler.getStateFromSupport(world, pos, player, Hand.MAIN_HAND, slabStack, positive ? SlabType.BOTTOM : SlabType.TOP, support);
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
    public String getTranslationKey(ItemStack stack) {
        return getStack(stack).getTranslationKey();
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        // TODO rename
        return new TranslationTextComponent("item.vertical_slab.prefix", new TranslationTextComponent(this.getTranslationKey(stack)));
    }
}
