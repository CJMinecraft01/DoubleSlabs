package cjminecraft.doubleslabs.common.capability.config;

import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerConfig implements IPlayerConfig {

    private VerticalSlabPlacementMethod verticalSlabPlacementMethod = VerticalSlabPlacementMethod.DYNAMIC;
    private boolean placeVerticalSlabs;

    @Override
    public VerticalSlabPlacementMethod getVerticalSlabPlacementMethod() {
        return verticalSlabPlacementMethod;
    }

    @Override
    public void setVerticalSlabPlacementMethod(VerticalSlabPlacementMethod method) {
        this.verticalSlabPlacementMethod = method;
    }

    @Override
    public boolean placeVerticalSlabs() {
        return this.placeVerticalSlabs;
    }

    @Override
    public void setPlaceVerticalSlabs(boolean place) {
        this.placeVerticalSlabs = place;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("verticalSlabPlacementMethod", this.verticalSlabPlacementMethod.getIndex());
        nbt.setBoolean("placeVerticalSlabs", this.placeVerticalSlabs);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.verticalSlabPlacementMethod = VerticalSlabPlacementMethod.fromIndex(nbt.getInteger("verticalSlabPlacementMethod"));
        this.placeVerticalSlabs = nbt.getBoolean("placeVerticalSlabs");
    }
}
