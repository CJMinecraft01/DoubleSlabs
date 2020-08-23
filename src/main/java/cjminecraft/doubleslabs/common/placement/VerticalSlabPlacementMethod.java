package cjminecraft.doubleslabs.common.placement;

public enum VerticalSlabPlacementMethod {
    PLACE_WHEN_SNEAKING(0),
    DYNAMIC(1);
    // TODO keybinding too

    private final int index;

    VerticalSlabPlacementMethod(int index) {
        this.index = index;
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
}
