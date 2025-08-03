package cjminecraft.doubleslabs.forge.common.state;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.library.state.SlabStateContainer;
import com.google.common.base.Preconditions;
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
        Preconditions.checkState(blockEntity != null, "Cannot get the capability from the block entity if the block entity does not exist");
        return blockEntity.getCapability(capability, direction);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability) {
        Preconditions.checkState(blockEntity != null, "Cannot get the capability from the block entity if the block entity does not exist");
        return blockEntity.getCapability(capability);
    }
}
