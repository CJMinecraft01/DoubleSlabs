package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.common.init.IBlockEntities;
import cjminecraft.doubleslabs.forge.common.init.DSBlockEntities;
import cjminecraft.doubleslabs.platform.services.IRegistryHelper;

public class ForgeRegistryHelper implements IRegistryHelper {
    @Override
    public IBlockEntities getBlockEntities() {
        return DSBlockEntities.INSTANCE;
    }
}
