package cjminecraft.doubleslabs.common;

import cjminecraft.doubleslabs.api.helpers.ISlabHelper;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

public class Internal {

    @Nullable
    private static ISlabHelper slabHelper;

    public static void setSlabHelper(@Nullable ISlabHelper slabHelper) {
        Internal.slabHelper = slabHelper;
    }

    public static ISlabHelper getSlabHelper() {
        Preconditions.checkState(slabHelper != null, "SlabHelper not initialized");
        return slabHelper;
    }

}
