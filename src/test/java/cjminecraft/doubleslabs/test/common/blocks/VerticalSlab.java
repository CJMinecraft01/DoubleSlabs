package cjminecraft.doubleslabs.test.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class VerticalSlab extends Block {

    public static final PropertyEnum<VerticalSlabType> TYPE = PropertyEnum.create("type", VerticalSlabType.class);

    public VerticalSlab(Material material) {
        super(material);
        setDefaultState(getDefaultState().withProperty(TYPE, VerticalSlabType.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TYPE).build();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(TYPE).shape;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this)
            return state.withProperty(TYPE, VerticalSlabType.DOUBLE);
        if (facing.getAxis().isVertical()) {
            Vec3d vec = new Vec3d(hitX, hitY, hitZ).subtract(0.5, 0, 0.5);
            double angle = Math.atan2(vec.x, vec.z) * -180 / Math.PI;
            return this.getDefaultState().withProperty(TYPE, VerticalSlabType.fromDirection(EnumFacing.fromAngle(angle)));
        }
        float value = placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.X ? hitZ : hitX;
        if (value > 0.25 && value < 0.75)
            return this.getDefaultState().withProperty(TYPE, VerticalSlabType.fromDirection(placer.getHorizontalFacing()));
        boolean positive = placer.getHorizontalFacing().getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? value > 0.5d : value < 0.5d;
        if (placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.Z)
            positive = !positive;

        return this.getDefaultState().withProperty(TYPE, VerticalSlabType.fromDirection(positive ? facing.rotateYCCW() : facing.rotateY()));
    }

    public enum VerticalSlabType implements IStringSerializable {
        NORTH(EnumFacing.NORTH),
        SOUTH(EnumFacing.SOUTH),
        WEST(EnumFacing.WEST),
        EAST(EnumFacing.EAST),
        DOUBLE(null);

        private final String name;
        public final EnumFacing direction;
        public final AxisAlignedBB shape;

        VerticalSlabType(EnumFacing direction) {
            this.name = direction == null ? "double" : direction.getName2();
            this.direction = direction;

            if(direction == null)
                shape = Block.FULL_BLOCK_AABB;
            else {
                double min = 0;
                double max = 8;
                if(direction.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                    min = 8;
                    max = 16;
                }

                if(direction.getAxis() == EnumFacing.Axis.X)
                    shape = new AxisAlignedBB(min / 16, 0, 0, max / 16, 16, 16);
                else shape = new AxisAlignedBB(0, 0, min / 16, 16, 16, max / 16);
            }
        }

        @Override
        public String toString() {
            return name;
        }

        public static VerticalSlabType fromDirection(EnumFacing direction) {
            for(VerticalSlabType type : VerticalSlabType.values())
                if(type.direction != null && direction == type.direction)
                    return type;

            return null;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
