package cjminecraft.doubleslabs.common.util.registry;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SimpleRegistrar<T extends IForgeRegistryEntry<T>> implements IRegistrar<T> {

    private final String modid;
    private final List<T> objects = Lists.newArrayList();
    private final Class<T> superType;

    public SimpleRegistrar(IForgeRegistry<T> reg, String modid) {
        this.modid = modid;
        this.superType = reg.getRegistrySuperType();
    }

    public <V extends T> V register(String name, V object) {
        object.setRegistryName(new ResourceLocation(this.modid, name));
        this.objects.add(object);
        return object;
    }

    public void register(EventBus bus) {
        bus.register(this);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        if (this.superType != event.getRegistry().getRegistrySuperType()) return;
        objects.forEach(o -> event.getRegistry().register((Block) o));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        if (this.superType != event.getRegistry().getRegistrySuperType()) return;
        objects.forEach(o -> event.getRegistry().register((Item) o));
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        if (this.superType != event.getRegistry().getRegistrySuperType()) return;
        objects.forEach(o -> event.getRegistry().register((IRecipe) o));
    }
}
