package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.util.AnnotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

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
    public static IVerticalSlabSupport getVerticalSlabSupport(IBlockReader world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IVerticalSlabSupport && ((IVerticalSlabSupport) state.getBlock()).isVerticalSlab(world, pos, state))
            return (IVerticalSlabSupport) state.getBlock();
        for (IVerticalSlabSupport support : verticalSlabSupports)
            if (support.isVerticalSlab(world, pos, state))
                return support;
        return null;
    }

    @Nullable
    public static IVerticalSlabSupport getVerticalSlabSupport(ItemStack stack, PlayerEntity player, Hand hand) {
        if (stack.getItem() instanceof IVerticalSlabSupport && ((IVerticalSlabSupport) stack.getItem()).isVerticalSlab(stack, player, hand))
            return (IVerticalSlabSupport) stack.getItem();
        if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof IVerticalSlabSupport && ((IVerticalSlabSupport) ((BlockItem) stack.getItem()).getBlock()).isVerticalSlab(stack, player, hand))
            return (IVerticalSlabSupport) ((BlockItem) stack.getItem()).getBlock();
        for (IVerticalSlabSupport support : verticalSlabSupports)
            if (support.isVerticalSlab(stack, player, hand))
                return support;
        return null;
    }

    @Nullable
    public static IHorizontalSlabSupport getHorizontalSlabSupport(IBlockReader world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IHorizontalSlabSupport && ((IHorizontalSlabSupport) state.getBlock()).isHorizontalSlab(world, pos, state))
            return (IHorizontalSlabSupport) state.getBlock();
        for (IHorizontalSlabSupport support : horizontalSlabSupports)
            if (support.isHorizontalSlab(world, pos, state))
                return support;
        return null;
    }

    @Nullable
    public static IHorizontalSlabSupport getHorizontalSlabSupport(ItemStack stack, PlayerEntity player, Hand hand) {
        if (stack.getItem() instanceof IHorizontalSlabSupport && ((IHorizontalSlabSupport) stack.getItem()).isHorizontalSlab(stack, player, hand))
            return (IHorizontalSlabSupport) stack.getItem();
        if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof IHorizontalSlabSupport && ((IHorizontalSlabSupport) ((BlockItem) stack.getItem()).getBlock()).isHorizontalSlab(stack, player, hand))
            return (IHorizontalSlabSupport) ((BlockItem) stack.getItem()).getBlock();
        for (IHorizontalSlabSupport support : horizontalSlabSupports)
            if (support.isHorizontalSlab(stack, player, hand))
                return support;
        return null;
    }

    public static boolean isHorizontalSlab(Item item) {
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof IHorizontalSlabSupport)
            return ((IHorizontalSlabSupport) ((BlockItem) item).getBlock()).isHorizontalSlab(item);
        for (IHorizontalSlabSupport support : horizontalSlabSupports)
            if (support.isHorizontalSlab(item))
                return true;
        return false;
    }

    public static boolean isHorizontalSlab(Block block) {
        if (DSConfig.SERVER.isBlacklistedHorizontalSlab(block))
            return false;
        if (block instanceof IHorizontalSlabSupport)
            return true;
        for (IHorizontalSlabSupport support : horizontalSlabSupports)
            if (support.isHorizontalSlab(block))
                return true;
        return false;
    }

    @Nullable
    public static ISlabSupport getSlabSupport(IBlockReader world, BlockPos pos, BlockState state) {
        IHorizontalSlabSupport horizontalSlabSupport = getHorizontalSlabSupport(world, pos, state);
        return horizontalSlabSupport != null ? horizontalSlabSupport : getVerticalSlabSupport(world, pos, state);
    }

    @Nullable
    public static ISlabSupport getSlabSupport(ItemStack stack, PlayerEntity player, Hand hand) {
        IHorizontalSlabSupport horizontalSlabSupport = getHorizontalSlabSupport(stack, player, hand);
        return horizontalSlabSupport != null ? horizontalSlabSupport : getVerticalSlabSupport(stack, player, hand);
    }

}
