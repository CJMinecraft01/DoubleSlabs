package cjminecraft.doubleslabs.proxy;

import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.client.render.TileEntityRendererDoubleSlab;
import cjminecraft.doubleslabs.client.render.TileEntityRendererVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        IProxy.super.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVerticalSlab.class, new TileEntityRendererVerticalSlab());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDoubleSlab.class, new TileEntityRendererDoubleSlab());
//        ModelLoader.setCustomStateMapper(Registrar.VERTICAL_SLAB, new StateMap.Builder().ignore(BlockVerticalSlab.FACING, BlockVerticalSlab.DOUBLE).build());
    }

    @Override
    public void postInit() {
        Utils.checkOptiFineInstalled();
    }
}
