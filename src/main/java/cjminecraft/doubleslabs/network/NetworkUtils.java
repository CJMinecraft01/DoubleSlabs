package cjminecraft.doubleslabs.network;

import cjminecraft.doubleslabs.util.WorldWrapper;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class NetworkUtils {

    public static void openGui(ServerPlayerEntity player, INamedContainerProvider containerSupplier, BlockPos pos, WorldWrapper world, boolean positive)
    {
        if (player.world.isRemote) return;
        player.closeContainer();
        player.getNextWindowId();
        int openContainerId = player.currentWindowId;
        PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
        extraData.writeBlockPos(pos);

        PacketBuffer output = new PacketBuffer(Unpooled.buffer());
        output.writeVarInt(extraData.readableBytes());
        output.writeBytes(extraData);

        if (output.readableBytes() > 32600 || output.readableBytes() < 1) {
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found "+ output.readableBytes()+ " bytes");
        }
        player.setWorld(world);
        Container c = containerSupplier.createMenu(openContainerId, player.inventory, player);
        player.setWorld(world.getWorld());
        ContainerType<?> type = c.getType();
        PacketOpenGui msg = new PacketOpenGui(type, openContainerId, containerSupplier.getDisplayName(), output, positive);
        PacketHandler.INSTANCE.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);

        player.openContainer = c;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
    }

}
