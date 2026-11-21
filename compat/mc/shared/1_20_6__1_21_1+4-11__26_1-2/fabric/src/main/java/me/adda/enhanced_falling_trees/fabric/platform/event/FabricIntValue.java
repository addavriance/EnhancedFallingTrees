package me.adda.enhanced_falling_trees.fabric.platform.event;

import me.adda.enhanced_falling_trees.api.platform.event.IntValue;

public class FabricIntValue implements IntValue {
    private int value;

    public FabricIntValue(int initialValue) {
        this.value = initialValue;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }
}