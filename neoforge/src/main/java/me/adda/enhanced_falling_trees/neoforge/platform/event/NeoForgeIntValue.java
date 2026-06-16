package me.adda.enhanced_falling_trees.neoforge.platform.event;

import me.adda.enhanced_falling_trees.api.platform.event.IntValue;

// NeoForge 20.6.x removed exp drop API from BlockEvent.BreakEvent
public class NeoForgeIntValue implements IntValue {
    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public void setValue(int value) {
    }
}
