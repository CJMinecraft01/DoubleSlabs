package cjminecraft.doubleslabs.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class SlabConverterTileEntity extends SlabTileEntity {

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("negative"))
            this.negativeBlockInfo.setBlockState(NBTUtil.readBlockState(nbt.getCompoundTag("negative")));
        if (nbt.hasKey("negative_tile"))
            this.negativeBlockInfo.setTileEntity(TileEntity.create(this.world, nbt.getCompoundTag("negative_tile")));
        if (nbt.hasKey("positive"))
            this.positiveBlockInfo.setBlockState(NBTUtil.readBlockState(nbt.getCompoundTag("positive")));
        if (nbt.hasKey("positive_tile"))
            this.positiveBlockInfo.setTileEntity(TileEntity.create(this.world, nbt.getCompoundTag("positive_tile")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("negativeBlock", this.negativeBlockInfo.serializeNBT());
        nbt.setTag("positiveBlock", this.positiveBlockInfo.serializeNBT());
        return writeInternal(nbt);
    }

    private NBTTagCompound writeInternal(NBTTagCompound compound) {
        ResourceLocation resourcelocation = getKey(SlabTileEntity.class);

        if (resourcelocation == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            compound.setString("id", resourcelocation.toString());
            compound.setInteger("x", this.pos.getX());
            compound.setInteger("y", this.pos.getY());
            compound.setInteger("z", this.pos.getZ());
            return compound;
        }
    }
}
