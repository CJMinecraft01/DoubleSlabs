package cjminecraft.doubleslabs.common.capability.config;

import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;
import net.minecraft.nbt.CompoundNBT;

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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("verticalSlabPlacementMethod", this.verticalSlabPlacementMethod.getIndex());
        nbt.putBoolean("placeVerticalSlabs", this.placeVerticalSlabs);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.verticalSlabPlacementMethod = VerticalSlabPlacementMethod.fromIndex(nbt.getInt("verticalSlabPlacementMethod"));
        this.placeVerticalSlabs = nbt.getBoolean("placeVerticalSlabs");
    }
}
