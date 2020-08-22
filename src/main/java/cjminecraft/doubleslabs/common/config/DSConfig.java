package cjminecraft.doubleslabs.common.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class DSConfig {

    public static class Server {
        public final ConfigValue<List<String>> slabBlacklist;
        public final ConfigValue<List<String>> verticalSlabBlacklist;
        public final BooleanValue replaceSameSlab;
        public final BooleanValue disableVerticalSlabPlacement;
        public final BooleanValue disableVerticalSlabItems;

        Server(Builder builder) {
            builder.comment("General Configuration")
                    .push("general");

            slabBlacklist = builder
                    .comment("The list of slabs (or tags) to ignore when creating double slabs",
                            "Example: minecraft:purpur_slab")
                    .translation("doubleslabs.configgui.slabBlacklist")
                    .define("slab_blacklist", new ArrayList<>());

            verticalSlabBlacklist = builder
                    .comment("The list of slabs (or tags) to ignore when creating vertical slabs",
                            "Example: minecraft:purpur_slab",
                            "Example: #minecraft:slabs")
                    .translation("doubleslabs.configgui.verticalSlabBlacklist")
                    .define("vertical_slab_blacklist", new ArrayList<>());

            replaceSameSlab = builder
                    .comment("Whether to use the custom double slab when combining slabs of the same type")
                    .translation("doubleslabs.configgui.replaceSameSlab")
                    .define("replace_same_slab", true);

            disableVerticalSlabPlacement = builder
                    .comment("Whether to disable the placement of vertical slabs from regular horizontal slabs")
                    .translation("doubleslabs.configgui.disableVerticalSlabPlacement")
                    .define("disable_vertical_slab_placement", false);

            // TODO implement vertical slab items
            disableVerticalSlabItems = builder
                    .comment("Whether to disable the vertical slab items")
                    .translation("doubleslabs.configgui.disableVerticalSlabItems")
                    .worldRestart()
                    .define("disable_vertical_slab_items", true);

            builder.pop();
        }
    }

    public static class Client {
        public final ConfigValue<List<String>> lazyVerticalSlabModels;
        public final ConfigValue<List<String>> slabCullBlacklist;

        Client(Builder builder) {
            builder.push("Client Only Settings");

            lazyVerticalSlabModels = builder
                    .comment("The list of slabs (or tags) which should use the lazy model rendering technique",
                            "Lazy model rendering does not physically rotate the original slab model, but applies the same texture to a default vertical slab model",
                            "This often yields better looking results with wooden planks and does not necessarily improve the look of all vertical slabs")
                    .translation("doubleslabs.configgui.lazyVerticalSlabModels")
                    .define("lazy_vertical_slabs", Lists.newArrayList("#doubleslabs:plank_slabs"));

            slabCullBlacklist = builder
                    .comment("The list of slabs (or tags) which should not be culled when combined")
                    .translation("doubleslabs.configgui.slabCullBlacklist")
                    .define("slab_cull_blacklist", Lists.newArrayList("#minecraft:campfires"));

            // TODO placement options

            builder.pop();
        }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

}
