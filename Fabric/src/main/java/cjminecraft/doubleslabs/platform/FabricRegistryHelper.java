package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.*;
import cjminecraft.doubleslabs.fabric.common.init.*;
import cjminecraft.doubleslabs.platform.services.IRegistryHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.Map;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public List<IHorizontalSlabSupport> getHorizontalSlabSupports() {
        return DSRegistries.HORIZONTAL_SLAB_SUPPORTS.entrySet().stream().filter(e -> FabricLoader.getInstance().isModLoaded(e.getKey().location().getNamespace())).map(Map.Entry::getValue).toList();
    }

    @Override
    public List<IVerticalSlabSupport> getVerticalSlabSupports() {
        return DSRegistries.VERTICAL_SLAB_SUPPORTS.entrySet().stream().filter(e -> FabricLoader.getInstance().isModLoaded(e.getKey().location().getNamespace())).map(Map.Entry::getValue).toList();
    }

    @Override
    public List<IContainerSupport> getContainerSupports() {
        return DSRegistries.CONTAINER_SUPPORTS.entrySet().stream().filter(e -> FabricLoader.getInstance().isModLoaded(e.getKey().location().getNamespace())).map(Map.Entry::getValue).toList();
    }

    @Override
    public IBlockEntities getBlockEntities() {
        return DSBlockEntities.INSTANCE;
    }

    @Override
    public IMenuTypes getMenuTypes() {
        return DSMenuTypes.INSTANCE;
    }

    @Override
    public IBlocks getBlocks() {
        return DSBlocks.INSTANCE;
    }

    @Override
    public IItems getItems() {
        return DSItems.INSTANCE;
    }

    @Override
    public ITabs getTabs() {
        return DSTabs.INSTANCE;
    }
}
