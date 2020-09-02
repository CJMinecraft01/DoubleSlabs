package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.old.DoubleSlabs;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContainerSupportOld {

    private static final List<IContainerSupportOld> supportedContainers = new ArrayList<>();

    public static void init() {
    }

    public static void addContainerSupport(@Nonnull IContainerSupportOld support) {
        if (supportedContainers.contains(support)) {
            DoubleSlabs.LOGGER.warn("A container support of type %s has already been registered - SKIPPING", support.getClass().getSimpleName());
        } else {
            supportedContainers.add(support);
            DoubleSlabs.LOGGER.info("Successfully added container support for type %s", support.getClass().getSimpleName());
        }
    }

    public static @Nullable
    IContainerSupportOld getSupport(World world, BlockPos pos, BlockState state) {
        for (IContainerSupportOld support : supportedContainers)
            if (support.isValid(world, pos, state))
                return support;
        return null;
    }

}
