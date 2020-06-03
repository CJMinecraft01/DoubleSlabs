package cjminecraft.doubleslabs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;

public class Utils {

    public static boolean isTransparent(BlockState state) {
        return !state.getMaterial().isOpaque();
    }

    public static BlockRayTraceResult rayTrace(PlayerEntity player) {
        double length = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
        Vec3d startPos = new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
        Vec3d endPos = startPos.add(player.getLookVec().x * length, player.getLookVec().y * length, player.getLookVec().z * length);
        RayTraceContext rayTraceContext = new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        return player.world.rayTraceBlocks(rayTraceContext);
    }
    
    public static Direction rotateFace(Direction face, Direction verticalSlabDirection) {
        if (face == null)
            return null;
        if (face.getAxis() == (verticalSlabDirection.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X))
            return face;
        if (face == Direction.UP)
            return verticalSlabDirection;
        if (face == Direction.DOWN)
            return verticalSlabDirection.getOpposite();
        if (face.getAxisDirection() == Direction.AxisDirection.POSITIVE)
            return Direction.UP;
        return Direction.DOWN;
    }

}
