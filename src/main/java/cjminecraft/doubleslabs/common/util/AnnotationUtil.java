package cjminecraft.doubleslabs.common.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotationUtil {

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

}
