package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.*;
import cjminecraft.doubleslabs.fabric.common.init.*;
import cjminecraft.doubleslabs.platform.services.IRegistryHelper;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public ResourceLocation getKey(Item item) {
        return Registry.ITEM.getKey(item);
    }

    @Override
    public ResourceLocation getKey(Block block) {
        return Registry.BLOCK.getKey(block);
    }

    @Override
    public TagKey<Item> getItemTag(ResourceLocation location) {
        return TagKey.create(Registry.ITEM_REGISTRY, location);
    }

    @Override
    public TagKey<Block> getBlockTag(ResourceLocation location) {
        return TagKey.create(Registry.BLOCK_REGISTRY, location);
    }

    @Override
    public boolean isIn(TagKey<Item> tag, Item item) {
        return TagUtil.isIn(tag, item);
    }

    @Override
    public boolean isIn(TagKey<Block> tag, Block block) {
        return TagUtil.isIn(tag, block);
    }

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
