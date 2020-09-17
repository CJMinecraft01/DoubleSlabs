package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.util.AnnotationUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.List;

public class SlabSupport {

    private static List<IVerticalSlabSupport> verticalSlabSupports;
    private static List<IHorizontalSlabSupport> horizontalSlabSupports;

    public static void load() {
        verticalSlabSupports = AnnotationUtil.getClassInstances(SlabSupportProvider.class, IVerticalSlabSupport.class, AnnotationUtil.MODID_PREDICATE);
        horizontalSlabSupports = AnnotationUtil.getClassInstances(SlabSupportProvider.class, IHorizontalSlabSupport.class, AnnotationUtil.MODID_PREDICATE);

        DoubleSlabs.LOGGER.info("Loaded %s vertical slab support classes", verticalSlabSupports.size());
        DoubleSlabs.LOGGER.info("Loaded %s horizontal slab support classes", horizontalSlabSupports.size());
    }

    @Nullable
    public static IVerticalSlabSupport getVerticalSlabSupport(IBlockAccess world, BlockPos pos, IBlockState state) {
        if (state.getBlock() instanceof IVerticalSlabSupport && ((IVerticalSlabSupport) state.getBlock()).isVerticalSlab(world, pos, state))
            return (IVerticalSlabSupport) state.getBlock();
        for (IVerticalSlabSupport support : verticalSlabSupports)
            if (support.isVerticalSlab(world, pos, state))
                return support;
        return null;
    }

    @Nullable
    public static IVerticalSlabSupport getVerticalSlabSupport(ItemStack stack, EntityPlayer player, EnumHand hand) {
        if (stack.getItem() instanceof IVerticalSlabSupport && ((IVerticalSlabSupport) stack.getItem()).isVerticalSlab(stack, player, hand))
            return (IVerticalSlabSupport) stack.getItem();
        if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IVerticalSlabSupport && ((IVerticalSlabSupport) ((ItemBlock) stack.getItem()).getBlock()).isVerticalSlab(stack, player, hand))
            return (IVerticalSlabSupport) ((ItemBlock) stack.getItem()).getBlock();
        for (IVerticalSlabSupport support : verticalSlabSupports)
            if (support.isVerticalSlab(stack, player, hand))
                return support;
        return null;
    }

    @Nullable
    public static IHorizontalSlabSupport isHorizontalSlab(IBlockAccess world, BlockPos pos, IBlockState state) {
        if (state.getBlock() instanceof IHorizontalSlabSupport && ((IHorizontalSlabSupport) state.getBlock()).isHorizontalSlab(world, pos, state))
            return (IHorizontalSlabSupport) state.getBlock();
        for (IHorizontalSlabSupport support : horizontalSlabSupports)
            if (support.isHorizontalSlab(world, pos, state))
                return support;
        return null;
    }

    @Nullable
    public static IHorizontalSlabSupport isHorizontalSlab(ItemStack stack, EntityPlayer player, EnumHand hand) {
        if (stack.getItem() instanceof IHorizontalSlabSupport && ((IHorizontalSlabSupport) stack.getItem()).isHorizontalSlab(stack, player, hand))
            return (IHorizontalSlabSupport) stack.getItem();
        if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IHorizontalSlabSupport && ((IHorizontalSlabSupport) ((ItemBlock) stack.getItem()).getBlock()).isHorizontalSlab(stack, player, hand))
            return (IHorizontalSlabSupport) ((ItemBlock) stack.getItem()).getBlock();
        for (IHorizontalSlabSupport support : horizontalSlabSupports)
            if (support.isHorizontalSlab(stack, player, hand))
                return support;
        return null;
    }

    public static boolean isHorizontalSlab(Item item) {
        if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IHorizontalSlabSupport)
            return ((IHorizontalSlabSupport) ((ItemBlock) item).getBlock()).isHorizontalSlab(item);
        for (IHorizontalSlabSupport support : horizontalSlabSupports)
            if (support.isHorizontalSlab(item))
                return true;
        return false;
    }

    @Nullable
    public static ISlabSupport getSlabSupport(IBlockAccess world, BlockPos pos, IBlockState state) {
        IHorizontalSlabSupport horizontalSlabSupport = isHorizontalSlab(world, pos, state);
        return horizontalSlabSupport != null ? horizontalSlabSupport : getVerticalSlabSupport(world, pos, state);
    }

    @Nullable
    public static ISlabSupport getSlabSupport(ItemStack stack, EntityPlayer player, EnumHand hand) {
        IHorizontalSlabSupport horizontalSlabSupport = isHorizontalSlab(stack, player, hand);
        return horizontalSlabSupport != null ? horizontalSlabSupport : getVerticalSlabSupport(stack, player, hand);
    }

}
