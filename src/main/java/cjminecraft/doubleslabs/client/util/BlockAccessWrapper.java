package cjminecraft.doubleslabs.client.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class BlockAccessWrapper implements IBlockAccess {

    private final IBlockAccess world;
    private final BlockPos pos;
    private final BlockPos otherPos;
    private final IBlockState block;
    private final IBlockState otherBlock;

    public BlockAccessWrapper(IBlockAccess world, BlockPos pos, BlockPos otherPos, IBlockState block, IBlockState otherBlock) {
        this.world = world;
        this.pos = pos;
        this.otherPos = otherPos;
        this.block = block;
        this.otherBlock = otherBlock;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return world.getTileEntity(pos);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return world.getCombinedLight(pos, lightValue);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return pos.equals(this.pos) ? block : pos.equals(otherPos) ? otherBlock : world.getBlockState(pos);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return world.isAirBlock(pos);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return world.getBiome(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return world.getStrongPower(pos, direction);
    }

    @Override
    public WorldType getWorldType() {
        return world.getWorldType();
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return world.isSideSolid(pos, side, _default);
    }
}
