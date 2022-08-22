package cjminecraft.doubleslabs.forge.common.config;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.config.Option;
import com.google.common.collect.Maps;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class DSForgeConfig {

    private static final ForgeConfig CLIENT = new ForgeConfig(DSConfig.Client.class);
    private static final ForgeConfig COMMON = new ForgeConfig(DSConfig.Common.class);

    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.spec);
    }

    public static void onFileChange(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(Constants.MODID)) {
            switch (event.getConfig().getType()) {
                case CLIENT -> CLIENT.onFileChanged();
                case COMMON -> COMMON.onFileChanged();
            }
        }
    }

    private static class ForgeConfig {

        private final Map<Field, ForgeConfigSpec.ConfigValue<?>> values = Maps.newHashMap();
        private final ForgeConfigSpec spec;

        ForgeConfig(Class<?> configClass) {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            final Map<String, List<Pair<Field, Option>>> groupedOptions = Maps.newHashMap();
            for (Field field : configClass.getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option != null)
                    groupedOptions.computeIfAbsent(option.category(), c -> Lists.newArrayList()).add(Pair.of(field, option));
            }
            groupedOptions.forEach((category, options) -> {
                builder.push(category);
                options.forEach(pair -> {
                    Option option = pair.getRight();
                    Field field = pair.getLeft();
                    String path = option.name().isBlank() ? field.getName() : option.name();
                    try {
                        values.put(field, builder.comment(option.comment()).translation(option.translation()).define(path, field.get(null)));
                    } catch (IllegalAccessException ignored) {
                        Constants.LOG.warn("Unable to add config option %s".formatted(path));
                    }
                });
                builder.pop();
            });
            this.spec = builder.build();
        }

        public void onFileChanged() {
            values.forEach((field, value) -> {
                try {
                    field.set(null, value.get());
                } catch (IllegalAccessException ignored) {
                    Constants.LOG.warn("Unable to update config option %s".formatted(field.getName()));
                }
            });
        }
    }
}
