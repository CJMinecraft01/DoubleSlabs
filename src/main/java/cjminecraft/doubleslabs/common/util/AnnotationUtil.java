package cjminecraft.doubleslabs.common.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnnotationUtil {

    public static final Predicate<Map<String, Object>> MODID_PREDICATE = data -> {
        String modid = (String) data.getOrDefault("modid", "");
        return modid.length() == 0 || ModList.get().isLoaded(modid);
    };

    public static <T> List<T> getFieldInstances(Class<?> annotation, Class<T> instance) {
        Type type = Type.getType(annotation);
        List<ModFileScanData> scanData = ModList.get().getAllScanData();
        List<Pair<String, String>> classAndFieldNames = new ArrayList<>();
        scanData.stream().map(datum -> datum.getAnnotations().stream()
                    .filter(a -> Objects.equals(a.getAnnotationType(), type))
                    .map(a -> Pair.of(a.getClassType().getClassName(), a.getMemberName()))
                .collect(Collectors.toList()))
                .forEach(classAndFieldNames::addAll);
        return classAndFieldNames.stream().map(pair -> {
            try {
                Class<?> clazz = Class.forName(pair.getLeft());
                Field field = clazz.getField(pair.getRight());
                if (!instance.isAssignableFrom(field.getType()))
                    return null;
                if (!field.isAccessible())
                    field.setAccessible(true);
                return (T)field.get(null);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
                return (T)null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T> List<T> getClassInstances(Class<?> annotation, Class<T> instance) {
        return getClassInstances(annotation, instance, data -> true);
    }

    public static <T> List<T> getClassInstances(Class<?> annotation, Class<T> instance, Predicate<Map<String, Object>> filter) {
        Type type = Type.getType(annotation);
        List<ModFileScanData> scanData = ModList.get().getAllScanData();
        List<String> classNames = new ArrayList<>();
        scanData.stream().map(datum -> datum.getAnnotations().stream()
                .filter(a -> Objects.equals(a.getAnnotationType(), type) && filter.test(a.getAnnotationData()))
                .map(ModFileScanData.AnnotationData::getMemberName)
                .collect(Collectors.toList()))
                .forEach(classNames::addAll);
        return classNames.stream().map(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                if (!instance.isAssignableFrom(clazz))
                    return null;
                Class<? extends T> instanceClass = clazz.asSubclass(instance);
                return instanceClass.newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ignored) {
                return (T)null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T> List<T> getClassInstances(Class<?> annotation, Class<T> instance, Predicate<Map<String, Object>> filter, Comparator<ModFileScanData.AnnotationData> sort) {
        Type type = Type.getType(annotation);
        List<ModFileScanData> scanData = ModList.get().getAllScanData();
        List<String> classNames = new ArrayList<>();
        scanData.stream().map(datum -> datum.getAnnotations().stream()
                .filter(a -> Objects.equals(a.getAnnotationType(), type) && filter.test(a.getAnnotationData()))
                .sorted(sort)
                .map(ModFileScanData.AnnotationData::getMemberName)
                .collect(Collectors.toList()))
                .forEach(classNames::addAll);
        return classNames.stream().map(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                if (!instance.isAssignableFrom(clazz))
                    return null;
                Class<? extends T> instanceClass = clazz.asSubclass(instance);
                return instanceClass.newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ignored) {
                return (T)null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
