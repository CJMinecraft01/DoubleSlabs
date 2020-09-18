package cjminecraft.doubleslabs.test.common.container;

import cjminecraft.doubleslabs.test.client.gui.ChestSlabScreen;
import cjminecraft.doubleslabs.test.common.tileentity.ChestSlabTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static final int CHEST_SLAB = 0;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case CHEST_SLAB:
                return new ChestSlabContainer(player.inventory, (ChestSlabTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case CHEST_SLAB:
                return new ChestSlabScreen(player.inventory, (ChestSlabTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}
