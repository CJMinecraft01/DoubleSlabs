package cjminecraft.doubleslabs;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.List;

@Config(modid = DoubleSlabs.MODID)
public class DoubleSlabsConfig {

    @Config.Name("Slab Blacklist")
    @Config.Comment({"The list of slab types and variants to ignore when creating double slabs",
            "Example: minecraft:purpur_slab", "Example: minecraft:stone_slab#variant=cobblestone"})
    @Config.LangKey("config.double_slabs.slab_blacklist")
    public static String[] slabBlacklistArray = new String[]{};

    @Config.Ignore
    public static List<String> slabBlacklist = new ArrayList<>();

    public static String slabToString(IBlockState state) {
        BlockSlab slab = (BlockSlab) state.getBlock();
        if (state.getBlock().getRegistryName() == null)
            return "";
        if (slab.getVariantProperty() == BlockSlab.HALF)
            return state.getBlock().getRegistryName().toString();
        return state.getBlock().getRegistryName().toString() + (slab.getVariantProperty().getAllowedValues().size() == 1 ? "" : "#" + slab.getVariantProperty().getName() + "=" + state.getValue(slab.getVariantProperty()).toString());
    }

}
