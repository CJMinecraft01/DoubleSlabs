package cjminecraft.doubleslabs.api.containers;

import cjminecraft.doubleslabs.api.IBlockInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

import javax.annotation.Nullable;

@FunctionalInterface
public interface ISlabContainerProvider {
    @Nullable
    Container createMenu(int windowId, IBlockInfo block, PlayerInventory playerInventory, PlayerEntity entity);
}
