package me.adda.enhanced_falling_trees.api.platform.event;

public enum EventResult {
    PASS,
    INTERRUPT_TRUE,
    INTERRUPT_FALSE;

    public static EventResult pass() {
        return PASS;
    }

    public static EventResult interruptTrue() {
        return INTERRUPT_TRUE;
    }

    public static EventResult interruptFalse() {
        return INTERRUPT_FALSE;
    }
}