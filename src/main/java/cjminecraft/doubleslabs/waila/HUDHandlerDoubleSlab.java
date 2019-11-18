package cjminecraft.doubleslabs.waila;

import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;

public class HUDHandlerDoubleSlab implements IWailaDataProvider {

    public static final HUDHandlerDoubleSlab INSTANCE = new HUDHandlerDoubleSlab();

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() == Registrar.DOUBLE_SLAB)
            return accessor.getBlock().getPickBlock(accessor.getBlockState(), accessor.getMOP(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
        return ItemStack.EMPTY;
    }
}
