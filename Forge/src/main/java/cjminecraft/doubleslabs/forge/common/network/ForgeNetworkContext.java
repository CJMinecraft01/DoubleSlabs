package cjminecraft.doubleslabs.forge.common.network;

import cjminecraft.doubleslabs.common.network.INetworkContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

public class ForgeNetworkContext implements INetworkContext {

    private final NetworkEvent.Context ctx;

    public ForgeNetworkContext(NetworkEvent.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public @Nullable ServerPlayer getSender() {
        return ctx.getSender();
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        ctx.enqueueWork(runnable);
    }
}
