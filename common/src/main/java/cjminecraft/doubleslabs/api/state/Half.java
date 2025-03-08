package cjminecraft.doubleslabs.api.state;

public enum Half {
    TOP("top"),
    BOTTOM("bottom");

    private final String name;

    Half(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Half getOpposite() {
        return switch (this) {
            case TOP -> BOTTOM;
            case BOTTOM -> TOP;
        };
    }
}
