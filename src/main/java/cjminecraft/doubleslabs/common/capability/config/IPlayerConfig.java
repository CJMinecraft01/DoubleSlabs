package cjminecraft.doubleslabs.common.capability.config;

import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerConfig extends INBTSerializable<CompoundNBT> {

    VerticalSlabPlacementMethod getVerticalSlabPlacementMethod();

    void setVerticalSlabPlacementMethod(VerticalSlabPlacementMethod method);

    boolean placeVerticalSlabs();

    void setPlaceVerticalSlabs(boolean place);

}
