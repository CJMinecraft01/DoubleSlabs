package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.common.util.TriFunction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import java.util.function.BiFunction;

public enum VerticalSlabPlacementMethod {
    PLACE_WHEN_SNEAKING(0, (player, face, keybindingDown) -> player.isSneaking()),
    DYNAMIC(1, (player, face, keybindingDown) ->
            (player.isSneaking() && face.getAxis().isVertical()) ||
                    (!player.isSneaking() && !face.getAxis().isVertical())),
    KEYBINDING(2, (player, face, keybindingDown) -> keybindingDown);

    private final int index;
    private final TriFunction<EntityPlayer, EnumFacing, Boolean, Boolean> shouldPlace;

    VerticalSlabPlacementMethod(int index, TriFunction<EntityPlayer, EnumFacing, Boolean, Boolean> shouldPlace) {
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

    public boolean shouldPlace(EntityPlayer player, EnumFacing face, boolean keybindingDown) {
        return this.shouldPlace.apply(player, face, keybindingDown);
    }
}
