package type.model.inheritance.enumdiscriminator;

/**
 * fixed enum type for discriminator.
 */
public enum SnakeKind {
    /**
     * Species cobra.
     */
    COBRA("cobra");

    /**
     * The actual serialized value for a SnakeKind instance.
     */
    private final String value;

    SnakeKind(String value) {
        this.value = value;
    }

    /**
     * Parses a serialized value to a SnakeKind instance.
     * 
     * @param value the serialized value to parse.
     * @return the parsed SnakeKind object, or null if unable to parse.
     */
    public static SnakeKind fromString(String value) {
        if (value == null) {
            return null;
        }
        SnakeKind[] items = SnakeKind.values();
        for (SnakeKind item : items) {
            if (item.toString().equalsIgnoreCase(value)) {
                return item;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.value;
    }
}
