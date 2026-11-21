package me.adda.enhanced_falling_trees.api.platform.event;

import java.util.ServiceLoader;

public class EventServices {
    private static final EventManager EVENT_MANAGER = ServiceLoader.load(EventManager.class)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Failed to load event manager service!"));

    public static EventManager getEventManager() {
        return EVENT_MANAGER;
    }
}