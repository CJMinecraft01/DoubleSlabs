package cjminecraft.doubleslabs.api;

import cjminecraft.doubleslabs.common.network.packet.modelrefresh.UpdateSlabPacket;
import cjminecraft.doubleslabs.platform.Services;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerLevelWrapper extends ServerLevel implements ILevelWrapper<ServerLevel> {
    private ServerLevel level;
    private boolean positive;
    private BlockPos pos;
    private IStateContainer container;

    public ServerLevelWrapper(ServerLevel level) { // todo: check below
        super(level.getServer(), Util.backgroundExecutor(), Services.PLATFORM.getStorageFromServer(level.getServer()), (ServerLevelData) level.getLevelData(), level.dimension(), level.getServer().getWorldData().worldGenSettings().dimensions().get(level.dimension().location()), new ChunkProgressListener() {
            @Override
            public void updateSpawnPos(@NotNull ChunkPos pos) {

            }

            @Override
            public void onStatusChange(@NotNull ChunkPos pos, @Nullable ChunkStatus p_9619_) {

            }

            @Override
            public void start() {

            }

            @Override
            public void stop() {

            }
        }, level.isDebug(), level.getSeed(), Lists.newArrayList(), false);
        this.level = level;
        // todo: init capabilities
//        super.initCapabilities();
    }

    @Override
    public boolean isPositive() {
        return this.positive;
    }

    @Override
    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public IStateContainer getStateContainer() {
        return this.container;
    }

    @Override
    public void setStateContainer(IStateContainer container) {
        this.container = container;
    }

    @Override
    public @NotNull ServerLevel getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(Level world) {
        this.level = (ServerLevel) world;
    }

    @Override
    public void blockEntityChanged(@NotNull BlockPos pos) {
        if (this.pos.equals(pos))
            this.container.markDirty();
        else
            super.blockEntityChanged(pos);
    }

    @Override
    public boolean addFreshEntity(@NotNull Entity entity) {
        return this.level.addFreshEntity(entity);
    }

    @Override
    @Nonnull
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.pos)) {
            BlockState state = this.positive ? this.container.getPositiveBlockInfo().getBlockState() : this.container.getNegativeBlockInfo().getBlockState();
            if (state != null)
                return state;
        }
        return super.getBlockState(pos);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return pos.equals(this.pos) ? (this.positive ? this.container.getPositiveBlockInfo().getBlockEntity() : this.container.getNegativeBlockInfo().getBlockEntity()) : super.getBlockEntity(pos);
    }

    @Override
    public void setBlockEntity(@Nullable BlockEntity entity) {
        if (entity != null && entity.getBlockPos().equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockEntity(entity);
            else
                this.container.getNegativeBlockInfo().setBlockEntity(entity);
        } else {
            super.setBlockEntity(entity);
        }
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
        if (pos.equals(this.pos))
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockEntity(null);
            else
                this.container.getNegativeBlockInfo().setBlockEntity(null);
        else
            this.level.removeBlockEntity(pos);
    }

    @Override
    public boolean setBlock(@NotNull BlockPos pos, @NotNull BlockState newState, int flags) {
        return this.setBlock(pos, newState, flags, 512);
    }

    @Override
    public boolean setBlock(BlockPos pos, @NotNull BlockState state, int flags, int height) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockState(state);
            else
                this.container.getNegativeBlockInfo().setBlockState(state);
            if (state.getBlock() instanceof ChangeOverTimeBlock<?>)
                Services.NETWORK.sendToLevelClients(this.level, new UpdateSlabPacket(pos, this.positive, state));
            return true;
        } else {
            return super.setBlock(pos, state, flags, height);
        }
    }

}
