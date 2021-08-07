package cjminecraft.doubleslabs.common.tileentity;

import cjminecraft.doubleslabs.api.BlockInfo;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.IStateContainer;
import cjminecraft.doubleslabs.api.IWorldWrapper;
import cjminecraft.doubleslabs.client.model.DynamicSlabBakedModel;
import cjminecraft.doubleslabs.common.init.DSTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlabTileEntity extends BlockEntity implements IStateContainer {

    protected final BlockInfo negativeBlockInfo = new BlockInfo(this, false);
    protected final BlockInfo positiveBlockInfo = new BlockInfo(this, true);

    public SlabTileEntity(BlockPos pos, BlockState state) {
        super(DSTiles.DYNAMIC_SLAB.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.negativeBlockInfo.deserializeNBT(nbt.getCompound("negativeBlock"));
        this.positiveBlockInfo.deserializeNBT(nbt.getCompound("positiveBlock"));
        super.load(nbt);
        markDirtyClient();
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.put("negativeBlock", this.negativeBlockInfo.serializeNBT());
        nbt.put("positiveBlock", this.positiveBlockInfo.serializeNBT());
        return super.save(nbt);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.deserializeNBT(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.deserializeNBT(pkt.getTag());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.serializeNBT());
    }

    @Override
    public CompoundTag getTileData() {
        return this.serializeNBT();
    }

    @Override
    public void markDirty() {
        markDirtyClient();
    }

    public void markDirtyClient() {
        setChanged();
        requestModelDataUpdate();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, Constants.BlockFlags.DEFAULT);
            this.level.getLightEngine().checkBlock(this.worldPosition);
        }
    }

    @Override
    public void setLevel(Level world) {
        // prevent a circular reference
        if (world instanceof IWorldWrapper<?>)
            return;
        super.setLevel(world);
        this.negativeBlockInfo.setWorld(world);
        this.positiveBlockInfo.setWorld(world);
    }

    @Override
    public IBlockInfo getPositiveBlockInfo() {
        return this.positiveBlockInfo;
    }

    @Override
    public IBlockInfo getNegativeBlockInfo() {
        return this.negativeBlockInfo;
    }

    @Override
    public void onLoad() {
        this.negativeBlockInfo.onLoad();
        this.positiveBlockInfo.onLoad();
    }

    @Override
    public void onChunkUnloaded() {
        this.negativeBlockInfo.onChunkUnloaded();
        this.positiveBlockInfo.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.negativeBlockInfo.remove();
        this.positiveBlockInfo.remove();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        LazyOptional<T> negativeCapability = this.negativeBlockInfo.getCapability(cap);
        return negativeCapability.isPresent() ? negativeCapability : this.positiveBlockInfo.getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        LazyOptional<T> negativeCapability = this.negativeBlockInfo.getCapability(cap, side);
        return negativeCapability.isPresent() ? negativeCapability : this.positiveBlockInfo.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(DynamicSlabBakedModel.NEGATIVE_BLOCK, this.negativeBlockInfo).withInitial(DynamicSlabBakedModel.POSITIVE_BLOCK, this.positiveBlockInfo).build();
    }

    @Override
    public boolean triggerEvent(int pA, int pB) {
        return this.negativeBlockInfo.triggerEvent(pA, pB) | this.positiveBlockInfo.triggerEvent(pA, pB);
    }

    @SuppressWarnings("unchecked")
    public static <E extends SlabTileEntity, A extends BlockEntity, B extends BlockEntity> void tick(Level world, BlockPos blockPos, BlockState blockState, E entity) {
        if (world != null) {
            if (entity.positiveBlockInfo.getBlockEntity() != null && entity.positiveBlockInfo.getBlockState() != null) {
                BlockEntityTicker<A> ticker = (BlockEntityTicker<A>) entity.positiveBlockInfo.getBlockState().getTicker(entity.getLevel(), entity.positiveBlockInfo.getBlockEntity().getType());
                if (ticker != null) {
                    if (entity.positiveBlockInfo.getBlockEntity().getLevel() == null)
                        entity.positiveBlockInfo.getBlockEntity().setLevel(entity.positiveBlockInfo.getWorld());
                    ticker.tick(entity.positiveBlockInfo.getWorld(), blockPos, entity.positiveBlockInfo.getBlockState(), (A) entity.positiveBlockInfo.getBlockEntity());
                }
            }
            if (entity.negativeBlockInfo.getBlockEntity() != null && entity.negativeBlockInfo.getBlockState() != null) {
                BlockEntityTicker<B> ticker = (BlockEntityTicker<B>) entity.negativeBlockInfo.getBlockState().getTicker(entity.getLevel(), entity.negativeBlockInfo.getBlockEntity().getType());
                if (ticker != null) {
                    if (entity.negativeBlockInfo.getBlockEntity().getLevel() == null)
                        entity.negativeBlockInfo.getBlockEntity().setLevel(entity.negativeBlockInfo.getWorld());
                    ticker.tick(entity.negativeBlockInfo.getWorld(), blockPos, entity.negativeBlockInfo.getBlockState(), (B) entity.negativeBlockInfo.getBlockEntity());
                }
            }
        }
    }
}
