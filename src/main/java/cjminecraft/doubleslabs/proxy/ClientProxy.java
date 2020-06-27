package cjminecraft.doubleslabs.proxy;

import cjminecraft.doubleslabs.Utils;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        IProxy.super.preInit();
//        ModelLoader.setCustomStateMapper(Registrar.VERTICAL_SLAB, new StateMap.Builder().ignore(BlockVerticalSlab.FACING, BlockVerticalSlab.DOUBLE).build());
    }

    @Override
    public void postInit() {
        Utils.checkOptiFineInstalled();
    }
}
