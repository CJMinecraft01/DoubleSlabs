package cjminecraft.doubleslabs.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IPlayerWrapper<P extends EntityPlayer> {

    P getOriginalPlayer();

}
