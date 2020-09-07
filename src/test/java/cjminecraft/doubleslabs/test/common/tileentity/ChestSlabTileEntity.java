package cjminecraft.doubleslabs.test.common.tileentity;

import cjminecraft.doubleslabs.test.common.init.DSTTiles;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChestSlabTileEntity extends TileEntity {

    private final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(9));

    public ItemStackHandler getInventory() {
        return this.inventory.orElseThrow(RuntimeException::new);
    }

    public ChestSlabTileEntity() {
        super(DSTTiles.CHEST.get());
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        this.getInventory().deserializeNBT(nbt.getCompound("inventory"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("inventory", this.getInventory().serializeNBT());
        return super.write(nbt);
    }

    @Override
    public CompoundNBT getTileData() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return new SUpdateTileEntityPacket(getPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        this.read(pkt.getNbtCompound());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.inventory.cast() : LazyOptional.empty();
    }
}
