package cjminecraft.doubleslabs.tileentitiy;

import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.util.WorldWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityVerticalSlab extends TileEntity implements ITickableTileEntity {

    public static final ModelProperty<BlockState> NEGATIVE_STATE = new ModelProperty<>();
    public static final ModelProperty<BlockState> POSITIVE_STATE = new ModelProperty<>();

    protected BlockState negativeState;
    protected BlockState positiveState;

    protected TileEntity negativeTile;
    protected TileEntity positiveTile;

    private WorldWrapper negativeWorld;
    private WorldWrapper positiveWorld;

    public TileEntityVerticalSlab() {
        super(Registrar.TILE_VERTICAL_SLAB);
    }

    protected TileEntityVerticalSlab(TileEntityType<?> type) {
        super(type);
    }

    public WorldWrapper getNegativeWorld() {
        return this.negativeWorld;
    }

    public WorldWrapper getPositiveWorld() {
        return this.positiveWorld;
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);
        this.negativeWorld = new WorldWrapper(world);
        this.negativeWorld.setVerticalSlab(this, false);
        this.positiveWorld = new WorldWrapper(world);
        this.positiveWorld.setVerticalSlab(this, true);

        if (this.negativeTile != null)
            this.negativeTile.setWorldAndPos(this.negativeWorld, this.pos);
        if (this.positiveTile != null)
            this.positiveTile.setWorldAndPos(this.positiveWorld, this.pos);
    }

    public BlockState getNegativeState() {
        return this.negativeState;
    }

    public BlockState getPositiveState() {
        return this.positiveState;
    }

    public void setNegativeState(BlockState negativeState) {
        if (this.negativeState != null && this.negativeState.hasTileEntity() && this.negativeTile != null)
            this.negativeTile.updateContainingBlockInfo();
        if (this.negativeState != null && negativeState != null && (this.negativeState.getBlock() != negativeState.getBlock() || !negativeState.hasTileEntity()) && this.negativeState.hasTileEntity())
            setNegativeTile(null);
        if (this.negativeState == null)
            setNegativeTile(null);
        this.negativeState = negativeState;
        if (negativeState != null && negativeState.hasTileEntity())
            if (this.negativeTile == null)
                setNegativeTile(negativeState.createTileEntity(this.negativeWorld));
            else
                this.negativeTile.updateContainingBlockInfo();
        markDirtyClient();
    }

    public void setPositiveState(BlockState positiveState) {
        if (this.positiveState != null && this.positiveState.hasTileEntity() && this.positiveTile != null)
            this.positiveTile.updateContainingBlockInfo();
        if (this.positiveState != null && positiveState != null && (this.positiveState.getBlock() != positiveState.getBlock() || !positiveState.hasTileEntity()) && this.positiveState.hasTileEntity())
            setPositiveTile(null);
        if (this.positiveState == null)
            setPositiveTile(null);
        this.positiveState = positiveState;
        if (positiveState != null && positiveState.hasTileEntity())
            if (this.positiveTile == null)
                setPositiveTile(positiveState.createTileEntity(this.positiveWorld));
            else
                this.positiveTile.updateContainingBlockInfo();
        markDirtyClient();
    }

    public TileEntity getNegativeTile() {
        return this.negativeTile;
    }

    public void setNegativeTile(TileEntity negativeTile) {
        prepareTile(negativeTile, false);
        this.negativeTile = negativeTile;
    }

    public TileEntity getPositiveTile() {
        return this.positiveTile;
    }

    public void setPositiveTile(TileEntity positiveTile) {
        prepareTile(positiveTile, true);
        this.positiveTile = positiveTile;
    }

    private void prepareTile(TileEntity tile, boolean positive) {
        if (tile != null) {
            tile.setWorldAndPos(this.world, this.pos);
            if (positive)
                if (this.positiveTile != null)
                    this.positiveTile.remove();
            else
                if (this.negativeTile != null)
                    this.negativeTile.remove();
            tile.onLoad();
            tile.setWorldAndPos(positive ? this.positiveWorld : this.negativeWorld, this.pos);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        if (this.negativeState != null)
            nbt.put("negative", NBTUtil.writeBlockState(this.negativeState));
        if (this.negativeTile != null)
            nbt.put("negative_tile", this.negativeTile.write(new CompoundNBT()));
        if (this.positiveState != null)
            nbt.put("positive", NBTUtil.writeBlockState(this.positiveState));
        if (this.positiveTile != null)
            nbt.put("positive_tile", this.positiveTile.write(new CompoundNBT()));
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT read) {
        super.read(read);
        if (read.contains("negative"))
            this.negativeState = NBTUtil.readBlockState(read.getCompound("negative"));
        if (read.contains("negative_tile"))
            this.negativeTile = TileEntity.create(read.getCompound("negative_tile"));
        if (read.contains("positive"))
            this.positiveState = NBTUtil.readBlockState(read.getCompound("positive"));
        if (read.contains("positive_tile"))
            this.positiveTile = TileEntity.create(read.getCompound("positive_tile"));
        markDirtyClient();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.write(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.write(nbt);
        return new SUpdateTileEntityPacket(getPos(), 0, nbt);
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        super.handleUpdateTag(tag);
        this.read(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        BlockState oldTopState = this.negativeState;
        BlockState oldBottomState = this.positiveState;
        this.read(pkt.getNbtCompound());
        if (this.world.isRemote)
            if (oldTopState != this.negativeState || oldBottomState != this.positiveState)
                this.world.markChunkDirty(getPos(), getTileEntity());
    }

    @Override
    public CompoundNBT getTileData() {
        return this.write(new CompoundNBT());
    }

    private void markDirtyClient() {
        markDirty();
        if (this.world != null) {
            BlockState state = this.world.getBlockState(getPos());
            requestModelDataUpdate();
            this.world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(NEGATIVE_STATE, this.negativeState).withInitial(POSITIVE_STATE, this.positiveState).build();
    }

    @Override
    public void tick() {
        if (this.world != null && this.positiveWorld != null && this.negativeWorld != null) {
            if (this.positiveTile != null && this.positiveTile instanceof ITickableTileEntity) {
                if (this.positiveTile.getWorld() == null)
                    this.positiveTile.setWorldAndPos(this.positiveWorld, this.pos);
                ((ITickableTileEntity) this.positiveTile).tick();
            }
            if (this.negativeTile != null && this.negativeTile instanceof ITickableTileEntity) {
                if (this.negativeTile.getWorld() == null)
                    this.negativeTile.setWorldAndPos(this.negativeWorld, this.pos);
                ((ITickableTileEntity) this.negativeTile).tick();
            }
        }
    }

    @Override
    public void onLoad() {
        if (this.positiveTile != null) {
            this.positiveTile.setWorldAndPos(this.positiveWorld, this.pos);
            this.positiveTile.onLoad();
        }
        if (this.negativeTile != null) {
            this.negativeTile.setWorldAndPos(this.negativeWorld, this.pos);
            this.negativeTile.onLoad();
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.positiveTile != null)
            this.positiveTile.onChunkUnloaded();
        if (this.negativeTile != null)
            this.negativeTile.onChunkUnloaded();
    }

    @Override
    public void rotate(Rotation rotation) {
        super.rotate(rotation);
        if (this.positiveTile != null)
            this.positiveTile.rotate(rotation);
        if (this.negativeTile != null)
            this.negativeTile.rotate(rotation);
    }

    @Override
    public void remove() {
        super.remove();
        if (this.positiveTile != null) {
            this.positiveTile.setWorldAndPos(this.world, this.pos);
            this.positiveTile.remove();
        }
        if (this.negativeTile != null) {
            this.negativeTile.setWorldAndPos(this.world, this.pos);
            this.negativeTile.remove();
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (this.positiveTile != null)
            this.positiveTile.validate();
        if (this.negativeTile != null)
            this.negativeTile.validate();
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.positiveTile != null)
            this.positiveTile.updateContainingBlockInfo();
        if (this.negativeTile != null)
            this.negativeTile.updateContainingBlockInfo();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.positiveTile != null && this.negativeTile == null)
            return this.positiveTile.getCapability(cap, side);
        else if (this.negativeTile != null && this.positiveTile == null)
            return this.negativeTile.getCapability(cap, side);
        else if (this.positiveTile != null && this.negativeTile != null) {
            LazyOptional<T> positiveCapability = this.positiveTile.getCapability(cap, side);
            return positiveCapability.isPresent() ? positiveCapability : this.negativeTile.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }
}
