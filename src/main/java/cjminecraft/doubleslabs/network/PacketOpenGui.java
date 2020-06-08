package cjminecraft.doubleslabs.network;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.OpenGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenGui implements IMessage {

    private int windowId;
    private String modId;
    private int modGuiId;
    private int x;
    private int y;
    private int z;

    private boolean positive;

    public PacketOpenGui() {

    }

    public PacketOpenGui(int windowId, String modId, int modGuiId, int x, int y, int z, boolean positive) {
        this.windowId = windowId;
        this.modId = modId;
        this.modGuiId = modGuiId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.positive = positive;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.modId = ByteBufUtils.readUTF8String(buf);
        this.modGuiId = buf.readInt();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.positive = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.windowId);
        ByteBufUtils.writeUTF8String(buf, this.modId);
        buf.writeInt(this.modGuiId);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        buf.writeBoolean(this.positive);
    }

    public static class Handler implements IMessageHandler<PacketOpenGui, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenGui message, MessageContext ctx) {
            IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
            if (thread.isCallingFromMinecraftThread())
            {
                process(message);
            }
            else
            {
                thread.addScheduledTask(() -> Handler.this.process(message));
            }
            return null;
        }

        void process(PacketOpenGui message) {
            EntityPlayer player = FMLClientHandler.instance().getClient().player;
            World world = player.world;
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof TileEntityVerticalSlab)
                world = message.positive ? ((TileEntityVerticalSlab) tile).getPositiveWorld() : ((TileEntityVerticalSlab) tile).getNegativeWorld();
            tile = world.getTileEntity(pos);
            if (tile != null)
                tile.setWorld(world);
            player.openGui(message.modId, message.modGuiId, world, message.x, message.y, message.z);
            player.openContainer.windowId = message.windowId;
        }
    }
}
