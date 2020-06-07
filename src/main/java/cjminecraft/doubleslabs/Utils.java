package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.util.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Quaternion;

public class Utils {

    private static final double DEGREE_TO_RAD = Math.PI / 180.0D;

    public static boolean isTransparent(IBlockState state) {
        return !state.getMaterial().isOpaque();
    }

    public static RayTraceResult rayTrace(EntityPlayer player) {
        double length = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d startPos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d endPos = startPos.add(player.getLookVec().x * length, player.getLookVec().y * length, player.getLookVec().z * length);
        return player.world.rayTraceBlocks(startPos, endPos);
    }

    public static EnumFacing rotateFace(EnumFacing face, EnumFacing verticalSlabDirection) {
        if (face == null)
            return null;
        if (face == verticalSlabDirection)
            return EnumFacing.DOWN;
        if (face == verticalSlabDirection.getOpposite())
            return EnumFacing.UP;
        if (face == EnumFacing.UP)
            return EnumFacing.NORTH;
        if (face == EnumFacing.DOWN)
            return EnumFacing.SOUTH;
        if (face == EnumFacing.NORTH)
            return verticalSlabDirection.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? EnumFacing.EAST : EnumFacing.WEST;
        if (face == EnumFacing.SOUTH)
            return verticalSlabDirection.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? EnumFacing.WEST : EnumFacing.EAST;
        if (face == EnumFacing.EAST)
            return verticalSlabDirection.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? EnumFacing.EAST : EnumFacing.WEST;
        if (face == EnumFacing.WEST)
            return verticalSlabDirection.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? EnumFacing.WEST : EnumFacing.EAST;
        return face;
    }

}
