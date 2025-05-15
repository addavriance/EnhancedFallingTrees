package me.adda.enhanced_falling_trees.compat;

import me.adda.enhanced_falling_trees.registry.EntityRegistry;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class Jade implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.hideTarget(EntityRegistry.TREE.get());
    }
}
