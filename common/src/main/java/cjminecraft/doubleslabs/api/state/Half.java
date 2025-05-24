package cjminecraft.doubleslabs.api.state;

public enum Half {
    POSITIVE("positive"),
    NEGATIVE("negative");

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
            case POSITIVE -> NEGATIVE;
            case NEGATIVE -> POSITIVE;
        };
    }
}
