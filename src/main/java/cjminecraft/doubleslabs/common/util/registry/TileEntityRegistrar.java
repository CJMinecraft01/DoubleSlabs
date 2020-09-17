package cjminecraft.doubleslabs.common.util.registry;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TileEntityRegistrar {

    private final String modid;
    private List<Pair<ResourceLocation, Class<? extends TileEntity>>> tiles = Lists.newArrayList();

    public TileEntityRegistrar(String modid) {
        this.modid = modid;
    }

    public <V extends TileEntity> Class<V> register(String name, Class<V> object) {
        this.tiles.add(Pair.of(new ResourceLocation(this.modid, name), object));
        return object;
    }

    public void register(EventBus bus) {
        bus.register(this);
    }

    @SubscribeEvent
    public void registerTiles(RegistryEvent.Register<Block> event) {
        this.tiles.forEach(pair -> {
            GameRegistry.registerTileEntity(pair.getRight(), pair.getLeft());
        });
    }
}
