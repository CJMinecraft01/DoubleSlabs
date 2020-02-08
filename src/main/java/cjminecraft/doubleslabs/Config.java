package cjminecraft.doubleslabs;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<ArrayList<String>> SLAB_BLACKLIST;

    static {
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        SLAB_BLACKLIST = COMMON_BUILDER.comment("The list of slab types and variants to ignore when creating double slabs", "Example: minecraft:purpur_slab")
                .define("slab_blacklist", new ArrayList<>());

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig config = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        config.load();
        spec.setConfig(config);
    }

    public static String slabToString(BlockState state) {
        if (state.getBlock().getRegistryName() == null)
            return "";
        return state.getBlock().getRegistryName().toString();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading event) {

    }
}
