package cjminecraft.doubleslabs.common.items;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.placement.PlacementHandler;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class VerticalSlabItem extends ItemBlock {
    public VerticalSlabItem() {
//        super(DSBlocks.VERTICAL_SLAB.get(), new Item.Properties().setISTER(() -> VerticalSlabItemStackTileEntityRenderer::new).group(DoubleSlabs.TAB));
        super(DSBlocks.VERTICAL_SLAB);
        this.setCreativeTab(DoubleSlabs.TAB);
    }
    
//    public static BlockState getState(ItemStack stack) {
//        CompoundNBT nbt = stack.getOrCreateChildTag("state");
//        return NBTUtil.readBlockState(nbt);
//    }

    public static ItemStack setStack(ItemStack stack, ItemStack toSet) {
        ItemStack copy = toSet.copy();
        copy.setCount(1);
        stack.setTagInfo("item", copy.writeToNBT(new NBTTagCompound()));
        return stack;
    }

    public static ItemStack getStack(ItemStack stack) {
        NBTTagCompound nbt = stack.getOrCreateSubCompound("item");
        return new ItemStack(nbt);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == DoubleSlabs.TAB) {
            ForgeRegistries.ITEMS.forEach(item -> {
                if (item != null && SlabSupport.isHorizontalSlab(item)) {
                    NonNullList<ItemStack> list = NonNullList.create();
                    if (item.getHasSubtypes())
                        item.getSubItems(CreativeTabs.SEARCH, list);
                    else
                        list.add(item.getDefaultInstance());
                    list.forEach(itemStack -> {
                        ItemStack stack = new ItemStack(this);
                        items.add(setStack(stack, itemStack));
                    });
                }
            });
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        VerticalSlabBlock.getTile(world, pos).ifPresent(tile -> {
            ItemStack slabStack = getStack(stack);
            IHorizontalSlabSupport support = SlabSupport.isHorizontalSlab(slabStack, player, EnumHand.MAIN_HAND);
            if (support != null) {
                boolean positive = tile.getPositiveBlockInfo().getBlockState() == null;
                IBlockState slabState = PlacementHandler.getStateFromSupport(world, pos, player, EnumHand.MAIN_HAND, slabStack, positive ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP, support);
                if (positive)
                    tile.getPositiveBlockInfo().setBlockState(slabState);
                else
                    tile.getNegativeBlockInfo().setBlockState(slabState);
            }
        });
        return result;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getStack(stack).getTranslationKey();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.format("item.vertical_slab.prefix", getStack(stack).getDisplayName());
    }

}
