package me.adda.enhanced_falling_trees;

import me.adda.enhanced_falling_trees.api.platform.PlatformServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnhancedFallingTrees");

    public static void testPlatform() {
        String platform = PlatformServices.getPlatform().getPlatformName();
        LOGGER.info("Running on platform: {}", platform);
        LOGGER.info("Environment: {}", PlatformServices.getPlatform().getEnvironmentType());
    }
}