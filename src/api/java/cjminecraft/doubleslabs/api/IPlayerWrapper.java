package cjminecraft.doubleslabs.api;

import net.minecraft.entity.player.PlayerEntity;

public interface IPlayerWrapper<P extends PlayerEntity> {

    P getOriginalPlayer();

}
