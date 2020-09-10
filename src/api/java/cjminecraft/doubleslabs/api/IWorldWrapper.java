package cjminecraft.doubleslabs.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWorldWrapper<W extends World> {
    boolean isPositive();

    void setPositive(boolean positive);

    BlockPos getPos();

    void setBlockPos(BlockPos pos);

    IStateContainer getStateContainer();

    void setStateContainer(IStateContainer container);

    default W getInternalWorld() {
        return (W) this;
    }

    World getWorld();

    void setWorld(World world);
}
