package cjminecraft.doubleslabs.client.util;

import net.minecraft.block.BlockState;

public class ClientUtils {

    public static boolean isTransparent(BlockState state) {
        return !state.getMaterial().isOpaque() || !state.isSolid();
    }

}
