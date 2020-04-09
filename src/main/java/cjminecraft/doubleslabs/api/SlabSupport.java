package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.addons.minecraft.MinecraftSlabSupport;
import cjminecraft.doubleslabs.api.ISlabSupport;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SlabSupport {

    private static List<ISlabSupport> supportedSlabs = new ArrayList<>();

    static {
        addSlabSupport(new MinecraftSlabSupport());
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
