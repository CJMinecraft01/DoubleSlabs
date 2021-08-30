package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.common.util.QuadFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;

public enum VerticalSlabPlacementMethod {
    PLACE_WHEN_SNEAKING(0, (player, face, keybindingDown, verticalSlabItem) -> player.isSneaking()),
    DYNAMIC(1, (player, face, keybindingDown, verticalSlabItem) ->
            (player.isSneaking() && face.getAxis().isVertical()) ||
                    (!player.isSneaking() && !face.getAxis().isVertical())),
    KEYBINDING(2, (player, face, keybindingDown, verticalSlabItem) -> keybindingDown),
    ITEM(3, (player, face, keybindingDown, verticalSlabItem) -> verticalSlabItem);

    private final int index;
    private final QuadFunction<PlayerEntity, Direction, Boolean, Boolean, Boolean> shouldPlace;

    VerticalSlabPlacementMethod(int index, QuadFunction<PlayerEntity, Direction, Boolean, Boolean, Boolean> shouldPlace) {
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

    public boolean shouldPlace(PlayerEntity player, Direction face, boolean keybindingDown, boolean verticalSlabItem) {
        return this.shouldPlace.apply(player, face, keybindingDown, verticalSlabItem);
    }
}
