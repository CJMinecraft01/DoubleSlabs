package cjminecraft.doubleslabs.library.load.registration;

import cjminecraft.doubleslabs.api.helpers.IHorizontalSlabHelper;
import cjminecraft.doubleslabs.api.helpers.ITickingSlabHelper;
import cjminecraft.doubleslabs.api.registration.ISlabHelperRegistration;
import cjminecraft.doubleslabs.library.helpers.SlabHelper;
import net.minecraft.world.level.block.Block;

public class SlabHelperRegistration implements ISlabHelperRegistration {

    private final SlabHelper slabHelper;

    public SlabHelperRegistration(SlabHelper slabHelper) {
        this.slabHelper = slabHelper;
    }

    @Override
    public void addHorizontalHelper(IHorizontalSlabHelper helper) {
        slabHelper.addHorizontalSlabSupport(helper);
    }

    @Override
    public void registerTickingSlabHelper(ITickingSlabHelper helper, Block... blocks) {
        slabHelper.registerTickingSlabHelper(helper, blocks);
    }
}
