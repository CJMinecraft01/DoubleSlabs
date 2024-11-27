package cjminecraft.doubleslabs.api.registration;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.helpers.ITickingSlabHelper;
import net.minecraft.world.level.block.Block;

public interface ISlabHelperRegistration {

    void addHorizontalHelper(IHorizontalSlabHelper helper);

    void registerTickingSlabHelper(ITickingSlabHelper helper, Block... blocks);

}
