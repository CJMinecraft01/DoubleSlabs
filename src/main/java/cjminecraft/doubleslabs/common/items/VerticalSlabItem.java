package cjminecraft.doubleslabs.common.items;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.client.render.VerticalSlabItemStackTileEntityRenderer;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.placement.PlacementHandler;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
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
        // TODO set creative tab
        super(DSBlocks.VERTICAL_SLAB.get(), new Item.Properties().setISTER(() -> VerticalSlabItemStackTileEntityRenderer::new).group(DoubleSlabs.TAB));
    }
    
//    public static BlockState getState(ItemStack stack) {
//        CompoundNBT nbt = stack.getOrCreateChildTag("state");
//        return NBTUtil.readBlockState(nbt);
//    }

    public static ItemStack getStack(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateChildTag("item");
        return ItemStack.read(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ForgeRegistries.ITEMS.forEach(item -> {
                IHorizontalSlabSupport support = SlabSupport.getHorizontalSlabSupport(item);
                if (support != null) {
                    ItemStack stack = new ItemStack(this);
                    stack.setTagInfo("item", item.getDefaultInstance().write(new CompoundNBT()));
//                    stack.setTagInfo("state", NBTUtil.writeBlockState(PlacementHandler.getStateFromSupport(Minecraft.getInstance().world, BlockPos.ZERO, Minecraft.getInstance().player, Hand.MAIN_HAND, stack, SlabType.BOTTOM, support)));
                    items.add(stack);
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
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof SlabTileEntity && player != null) {
            ItemStack slabStack = getStack(stack);
            IHorizontalSlabSupport support = SlabSupport.getHorizontalSlabSupport(slabStack, player, Hand.MAIN_HAND);
            if (support != null)
                ((SlabTileEntity) tileEntity).getPositiveBlockInfo().setBlockState(PlacementHandler.getStateFromSupport(world, pos, player, Hand.MAIN_HAND, slabStack, SlabType.BOTTOM, support));
            //            if (slabState.getBlock() != Blocks.AIR)
//                ((SlabTileEntity) tileEntity).getPositiveBlockInfo().setBlockState(slabState);
        }
        return result;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getStack(stack).getTranslationKey();
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        // TODO add vertical slab prefix
        return new TranslationTextComponent("item.vertical_slab.prefix", I18n.format(this.getTranslationKey(stack)));
    }
}
