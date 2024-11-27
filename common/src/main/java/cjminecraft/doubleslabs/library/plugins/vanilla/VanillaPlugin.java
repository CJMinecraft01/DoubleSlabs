package cjminecraft.doubleslabs.library.plugins.vanilla;

import cjminecraft.doubleslabs.api.DoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.registration.ISlabHelperRegistration;
import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.library.plugins.vanilla.helpers.MinecraftSlabHelper;
import cjminecraft.doubleslabs.library.plugins.vanilla.helpers.WeatheringCopperTickingSlabHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

@DoubleSlabsPlugin
public class VanillaPlugin implements IDoubleSlabsPlugin {
    @Override
    public ResourceLocation getPluginId() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "minecraft");
    }

    @Override
    public void registerSlabHelpers(ISlabHelperRegistration registration) {
        registration.addHorizontalHelper(new MinecraftSlabHelper());

        registration.registerTickingSlabHelper(new WeatheringCopperTickingSlabHelper(),
                Blocks.CUT_COPPER_SLAB,
                Blocks.EXPOSED_CUT_COPPER_SLAB,
                Blocks.WEATHERED_CUT_COPPER_SLAB,
                Blocks.OXIDIZED_CUT_COPPER_SLAB);
    }
}
