package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.addons.slabmachines.SlabMachinesContainerSupport;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContainerSupport {

    private static final List<IContainerSupport> supportedContainers = new ArrayList<>();

    public static void init() {
        if (Loader.isModLoaded("slabmachines"))
            addContainerSupport(new SlabMachinesContainerSupport());
    }

    public static void addContainerSupport(@Nonnull IContainerSupport support) {
        if (supportedContainers.contains(support)) {
            DoubleSlabs.LOGGER.warn("A container support of type %s has already been registered - SKIPPING", support.getClass().getSimpleName());
        } else {
            supportedContainers.add(support);
            DoubleSlabs.LOGGER.info("Successfully added container support for type %s", support.getClass().getSimpleName());
        }
    }

    public static @Nullable IContainerSupport getSupport(World world, BlockPos pos, IBlockState state) {
        for (IContainerSupport support : supportedContainers)
            if (support.isValid(world, pos, state))
                return support;
        return null;
    }

}
