package me.adda.enhanced_falling_trees.api.platform;

import java.util.ServiceLoader;

public class PlatformServices {
    private static final PlatformHelper PLATFORM = load(PlatformHelper.class);

    public static final RegistrationHelper REGISTRATION = load(RegistrationHelper.class);

    public static final ClientHelper CLIENT = load(ClientHelper.class);

    public static PlatformHelper getPlatform() {
        return PLATFORM;
    }

    private static <T> T load(Class<T> serviceClass) {
        return ServiceLoader.load(serviceClass)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to load " + serviceClass.getName() +  " service!"));
    }
}
