package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.addons.atum2.Atum2SlabSupport;
import cjminecraft.doubleslabs.addons.erebus.ErebusSlabSupport;
import cjminecraft.doubleslabs.addons.libraryex.LibraryExSlabSupport;
import cjminecraft.doubleslabs.addons.minecraft.MinecraftSlabSupport;
import cjminecraft.doubleslabs.addons.thebetweenlands.TheBetweenlandsSlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
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

    public static @Nullable ISlabSupport getSupport(World world, BlockPos pos, IBlockState state) {
        if (state.getBlock() instanceof ISlabSupport && ((ISlabSupport) state.getBlock()).isValid(world, pos, state))
            return (ISlabSupport) state.getBlock();
        for (ISlabSupport support : supportedSlabs)
            if (support.isValid(world, pos, state))
                return support;
        return null;
    }

    public static @Nullable ISlabSupport getSupport(ItemStack stack, EntityPlayer player, EnumHand hand) {
        if ((stack.getItem() instanceof ItemBlock)) {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof ISlabSupport) {
                ISlabSupport support = (ISlabSupport) block;
                if (support.isValid(stack, player, hand))
                    return support;
            }
        }
        for (ISlabSupport support : supportedSlabs)
            if (support.isValid(stack, player, hand))
                return support;
        return null;
    }
}
