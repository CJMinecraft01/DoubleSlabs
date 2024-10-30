package cjminecraft.doubleslabs.common.platform.services;

public interface IPlatformHelper {

    IPlatformPluginHelper getPluginHelper();

    IPlatformBlockFactory getBlockFactory();

    IPlatformBlockEntityFactory getBlockEntityFactory();

}