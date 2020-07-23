package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.addons.engineersdecor.EngineersDecorSlabSupport;
import cjminecraft.doubleslabs.addons.lottaterracotta.LottaTerracottaVerticalSlabSupport;
import cjminecraft.doubleslabs.addons.minecraft.MinecraftCampfireSupport;
import cjminecraft.doubleslabs.addons.minecraft.MinecraftSlabSupport;
import cjminecraft.doubleslabs.addons.quark.QuarkSlabSupport;
import cjminecraft.doubleslabs.addons.stairwaytoaether.StairwayToAetherSlabSupport;
import cjminecraft.doubleslabs.addons.swampexpansion.SwampExpansionSupport;
import cjminecraft.doubleslabs.addons.sweetconcrete.SweetConcreteVerticalSlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SlabSupport {

    private static final List<ISlabSupport> supportedSlabs = new ArrayList<>();

    public static void init() {
        addSlabSupport(new MinecraftSlabSupport());
        addSlabSupport(new MinecraftCampfireSupport());

        if (ModList.get().isLoaded("engineersdecor"))
            addSlabSupport(new EngineersDecorSlabSupport());

        if (ModList.get().isLoaded("quark"))
            addSlabSupport(new QuarkSlabSupport());

        if (ModList.get().isLoaded("swampexpansion"))
            addSlabSupport(new SwampExpansionSupport());

        if (ModList.get().isLoaded("stairway"))
            addSlabSupport(new StairwayToAetherSlabSupport());

        if (ModList.get().isLoaded("lottaterracotta"))
            addSlabSupport(new LottaTerracottaVerticalSlabSupport());

        if (ModList.get().isLoaded("sweetconcrete"))
            addSlabSupport(new SweetConcreteVerticalSlabSupport());
    }

    public static void addSlabSupport(@Nonnull ISlabSupport support) {
        if (supportedSlabs.contains(support)) {
            DoubleSlabs.LOGGER.info("A slab support of type %s has already been registered - SKIPPING", support.getClass().getSimpleName());
        } else {
            supportedSlabs.add(support);
            DoubleSlabs.LOGGER.info("Successfully added slab support for type %s", support.getClass().getSimpleName());
        }
    }

    public static void addSupportFromIMC(String className) {
        try {
            Class<?> support = Class.forName(className);
            for (Class<?> interfaces : support.getInterfaces())
                if (interfaces.getName().equals(ISlabSupport.class.getName()))
                    addSlabSupport((ISlabSupport) support.newInstance());
        } catch (Exception e) {
            DoubleSlabs.LOGGER.error("An error occurred when registering slab support for class %s", className);
        }
    }

    public static @Nullable ISlabSupport getHorizontalSlabSupport(IBlockReader world, BlockPos pos, BlockState state) {
        return getSupport(world, pos, state);
    }

    public static @Nullable ISlabSupport getHorizontalSlabSupport(ItemStack stack, PlayerEntity player, Hand hand) {
        return getSupport(stack, player, hand);
    }

    public static @Nullable ISlabSupport getVerticalSlabSupport(IBlockReader world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof ISlabSupport && ((ISlabSupport) state.getBlock()).isVerticalSlab(world, pos, state))
            return (ISlabSupport) state.getBlock();
        for (ISlabSupport support : supportedSlabs)
            if (support.isVerticalSlab(world, pos, state))
                return support;
        return null;
    }

    public static @Nullable ISlabSupport getVerticalSlabSupport(ItemStack stack, PlayerEntity player, Hand hand) {
        if ((stack.getItem() instanceof BlockItem)) {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            if (block instanceof ISlabSupport) {
                ISlabSupport support = (ISlabSupport) block;
                if (support.isVerticalSlab(stack, player, hand))
                    return support;
            }
        }
        for (ISlabSupport support : supportedSlabs)
            if (support.isVerticalSlab(stack, player, hand))
                return support;
        return null;
    }

    @Deprecated
    public static @Nullable ISlabSupport getSupport(IBlockReader world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof ISlabSupport && ((ISlabSupport) state.getBlock()).isHorizontalSlab(world, pos, state))
            return (ISlabSupport) state.getBlock();
        for (ISlabSupport support : supportedSlabs)
            if (support.isHorizontalSlab(world, pos, state))
                return support;
        return null;
    }

    @Deprecated
    public static @Nullable ISlabSupport getSupport(ItemStack stack, PlayerEntity player, Hand hand) {
        if ((stack.getItem() instanceof BlockItem)) {
            Block block = ((BlockItem) stack.getItem()).getBlock();
            if (block instanceof ISlabSupport) {
                ISlabSupport support = (ISlabSupport) block;
                if (support.isHorizontalSlab(stack, player, hand))
                    return support;
            }
        }
        for (ISlabSupport support : supportedSlabs)
            if (support.isHorizontalSlab(stack, player, hand))
                return support;
        return null;
    }
}
