package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.common.util.TriFunction;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public enum VerticalSlabPlacementMethod {
    PLACE_WHEN_SNEAKING(0, (player, face, keybindingDown) -> player.isCrouching()),
    DYNAMIC(1, (player, face, keybindingDown) ->
            (player.isCrouching() && face.getAxis().isVertical()) ||
                    (!player.isCrouching() && !face.getAxis().isVertical())),
    KEYBINDING(2, (player, face, keybindingDown) -> keybindingDown);

    private final int index;
    private final TriFunction<Player, Direction, Boolean, Boolean> shouldPlace;

    VerticalSlabPlacementMethod(int index, TriFunction<Player, Direction, Boolean, Boolean> shouldPlace) {
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

    public boolean shouldPlace(Player player, Direction face, boolean keybindingDown) {
        return this.shouldPlace.apply(player, face, keybindingDown);
    }
}
