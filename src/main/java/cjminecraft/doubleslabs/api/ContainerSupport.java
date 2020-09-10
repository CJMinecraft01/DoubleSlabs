package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.containers.ContainerSupportProvider;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.util.AnnotationUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ContainerSupport {

    private static List<IContainerSupport> containerSupports;

    public static void load() {
        containerSupports = AnnotationUtil.getClassInstances(ContainerSupportProvider.class, IContainerSupport.class, AnnotationUtil.MODID_PREDICATE);

        DoubleSlabs.LOGGER.info("Loaded %s container support classes", containerSupports.size());
    }

    public static IContainerSupport getSupport(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IContainerSupport && ((IContainerSupport) state.getBlock()).hasSupport(world, pos, state))
            return (IContainerSupport) state.getBlock();
        for (IContainerSupport support : containerSupports)
            if (support.hasSupport(world, pos, state))
                return support;
        return null;
    }

}
