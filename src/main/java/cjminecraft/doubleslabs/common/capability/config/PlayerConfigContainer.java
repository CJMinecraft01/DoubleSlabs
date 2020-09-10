package cjminecraft.doubleslabs.common.capability.config;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerConfigContainer implements ICapabilitySerializable<CompoundNBT> {

    protected final LazyOptional<PlayerConfig> config;

    public PlayerConfigContainer() {
        this.config = LazyOptional.of(PlayerConfig::new);
    }

    protected PlayerConfig getConfig() {
        return this.config.orElseThrow(RuntimeException::new);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == PlayerConfigCapability.PLAYER_CONFIG ? this.config.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return this.getConfig().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.getConfig().deserializeNBT(nbt);
    }
}
