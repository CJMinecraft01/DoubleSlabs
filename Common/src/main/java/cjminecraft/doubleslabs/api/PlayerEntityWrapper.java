package cjminecraft.doubleslabs.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// todo override more?
public class PlayerEntityWrapper extends Player {

    private final Player player;

    public PlayerEntityWrapper(Player player, Level world) {
        super(world, player.getOnPos(), player.yRotO, player.getGameProfile(), player.getProfilePublicKey());
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

    // todo: get capability?

}
