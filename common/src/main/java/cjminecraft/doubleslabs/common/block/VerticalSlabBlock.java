package cjminecraft.doubleslabs.common.block;

import cjminecraft.doubleslabs.api.state.VerticalSlabType;
import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VerticalSlabBlock extends DynamicSlabBlock {
    // Anything specific to vertical slabs should go here
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<VerticalSlabType> TYPE = EnumProperty.create("type", VerticalSlabType.class);

    public static final VoxelShape X_POSITIVE_AABB = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape X_NEGATIVE_AABB = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    public static final VoxelShape Z_POSITIVE_AABB = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape Z_NEGATIVE_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);

    public VerticalSlabBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, VerticalSlabType.NEGATIVE).setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, TYPE);
    }

    @Override
    public Item asItem() {
        return DSItems.VERTICAL_SLAB.get();
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(TYPE) != VerticalSlabType.DOUBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        final var axis = state.getValue(AXIS);
        final var slabType = state.getValue(TYPE);

        return switch (slabType) {
            case POSITIVE -> axis == Direction.Axis.X ? X_POSITIVE_AABB : Z_POSITIVE_AABB;
            case NEGATIVE -> axis == Direction.Axis.X ? X_NEGATIVE_AABB : Z_NEGATIVE_AABB;
            case DOUBLE -> Shapes.block();
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        final var clickedPos = context.getClickedPos();
        final var clickedState = context.getLevel().getBlockState(clickedPos);

        // If the clicked block is a vertical slab then we are trying to combine two vertical slabs
        if (clickedState.is(this)) {
            return clickedState.setValue(TYPE, VerticalSlabType.DOUBLE);
        }

        final var clickedFace = context.getClickedFace();
        final var clickLocation = context.getClickLocation();
        final var locationRelativeToCenter = clickLocation.subtract(clickedPos.getCenter());

        // If the clicked face is either UP or DOWN then we divide the face into four quadrants that determine the axis and type
        if (clickedFace.getAxis().isVertical()) {
            // For the x and z dimensions, we will have a value between -0.5 and 0.5 which we can use to determine the angle
            // around the center which gives us which direction to face
            final var angle = Math.toDegrees(Math.atan2(locationRelativeToCenter.x, locationRelativeToCenter.z));
            final var direction = Direction.fromYRot(-angle);

            return this.defaultBlockState()
                    .setValue(AXIS, direction.getAxis())
                    .setValue(TYPE, VerticalSlabType.fromAxisDirection(direction.getAxisDirection()));
        }

        // If we clicked on the side of a face, then we divide the face into three quadrants
        // The middle quadrant is placing the slab aligned with the clicked block, the other two place it tangentially

        // This ranges between -0.5 and 0.5
        final var positionAlongAxis = clickedFace.getAxis() == Direction.Axis.X ? locationRelativeToCenter.z : locationRelativeToCenter.x;

        // If we are in the middle quadrant, place aligned with the clicked block
        if (-0.25 < positionAlongAxis && positionAlongAxis < 0.25) {
            return this.defaultBlockState()
                    .setValue(AXIS, clickedFace.getAxis())
                    .setValue(TYPE, VerticalSlabType.fromAxisDirection(clickedFace.getAxisDirection().opposite()));
        }

        // Otherwise we place tangentially
        return this.defaultBlockState()
                .setValue(AXIS, clickedFace.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X)
                .setValue(TYPE, positionAlongAxis < 0 ? VerticalSlabType.NEGATIVE : VerticalSlabType.POSITIVE);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        final var heldItem = context.getItemInHand();
        final var slabType = state.getValue(TYPE);

        if (slabType == VerticalSlabType.DOUBLE || !heldItem.is(this.asItem())) {
            return false;
        }

        if (!context.replacingClickedOnBlock()) {
            return true;
        }

        final var axis = state.getValue(AXIS);
        final var slabFacingDirection = Direction.fromAxisAndDirection(axis, slabType.toAxisDirection());

        // Only allow replacing the vertical slab if the side clicked is opposite to the side the slab is facing
        return slabFacingDirection == context.getClickedFace().getOpposite();
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return VerticalSlabBlockHooks.getDrops(params);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return VerticalSlabBlockHooks.getDestroyProgress(player, level, state, pos)
                .orElseGet(() -> super.getDestroyProgress(state, player, level, pos));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        VerticalSlabBlockHooks.playerDestroy(player, level, pos, state, blockEntity, tool);
    }
}
