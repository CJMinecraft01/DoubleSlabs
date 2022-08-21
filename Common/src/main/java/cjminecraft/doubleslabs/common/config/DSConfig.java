package cjminecraft.doubleslabs.common.config;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.config.value.BooleanValue;
import cjminecraft.doubleslabs.common.config.value.ConfigValue;
import cjminecraft.doubleslabs.common.config.value.EnumValue;
import cjminecraft.doubleslabs.common.placement.VerticalSlabPlacementMethod;
import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class DSConfig {

    public static final Client CLIENT;
    public static final Common COMMON;

    private static boolean isItemPresent(ConfigValue<List<String>> option, Item item) {
        ResourceLocation name = Services.REGISTRIES.getKey(item);
        if (name == null)
            return false;
        return option.get().stream().anyMatch(entry -> {
            if (entry.startsWith("*"))
                return true;
            if (entry.startsWith("#")) {
                ResourceLocation tagLocation = new ResourceLocation(entry.substring(1));
                return Services.REGISTRIES.isIn(Services.REGISTRIES.getItemTag(tagLocation), item);
            }
            return entry.equals(name.toString());
        });
    }

    private static boolean isBlockPresent(ConfigValue<List<String>> option, Block block) {
        ResourceLocation name = Services.REGISTRIES.getKey(block);
        if (name == null)
            return false;
        return option.get().stream().anyMatch(entry -> {
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
        public final ConfigValue<List<String>> slabBlacklist;
        public final ConfigValue<List<String>> verticalSlabBlacklist;
        public final BooleanValue replaceSameSlab;
        public final BooleanValue disableVerticalSlabPlacement;
        public final ConfigValue<List<String>> verticalSlabCraftingBlacklist;

        public boolean isBlacklistedHorizontalSlab(Block block) {
            return isBlockPresent(slabBlacklist, block);
        }

        public boolean isBlacklistedVerticalSlab(Block block) {
            return isBlockPresent(verticalSlabBlacklist, block);
        }

        public boolean isBlacklistedCraftingItem(Item item) {
            IHorizontalSlabSupport support = SlabSupport.getHorizontalSlabSupport(item);
            return isItemPresent(verticalSlabCraftingBlacklist, item) || (support != null && !support.canCraft(item));
        }
    }

    public static class Client {
        public final ConfigValue<List<String>> uvlockModelBlacklist;
        public final ConfigValue<List<String>> slabCullBlacklist;
        public final ConfigValue<List<String>> useDoubleSlabModelBlacklist;
        public final EnumValue<VerticalSlabPlacementMethod> verticalSlabPlacementMethod;

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
