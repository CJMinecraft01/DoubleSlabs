package cjminecraft.doubleslabs.forge.common.state;

import cjminecraft.doubleslabs.api.state.IDynamicSlabStateContainer;
import cjminecraft.doubleslabs.library.state.SlabStateContainer;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ForgeSlabStateContainer extends SlabStateContainer implements ICapabilityProvider {
    public ForgeSlabStateContainer(IDynamicSlabStateContainer parentContainer, BlockPos pos) {
        super(parentContainer, pos);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (blockEntity != null) {
            return blockEntity.getCapability(capability, direction);
        } else {
            return LazyOptional.empty();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability) {
        if (blockEntity != null) {
            return blockEntity.getCapability(capability);
        } else {
            return LazyOptional.empty();
        }
    }
}
