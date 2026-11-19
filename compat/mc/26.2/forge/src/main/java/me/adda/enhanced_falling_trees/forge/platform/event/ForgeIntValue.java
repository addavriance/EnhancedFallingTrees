package me.adda.enhanced_falling_trees.forge.platform.event;

import me.adda.enhanced_falling_trees.api.platform.event.IntValue;
import net.minecraftforge.event.level.BlockEvent;

public class ForgeIntValue implements IntValue {
    private final BlockEvent.BreakEvent event;

    public ForgeIntValue(BlockEvent.BreakEvent event) {
        this.event = event;
    }

    @Override
    public int getValue() {
        return event.getExpToDrop();
    }

    @Override
    public void setValue(int value) {
        event.setExpToDrop(value);
    }
}