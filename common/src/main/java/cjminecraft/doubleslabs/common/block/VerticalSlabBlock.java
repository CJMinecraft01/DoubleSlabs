package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class VerticalSlabBlock extends DynamicSlabBlock {
    // Anything specific to vertical slabs should go here
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.create("type", VerticalSlabType.class);

    public VerticalSlabBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, VerticalSlabType.NEGATIVE).setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, TYPE);
    }
}
