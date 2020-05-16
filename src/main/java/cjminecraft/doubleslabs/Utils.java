package cjminecraft.doubleslabs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Utils {

    public static boolean isTransparent(IBlockState state) {
        return !state.getMaterial().isOpaque();
    }

    public static RayTraceResult rayTrace(EntityPlayer player) {
        double length = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d startPos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d endPos = startPos.add(player.getLookVec().x * length, player.getLookVec().y * length, player.getLookVec().z * length);
        return player.world.rayTraceBlocks(startPos, endPos);
    }

}
