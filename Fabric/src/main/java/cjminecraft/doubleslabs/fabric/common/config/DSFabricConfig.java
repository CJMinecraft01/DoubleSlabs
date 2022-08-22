package cjminecraft.doubleslabs.fabric.common.config;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.config.Option;
import com.google.common.collect.Maps;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DSFabricConfig {

    public static final FabricConfig CLIENT = new FabricConfig(DSConfig.Client.class, FabricLoader.getInstance().getConfigDir().resolve("doubleslabs-client.cfg"));
    public static final FabricConfig COMMON = new FabricConfig(DSConfig.Common.class, FabricLoader.getInstance().getConfigDir().resolve("doubleslabs-common.cfg"));

    public static void loadConfigs() {
        CLIENT.load();
        COMMON.load();
    }

    private static class FabricConfig {
        private static final Pattern GROUP_PATTERN = Pattern.compile("(?:([a-z]+)|(?:\"(.+)\"))\s*\\{\s*", Pattern.CASE_INSENSITIVE);
        private static final Pattern END_GROUP_PATTERN = Pattern.compile("\s*}\s*");

        private final Map<String, List<Pair<Field, Option>>> groupedOptions = Maps.newHashMap();
        private final Path path;

        FabricConfig(Class<?> configClass, Path path) {
            this.path = path;
            for (Field field : configClass.getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option != null)
                    groupedOptions.computeIfAbsent(option.category(), c -> Lists.newArrayList()).add(Pair.of(field, option));
            }
        }

        public void load() {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line = "";
                String group = null;
                Map<String, Map<String, String>> values = Maps.newHashMap();
                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll("[\\r\\n]", "");
                    if (line.isEmpty() || line.startsWith("#"))
                        continue;
                    if (group == null) {
                        Matcher matcher = GROUP_PATTERN.matcher(line);
                        if (matcher.find()) {
                            group = matcher.group(1);
                            if (group == null)
                                group = matcher.group(2);
                        } else {
                            // todo: error
                        }
                    } else {
                        String[] fields = line.split("\s*=\s*");
                        if (fields.length == 2) {
                            values.computeIfAbsent(group, g -> Maps.newHashMap()).put(fields[0], fields[1]);
                        } else if (END_GROUP_PATTERN.matcher(line).find())
                            group = null;
                        else {
                            // todo: error
                        }
                    }
                }
                loadValues(values);
            } catch (FileNotFoundException e) {
                try {
                    Files.createFile(path);
                    Constants.LOG.info("Config file not found, will create a default config");
                    save();
                } catch (IOException ex) {
                    Constants.LOG.info("Config file not found and failed to create a default config");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Nullable
        @SuppressWarnings({"unchecked", "rawtypes"})
        private Object parseValue(Class<?> type, String value) {
            if (String.class.isAssignableFrom(type))
                return value.substring(1, value.length() - 1);
            if (Integer.class.isAssignableFrom(type))
                return Integer.parseInt(value);
            if (Long.class.isAssignableFrom(type))
                return Long.parseLong(value);
            if (Float.class.isAssignableFrom(type))
                return Float.parseFloat(value);
            if (Double.class.isAssignableFrom(type))
                return Double.parseDouble(value);
            if (Boolean.class.isAssignableFrom(type))
                return Boolean.parseBoolean(value);
            if (List.class.isAssignableFrom(type)) {
                List<Object> result = Lists.newArrayList();
                String[] values = value.substring(1, value.length() - 1).split("\s*,\s*");
                for (String v : values)
                    result.add(parseValue(type.getComponentType(), v));
                return result;
            }
            if (type.isEnum()) {
                return Enum.valueOf((Class<Enum>) type, value);
            }
            return null;
        }

        private void loadValues(Map<String, Map<String, String>> values) {
            groupedOptions.forEach((group, options) -> {
                if (values.containsKey(group)) {
                    Map<String, String> groupValues = values.get(group);
                    options.forEach(pair -> {
                        Option option = pair.getRight();
                        Field field = pair.getLeft();
                        String name = option.name().isBlank() ? field.getName() : option.name();
                        if (groupValues.containsKey(name)) {
                            try {
                                field.set(null, parseValue(field.getType(), groupValues.get(name)));
                            } catch (IllegalAccessException ignored) {
                                // todo: error
                            }
                        }
                    });
                }
            });
        }

        public void save() {
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (Map.Entry<String, List<Pair<Field, Option>>> entry : groupedOptions.entrySet()) {
                    String group = entry.getKey();
                    List<Pair<Field, Option>> options = entry.getValue();

                    // Write the group opening
                    writer.write((group.contains(" ") ? "\"%s\" {" : "%s {").formatted(group));
                    writer.newLine();

                    for (Pair<Field, Option> pair : options) {
                        Field field = pair.getLeft();
                        Option option = pair.getRight();

                        String name = option.name().isBlank() ? field.getName() : option.name();
                        try {
                            String value = convertValue(field.get(null));

                            // Write comments
                            for (String line : option.comment()) {
                                writer.write("# " + line);
                                writer.newLine();
                            }

                            // Write value
                            writer.write("\t%s = %s".formatted(name, value));
                            writer.newLine();
                        } catch (IllegalAccessException ignored) {

                        }
                    }

                    // Write the group closing
                    writer.write("}");
                    writer.newLine();
                }
            } catch (IOException ignored) {

            }
        }

        private String convertValue(Object value) {
            if (value instanceof String)
                return "\"" + value + "\"";
            if (value.getClass().isPrimitive())
                return value.toString();
            if (value instanceof List<?>) {
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                for (Object v : (List<?>) value) {
                    builder.append(convertValue(value));
                    builder.append(", ");
                }
                // todo: check
                if (((List<?>) value).size() > 0) {
                    builder.deleteCharAt(builder.length() - 2);
                }
                return builder.toString();
            }
            if (value instanceof Enum<?>) {
                return ((Enum<?>) value).name();
            }
            return "";
        }
    }

}
