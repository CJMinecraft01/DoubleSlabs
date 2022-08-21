package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ContainerSupport {

    private static final List<IContainerSupport> containerSupports = Services.PLATFORM.getContainerSupports();

    public static IContainerSupport getSupport(Level world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IContainerSupport && ((IContainerSupport) state.getBlock()).hasSupport(world, pos, state))
            return (IContainerSupport) state.getBlock();
        for (IContainerSupport support : containerSupports)
            if (support.hasSupport(world, pos, state))
                return support;
        return null;
    }

}
