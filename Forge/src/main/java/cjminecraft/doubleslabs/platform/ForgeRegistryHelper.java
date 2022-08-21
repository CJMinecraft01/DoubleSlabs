package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.*;
import cjminecraft.doubleslabs.forge.common.init.*;
import cjminecraft.doubleslabs.platform.services.IRegistryHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;

public class ForgeRegistryHelper implements IRegistryHelper {

    @Override
    public List<IHorizontalSlabSupport> getHorizontalSlabSupports() {
        return DSRegistries.HORIZONTAL_SLAB_SUPPORTS.getEntries().stream().filter(o -> ModList.get().isLoaded(Objects.requireNonNull(o.getKey()).location().getNamespace())).map(RegistryObject::get).toList();
    }

    @Override
    public List<IVerticalSlabSupport> getVerticalSlabSupports() {
        return DSRegistries.VERTICAL_SLAB_SUPPORTS.getEntries().stream().filter(o -> ModList.get().isLoaded(Objects.requireNonNull(o.getKey()).location().getNamespace())).map(RegistryObject::get).toList();
    }

    @Override
    public List<IContainerSupport> getContainerSupports() {
        return DSRegistries.CONTAINER_SUPPORTS.getEntries().stream().filter(o -> ModList.get().isLoaded(Objects.requireNonNull(o.getKey()).location().getNamespace())).map(RegistryObject::get).toList();
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
