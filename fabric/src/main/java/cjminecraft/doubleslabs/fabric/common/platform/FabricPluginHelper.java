package cjminecraft.doubleslabs.fabric.common.platform;

import cjminecraft.doubleslabs.api.IDoubleSlabsPlugin;
import cjminecraft.doubleslabs.common.platform.services.IPlatformPluginHelper;
import net.fabricmc.loader.api.EntrypointException;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class FabricPluginHelper implements IPlatformPluginHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<IDoubleSlabsPlugin> getPlugins() {
        return getInstances("doubleslabs_plugin", IDoubleSlabsPlugin.class);
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> List<T> getInstances(String entrypointContainerKey, Class<T> instanceClass) {
        final var fabricLoader = FabricLoader.getInstance();
        final var pluginContainers = fabricLoader.getEntrypointContainers(entrypointContainerKey, instanceClass);

        return pluginContainers.stream().<T>mapMulti(((entrypointContainer, consumer) -> {
            try {
                final var entrypoint = entrypointContainer.getEntrypoint();
                consumer.accept(entrypoint);
            } catch (EntrypointException e) {
                String modName;

                try {
                    ModContainer provider = entrypointContainer.getProvider();
                    ModMetadata metadata = provider.getMetadata();
                    modName = metadata.getName();
                } catch (RuntimeException ignored) {
                    modName = "unknown";
                }

                LOGGER.error("{} specified an invalid entrypoint for its DoubleSlabs plugin", modName, e);
            }
        })).collect(Collectors.toList());
    }
}
