package cjminecraft.doubleslabs.common.config;

import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class DSConfig {

    public static final Client CLIENT;
    public static final Server SERVER;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    private static boolean isItemPresent(ForgeConfigSpec.ConfigValue<List<String>> option, Item item) {
        if (item.getRegistryName() == null)
            return false;
        return option.get().stream().anyMatch(entry -> {
            if (entry.startsWith("*"))
                return true;
            if (entry.startsWith("#")) {
                ResourceLocation tagLocation = new ResourceLocation(entry.substring(1));
                ITag<Item> tag = ItemTags.getCollection().get(tagLocation);
                return tag != null && tag.contains(item);
            }
            return entry.equals(item.getRegistryName().toString());
        });
    }

    private static boolean isBlockPresent(ForgeConfigSpec.ConfigValue<List<String>> option, Block block) {
        if (block.getRegistryName() == null)
            return false;
        return option.get().stream().anyMatch(entry -> {
            if (entry.startsWith("*"))
                return true;
            if (entry.startsWith("#")) {
                ResourceLocation tagLocation = new ResourceLocation(entry.substring(1));
                ITag<Block> tag = BlockTags.getCollection().get(tagLocation);
                return tag != null && tag.contains(block);
            }
            return entry.equals(block.getRegistryName().toString());
        });
    }

    public static class Server {
        public final ConfigValue<List<String>> slabBlacklist;
        public final ConfigValue<List<String>> verticalSlabBlacklist;
        public final BooleanValue replaceSameSlab;
        public final BooleanValue disableVerticalSlabPlacement;
        public final ConfigValue<List<String>> verticalSlabCraftingBlacklist;
//        public final BooleanValue disableVerticalSlabItems;

        Server(Builder builder) {
            builder.comment("General Configuration")
                    .push("general");

            slabBlacklist = builder
                    .comment("The list of slabs (or tags) to ignore when creating double slabs",
                            "Example: minecraft:purpur_slab")
                    .translation("doubleslabs.configgui.slabBlacklist")
                    .define("slabBlacklist", new ArrayList<>());

            verticalSlabBlacklist = builder
                    .comment("The list of slabs (or tags) to ignore when creating vertical slabs",
                            "Example: minecraft:purpur_slab",
                            "Example: #minecraft:slabs")
                    .translation("doubleslabs.configgui.verticalSlabBlacklist")
                    .define("verticalSlabBlacklist", new ArrayList<>());

            replaceSameSlab = builder
                    .comment("Whether to use the custom double slab when combining slabs of the same type")
                    .translation("doubleslabs.configgui.replaceSameSlab")
                    .define("replaceSameSlab", true);

            disableVerticalSlabPlacement = builder
                    .comment("Whether to disable the placement of vertical slabs from regular horizontal slabs")
                    .translation("doubleslabs.configgui.disableVerticalSlabPlacement")
                    .define("disableVerticalSlabPlacement", false);

            verticalSlabCraftingBlacklist = builder
                    .comment("The list of slabs (or tags) to ignore when trying to convert between a regular slab and a vertical slab item",
                            "Use the wildcard value * to disable this feature for all slabs")
                    .translation("doubleslabs.configgui.verticalSlabCraftingBlacklist")
                    .define("verticalSlabCraftingBlacklist", new ArrayList<>());

//            disableVerticalSlabItems = builder
//                    .comment("Whether to disable the vertical slab items")
//                    .translation("doubleslabs.configgui.disableVerticalSlabItems")
//                    .worldRestart()
//                    .define("disableVerticalSlabItems", true);

            builder.pop();
        }

        public boolean isBlacklistedHorizontalSlab(Block block) {
            return isBlockPresent(slabBlacklist, block);
        }

        public boolean isBlacklistedVerticalSlab(Block block) {
            return isBlockPresent(verticalSlabBlacklist, block);
        }

        public boolean isBlacklistedCraftingItem(Item item) {
            return isItemPresent(verticalSlabCraftingBlacklist, item);
        }
    }

    public static class Client {
        public final ConfigValue<List<String>> uvlockModelBlacklist;
        public final ConfigValue<List<String>> slabCullBlacklist;
        public final ConfigValue<List<String>> useDoubleSlabModelBlacklist;
        public final EnumValue<VerticalSlabPlacementMethod> verticalSlabPlacementMethod;

        Client(Builder builder) {
            builder.push("Client Only Settings");

            uvlockModelBlacklist = builder
                    .comment("The list of slabs (or tags) which should NOT have uvlock enabled when creating a vertical slab model.",
                            "This often yields better looking results with wooden planks and does not necessarily improve the look of all vertical slabs",
                            "Use the wildcard value * to enable this feature for all slabs")
                    .translation("doubleslabs.configgui.uvlockModels")
                    .define("uvlockModelBlacklist", Lists.newArrayList("minecraft:smooth_stone_slab",
                            "minecraft:sandstone_slab", "minecraft:cut_sandstone_slab", "minecraft:red_sandstone_slab",
                            "minecraft:cut_red_sandstone_slab", "minecraft:prismarine_brick_slab", "minecraft:campfire",
                            "minecraft:soul_campfire", "doubleslabs:raised_campfire", "doubleslabs:raised_soul_campfire",
                            "byg:boric_campfire", "doubleslabs:raised_boric_campfire", "byg:cryptic_campfire",
                            "doubleslabs:raised_cryptic_campfire", "endergetic:ender_campfire",
                            "doubleslabs:raised_ender_campfire"));

            slabCullBlacklist = builder
                    .comment("The list of slabs (or tags) which should not be culled when combined",
                            "Use the wildcard value * to disable this feature for all slabs")
                    .translation("doubleslabs.configgui.slabCullBlacklist")
                    .define("slabCullBlacklist", Lists.newArrayList("#minecraft:campfires"));

            useDoubleSlabModelBlacklist = builder
                    .comment("The list of slabs (or tags) which should not use the double variant model when two of the same slab are combined together",
                            "Use the wildcard value * to disable this feature for all slabs")
                    .translation("doubleslabs.configgui.useDoubleSlabModelBlacklist")
                    .define("useDoubleSlabModelBlacklist", new ArrayList<>());

            builder.pop();

            builder.push("Player Settings (synced with servers)");

            verticalSlabPlacementMethod = builder
                    .comment("Which placement method to use to place vertical slabs",
                            "This is a per user option and can be any of the following values:",
                            "PLACE_WHEN_SNEAKING - Only place vertical slabs when you are sneaking",
                            "DYNAMIC - Place vertical slabs when clicking on the side of a block unless you are sneaking and place vertical slabs when sneaking when looking at the top or bottom face of a block but place regular slabs by default",
                            "KEYBINDING - Only place vertical slabs when the keybinding is active",
                            "ITEM - Only place vertical slabs when holding a vertical slab item (not a horizontal slab item)")
                    .translation("doubleslabs.configgui.verticalSlabPlacementMethod")
                    .defineEnum("verticalSlabPlacementMethod", VerticalSlabPlacementMethod.ITEM);

            builder.pop();
        }

        public boolean uvlock(Block block) {
            return !isBlockPresent(uvlockModelBlacklist, block);
        }

        public boolean uvlock(Item item) {
            return !isItemPresent(uvlockModelBlacklist, item);
        }

        public boolean shouldCull(Block block) {
            return !isBlockPresent(slabCullBlacklist, block);
        }

        public boolean useDoubleSlabModel(Block block) {
            return !isBlockPresent(useDoubleSlabModelBlacklist, block);
        }
    }

}
