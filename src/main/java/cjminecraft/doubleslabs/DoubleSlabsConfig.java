package cjminecraft.doubleslabs;

import net.minecraft.block.Block;
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
    @Config.LangKey("config.doubleslabs.slab_blacklist")
    public static String[] SLAB_BLACKLIST_ARRAY = new String[]{};

    @Config.Ignore
    public static List<String> SLAB_BLACKLIST = new ArrayList<>();

    @Config.Name("Replace Same Slab")
    @Config.Comment({"Whether to use the custom double slab when combining slabs of the same type"})
    @Config.LangKey("config.doubleslabs.replace_same_slab")
    public static boolean REPLACE_SAME_SLAB = true;

    @Config.Name("Disable Vertical Slab Placement")
    @Config.Comment({"Whether to disable the placement of vertical slabs from regular horizontal slabs when holding shift"})
    @Config.LangKey("config.doubleslabs.disable_vertical_slab_placement")
    public static boolean DISABLE_VERTICAL_SLAB_PLACEMENT = false;

    @Config.Name("Alternate Vertical Slab Placement")
    @Config.Comment({"Whether to use an alternate system when placing vertical slabs"})
    @Config.LangKey("config.doubleslabs.alternate_vertical_slab_placement")
    public static boolean ALTERNATE_VERTICAL_SLAB_PLACEMENT = true;


    public static String slabToString(IBlockState state) {
        if (state == null)
            return "null";
        Block block = state.getBlock();
        if (state.getBlock().getRegistryName() == null)
            return "";
        if (block instanceof BlockSlab) {
            BlockSlab slab = (BlockSlab) block;
            try {
                //noinspection ConstantConditions
                if (slab.getVariantProperty() == BlockSlab.HALF || slab.getVariantProperty() == null)
                    return state.getBlock().getRegistryName().toString();
                return state.getBlock().getRegistryName().toString() + (slab.getVariantProperty().getAllowedValues().size() == 1 ? "" : "#" + slab.getVariantProperty().getName() + "=" + state.getValue(slab.getVariantProperty()).toString());
            } catch (Exception e) {
                // From what I've seen, any error can crop up so might as well catch them all :(
                return state.getBlock().getRegistryName().toString();
            }
        }
        return state.getBlock().getRegistryName().toString();
    }

}
