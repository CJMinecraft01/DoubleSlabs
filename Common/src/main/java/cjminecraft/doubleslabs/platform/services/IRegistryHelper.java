package cjminecraft.doubleslabs.platform.services;

import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface IRegistryHelper {

    List<IHorizontalSlabSupport> getHorizontalSlabSupports();
    List<IVerticalSlabSupport> getVerticalSlabSupports();
    List<IContainerSupport> getContainerSupports();

    IBlockEntities getBlockEntities();
    IMenuTypes getMenuTypes();
    IBlocks getBlocks();
    IItems getItems();
    ITabs getTabs();

    ResourceLocation getKey(Item item);
    ResourceLocation getKey(Block block);

    TagKey<Item> getItemTag(ResourceLocation location);
    TagKey<Block> getBlockTag(ResourceLocation location);

    boolean isIn(TagKey<Item> tag, Item item);
    boolean isIn(TagKey<Block> tag, Block block);

}
