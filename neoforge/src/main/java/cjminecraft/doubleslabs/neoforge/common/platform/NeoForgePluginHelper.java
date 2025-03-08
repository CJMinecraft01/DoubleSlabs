package cjminecraft.doubleslabs.neoforge.common.platform;

import cjminecraft.doubleslabs.api.DoubleSlabsPlugin;
import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class NeoForgePluginHelper implements IPlatformPluginHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<IDoubleSlabsPlugin> getPlugins() {
        return getInstances(DoubleSlabsPlugin.class, IDoubleSlabsPlugin.class);
    }

    public static <T> List<T> getInstances(Class<?> annotation, Class<T> instance) {
        final var type = Type.getType(annotation);
        final var scanData = ModList.get().getAllScanData();

        final var pluginClassNames = new LinkedHashSet<String>();

        scanData.stream().map(datum -> datum.getAnnotations().stream()
                .filter(a -> Objects.equals(a.annotationType(), type))
                .map(ModFileScanData.AnnotationData::memberName)
                .collect(Collectors.toSet())).forEach(pluginClassNames::addAll);

        return pluginClassNames.stream().map(className -> {
            try {
                final var asmClass = Class.forName(className);
                final var asmInstanceClass = asmClass.asSubclass(instance);
                final var constructor = asmInstanceClass.getDeclaredConstructor();
                return constructor.newInstance();
            } catch (ReflectiveOperationException | LinkageError e) {
                LOGGER.error("Failed to load: {}", className, e);

                return (T) null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
