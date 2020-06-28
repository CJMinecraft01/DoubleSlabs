package cjminecraft.doubleslabs;

import net.minecraft.block.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    // region Client Server Defaults
    public static ForgeConfigSpec.ConfigValue<ArrayList<String>> DEFAULT_SLAB_BLACKLIST;
    public static ForgeConfigSpec.BooleanValue DEFAULT_REPLACE_SAME_SLAB;
    public static ForgeConfigSpec.BooleanValue DEFAULT_DISABLE_VERTICAL_SLAB_PLACEMENT;
    public static ForgeConfigSpec.BooleanValue DEFAULT_ALTERNATE_VERTICAL_SLAB_PLACEMENT;
    // endregion

    // region Server Options
    public static ForgeConfigSpec.ConfigValue<ArrayList<String>> SLAB_BLACKLIST;
    public static ForgeConfigSpec.BooleanValue REPLACE_SAME_SLAB;
    public static ForgeConfigSpec.BooleanValue DISABLE_VERTICAL_SLAB_PLACEMENT;
    public static ForgeConfigSpec.BooleanValue ALTERNATE_VERTICAL_SLAB_PLACEMENT;
    // endregion

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

        CLIENT_BUILDER.comment("Default Server Settings").push(CATEGORY_GENERAL);

        DEFAULT_SLAB_BLACKLIST = CLIENT_BUILDER.comment("The default list of slab types and variants to ignore when creating double slabs for use when the server config generates", "Example: minecraft:purpur_slab")
                .define("default_slab_blacklist", new ArrayList<>());
        DEFAULT_REPLACE_SAME_SLAB = CLIENT_BUILDER.comment("Whether to use the custom double slab when combining slabs of the same type")
                .define("default_replace_same_slab", true);
        DEFAULT_DISABLE_VERTICAL_SLAB_PLACEMENT = CLIENT_BUILDER.comment("Whether to disable the placement of vertical slabs from regular horizontal slabs when holding shift")
                .define("default_disable_vertical_slab_placement", false);
        DEFAULT_ALTERNATE_VERTICAL_SLAB_PLACEMENT = CLIENT_BUILDER.comment("Whether to use an alternate system when placing vertical slabs")
                .define("default_alternate_vertical_slab_placement", true);

        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static String slabToString(BlockState state) {
        if (state == null)
            return "null";
        if (state.getBlock().getRegistryName() == null)
            return "";
        return state.getBlock().getRegistryName().toString();
    }
}
