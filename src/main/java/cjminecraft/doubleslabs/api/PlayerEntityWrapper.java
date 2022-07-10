package cjminecraft.doubleslabs.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

// todo override more?
public class PlayerEntityWrapper extends Player {

    private final Player player;

    public PlayerEntityWrapper(Player player, Level world) {
        super(world, player.getOnPos(), player.yRotO, player.getGameProfile(), player.getProfilePublicKey());
//        super(player.server, player.getServerWorld(), player.getGameProfile(), player.interactionManager);
        this.player = player;
        this.level = world;
    }

//    @Override
//    public void readAdditionalSaveData(CompoundTag tag) {
//        this.player.readAdditionalSaveData(tag);
//    }
//
//    @Override
//    public void addAdditionalSaveData(CompoundTag tag) {
//        this.player.addAdditionalSaveData(tag);
//    }

    @Override
    public boolean isSpectator() {
        return this.player.isSpectator();
    }

    @Override
    public boolean isCreative() {
        return this.player.isCreative();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return this.player.getCapability(cap);
    }


}
