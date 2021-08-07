package cjminecraft.doubleslabs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IWorldWrapper<W extends Level> {
    boolean isPositive();

    void setPositive(boolean positive);

    BlockPos getPos();

    void setBlockPos(BlockPos pos);

    IStateContainer getStateContainer();

    void setStateContainer(IStateContainer container);

    default W getInternalWorld() {
        return (W) this;
    }

    Level getWorld();

    void setWorld(Level world);
}
