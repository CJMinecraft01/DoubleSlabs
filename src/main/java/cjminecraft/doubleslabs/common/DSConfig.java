package cjminecraft.doubleslabs.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class DSConfig {

    public static class Server {
        public final ConfigValue<List<String>> slabBlacklist;

        Server(Builder builder) {
            builder.comment("General Configuration")
                    .push("general");

            slabBlacklist = builder
                    .comment("The list of slabs (or tags) to ignore when creating double slabs",
                            "Example: minecraft:purpur_slab")
                    .translation("doubleslabs.configgui.slabBlacklist")
                    .define("slab_blacklist", new ArrayList<>());

            builder.pop();
        }
    }

    public static class Client {
        Client(Builder builder) {

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
