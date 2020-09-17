package cjminecraft.doubleslabs.common.container;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.client.gui.WrappedGui;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static final int WRAPPER_POSITIVE = 0;
    public static final int WRAPPER_NEGATIVE = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof SlabTileEntity) {
            boolean positive = ID == WRAPPER_POSITIVE;
            IBlockInfo blockInfo = positive ? ((SlabTileEntity) tileEntity).getPositiveBlockInfo() : ((SlabTileEntity) tileEntity).getNegativeBlockInfo();
            if (blockInfo.getBlockState() != null) {
                IContainerSupport support = ContainerSupport.getSupport(world, tileEntity.getPos(), blockInfo.getBlockState());
                if (support != null)
                    return new WrappedContainer(player, support.getModInstance(), support.getId(world, pos, blockInfo.getBlockState(), player, RayTraceUtil.rayTrace(player)), blockInfo, x, y, z);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        WrappedContainer container = (WrappedContainer) getServerGuiElement(ID, player, world, x, y, z);
        if (container != null)
            return new WrappedGui(container, x, y, z);
        return null;
    }
}
