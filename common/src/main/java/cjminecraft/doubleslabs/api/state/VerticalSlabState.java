package cjminecraft.doubleslabs.api.state;

import net.minecraft.core.Direction;

public record VerticalSlabState(Direction.Axis axis, VerticalSlabType type) {
    public Direction getFacingDirection() {
        if (type == VerticalSlabType.DOUBLE) {
            throw new IllegalStateException("Cannot get the facing direction for double vertical slabs");
        }

        return Direction.fromAxisAndDirection(axis, type.toAxisDirection());
    }

    public boolean isDouble() {
        return type == VerticalSlabType.DOUBLE;
    }
}
