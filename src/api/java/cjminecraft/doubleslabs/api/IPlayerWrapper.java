package cjminecraft.doubleslabs.api;

import net.minecraft.world.entity.player.Player;

public interface IPlayerWrapper<P extends Player> {

    P getOriginalPlayer();

}
