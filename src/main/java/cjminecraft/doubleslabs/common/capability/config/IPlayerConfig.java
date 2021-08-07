package cjminecraft.doubleslabs.common.capability.config;

import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;
import net.minecraft.nbt.CompoundTag;

public interface IPlayerConfig {

    VerticalSlabPlacementMethod getVerticalSlabPlacementMethod();

    void setVerticalSlabPlacementMethod(VerticalSlabPlacementMethod method);

    boolean placeVerticalSlabs();

    void setPlaceVerticalSlabs(boolean place);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);

}
