package cjminecraft.doubleslabs.common.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

public class UnlistedPropertyCullInfo implements IUnlistedProperty<List> {
    @Override
    public String getName() {
        return "cull_info";
    }

    @Override
    public boolean isValid(List value) {
        return true;
    }

    @Override
    public Class<List> getType() {
        return List.class;
    }

    @Override
    public String valueToString(List value) {
        return value.toString();
    }
}
