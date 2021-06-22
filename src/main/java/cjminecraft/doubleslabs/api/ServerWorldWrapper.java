package cjminecraft.doubleslabs.api;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ServerWorldWrapper extends ServerWorld implements IWorldWrapper<ServerWorld> {
    private ServerWorld world;
    private boolean positive;
    private BlockPos pos;
    private IStateContainer container;

    public ServerWorldWrapper(ServerWorld world) {
        super(world.getServer(), Util.getServerExecutor(), world.getServer().anvilConverterForAnvilFile, (IServerWorldInfo) world.getWorldInfo(), world.getDimensionKey(), world.func_230315_m_(), new IChunkStatusListener() {
            @Override
            public void start(ChunkPos center) {

            }

            @Override
            public void statusChanged(ChunkPos chunkPosition, @Nullable ChunkStatus newStatus) {

            }

            @Override
            public void stop() {

            }
        }, world.getChunkProvider().generator, world.isDebug(), world.getSeed(), new ArrayList<>(), false);
        this.world = world;
        super.initCapabilities();
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
    public void setWorld(World world) {
        this.world = (ServerWorld) world;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return pos.equals(this.pos) ? (this.positive ? this.container.getPositiveBlockInfo().getBlockState() : this.container.getNegativeBlockInfo().getBlockState()) : super.getBlockState(pos);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return pos.equals(this.pos) ? (this.positive ? this.container.getPositiveBlockInfo().getTileEntity() : this.container.getNegativeBlockInfo().getTileEntity()) : super.getTileEntity(pos);
    }

    @Override
    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setTileEntity(tileEntityIn);
            else
                this.container.getNegativeBlockInfo().setTileEntity(tileEntityIn);
        } else {
            super.setTileEntity(pos, tileEntityIn);
        }
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockState(state);
            else
                this.container.getNegativeBlockInfo().setBlockState(state);
            return true;
        }
        return this.world.setBlockState(pos, state);
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        if (pos.equals(this.pos))
            if (this.positive)
                this.container.getPositiveBlockInfo().setTileEntity(null);
            else
                this.container.getNegativeBlockInfo().setTileEntity(null);
        else
            this.world.removeTileEntity(pos);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
        return this.setBlockState(pos, newState, flags, 512);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int p_241211_4_) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockState(state);
            else
                this.container.getNegativeBlockInfo().setBlockState(state);
            return true;
        } else {
            return super.setBlockState(pos, state, flags, p_241211_4_);
        }
    }

}
