package cjminecraft.doubleslabs.common.util;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnnotationUtil {

    private static ASMDataTable dataTable;

    public static void prepare(ASMDataTable table) {
        dataTable = table;
    }

    public static final Predicate<Map<String, Object>> MODID_PREDICATE = data -> {
        String modid = (String) data.getOrDefault("modid", "");
        return modid.length() == 0 || Loader.isModLoaded(modid);
    };

    public static <T> List<T> getClassInstances(Class<?> annotation, Class<T> instance) {
        return getClassInstances(annotation, instance, data -> true);
    }

    public static <T> List<T> getClassInstances(Class<?> annotation, Class<T> instance, Predicate<Map<String, Object>> filter) {
        return dataTable.getAll(annotation.getName()).stream()
                .filter(data -> filter.test(data.getAnnotationInfo()))
                .map(data -> {
                    try {
                        Class<?> clazz = Class.forName(data.getClassName());
                        if (!instance.isAssignableFrom(clazz))
                            return null;
                        Class<? extends T> instanceClass = clazz.asSubclass(instance);
                        return instanceClass.newInstance();
                    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ignored) {
                        return (T)null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
