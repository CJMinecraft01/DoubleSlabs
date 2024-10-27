package cjminecraft.doubleslabs.neoforge.platform;

import cjminecraft.doubleslabs.api.DoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class NeoForgePluginHelper implements IPlatformPluginHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<IDoubleSlabsPlugin> getPlugins() {
        return getInstances(DoubleSlabsPlugin.class, IDoubleSlabsPlugin.class);
    }

    public static <T> List<T> getInstances(Class<?> annotation, Class<T> instance) {
        Type type = Type.getType(annotation);
        List<ModFileScanData> scanData = ModList.get().getAllScanData();

        Set<String> pluginClassNames = new LinkedHashSet<>();

        scanData.stream().map(datum -> datum.getAnnotations().stream()
                .filter(a -> Objects.equals(a.annotationType(), type))
                .map(ModFileScanData.AnnotationData::memberName)
                .collect(Collectors.toSet())).forEach(pluginClassNames::addAll);

        return pluginClassNames.stream().map(className -> {
            try {
                Class<?> asmClass = Class.forName(className);
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instance);
                Constructor<? extends T> constructor = asmInstanceClass.getDeclaredConstructor();
                return (T) constructor.newInstance();
            } catch (ReflectiveOperationException | LinkageError e) {
                LOGGER.error("Failed to load: {}", className, e);

                return (T) null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
