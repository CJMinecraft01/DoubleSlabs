package cjminecraft.doubleslabs.network;

import cjminecraft.doubleslabs.DoubleSlabs;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkUtils {

    public static void openGui(EntityPlayer entityPlayer, Object mod, int modGuiId, World world, int x, int y, int z, boolean positive)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (entityPlayer instanceof EntityPlayerMP && !(entityPlayer instanceof FakePlayer))
        {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityPlayer;
            Container remoteGuiContainer = NetworkRegistry.INSTANCE.getRemoteGuiContainer(mc, entityPlayerMP, modGuiId, world, x, y, z);
            if (remoteGuiContainer != null)
            {
                entityPlayerMP.getNextWindowId();
                entityPlayerMP.closeContainer();
                int windowId = entityPlayerMP.currentWindowId;
                PacketOpenGui openGui = new PacketOpenGui(windowId, mc.getModId(), modGuiId, x, y, z, positive);
                PacketHandler.INSTANCE.sendTo(openGui, entityPlayerMP);
//                FMLMessage.OpenGui openGui = new FMLMessage.OpenGui(windowId, mc.getModId(), modGuiId, x, y, z);
//                EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
//                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
//                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(entityPlayerMP);
//                embeddedChannel.writeOutbound(openGui);
                entityPlayerMP.openContainer = remoteGuiContainer;
                entityPlayerMP.openContainer.windowId = windowId;
                entityPlayerMP.openContainer.addListener(entityPlayerMP);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(entityPlayer, entityPlayer.openContainer));
            }
        }
        else if (entityPlayer instanceof FakePlayer)
        {
            // NO OP - I won't even log a message!
        }
        else if (FMLCommonHandler.instance().getSide().equals(Side.CLIENT))
        {
            Object guiContainer = NetworkRegistry.INSTANCE.getLocalGuiContainer(mc, entityPlayer, modGuiId, world, x, y, z);
            FMLCommonHandler.instance().showGuiScreen(guiContainer);
        }
        else
        {
            DoubleSlabs.LOGGER.debug("Invalid attempt to open a local GUI on a dedicated server. This is likely a bug. GUI ID: {},{}", mc.getModId(), modGuiId);
        }

    }

}
