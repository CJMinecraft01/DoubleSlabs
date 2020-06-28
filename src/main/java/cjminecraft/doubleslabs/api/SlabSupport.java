package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.addons.atum2.Atum2SlabSupport;
import cjminecraft.doubleslabs.addons.erebus.ErebusSlabSupport;
import cjminecraft.doubleslabs.addons.libraryex.LibraryExSlabSupport;
import cjminecraft.doubleslabs.addons.minecraft.MinecraftSlabSupport;
import cjminecraft.doubleslabs.addons.stairwaytoaether.StairwayToAetherSlabSupport;
import cjminecraft.doubleslabs.addons.thebetweenlands.TheBetweenlandsSlabSupport;
import cjminecraft.doubleslabs.addons.engineersdecor.EngineersDecorSlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlabSupport {

    private static final List<ISlabSupport> supportedSlabs = new ArrayList<>();

    public static void init() {
        addSlabSupport(new MinecraftSlabSupport());

        if (Loader.isModLoaded("atum"))
            addSlabSupport(new Atum2SlabSupport());

        if (Loader.isModLoaded("libraryex"))
            addSlabSupport(new LibraryExSlabSupport());

        if (Loader.isModLoaded("erebus"))
            addSlabSupport(new ErebusSlabSupport());

        if (Loader.isModLoaded("thebetweenlands"))
            addSlabSupport(new TheBetweenlandsSlabSupport());

        if (Loader.isModLoaded("engineersdecor"))
            addSlabSupport(new EngineersDecorSlabSupport());

        if (Loader.isModLoaded("stairway"))
            addSlabSupport(new StairwayToAetherSlabSupport());
    }

    public static void addSlabSupport(@Nonnull ISlabSupport support) {
        if (supportedSlabs.contains(support)) {
            DoubleSlabs.LOGGER.warn("A slab support of type %s has already been registered - SKIPPING", support.getClass().getSimpleName());
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

    public static @Nullable ISlabSupport getHorizontalSlabSupport(IBlockAccess world, BlockPos pos, IBlockState state) {
        return getSupport(world, pos, state);
    }

    public static @Nullable ISlabSupport getHorizontalSlabSupport(ItemStack stack, EntityPlayer player, EnumHand hand) {
        return getSupport(stack, player, hand);
    }

    public static @Nullable ISlabSupport getVerticalSlabSupport(IBlockAccess world, BlockPos pos, IBlockState state) {
        if (state.getBlock() instanceof ISlabSupport && ((ISlabSupport) state.getBlock()).isVerticalSlab(world, pos, state))
            return (ISlabSupport) state.getBlock();
        for (ISlabSupport support : supportedSlabs)
            if (support.isVerticalSlab(world, pos, state))
                return support;
        return null;
    }

    public static @Nullable ISlabSupport getVerticalSlabSupport(ItemStack stack, EntityPlayer player, EnumHand hand) {
        if ((stack.getItem() instanceof ItemBlock)) {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
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
    public static @Nullable ISlabSupport getSupport(IBlockAccess world, BlockPos pos, IBlockState state) {
        if (state.getBlock() instanceof ISlabSupport && ((ISlabSupport) state.getBlock()).isHorizontalSlab(world, pos, state))
            return (ISlabSupport) state.getBlock();
        for (ISlabSupport support : supportedSlabs)
            if (support.isHorizontalSlab(world, pos, state))
                return support;
        return null;
    }

    @Deprecated
    public static @Nullable ISlabSupport getSupport(ItemStack stack, EntityPlayer player, EnumHand hand) {
        if ((stack.getItem() instanceof ItemBlock)) {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
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
