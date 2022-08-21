package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class BlockInfo implements IBlockInfo {

    protected ISlabSupport support;
    protected BlockState state;
    protected BlockEntity blockEntity;
    protected Level level;

    private final SlabBlockEntity<?> slab;
    private final boolean positive;

    public BlockInfo(SlabBlockEntity<?> slab, boolean positive) {
        this.slab = slab;
        this.positive = positive;
    }

    @Nullable
    @Override
    public BlockState getBlockState() {
        return state;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @NotNull
    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public boolean isPositive() {
        return positive;
    }

    @Override
    public BlockPos getPos() {
        return slab.getBlockPos();
    }

    @Nullable
    @Override
    public ISlabSupport getSupport() {
        return support;
    }

    @Override
    public void setBlockState(@Nullable BlockState state) {
        if (this.state != null && this.state.hasBlockEntity() && this.blockEntity != null)
            this.blockEntity.setBlockState(this.state);
        if (this.state != null && state != null && (this.state.getBlock() != state.getBlock() || !state.hasBlockEntity()) && this.state.hasBlockEntity())
            setBlockEntity(null);
        if (this.state == null)
            setBlockEntity(null);

        this.state = state;
        this.support = state == null ? null : SlabSupport.getSlabSupport(this.level, this.getPos(), this.state);

        if (this.state != null && this.support != null && this.support.requiresWrappedWorld(this.state)) {
            if (!(this.level instanceof ILevelWrapper<?>))
                wrapLevel(this.level);
            if (state.hasBlockEntity() && this.blockEntity == null)
                setBlockEntity(((EntityBlock) state.getBlock()).newBlockEntity(this.getPos(), this.state));
            else if (this.blockEntity != null) {
                this.blockEntity.setBlockState(this.state);
            }
        }

        this.slab.markDirtyClient();
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
        if (blockEntity != null) {
            Preconditions.checkNotNull(slab.getLevel());
            blockEntity.setLevel(slab.getLevel());
            if (this.blockEntity != null) {
                this.blockEntity.setLevel(slab.getLevel());
                this.blockEntity.setRemoved();
            }
            loadBlockEntity(blockEntity);

            if (!(this.level instanceof ILevelWrapper<?>))
                wrapLevel(this.level);

            blockEntity.setLevel(this.level);
        }
        this.blockEntity = blockEntity;
    }

    private void wrapLevel(Level level) {
        ILevelWrapper<?> l = createWrappedLevel(level);
        l.setPositive(positive);
        l.setBlockPos(getPos());
        l.setStateContainer(slab);
        this.level = (Level) l;
    }

    public void setLevel(Level level) {
        if (this.level != null && this.level instanceof ILevelWrapper<?>)
            ((ILevelWrapper<?>) this.level).setLevel(level);
        else if (this.blockEntity != null || (this.support != null && this.state != null && this.support.requiresWrappedWorld(this.state)))
            this.level = (Level) createWrappedLevel(level);
        else if (level != null)
            this.level = level;

        if (this.level instanceof ILevelWrapper<?> w) {
            w.setPositive(this.positive);
            w.setBlockPos(this.slab.getBlockPos());
            w.setStateContainer(this.slab);
        }

        if (this.blockEntity != null)
            this.blockEntity.setLevel(this.level);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (state != null)
            tag.put("state", NbtUtils.writeBlockState(state));
        if (blockEntity != null)
            tag.put("block_entity", blockEntity.saveWithId());
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("state")) {
            this.state = NbtUtils.readBlockState(tag.getCompound("state"));
            this.support = SlabSupport.getSlabSupport(this.level, this.getPos(), this.state);
        }
        if (tag.contains("tile") || tag.contains("block_entity")) {
            CompoundTag blockEntityTag = tag.contains("tile") ? tag.getCompound("tile") : tag.getCompound("block_entity");
            this.blockEntity = BlockEntity.loadStatic(this.getPos(), this.state, blockEntityTag);
        }
        if ((this.blockEntity != null || (this.support != null && this.state != null && this.support.requiresWrappedWorld(this.state))) && this.level != null && !(this.level instanceof ILevelWrapper<?>))
            this.wrapLevel(this.level);
    }

    public void setRemoved() {
        if (blockEntity != null) {
            blockEntity.setLevel(Objects.requireNonNull(slab.getLevel()));
            blockEntity.setRemoved();
            blockEntity = null;
        }
    }

    public boolean triggerEvent(int pA, int pB) {
        return blockEntity != null && blockEntity.triggerEvent(pA, pB);
    }

    public ILevelWrapper<?> createWrappedLevel(Level level) {
        return level instanceof ServerLevel ? new ServerLevelWrapper((ServerLevel) level) : new LevelWrapper(level);
    }

    public void loadBlockEntity(BlockEntity blockEntity) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockInfo blockInfo = (BlockInfo) o;
        return positive == blockInfo.positive &&
                Objects.equals(state, blockInfo.state) &&
                Objects.equals(blockEntity, blockInfo.blockEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, blockEntity, positive);
    }

}
