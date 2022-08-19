package cjminecraft.doubleslabs.forge.api;

import cjminecraft.doubleslabs.api.BlockInfo;
import cjminecraft.doubleslabs.api.ILevelWrapper;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ForgeBlockInfo extends BlockInfo implements INBTSerializable<CompoundTag>, ICapabilityProvider {
    public ForgeBlockInfo(SlabBlockEntity<?> slab, boolean positive) {
        super(slab, positive);
    }

    @Override
    public ILevelWrapper<?> createWrappedLevel(Level level) {
        // todo
        return null;
    }

    public void onLoad() {
        if (this.blockEntity != null) {
            this.blockEntity.setLevel(this.level);
            this.blockEntity.onLoad();
        }
    }

    public void onChunkUnloaded() {
        if (this.blockEntity != null)
            this.blockEntity.onChunkUnloaded();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return this.blockEntity != null ? this.blockEntity.getCapability(cap, side) : LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return this.blockEntity != null ? this.blockEntity.getCapability(cap) : LazyOptional.empty();
    }
}
