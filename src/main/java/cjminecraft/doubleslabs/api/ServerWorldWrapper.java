package cjminecraft.doubleslabs.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;

public class ServerWorldWrapper extends WorldServer implements IWorldWrapper<WorldServer> {

    private static final Field WEAK_WORLD_MAP;

    static {
        Field weakWorldMap;
        try {
            weakWorldMap = DimensionManager.class.getDeclaredField("weakWorldMap");
            weakWorldMap.setAccessible(true);
        } catch (NoSuchFieldException e) {
            weakWorldMap = null;
        }
        WEAK_WORLD_MAP = weakWorldMap;
    }

    private static void patch(ServerWorldWrapper instance) {
        if (WEAK_WORLD_MAP == null)
            return;
        try {
            ConcurrentMap<World, World> map = (ConcurrentMap<World, World>) WEAK_WORLD_MAP.get(null);
            map.remove(instance);
        } catch (IllegalAccessException ignored) {

        }
    }

    private WorldServer world;
    private boolean positive;
    private BlockPos pos;
    private IStateContainer container;

    public ServerWorldWrapper(WorldServer world) {
        super(world.getMinecraftServer(), world.getSaveHandler(), world.getWorldInfo(), world.provider.getDimension(), world.profiler);
        this.world = world;
        DimensionManager.setWorld(world.getWorldType().getId(), world, world.getMinecraftServer());
        patch(this);
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
    public World getWorld() {
        return this.world;
    }

    @Override
    public void setWorld(World world) {
        this.world = (WorldServer) world;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.pos)) {
            IBlockState state = (this.positive ? this.container.getPositiveBlockInfo().getBlockState() : this.container.getNegativeBlockInfo().getBlockState());
            return state != null ? state : this.world.getBlockState(pos);
        }
        return this.world.getBlockState(pos);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return pos.equals(this.pos) ? (this.positive ? this.container.getPositiveBlockInfo().getTileEntity() : this.container.getNegativeBlockInfo().getTileEntity()) : this.world.getTileEntity(pos);
    }

    @Override
    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setTileEntity(tileEntityIn);
            else
                this.container.getNegativeBlockInfo().setTileEntity(tileEntityIn);
        } else {
            this.world.setTileEntity(pos, tileEntityIn);
        }
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state) {
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
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockState(newState);
            else
                this.container.getNegativeBlockInfo().setBlockState(newState);
            return true;
        } else {
            return this.world.setBlockState(pos, newState, flags);
        }
    }
}
