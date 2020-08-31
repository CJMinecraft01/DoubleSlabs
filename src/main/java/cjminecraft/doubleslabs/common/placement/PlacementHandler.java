package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import cjminecraft.doubleslabs.old.Utils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class PlacementHandler {

    private static boolean canPlace(World world, BlockPos pos, Direction face, PlayerEntity player, Hand hand, ItemStack stack, Consumer<ActionResultType> cancelEventConsumer, boolean activateBlock) {
        if (!player.canPlayerEdit(pos, face, stack))
            return false;
        if (MathHelper.floor(player.getPosX()) == pos.getX() && MathHelper.floor(player.getPosY()) == pos.getY() && MathHelper.floor(player.getPosZ()) == pos.getZ())
            return false;
        if (!activateBlock)
            return true;
        boolean useItem = !player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) || !player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player);
        boolean flag = player.isSecondaryUseActive() && useItem;
        if (!flag) {
            ActionResultType result = world.getBlockState(pos).onBlockActivated(world, player, hand, Utils.rayTrace(player).func_237485_a_(pos));
            if (result.isSuccessOrConsume())
                cancelEventConsumer.accept(result);
            return !result.isSuccessOrConsume();
        }
        return true;
    }

    private static BlockState prepareState(BlockState state) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED))
            return state.with(BlockStateProperties.WATERLOGGED, false);
        return state;
    }

    public static BlockItemUseContext getUseContext(PlayerEntity player, Hand hand, ItemStack stack) {
        return new BlockItemUseContext(player, hand, stack, RayTraceUtil.rayTrace(player));
    }

    public static BlockState getStateFromSupport(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, SlabType half, IHorizontalSlabSupport support) {
        return support.getStateForHalf(world, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack)), half);
    }

    private static BlockState getStateFromSupport(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, Direction direction, IVerticalSlabSupport support) {
        return support.getStateForDirection(world, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack)), direction);
    }

    private static void finishBlockPlacement(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack stack, Consumer<ActionResultType> cancel) {
        SoundType soundType = state.getSoundType(world, pos, player);
        world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        if (!player.isCreative())
            stack.shrink(1);
        cancel.accept(ActionResultType.SUCCESS);
        if (player instanceof ServerPlayerEntity)
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
    }

    private static boolean placeSlab(World world, BlockPos pos, BlockState state, Consumer<SlabTileEntity> setStates) {
        if (world.setBlockState(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof SlabTileEntity) {
                SlabTileEntity tile = (SlabTileEntity) tileEntity;
                setStates.accept(tile);
            }
            return true;
        }
        return false;
    }

    private static boolean placeSlab(World world, BlockPos pos, BlockState state, BlockState negativeState, BlockState positiveState) {
        return placeSlab(world, pos, state, tile -> {
            tile.getNegativeBlockInfo().setBlockState(negativeState);
            tile.getPositiveBlockInfo().setBlockState(positiveState);
        });
    }

    private static boolean shouldPlaceVerticalSlab(PlayerEntity player, Direction face) {
        if (DSConfig.SERVER.disableVerticalSlabPlacement.get())
            return false;
        IPlayerConfig config = player.getCapability(PlayerConfigCapability.PLAYER_CONFIG).orElse(new PlayerConfig());

        return config.placeVerticalSlabs() || config.getVerticalSlabPlacementMethod().shouldPlace(player, face);
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ItemStack stack = event.getItemStack();
            World world = event.getWorld();
            PlayerEntity player = event.getPlayer();
            Hand hand = event.getHand();
            Direction face = event.getFace();
            BlockPos pos = event.getPos();

            IHorizontalSlabSupport horizontalSlabItemSupport = SlabSupport.isHorizontalSlab(stack, player, hand);
            Consumer<ActionResultType> cancel = resultType -> {
                event.setCanceled(true);
                event.setCancellationResult(resultType);
            };

            if (horizontalSlabItemSupport == null) {
                // The item we are holding is not a horizontal slab
                // TODO implement
            } else if (canPlace(world, pos, face, player, hand, stack, cancel, true)) {
                BlockState state = world.getBlockState(pos);

                BlockItemUseContext context = getUseContext(player, hand, stack);

                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.isHorizontalSlab(world, pos, state);

                boolean verticalSlab = state.getBlock() == DSBlocks.VERTICAL_SLAB.get() && !state.get(VerticalSlabBlock.DOUBLE) && (((SlabTileEntity) world.getTileEntity(pos)).getPositiveBlockInfo().getBlockState() != null ? face == state.get(VerticalSlabBlock.FACING).getOpposite() : face == state.get(VerticalSlabBlock.FACING));

                if (horizontalSlabSupport == null && !verticalSlab) {
                    // The block at the position which was clicked is not a horizontal slab

                    // Check if the block is instead a vertical slab block
                    IVerticalSlabSupport verticalSlabSupport = SlabSupport.getVerticalSlabSupport(world, pos, state);
                    if (verticalSlabSupport != null) {
                        // If so try and combine a regular horizontal slab with a vertical slab

                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(state.getBlock()))
                            return;

                        // Get the direction of the vertical slab
                        Direction direction = verticalSlabSupport.getDirection(world, pos, state);

                        // If we are placing on the front of the vertical slab
                        if (face == direction) {
                            state = prepareState(state);

                            BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            // TODO check waterlogging with campfire
                            BlockState newState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context).with(VerticalSlabBlock.DOUBLE, true).with(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(world, pos, newState, state, slabState))
                                finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                            return;
                        }
                    }

                    BlockPos newPos = pos.offset(face);
                    BlockState newState = world.getBlockState(newPos);

                    if (!canPlace(world, newPos, face, player, hand, stack, cancel, false))
                        return;

                    verticalSlab = newState.getBlock() == DSBlocks.VERTICAL_SLAB.get() && !newState.get(VerticalSlabBlock.DOUBLE);

                    horizontalSlabSupport = SlabSupport.isHorizontalSlab(world, newPos, newState);
                    if (horizontalSlabSupport == null && !verticalSlab) {
                        // If the offset block is not a horizontal slab and is not a dynamic vertical slab
                        verticalSlabSupport = SlabSupport.getVerticalSlabSupport(world, newPos, newState);
                        if (verticalSlabSupport != null) {
                            // The offset block is a vertical slab from another mod so we should try to combine them with a regular slab

                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(newState.getBlock()))
                                return;

                            newState = prepareState(newState);

                            Direction direction = verticalSlabSupport.getDirection(world, newPos, newState);

                            BlockState slabState = getStateFromSupport(world, newPos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            BlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context).with(VerticalSlabBlock.DOUBLE, true).with(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(world, newPos, verticalSlabState, newState, slabState))
                                finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                        } else if (shouldPlaceVerticalSlab(player, face)) {
                            // We should place the horizontal slab as a vertical slab
                            BlockRayTraceResult result = RayTraceUtil.rayTrace(player);
                            if (state.isReplaceable(context)) {
                                newState = state;
                                newPos = pos;
                            } else if (!newState.isReplaceable(context))
                                return;

                            if (face.getAxis().isVertical()) {
                                Direction direction = player.getHorizontalFacing();

                                double distance;

                                if (direction.getAxis() == Direction.Axis.X)
                                    distance = result.getHitVec().x - newPos.getX();
                                else
                                    distance = result.getHitVec().z - newPos.getZ();

                                if ((direction.getAxisDirection() == Direction.AxisDirection.POSITIVE && distance < 0.5f) || (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE && distance > 0.5f))
                                    direction = direction.getOpposite();

                                BlockState slabState = getStateFromSupport(world, newPos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                                if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                                    return;

                                BlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context).with(VerticalSlabBlock.FACING, direction);

                                if (placeSlab(world, newPos, verticalSlabState, tile -> tile.getPositiveBlockInfo().setBlockState(slabState)))
                                    finishBlockPlacement(world, newPos, slabState, player, stack, cancel);
                            } else {
                                BlockState slabState = getStateFromSupport(world, newPos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                                if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                                    return;

                                BlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context);

                                if (placeSlab(world, newPos, verticalSlabState, tile -> tile.getPositiveBlockInfo().setBlockState(slabState)))
                                    finishBlockPlacement(world, newPos, slabState, player, stack, cancel);
                            }
                        }
                        return;
                    }
                    state = newState;
                    pos = newPos;
                    if (horizontalSlabSupport != null)
                        face = horizontalSlabSupport.getHalf(world, pos, newState) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN;
                }

                // Check if the block is a dynamic vertical slab and try to join the two slabs together
                if (verticalSlab) {
                    TileEntity tileEntity = world.getTileEntity(pos);
                    if (tileEntity instanceof SlabTileEntity && !player.isSneaking() && (face != state.get(VerticalSlabBlock.FACING) || ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() == null)) {
                        SlabTileEntity tile = (SlabTileEntity) tileEntity;
                        BlockState newState = state.with(VerticalSlabBlock.DOUBLE, true);
                        BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, tile.getPositiveBlockInfo().getBlockState() != null ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                            return;

                        if (placeSlab(world, pos, newState, t -> {
                            if (t.getPositiveBlockInfo().getBlockState() != null)
                                t.getNegativeBlockInfo().setBlockState(slabState);
                            else
                                t.getPositiveBlockInfo().setBlockState(slabState);
                        }))
                            finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                        return;
                    }
                }

                if (horizontalSlabSupport == null)
                    return;

                if (DSConfig.SERVER.isBlacklistedHorizontalSlab(state.getBlock()))
                    return;

                SlabType half = horizontalSlabSupport.getHalf(world, pos, state);
                if (half == SlabType.DOUBLE)
                    return;

                if (!DSConfig.SERVER.replaceSameSlab.get() && horizontalSlabItemSupport.equals(horizontalSlabSupport) && horizontalSlabSupport.areSame(world, pos, state, stack))
                    return;

                if ((face == Direction.UP && half == SlabType.BOTTOM) || (face == Direction.DOWN && half == SlabType.TOP)) {
                    state = prepareState(state);
                    BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                    if (DSConfig.SERVER.isBlacklistedHorizontalSlab(slabState.getBlock()))
                        return;

                    BlockState newState = DSBlocks.DOUBLE_SLAB.get().getStateForPlacement(context);

                    if (placeSlab(world, pos, newState, half == SlabType.BOTTOM ? state : slabState, half == SlabType.TOP ? state : slabState))
                        finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                }
            }
        }
    }

}
