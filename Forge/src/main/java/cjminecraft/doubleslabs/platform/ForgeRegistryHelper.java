package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.*;
import cjminecraft.doubleslabs.forge.common.init.*;
import cjminecraft.doubleslabs.platform.services.IRegistryHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;

public class ForgeRegistryHelper implements IRegistryHelper {

    @Override
    public ResourceLocation getKey(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    @Override
    public ResourceLocation getKey(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    @Override
    public TagKey<Item> getItemTag(ResourceLocation location) {
        return ForgeRegistries.ITEMS.tags().createTagKey(location);
    }

    @Override
    public TagKey<Block> getBlockTag(ResourceLocation location) {
        return ForgeRegistries.BLOCKS.tags().createTagKey(location);
    }

    @Override
    public boolean isIn(TagKey<Item> tag, Item item) {
        return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
    }

    @Override
    public boolean isIn(TagKey<Block> tag, Block block) {
        return ForgeRegistries.BLOCKS.tags().getTag(tag).contains(block);
    }

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
