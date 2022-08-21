package cjminecraft.doubleslabs.common.config;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.config.api.BooleanValue;
import cjminecraft.doubleslabs.common.config.api.ConfigValue;
import cjminecraft.doubleslabs.common.config.api.EnumValue;
import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;
import cjminecraft.doubleslabs.platform.Services;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DSConfig {

    private static boolean isItemPresent(List<String> option, Item item) {
        ResourceLocation name = Services.REGISTRIES.getKey(item);
        if (name == null)
            return false;
        return option.stream().anyMatch(entry -> {
            if (entry.startsWith("*"))
                return true;
            if (entry.startsWith("#")) {
                ResourceLocation tagLocation = new ResourceLocation(entry.substring(1));
                return Services.REGISTRIES.isIn(Services.REGISTRIES.getItemTag(tagLocation), item);
            }
            return entry.equals(name.toString());
        });
    }

    private static boolean isBlockPresent(List<String> option, Block block) {
        ResourceLocation name = Services.REGISTRIES.getKey(block);
        if (name == null)
            return false;
        return option.stream().anyMatch(entry -> {
            if (entry.startsWith("*"))
                return true;
            if (entry.startsWith("#")) {
                ResourceLocation tagLocation = new ResourceLocation(entry.substring(1));
                return Services.REGISTRIES.isIn(Services.REGISTRIES.getBlockTag(tagLocation), block);
            }
            return entry.equals(name.toString());
        });
    }

    public static class Common {
        @Option(comment = {"The list of slabs (or tags) to ignore when creating double slabs",
                "Example: minecraft:purpur_slab"},
                translation = "doubleslabs.configgui.slabBlacklist")
        public static List<String> slabBlacklist = Lists.newArrayList();
        @Option(comment = {"The list of slabs (or tags) to ignore when creating vertical slabs",
                "Example: minecraft:purpur_slab",
                "Example: #minecraft:slabs"},
                translation = "doubleslabs.configgui.verticalSlabBlacklist")
        public static List<String> verticalSlabBlacklist = Lists.newArrayList();
        @Option(comment = {"Whether to use the custom double slab when combining slabs of the same type"},
                translation = "doubleslabs.configgui.replaceSameSlab")
        public static boolean replaceSameSlab = true;
        @Option(comment = {"Whether to disable the placement of vertical slabs from regular horizontal slabs"},
                translation = "doubleslabs.configgui.disableVerticalSlabPlacement")
        public static boolean disableVerticalSlabPlacement = false;
        @Option(comment = {"The list of slabs (or tags) to ignore when trying to convert between a regular slab and a vertical slab item",
                "Use the wildcard value * to disable this feature for all slabs"},
                translation = "doubleslabs.configgui.verticalSlabCraftingBlacklist")
        public static List<String> verticalSlabCraftingBlacklist = Lists.newArrayList();

        public static boolean isBlacklistedHorizontalSlab(Block block) {
            return isBlockPresent(slabBlacklist, block);
        }

        public static boolean isBlacklistedVerticalSlab(Block block) {
            return isBlockPresent(verticalSlabBlacklist, block);
        }

        public static boolean isBlacklistedCraftingItem(Item item) {
            IHorizontalSlabSupport support = SlabSupport.getHorizontalSlabSupport(item);
            return isItemPresent(verticalSlabCraftingBlacklist, item) || (support != null && !support.canCraft(item));
        }
    }

    public static class Client {
        @Option(category = "client",
                comment = {"The list of slabs (or tags) which should NOT have uvlock enabled when creating a vertical slab model.",
                        "This often yields better looking results with wooden planks and does not necessarily improve the look of all vertical slabs",
                        "Use the wildcard value * to enable this feature for all slabs"},
                translation = "doubleslabs.configgui.uvlockModels")
        public static List<String> uvlockModelBlacklist = Lists.newArrayList("minecraft:smooth_stone_slab",
                "minecraft:sandstone_slab", "minecraft:cut_sandstone_slab", "minecraft:red_sandstone_slab",
                "minecraft:cut_red_sandstone_slab", "minecraft:prismarine_brick_slab", "minecraft:campfire",
                "minecraft:soul_campfire", "doubleslabs:raised_campfire", "doubleslabs:raised_soul_campfire",
                "byg:boric_campfire", "doubleslabs:raised_boric_campfire", "byg:cryptic_campfire",
                "doubleslabs:raised_cryptic_campfire", "endergetic:ender_campfire",
                "doubleslabs:raised_ender_campfire");
        @Option(category = "client",
                comment = {"The list of slabs (or tags) which should not be culled when combined",
                        "Use the wildcard value * to disable this feature for all slabs"},
                translation = "doubleslabs.configgui.slabCullBlacklist")
        public static List<String> slabCullBlacklist = Lists.newArrayList("#minecraft:campfires");
        @Option(category = "client",
                comment = {"The list of slabs (or tags) which should not use the double variant model when two of the same slab are combined together",
                        "Use the wildcard value * to disable this feature for all slabs"},
                translation = "doubleslabs.configgui.useDoubleSlabModelBlacklist")
        public static List<String> useDoubleSlabModelBlacklist = Lists.newArrayList();
        @Option(category = "player",
                comment = {"Which placement method to use to place vertical slabs",
                        "This is a per user option and can be any of the following values:",
                        "PLACE_WHEN_SNEAKING - Only place vertical slabs when you are sneaking",
                        "DYNAMIC - Place vertical slabs when clicking on the side of a block unless you are sneaking and place vertical slabs when sneaking when looking at the top or bottom face of a block but place regular slabs by default"},
                translation = "doubleslabs.configgui.verticalSlabPlacementMethod")
        public static VerticalSlabPlacementMethod verticalSlabPlacementMethod = VerticalSlabPlacementMethod.DYNAMIC;

        public static boolean uvlock(Block block) {
            return !isBlockPresent(uvlockModelBlacklist, block);
        }

        public static boolean uvlock(Item item) {
            return !isItemPresent(uvlockModelBlacklist, item);
        }

        public static boolean shouldCull(Block block) {
            return !isBlockPresent(slabCullBlacklist, block);
        }

        public static boolean useDoubleSlabModel(Block block) {
            return !isBlockPresent(useDoubleSlabModelBlacklist, block);
        }
    }

}
