package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.common.util.TriFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;

import java.util.function.BiFunction;

public enum VerticalSlabPlacementMethod {
    PLACE_WHEN_SNEAKING(0, (player, face, keybindingDown) -> player.isSneaking()),
    DYNAMIC(1, (player, face, keybindingDown) ->
            (player.isSneaking() && face.getAxis().isVertical()) ||
                    (!player.isSneaking() && !face.getAxis().isVertical())),
    KEYBINDING(2, (player, face, keybindingDown) -> keybindingDown);

    private final int index;
    private final TriFunction<PlayerEntity, Direction, Boolean, Boolean> shouldPlace;

    VerticalSlabPlacementMethod(int index, TriFunction<PlayerEntity, Direction, Boolean, Boolean> shouldPlace) {
        this.index = index;
        this.shouldPlace = shouldPlace;
    }

    public int getIndex() {
        return this.index;
    }

    public static VerticalSlabPlacementMethod fromIndex(int index) {
        for (VerticalSlabPlacementMethod method : values())
            if (method.index == index)
                return method;
        return DYNAMIC;
    }

    public boolean shouldPlace(PlayerEntity player, Direction face, boolean keybindingDown) {
        return this.shouldPlace.apply(player, face, keybindingDown);
    }
}
