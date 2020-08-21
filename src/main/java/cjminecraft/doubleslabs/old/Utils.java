package cjminecraft.doubleslabs.old;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;

public class Utils {

    private static boolean optifine = false;

    public static boolean isOptiFineInstalled() {
        return optifine;
    }

    public static void checkOptiFineInstalled() {
        try {
            Class.forName("net.optifine.Config");
            optifine = true;
            DoubleSlabs.LOGGER.info("Detected OptiFine is installed, tweaking vertical slabs rendering");
        } catch (ClassNotFoundException ignored) {
            optifine = false;
        }
    }

    public static boolean isTransparent(BlockState state) {
        return !state.getMaterial().isOpaque() || !state.isSolid();
    }

    public static BlockRayTraceResult rayTrace(PlayerEntity player) {
        double length = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        Vector3d startPos = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
        Vector3d endPos = startPos.add(player.getLookVec().x * length, player.getLookVec().y * length, player.getLookVec().z * length);
        RayTraceContext rayTraceContext = new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        return player.world.rayTraceBlocks(rayTraceContext);
    }
    
    public static Direction rotateFace(Direction face, Direction verticalSlabDirection) {
        if (face == null)
            return null;
        if (face == verticalSlabDirection)
            return Direction.DOWN;
        if (face == verticalSlabDirection.getOpposite())
            return Direction.UP;
        if (face == Direction.UP)
            return Direction.NORTH;
        if (face == Direction.DOWN)
            return Direction.SOUTH;
        if (face == Direction.NORTH)
            return verticalSlabDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? Direction.EAST : Direction.WEST;
        if (face == Direction.SOUTH)
            return verticalSlabDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? Direction.WEST : Direction.EAST;
        if (face == Direction.EAST)
            return verticalSlabDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? Direction.EAST : Direction.WEST;
        if (face == Direction.WEST)
            return verticalSlabDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? Direction.WEST : Direction.EAST;
        return face;
    }

}
