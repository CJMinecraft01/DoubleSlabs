package cjminecraft.doubleslabs.common.util;

import cjminecraft.doubleslabs.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RayTraceUtil {

    public static BlockHitResult rayTrace(Player player) {
        double length = Services.PLATFORM.getReachDistance(player);
        Vec3 startPos = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        Vec3 endPos = startPos.add(player.getLookAngle().x * length, player.getLookAngle().y * length, player.getLookAngle().z * length);
        ClipContext context = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        return player.level.clip(context);
    }

}