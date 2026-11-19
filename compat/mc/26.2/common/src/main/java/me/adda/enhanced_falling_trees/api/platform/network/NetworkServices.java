package me.adda.enhanced_falling_trees.api.platform.network;

import java.util.ServiceLoader;

public class NetworkServices {
    private static final NetworkService NETWORK = ServiceLoader.load(NetworkService.class)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Failed to load network service!"));

    public static NetworkService getNetworkService() {
        return NETWORK;
    }
}