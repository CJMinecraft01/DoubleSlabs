package cjminecraft.doubleslabs.platform;

import cjminecraft.doubleslabs.common.Constants;
import cjminecraft.doubleslabs.platform.services.IClientHelper;

import java.util.ServiceLoader;

public class ClientServices {

    public static final IClientHelper INSTANCE = load(IClientHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

}
