package cjminecraft.doubleslabs.old.tileentitiy;

import cjminecraft.doubleslabs.old.Registrar;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;

public class TileEntityDoubleSlab extends TileEntityVerticalSlab {

    public static final ModelProperty<BlockState> TOP_STATE = new ModelProperty<>();
    public static final ModelProperty<BlockState> BOTTOM_STATE = new ModelProperty<>();

//    private BlockState topState;
//    private BlockState bottomState;

    public TileEntityDoubleSlab() {
        super(Registrar.TILE_DOUBLE_SLAB);
    }

    public BlockState getTopState() {
        return getPositiveState();
    }

    public BlockState getBottomState() {
        return getNegativeState();
    }

    public void setTopState(BlockState topState) {
        setPositiveState(topState);
    }

    public void setBottomState(BlockState bottomState) {
        setNegativeState(bottomState);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT read) {
        super.read(read);
        if (read.contains("top"))
            this.positiveState = NBTUtil.readBlockState(read.getCompound("top"));
        if (read.contains("bottom"))
            this.negativeState = NBTUtil.readBlockState(read.getCompound("bottom"));
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(BOTTOM_STATE, this.getBottomState()).withInitial(TOP_STATE, this.getTopState()).build();
    }

}
