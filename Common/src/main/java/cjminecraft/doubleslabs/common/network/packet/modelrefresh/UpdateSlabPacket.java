package cjminecraft.doubleslabs.common.network.packet.modelrefresh;

import cjminecraft.doubleslabs.common.block.entity.SlabBlockEntity;
import cjminecraft.doubleslabs.common.network.INetworkContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.function.Supplier;

public record UpdateSlabPacket(BlockPos pos, boolean positive, BlockState newState) {

    public static UpdateSlabPacket decode(FriendlyByteBuf buf) {
        return new UpdateSlabPacket(buf.readBlockPos(), buf.readBoolean(), NbtUtils.readBlockState(Objects.requireNonNull(buf.readNbt())));
    }

    public static void handle(UpdateSlabPacket message, Supplier<INetworkContext> ctxSupplier) {
        INetworkContext ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.isLoaded(message.pos)) {
                BlockEntity entity = Minecraft.getInstance().level.getBlockEntity(message.pos);
                if (entity instanceof SlabBlockEntity<?> slab) {
                    if (message.positive)
                        slab.getPositiveBlockInfo().setBlockState(message.newState);
                    else
                        slab.getNegativeBlockInfo().setBlockState(message.newState);
                }
            }
        });
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.positive);
        buf.writeNbt(NbtUtils.writeBlockState(this.newState));
    }

}
