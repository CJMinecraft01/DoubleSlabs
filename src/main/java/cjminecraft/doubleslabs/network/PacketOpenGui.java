package cjminecraft.doubleslabs.network;

import cjminecraft.doubleslabs.api.Flags;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenGui {

    private final int id;
    private final int windowId;
    private final ITextComponent name;
    private final PacketBuffer additionalData;
    private final boolean positive;

    PacketOpenGui(ContainerType<?> id, int windowId, ITextComponent name, PacketBuffer additionalData, boolean positive) {
        this(Registry.MENU.getId(id), windowId, name, additionalData, positive);
    }

    private PacketOpenGui(int id, int windowId, ITextComponent name, PacketBuffer additionalData, boolean positive) {
        this.id = id;
        this.windowId = windowId;
        this.name = name;
        this.additionalData = additionalData;
        this.positive = positive;
    }

    public static void encode(PacketOpenGui msg, PacketBuffer buf) {
        buf.writeVarInt(msg.id);
        buf.writeVarInt(msg.windowId);
        buf.writeTextComponent(msg.name);
        buf.writeByteArray(msg.additionalData.readByteArray());
        buf.writeBoolean(msg.positive);
    }

    public static PacketOpenGui decode(PacketBuffer buf) {
        return new PacketOpenGui(buf.readVarInt(), buf.readVarInt(), buf.readTextComponent(), new PacketBuffer(Unpooled.wrappedBuffer(buf.readByteArray(32600))), buf.readBoolean());
    }

    public static void handle(PacketOpenGui msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ScreenManager.getScreenFactory(msg.getType(), Minecraft.getInstance(), msg.getWindowId(), msg.getName())
                    .ifPresent(f -> {
                        BlockPos pos = new PacketBuffer(msg.getAdditionalData().copy()).readBlockPos();
                        Flags.setPositive(pos, msg.positive);
                        Container c = msg.getType().create(msg.getWindowId(), Minecraft.getInstance().player.inventory, msg.getAdditionalData());
                        @SuppressWarnings("unchecked")
                        Screen s = ((ScreenManager.IScreenFactory<Container, ?>) f).create(c, Minecraft.getInstance().player.inventory, msg.getName());
                        Minecraft.getInstance().player.openContainer = ((IHasContainer<?>) s).getContainer();
                        Minecraft.getInstance().displayGuiScreen(s);
                    });
        });
        ctx.get().setPacketHandled(true);
    }

    public final ContainerType<?> getType() {
        return Registry.MENU.getByValue(this.id);
    }

    public int getWindowId() {
        return windowId;
    }

    public ITextComponent getName() {
        return name;
    }

    public PacketBuffer getAdditionalData() {
        return additionalData;
    }
}
