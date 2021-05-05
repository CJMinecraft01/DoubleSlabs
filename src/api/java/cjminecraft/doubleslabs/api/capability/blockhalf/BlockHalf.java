package cjminecraft.doubleslabs.api.capability.blockhalf;

import net.minecraft.nbt.ByteNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class BlockHalf implements IBlockHalf {

    private boolean half;

    public BlockHalf() {
    }

    public BlockHalf(boolean half) {
        this.half = half;
    }

    @Override
    public boolean isPositiveHalf() {
        return this.half;
    }

    public void setHalf(boolean half) {
        this.half = half;
    }


    @Override
    public ByteNBT serializeNBT() {
        return ByteNBT.valueOf(this.half);
    }

    @Override
    public void deserializeNBT(ByteNBT nbt) {
        this.half = nbt.getByte() != 0;
    }
}
