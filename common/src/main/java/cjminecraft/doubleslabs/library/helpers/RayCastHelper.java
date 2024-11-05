package cjminecraft.doubleslabs.library.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RayCastHelper {

    public static BlockHitResult getLookingAtBlock(Player player) {
        final double reachLength = player.blockInteractionRange();
        final Vec3 startPos = player.getEyePosition();
        final Vec3 endPos = startPos.add(player.getLookAngle().x * reachLength, player.getLookAngle().y * reachLength
                , player.getLookAngle().z * reachLength);
        final ClipContext context = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, player);
        return player.level().clip(context);
    }

}
