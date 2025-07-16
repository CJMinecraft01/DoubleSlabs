package cjminecraft.doubleslabs.api.state;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum VerticalSlabType implements StringRepresentable {
    POSITIVE("positive"),
    NEGATIVE("negative"),
    DOUBLE("double");

    private final String name;

    VerticalSlabType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Half getHalf() {
        return switch (this) {
            case POSITIVE -> Half.POSITIVE;
            case NEGATIVE -> Half.NEGATIVE;
            case DOUBLE -> throw new IllegalStateException("Cannot get the half of a double slab type");
        };
    }

    public Direction.AxisDirection toAxisDirection() {
        return switch (this) {
            case POSITIVE -> Direction.AxisDirection.POSITIVE;
            case NEGATIVE -> Direction.AxisDirection.NEGATIVE;
            case DOUBLE -> throw new IllegalStateException("Cannot get the axis direction of a double slab type");
        };
    }

    public static VerticalSlabType fromAxisDirection(Direction.AxisDirection axisDirection) {
        return switch (axisDirection) {
            case POSITIVE -> POSITIVE;
            case NEGATIVE -> NEGATIVE;
        };
    }

    public Direction getDirection(Half half, Direction.Axis axis) {
        return switch (this) {
            case POSITIVE -> axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
            case NEGATIVE -> axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
            case DOUBLE -> switch (half) {
                case POSITIVE -> axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
                case NEGATIVE -> axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
            };
        };
    }

}
