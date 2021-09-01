package cjminecraft.doubleslabs.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PlayerEntityWrapper extends PlayerEntity {

    private final PlayerEntity player;

    public PlayerEntityWrapper(PlayerEntity player, World world) {
        super(world, player.getPosition(), player.cameraYaw, player.getGameProfile());
//        super(player.server, player.getServerWorld(), player.getGameProfile(), player.interactionManager);
        this.player = player;
        this.world = world;
    }

    @Override
    public boolean isSpectator() {
        return this.player != null && this.player.isSpectator();
    }

    @Override
    public boolean isCreative() {
        return this.player != null && this.player.isCreative();
    }
}
