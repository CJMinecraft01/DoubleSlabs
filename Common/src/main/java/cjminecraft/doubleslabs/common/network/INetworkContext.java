package cjminecraft.doubleslabs.common.network;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface INetworkContext {

    @Nullable ServerPlayer getSender();

    void enqueueWork(Runnable runnable);

}
