package cjminecraft.doubleslabs.common.config;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DSConfig {

    public static final Client CLIENT = new Client();
    public static final Server SERVER = new Server();
    private static Configuration config = null;

    public static void load(File configFile) {
        config = new Configuration(configFile);
        syncFromFiles();
    }

    private static void sync(boolean load, boolean read) {
        if (load)
            config.load();

        try {
            SERVER.sync(read);
            CLIENT.sync(read);
        } catch (IllegalArgumentException e) {
            DoubleSlabs.LOGGER.error("Error when syncing config. Error: " + e.getLocalizedMessage());
        }

        if (config.hasChanged())
            config.save();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static void syncFromFiles() {
        sync(true, true);
    }

    public static void syncFromGui() {
        sync(false, true);
    }

    public static void syncFromFields() {
        sync(false, false);
    }

    private static boolean isItemPresent(List<String> option, Item item) {
        if (item.getRegistryName() == null)
            return false;
        return option.stream().anyMatch(entry -> {
            if (entry.startsWith("*"))
                return true;
            return entry.equals(item.getRegistryName().toString());
        });
    }

    private static String slabToString(IBlockState state) {
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

    private static boolean isBlockPresent(List<String> option, IBlockState state) {
        if (state.getBlock().getRegistryName() == null)
            return false;
        String stringState = slabToString(state);
        return option.stream().anyMatch(entry -> {
            if (entry.startsWith("*"))
                return true;
            return entry.equals(stringState);
        });
    }

    public static class Server {
        public List<String> slabBlacklist;
        public List<String> verticalSlabBlacklist;
        public boolean replaceSameSlab;
        public boolean disableVerticalSlabPlacement;
        public List<String> verticalSlabCraftingBlacklist;

        public boolean isBlacklistedHorizontalSlab(IBlockState state) {
            return isBlockPresent(slabBlacklist, state);
        }

        public boolean isBlacklistedVerticalSlab(IBlockState state) {
            return isBlockPresent(verticalSlabBlacklist, state);
        }

        public boolean isBlacklistedCraftingItem(Item item) {
            return isItemPresent(verticalSlabCraftingBlacklist, item);
        }

        public void sync(boolean read) {
            Property propertySlabBlacklist = config.get(Configuration.CATEGORY_GENERAL, "slabBlacklist", new String[0]);
            propertySlabBlacklist.setComment("The list of slabs to ignore when creating double slabs\nExample: minecraft:purpur_slab");
            propertySlabBlacklist.setLanguageKey("doubleslabs.configgui.slabBlacklist");

            Property propertyVerticalSlabBlacklist = config.get(Configuration.CATEGORY_GENERAL, "verticalSlabBlacklist", new String[0]);
            propertyVerticalSlabBlacklist.setComment("The list of slabs to ignore when creating vertical slabs\nExample: minecraft:purpur_slab");
            propertyVerticalSlabBlacklist.setLanguageKey("doubleslabs.configgui.verticalSlabBlacklist");

            Property propertyReplaceSameSlab = config.get(Configuration.CATEGORY_GENERAL, "replaceSameSlab", true);
            propertyReplaceSameSlab.setComment("Whether to use the custom double slab when combining slabs of the same type");
            propertyReplaceSameSlab.setLanguageKey("doubleslabs.configgui.replaceSameSlab");

            Property propertyDisableVerticalSlabPlacement = config.get(Configuration.CATEGORY_GENERAL, "disableVerticalSlabPlacement", false);
            propertyDisableVerticalSlabPlacement.setComment("Whether to disable the placement of vertical slabs from regular horizontal slabs");
            propertyDisableVerticalSlabPlacement.setLanguageKey("doubleslabs.configgui.disableVerticalSlabPlacement");

            Property propertyVerticalSlabCraftingBlacklist = config.get(Configuration.CATEGORY_GENERAL, "verticalSlabCraftingBlacklist", new String[0]);
            propertyVerticalSlabCraftingBlacklist.setComment(
                    "The list of slabs to ignore when trying to convert between a regular slab and a vertical slab item\n" +
                            "Use the wildcard value * to disable this feature for all slabs");
            propertyVerticalSlabCraftingBlacklist.setLanguageKey("doubleslabs.configgui.verticalSlabCraftingBlacklist");

            config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL,
                    Lists.newArrayList(propertySlabBlacklist.getName(),
                            propertyVerticalSlabBlacklist.getName(),
                            propertyReplaceSameSlab.getName(),
                            propertyDisableVerticalSlabPlacement.getName(),
                            propertyVerticalSlabCraftingBlacklist.getName()));

            if (read) {
                this.slabBlacklist = Lists.newArrayList(propertySlabBlacklist.getStringList());
                this.verticalSlabBlacklist = Lists.newArrayList(propertyVerticalSlabBlacklist.getStringList());
                this.replaceSameSlab = propertyReplaceSameSlab.getBoolean();
                this.disableVerticalSlabPlacement = propertyDisableVerticalSlabPlacement.getBoolean();
                this.verticalSlabCraftingBlacklist = Lists.newArrayList(propertyVerticalSlabCraftingBlacklist.getStringList());
            }

            propertySlabBlacklist.set(this.slabBlacklist.toArray(new String[0]));
            propertyVerticalSlabBlacklist.set(this.verticalSlabBlacklist.toArray(new String[0]));
            propertyReplaceSameSlab.set(this.replaceSameSlab);
            propertyDisableVerticalSlabPlacement.set(this.disableVerticalSlabPlacement);
            propertyVerticalSlabCraftingBlacklist.set(this.verticalSlabCraftingBlacklist.toArray(new String[0]));
        }
    }

    public static class Client {
        public List<String> lazyVerticalSlabModels;
        public List<String> slabCullBlacklist;
        public List<String> useDoubleSlabModelBlacklist;
        public VerticalSlabPlacementMethod verticalSlabPlacementMethod;

        public void sync(boolean read) {
            Property propertyLazyVerticalSlabModels = config.get(Configuration.CATEGORY_CLIENT, "lazyVerticalSlabModels", new String[]{
                    "minecraft:wooden_slab#variant=oak",
                    "minecraft:wooden_slab#variant=spruce",
                    "minecraft:wooden_slab#variant=birch",
                    "minecraft:wooden_slab#variant=jungle",
                    "minecraft:wooden_slab#variant=acacia",
                    "minecraft:wooden_slab#variant=dark_oak"});
            propertyLazyVerticalSlabModels.setComment(
                    "The list of slabs which should use the lazy model rendering technique\n" +
                            "Lazy model rendering does not physically rotate the original slab model, but applies the same texture to a default vertical slab model\n" +
                            "This often yields better looking results with wooden planks and does not necessarily improve the look of all vertical slabs\n" +
                            "Use the wildcard value * to enable this feature for all slabs");
            propertyLazyVerticalSlabModels.setLanguageKey("doubleslabs.configgui.lazyVerticalSlabModels");

            Property propertySlabCullBlacklist = config.get(Configuration.CATEGORY_CLIENT, "slabCullBlacklist", new String[0]);
            propertySlabCullBlacklist.setComment(
                    "The list of slabs which should not be culled when combined\n" +
                            "Use the wildcard value * to disable this feature for all slabs");
            propertySlabCullBlacklist.setLanguageKey("doubleslabs.configgui.slabCullBlacklist");

            Property propertyUseDoubleSlabModelBlacklist = config.get(Configuration.CATEGORY_CLIENT, "useDoubleSlabModelBlacklist", new String[0]);
            propertyUseDoubleSlabModelBlacklist.setComment(
                    "The list of slabs which should not use the double variant model when two of the same slab are combined together\n" +
                            "Use the wildcard value * to disable this feature for all slabs");
            propertyUseDoubleSlabModelBlacklist.setLanguageKey("doubleslabs.configgui.useDoubleSlabModelBlacklist");

            Property propertyVerticalSlabPlacementMethod = config.get(Configuration.CATEGORY_CLIENT, "verticalSlabPlacementMethod", VerticalSlabPlacementMethod.DYNAMIC.name());
            propertyVerticalSlabPlacementMethod.setComment(
                    "Which placement method to use to place vertical slabs\n" +
                            "This is a per user option and can be any of the following values:\n" +
                            "PLACE_WHEN_SNEAKING - Only place vertical slabs when you are sneaking\n" +
                            "DYNAMIC - Place vertical slabs when clicking on the side of a block unless you are sneaking and place vertical slabs when sneaking when looking at the top or bottom face of a block but place regular slabs by default");
            propertyVerticalSlabPlacementMethod.setValidValues(Arrays.stream(VerticalSlabPlacementMethod.values()).map(Enum::name).toArray(String[]::new));
            propertyVerticalSlabPlacementMethod.setLanguageKey("doubleslabs.configgui.verticalSlabPlacementMethod");

            config.setCategoryPropertyOrder(Configuration.CATEGORY_CLIENT,
                    Lists.newArrayList(propertyLazyVerticalSlabModels.getName(),
                            propertySlabCullBlacklist.getName(),
                            propertyUseDoubleSlabModelBlacklist.getName(),
                            propertyVerticalSlabPlacementMethod.getName()));

            if (read) {
                this.lazyVerticalSlabModels = Lists.newArrayList(propertyLazyVerticalSlabModels.getStringList());
                this.slabCullBlacklist = Lists.newArrayList(propertySlabCullBlacklist.getStringList());
                this.useDoubleSlabModelBlacklist = Lists.newArrayList(propertyUseDoubleSlabModelBlacklist.getStringList());
                this.verticalSlabPlacementMethod = VerticalSlabPlacementMethod.valueOf(propertyVerticalSlabPlacementMethod.getString());
            }

            propertyLazyVerticalSlabModels.set(this.lazyVerticalSlabModels.toArray(new String[0]));
            propertySlabCullBlacklist.set(this.slabCullBlacklist.toArray(new String[0]));
            propertyUseDoubleSlabModelBlacklist.set(this.useDoubleSlabModelBlacklist.toArray(new String[0]));
            propertyVerticalSlabPlacementMethod.set(this.verticalSlabPlacementMethod.name());
        }

        public boolean shouldCull(IBlockState state) {
            return !isBlockPresent(slabCullBlacklist, state);
        }

        public boolean useLazyModel(IBlockState state) {
            return isBlockPresent(lazyVerticalSlabModels, state);
        }

        public boolean useDoubleSlabModel(IBlockState state) {
            return !isBlockPresent(useDoubleSlabModelBlacklist, state);
        }
    }

}
