package cjminecraft.doubleslabs;

import net.minecraft.block.BlockState;

public class Utils {

    public static boolean isTransparent(BlockState state) {
        return !state.getMaterial().isOpaque();
    }

}
