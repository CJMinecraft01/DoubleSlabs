package cjminecraft.doubleslabs;

import net.minecraft.block.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

public class Config {

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<ArrayList<String>> SLAB_BLACKLIST;
    public static ForgeConfigSpec.BooleanValue REPLACE_SAME_SLAB;

    static {
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        SLAB_BLACKLIST = COMMON_BUILDER.comment("The list of slab types and variants to ignore when creating double slabs", "Example: minecraft:purpur_slab")
                .define("slab_blacklist", new ArrayList<>());
        REPLACE_SAME_SLAB = COMMON_BUILDER.comment("Whether to use the custom double slab when combining slabs of the same type")
                .define("replace_same_slab", true);

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static String slabToString(BlockState state) {
        if (state == null)
            return "null";
        if (state.getBlock().getRegistryName() == null)
            return "";
        return state.getBlock().getRegistryName().toString();
    }

}
