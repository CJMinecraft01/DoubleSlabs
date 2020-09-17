package cjminecraft.doubleslabs.common.placement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import java.util.function.BiFunction;

public enum VerticalSlabPlacementMethod {
    PLACE_WHEN_SNEAKING(0, (player, face) -> player.isSneaking()),
    DYNAMIC(1, (player, face) ->
            (player.isSneaking() && face.getAxis().isVertical()) ||
                    (!player.isSneaking() && !face.getAxis().isVertical()));

    private final int index;
    private final BiFunction<EntityPlayer, EnumFacing, Boolean> shouldPlace;

    VerticalSlabPlacementMethod(int index, BiFunction<EntityPlayer, EnumFacing, Boolean> shouldPlace) {
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

    public boolean shouldPlace(EntityPlayer player, EnumFacing face) {
        return this.shouldPlace.apply(player, face);
    }
}
