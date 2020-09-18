package cjminecraft.doubleslabs.test.common.util;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum DummyVariantProperty implements IStringSerializable {
    DEFAULT("default");

    public static final PropertyEnum<DummyVariantProperty> DUMMY = PropertyEnum.create("variant", DummyVariantProperty.class);

    private final String name;

    DummyVariantProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
