package com.sgtc.launcher.ClockSkin.View;

public class DigitalObject {
    Object resource;
    ItemType type;
    DividerVisibility dividerVisibility;

    /**
     * Create new digital object
     * @param resource This can be drawable or input stream
     * @param type type of object slice or divider
     * @param dividerVisibility visibility for all provided dividers
     */
    public DigitalObject(Object resource, ItemType type, DividerVisibility dividerVisibility) {
        this.resource = resource;
        this.type = type;
        this.dividerVisibility = dividerVisibility;
    }

    public Object getResource() {
        return resource;
    }

    public ItemType getType() {
        return type;
    }

    public DividerVisibility getDividerVisibility() {
        return dividerVisibility;
    }

    public enum ItemType {
        SLICE,
        DIVIDER
    }
    public enum DividerVisibility {
        VISIBLE,
        INVISIBLE,
        GONE
    }
}