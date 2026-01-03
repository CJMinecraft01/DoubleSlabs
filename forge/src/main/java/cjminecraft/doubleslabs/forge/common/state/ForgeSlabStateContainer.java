package cjminecraft.doubleslabs.forge.common.state;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.library.state.SlabStateContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class ForgeSlabStateContainer extends SlabStateContainer implements ICapabilityProvider {
    public ForgeSlabStateContainer(IDynamicSlabStateContainer parentContainer, BlockPos pos) {
        super(parentContainer, pos);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {
        return blockEntity != null ? blockEntity.getCapability(capability, direction) : LazyOptional.empty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability) {
        return blockEntity != null ?  blockEntity.getCapability(capability) : LazyOptional.empty();
    }
}
