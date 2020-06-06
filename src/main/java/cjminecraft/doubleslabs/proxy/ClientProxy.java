package cjminecraft.doubleslabs.proxy;

import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        ModelLoader.setCustomStateMapper(Registrar.VERTICAL_SLAB, new StateMap.Builder().ignore(BlockVerticalSlab.FACING, BlockVerticalSlab.DOUBLE).build());
    }
}
