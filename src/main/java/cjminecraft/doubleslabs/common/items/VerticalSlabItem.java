package cjminecraft.doubleslabs.common.items;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.placement.PlacementHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class VerticalSlabItem extends BlockItem {
    public VerticalSlabItem() {
        super(DSBlocks.VERTICAL_SLAB.get(), new Item.Properties().group(DoubleSlabs.TAB));
    }

    public static ItemStack setStack(ItemStack stack, ItemStack toSet) {
        ItemStack copy = toSet.copy();
        copy.setCount(1);
        stack.setTagInfo("item", copy.write(new CompoundNBT()));
        return stack;
    }

    public static ItemStack getStack(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateChildTag("item");
        return ItemStack.read(nbt);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        ItemStack slab = getStack(stack);
        ResourceLocation registryName = slab.getItem().getRegistryName();
        if (registryName != null)
            ModList.get().getModContainerById(registryName.getNamespace()).ifPresent(
                    c -> tooltip.add(new TranslationTextComponent("item.vertical_slab.tooltip").modifyStyle(s -> s.applyFormatting(TextFormatting.GRAY)).append(new StringTextComponent(c.getModInfo().getDisplayName()).modifyStyle(s -> s.applyFormatting(TextFormatting.BLUE).setItalic(true))))
            );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ForgeRegistries.ITEMS.forEach(item -> {
                if (SlabSupport.isHorizontalSlab(item)) {
                    ItemStack stack = new ItemStack(this);
                    items.add(setStack(stack, item.getDefaultInstance()));
                }
            });
        }
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        boolean result = super.onBlockPlaced(pos, world, player, stack, state);
        if (player != null) {
            VerticalSlabBlock.getTile(world, pos).ifPresent(tile -> {
                ItemStack slabStack = getStack(stack);
                IHorizontalSlabSupport support = SlabSupport.getHorizontalSlabSupport(slabStack, player, Hand.MAIN_HAND);
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
        return new TranslationTextComponent("item.vertical_slab.prefix", new TranslationTextComponent(this.getTranslationKey(stack)));
    }

    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> {
            ItemStack actualStack = getStack(stack);
            return Minecraft.getInstance().getItemColors().getColor(actualStack, tintIndex);
        };
    }
}
