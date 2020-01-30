package io.webdevice.lang.annotation;

/**
 * A {@link Toggle} represents a tri-state boolean annotation attribute.
 * Useful in scenarios where detection of the unset/null state is required.
 */
public enum Toggle {
    ON(true),
    OFF(false),
    UNSET(null);

    private final Boolean value;

    Toggle(Boolean value) {
        this.value = value;
    }

    public Boolean valueOf() {
        return value;
    }

    public boolean on() {
        return this == ON;
    }

    public boolean off() {
        return this == OFF;
    }

    public boolean unset() {
        return this == UNSET;
    }

    @Override
    public String toString() {
        return value != null
                ? value.toString()
                : null;
    }
}
