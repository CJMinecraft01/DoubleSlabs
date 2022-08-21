package cjminecraft.doubleslabs.platform.services;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.IBlockEntities;
import cjminecraft.doubleslabs.common.init.IBlocks;
import cjminecraft.doubleslabs.common.init.IMenuTypes;

import java.util.List;

public interface IRegistryHelper {

    List<IHorizontalSlabSupport> getHorizontalSlabSupports();
    List<IVerticalSlabSupport> getVerticalSlabSupports();
    List<IContainerSupport> getContainerSupports();

    IBlockEntities getBlockEntities();
    IMenuTypes getMenuTypes();
    IBlocks getBlocks();

}
