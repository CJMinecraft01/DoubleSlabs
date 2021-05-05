package cjminecraft.doubleslabs.api.capability.blockhalf;

import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockHalfContainer implements ICapabilitySerializable<CompoundNBT> {

    private final LazyOptional<BlockHalf> half;

    public BlockHalfContainer() {
        this.half = LazyOptional.of(BlockHalf::new);
    }

    protected BlockHalf getHalf() {
        return this.half.orElseThrow(RuntimeException::new);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == BlockHalfCapability.BLOCK_HALF ? this.half.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("half", this.getHalf().serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("half", Constants.NBT.TAG_BYTE))
            this.getHalf().deserializeNBT((ByteNBT) nbt.get("half"));
    }
}
