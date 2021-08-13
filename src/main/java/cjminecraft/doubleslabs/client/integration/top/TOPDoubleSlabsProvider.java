package cjminecraft.doubleslabs.client.integration.top;

import cjminecraft.doubleslabs.common.blocks.DynamicSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

import static mcjty.theoneprobe.api.TextStyleClass.MODNAME;

public class TOPDoubleSlabsProvider implements IBlockDisplayOverride, Function<ITheOneProbe, Void> {

    private static String getModName(IForgeRegistryEntry<?> entry) {
        ResourceLocation registryName = entry.getRegistryName();
        String modId = registryName == null ? "minecraft" : registryName.getNamespace();
        return ModList.get().getModContainerById(modId)
                .map(mod -> mod.getModInfo().getDisplayName())
                .orElseGet(() -> StringUtils.capitalize(modId));
    }

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo info, PlayerEntity player, World world, BlockState state, IProbeHitData data) {
        if (state.getBlock() instanceof DynamicSlabBlock) {
            ItemStack pickBlock = data.getPickBlock();
            if (!pickBlock.isEmpty() && pickBlock.getItem() instanceof BlockItem) {
                info.horizontal()
                        .item(pickBlock)
                        .vertical()
                        .itemLabel(pickBlock)
                        .text(CompoundText.create().style(MODNAME).text(getModName(((BlockItem) pickBlock.getItem()).getBlock())));
                return true;
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
