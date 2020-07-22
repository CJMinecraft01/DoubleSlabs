package cjminecraft.doubleslabs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @Config.Name("Lazy Vertical Slab Models")
    @Config.Comment({"The list of slabs which should use the lazy model rendering technique", "Lazy model rendering does not physically rotate the original slab model, but applies the same texture to a default vertical slab model", "This often yields better looking results with wooden planks and does not necessarily improve the look of all vertical slabs"})
    @Config.LangKey("config.doubleslabs.lazy_vertical_slabs")
    public static String[] LAZY_VERTICAL_SLABS_ARRAY = new String[]{
            "minecraft:wooden_slab#variant=oak",
            "minecraft:wooden_slab#variant=spruce",
            "minecraft:wooden_slab#variant=birch",
            "minecraft:wooden_slab#variant=jungle",
            "minecraft:wooden_slab#variant=acacia",
            "minecraft:wooden_slab#variant=dark_oak"
    };

    @Config.Ignore
    public static List<String> LAZY_VERTICAL_SLABS = new ArrayList<>();

    @Config.Name("Slab Cull Blacklist")
    @Config.Comment({"The list of slabs which should not be culled when combined"})
    @Config.LangKey("config.doubleslabs.slab_cull_blacklist")
    public static String[] SLAB_CULL_BLACKLIST = new String[]{};

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

    public static boolean useLazyModel(IBlockState state) {
        String slabString = slabToString(state);
        if (slabString.length() == 0)
            return false;
        for (String entry : LAZY_VERTICAL_SLABS_ARRAY)
            if (entry.equals(slabString))
                return true;
        return false;
//        return LAZY_VERTICAL_SLABS.stream().anyMatch(entry -> entry.equals(slabString));
    }

    public static boolean shouldCull(IBlockState state) {
        String slabString = slabToString(state);
        if (slabString.length() == 0)
            return false;
        for (String entry : SLAB_CULL_BLACKLIST)
            if (entry.equals(slabString))
                return true;
        return false;
    }

}
