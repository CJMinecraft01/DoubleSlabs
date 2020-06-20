package cjminecraft.doubleslabs;

import net.minecraft.block.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.ConfigValue<ArrayList<String>> SLAB_BLACKLIST;
    public static ForgeConfigSpec.BooleanValue REPLACE_SAME_SLAB;
    public static ForgeConfigSpec.BooleanValue DISABLE_VERTICAL_SLAB_PLACEMENT;
    public static ForgeConfigSpec.BooleanValue ALTERNATE_VERTICAL_SLAB_PLACEMENT;

    static {
        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        SLAB_BLACKLIST = SERVER_BUILDER.comment("The list of slab types and variants to ignore when creating double slabs", "Example: minecraft:purpur_slab")
                .define("slab_blacklist", new ArrayList<>());
        REPLACE_SAME_SLAB = SERVER_BUILDER.comment("Whether to use the custom double slab when combining slabs of the same type")
                .define("replace_same_slab", true);
        DISABLE_VERTICAL_SLAB_PLACEMENT = SERVER_BUILDER.comment("Whether to disable the placement of vertical slabs from regular horizontal slabs when holding shift")
            .define("disable_vertical_slab_placement", false);
        ALTERNATE_VERTICAL_SLAB_PLACEMENT = SERVER_BUILDER.comment("Whether to use an alternate system when placing vertical slabs")
                .define("alternate_vertical_slab_placement", true);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static String slabToString(BlockState state) {
        if (state == null)
            return "null";
        if (state.getBlock().getRegistryName() == null)
            return "";
        return state.getBlock().getRegistryName().toString();
    }
}
