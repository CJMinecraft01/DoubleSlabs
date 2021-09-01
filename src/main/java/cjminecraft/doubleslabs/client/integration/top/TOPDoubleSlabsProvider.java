package cjminecraft.doubleslabs.client.integration.top;

import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.function.Function;

import static cjminecraft.doubleslabs.common.util.Utils.getModName;
import static mcjty.theoneprobe.api.TextStyleClass.MODNAME;

public class TOPDoubleSlabsProvider implements IBlockDisplayOverride, Function<ITheOneProbe, Void> {

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, PlayerEntity player, World world, BlockState state, IProbeHitData data) {
        if (state.getBlock() instanceof DynamicSlabBlock) {
            ItemStack pickBlock = data.getPickBlock();
            if (!pickBlock.isEmpty()) {
                if (pickBlock.getItem() instanceof VerticalSlabItem)
                    pickBlock = VerticalSlabItem.getStack(pickBlock);
                if (pickBlock.getItem() instanceof BlockItem) {
                    final String modName = getModName(((BlockItem) pickBlock.getItem()).getBlock());
                    info.horizontal()
                            .item(pickBlock)
                            .vertical()
                            .itemLabel(pickBlock)
                            .text(CompoundText.create().style(MODNAME).text(modName));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Void apply(ITheOneProbe probe) {
        probe.registerBlockDisplayOverride(this);
        return null;
    }
}
