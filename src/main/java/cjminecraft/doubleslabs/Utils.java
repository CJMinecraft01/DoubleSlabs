package cjminecraft.doubleslabs;

import net.minecraft.block.state.IBlockState;

public class Utils {

    public static boolean isTransparent(IBlockState state) {
        return !state.getMaterial().isOpaque();
    }

}
