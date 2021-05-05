package cjminecraft.doubleslabs.api.capability.blockhalf;

import net.minecraft.nbt.ByteNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IBlockHalf extends INBTSerializable<ByteNBT> {

    boolean isPositiveHalf();

    void setHalf(boolean half);

}
