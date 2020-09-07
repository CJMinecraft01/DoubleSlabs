package cjminecraft.doubleslabs.common.tileentity;

import cjminecraft.doubleslabs.common.init.DSTiles;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public class SlabConverterTileEntity extends SlabTileEntity {

    public SlabConverterTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains("negative"))
            this.negativeBlockInfo.setBlockState(NBTUtil.readBlockState(nbt.getCompound("negative")));
        if (nbt.contains("negative_tile"))
            this.negativeBlockInfo.setTileEntity(TileEntity.readTileEntity(this.negativeBlockInfo.getBlockState(), nbt.getCompound("negative_tile")));
        if (nbt.contains("positive"))
            this.positiveBlockInfo.setBlockState(NBTUtil.readBlockState(nbt.getCompound("positive")));
        if (nbt.contains("positive_tile"))
            this.positiveBlockInfo.setTileEntity(TileEntity.readTileEntity(this.positiveBlockInfo.getBlockState(), nbt.getCompound("positive_tile")));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("negativeBlock", this.negativeBlockInfo.serializeNBT());
        nbt.put("positiveBlock", this.positiveBlockInfo.serializeNBT());
        return writeInternal(nbt);
    }

    private CompoundNBT writeInternal(CompoundNBT compound) {
        ResourceLocation resourcelocation = TileEntityType.getId(DSTiles.DYNAMIC_SLAB.get());
        if (resourcelocation == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            compound.putString("id", resourcelocation.toString());
            compound.putInt("x", this.pos.getX());
            compound.putInt("y", this.pos.getY());
            compound.putInt("z", this.pos.getZ());
            if (getCapabilities() != null) compound.put("ForgeCaps", serializeCaps());
            return compound;
        }
    }
}
