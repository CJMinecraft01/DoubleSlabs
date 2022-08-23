package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.platform.services.INetworkHelper;
import cjminecraft.doubleslabs.platform.services.IPlatformHelper;
import cjminecraft.doubleslabs.platform.services.IRegistryHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegistryHelper REGISTRIES = load(IRegistryHelper.class);
    public static final INetworkHelper NETWORK = load(INetworkHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
