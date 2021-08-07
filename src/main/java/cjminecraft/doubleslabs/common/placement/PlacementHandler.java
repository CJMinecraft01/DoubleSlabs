package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.DoubleSlabBlock;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.DoubleSlabPlaceContext;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class PlacementHandler {

    private static boolean activateBlock(Level world, BlockPos pos, Player player, InteractionHand hand, Consumer<InteractionResult> cancelEventConsumer) {
        boolean useItem = !player.getMainHandItem().doesSneakBypassUse(world, pos, player) || !player.getOffhandItem().doesSneakBypassUse(world, pos, player);
        boolean flag = player.isSecondaryUseActive() && useItem;
        if (!flag) {
            InteractionResult result = world.getBlockState(pos).use(world, player, hand, RayTraceUtil.rayTrace(player).withPosition(pos));
            if (result.consumesAction())
                cancelEventConsumer.accept(result);
            return result.consumesAction();
        }
        return false;
    }

    private static BlockState prepareState(BlockState state) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED))
            return state.setValue(BlockStateProperties.WATERLOGGED, false);
        return state;
    }

    public static BlockPlaceContext getUseContext(Player player, InteractionHand hand, ItemStack stack) {
        return new BlockPlaceContext(player, hand, stack, RayTraceUtil.rayTrace(player));
    }

    public static BlockPlaceContext getUseContext(Player player, InteractionHand hand, ItemStack stack, BlockPos pos) {
        return new DoubleSlabPlaceContext(player, hand, stack, RayTraceUtil.rayTrace(player).withPosition(pos));
    }

    public static BlockState getStateFromSupport(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, SlabType half, IHorizontalSlabSupport support) {
        return support.getStateForHalf(world, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack, pos)), half);
    }

    public static BlockState getStateFromSupport(Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, Direction direction, IVerticalSlabSupport support) {
        return support.getStateForDirection(world, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack, pos)), direction);
    }

    private static void finishBlockPlacement(Level world, BlockPos pos, BlockState state, Player player, ItemStack stack, Consumer<InteractionResult> cancel) {
        SoundType soundType = state.getSoundType(world, pos, player);
        world.playSound(player, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        if (!player.isCreative())
            stack.shrink(1);
        cancel.accept(InteractionResult.SUCCESS);
        if (player instanceof ServerPlayer)
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, pos, stack);
    }

    private static boolean placeSlab(Level world, BlockPos pos, BlockState state, BlockPlaceContext context, Consumer<SlabTileEntity> setStates) {
        if (!context.canPlace())
            return false;
        if (world.setBlock(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof SlabTileEntity) {
                SlabTileEntity tile = (SlabTileEntity) tileEntity;
                setStates.accept(tile);
            }
            return true;
        }
        return false;
    }

    private static boolean placeSlab(Level world, BlockPos pos, BlockState state, BlockPlaceContext context, BlockState negativeState, BlockState positiveState) {
        return placeSlab(world, pos, state, context, tile -> {
            tile.getNegativeBlockInfo().setBlockState(negativeState);
            tile.getPositiveBlockInfo().setBlockState(positiveState);
        });
    }

    private static boolean shouldPlaceVerticalSlab(Player player, Direction face) {
        if (DSConfig.SERVER.disableVerticalSlabPlacement.get())
            return false;
        IPlayerConfig config = player.getCapability(PlayerConfigCapability.PLAYER_CONFIG).orElse(new PlayerConfig());

        return config.getVerticalSlabPlacementMethod().shouldPlace(player, face, config.placeVerticalSlabs());
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ItemStack stack = event.getItemStack();
            Level world = event.getWorld();
            Player player = event.getPlayer();
            InteractionHand hand = event.getHand();
            Direction face = event.getFace();
            BlockPos pos = event.getPos();

            if (stack.getItem() == DSItems.VERTICAL_SLAB.get())
                stack = VerticalSlabItem.getStack(stack);
            IHorizontalSlabSupport horizontalSlabItemSupport = SlabSupport.getHorizontalSlabSupport(stack, player, hand);
            Consumer<InteractionResult> cancel = resultType -> {
                event.setCanceled(true);
                event.setCancellationResult(resultType);
            };

            BlockState state = world.getBlockState(pos);

            BlockPlaceContext context = getUseContext(player, hand, stack, pos);

            if (horizontalSlabItemSupport == null) {
                // The item we are holding is not a horizontal slab

                // Check if the item is a supported vertical slab
                IVerticalSlabSupport verticalSlabItemSupport = SlabSupport.getVerticalSlabSupport(stack, player, hand);

                // If not then don't do anything special
                if (verticalSlabItemSupport == null)
                    return;

                boolean offset = false;

                if (state.getBlock() != DSBlocks.VERTICAL_SLAB.get() && world.getBlockState(pos.relative(face)).getBlock() == DSBlocks.VERTICAL_SLAB.get()) {
                    pos = pos.relative(face);
                    state = world.getBlockState(pos);
                    BlockEntity tileEntity = world.getBlockEntity(pos);
                    offset = true;
                    if (tileEntity instanceof SlabTileEntity)
                        face = ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? state.getValue(VerticalSlabBlock.FACING).getOpposite() : state.getValue(VerticalSlabBlock.FACING);
                }

                // Check if the block that they clicked on is a vertical slab
                if (state.getBlock() == DSBlocks.VERTICAL_SLAB.get()) {
                    // If we are trying to mix to one of our vertical slabs
                    if (!state.getValue(VerticalSlabBlock.DOUBLE) && face == state.getValue(VerticalSlabBlock.FACING).getOpposite()) {
                        BlockEntity tileEntity = world.getBlockEntity(pos);
                        // Check that the tile has been created and that the shift key isn't pressed and that we are clicking on the face that is inside of the block
                        if (tileEntity instanceof SlabTileEntity && !event.getPlayer().isCrouching() && (face != state.getValue(VerticalSlabBlock.FACING) || ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() == null)) {
//                                if (!canPlace(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event))
//                                    return;
                            // The new state for the vertical slab with the double property set
                            BlockState newState = state.setValue(VerticalSlabBlock.DOUBLE, true);
                            // Get the correct slab state for the vertical slab
                            BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? face.getOpposite() : face, verticalSlabItemSupport);
//                            BlockState slabState = verticalSlabItemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), new BlockItemUseContext(event.getPlayer(), event.getHand(), event.getItemStack(), Utils.rayTrace(event.getPlayer())), ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? face.getOpposite() : face);
                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                                return;

                            if (!offset && activateBlock(world, pos, player, hand, cancel))
                                return;

                            if (placeSlab(world, pos, newState, context, tile -> {
                                if (tile.getPositiveBlockInfo().getBlockState() != null)
                                    tile.getNegativeBlockInfo().setBlockState(slabState);
                                else
                                    tile.getPositiveBlockInfo().setBlockState(slabState);
                            })) {
                                finishBlockPlacement(world, pos, slabState, player, event.getItemStack(), cancel);
                            }
                        }
                    }
                } else {
                    // Otherwise check if we are trying to mix two vertical slabs from different mods

                    // Check that the block is a vertical slab
                    IVerticalSlabSupport blockSupport = SlabSupport.getVerticalSlabSupport(world, pos, state);

                    // If not, try offsetting by the face
                    if (blockSupport == null) {
                        offset = true;
                        // Offset the position
                        pos = pos.relative(face);
                        // Check the player isn't standing there
//                        if (MathHelper.floor(event.getPlayer().getPosX()) == pos.getX() && MathHelper.floor(event.getPlayer().getPosY()) == pos.getY() && MathHelper.floor(event.getPlayer().getPosZ()) == pos.getZ())
//                            return;
                        state = world.getBlockState(pos);

//                        if (!canPlace(event.getWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event, false))
//                            return;

                        blockSupport = SlabSupport.getVerticalSlabSupport(world, pos, state);
                        if (blockSupport == null)
                            return;

                        face = blockSupport.getDirection(event.getWorld(), pos, state).getOpposite();
                    }

                    if (DSConfig.SERVER.isBlacklistedVerticalSlab(state.getBlock()))
                        return;

                    if (!offset && activateBlock(world, pos, player, hand, cancel))
                        return;

                    state = prepareState(state);

                    // Get the direction that the vertical slab block is facing
                    Direction direction = blockSupport.getDirection(event.getWorld(), pos, state);

                    if (face == direction.getOpposite()) {
                        // Get the state for the vertical slab item using the direction of the already placed vertical slab
                        BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, direction.getOpposite(), verticalSlabItemSupport);
//                        BlockState slabState = itemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), new BlockItemUseContext(event.getPlayer(), event.getHand(), event.getItemStack(), Utils.rayTrace(event.getPlayer())), direction.getOpposite());
                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                            return;
                        // Create the state for the vertical slab
                        BlockState newState = DSBlocks.VERTICAL_SLAB.get().defaultBlockState().setValue(VerticalSlabBlock.FACING, direction).setValue(VerticalSlabBlock.DOUBLE, true);

                        // Try to set the block state
                        BlockState finalState = state;
                        if (placeSlab(world, pos, newState, context, tile -> {
                            tile.getPositiveBlockInfo().setBlockState(slabState);
                            tile.getNegativeBlockInfo().setBlockState(finalState);
                        }))
                            finishBlockPlacement(world, pos, slabState, player, event.getItemStack(), cancel);
                    }
                }
            } else {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, state);

                boolean verticalSlab = state.getBlock() == DSBlocks.VERTICAL_SLAB.get() && !state.getValue(VerticalSlabBlock.DOUBLE) && (((SlabTileEntity) world.getBlockEntity(pos)).getPositiveBlockInfo().getBlockState() != null ? face == state.getValue(VerticalSlabBlock.FACING).getOpposite() : face == state.getValue(VerticalSlabBlock.FACING));

                boolean offset = false;

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

                            if (activateBlock(world, pos, player, hand, cancel))
                                return;

                            BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            BlockState newState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context).setValue(VerticalSlabBlock.DOUBLE, true).setValue(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(world, pos, newState, context, state, slabState))
                                finishBlockPlacement(world, pos, slabState, player, event.getItemStack(), cancel);
                            return;
                        }
                    }

//                    if (activateBlock(world, pos, player, hand, cancel))
//                        return;

                    BlockPos newPos = pos.relative(face);
                    BlockState newState = world.getBlockState(newPos);

                    offset = true;

//                    if (!canPlace(world, newPos, face, player, hand, stack, cancel, false))
//                        return;

                    verticalSlab = newState.getBlock() == DSBlocks.VERTICAL_SLAB.get() && !newState.getValue(VerticalSlabBlock.DOUBLE);

                    horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, newPos, newState);
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
                            BlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context).setValue(VerticalSlabBlock.DOUBLE, true).setValue(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(world, newPos, verticalSlabState, context, newState, slabState))
                                finishBlockPlacement(world, pos, slabState, player, event.getItemStack(), cancel);
                        } else if (shouldPlaceVerticalSlab(player, face)) {
                            // We should place the horizontal slab as a vertical slab
//                            BlockRayTraceResult result = RayTraceUtil.rayTrace(player);
                            if (state.canBeReplaced(context)) {
                                newState = state;
                                newPos = pos;
                                if (activateBlock(world, pos, player, hand, cancel))
                                    return;
                            } else if (!newState.canBeReplaced(context))
                                return;

                            BlockState slabState = getStateFromSupport(world, newPos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                                return;

                            if (activateBlock(world, newPos, player, hand, cancel))
                                return;

                            context = getUseContext(player, hand, stack, newPos);

                            BlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context);

                            if (placeSlab(world, newPos, verticalSlabState, context, tile -> tile.getPositiveBlockInfo().setBlockState(slabState)))
                                finishBlockPlacement(world, newPos, slabState, player, event.getItemStack(), cancel);
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
                    if (!offset && activateBlock(world, pos, player, hand, cancel))
                        return;
                    BlockEntity tileEntity = world.getBlockEntity(pos);
                    if (tileEntity instanceof SlabTileEntity && !player.isCrouching() && (face != state.getValue(VerticalSlabBlock.FACING) || ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() == null)) {
                        SlabTileEntity tile = (SlabTileEntity) tileEntity;
                        FluidState fluidstate = world.getFluidState(pos);
                        BlockState newState = state.setValue(VerticalSlabBlock.DOUBLE, true).setValue(VerticalSlabBlock.WATERLOGGED, fluidstate.getType() == Fluids.WATER && VerticalSlabBlock.either(world, pos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getWorld(), i.getPos(), i.getBlockState())));
                        BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, tile.getPositiveBlockInfo().getBlockState() != null ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                            return;

                        if (placeSlab(world, pos, newState, context, t -> {
                            if (t.getPositiveBlockInfo().getBlockState() != null)
                                t.getNegativeBlockInfo().setBlockState(slabState);
                            else
                                t.getPositiveBlockInfo().setBlockState(slabState);
                        }))
                            finishBlockPlacement(world, pos, slabState, player, event.getItemStack(), cancel);
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
                    if (!offset && activateBlock(world, pos, player, hand, cancel))
                        return;
                    BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                    if (DSConfig.SERVER.isBlacklistedHorizontalSlab(slabState.getBlock()))
                        return;

                    FluidState fluidstate = world.getFluidState(pos);
//                    if (slabState.hasProperty(BlockStateProperties.WATERLOGGED) && fluidstate.getFluid() == Fluids.WATER) {
//                        slabState = slabState.with(BlockStateProperties.WATERLOGGED, true);
//                        slabState = slabState.updatePostPlacement(Direction.DOWN, state, world, pos, pos.down());
//                    }

//                    context = getUseContext(player, hand, stack, pos);

                    BlockState newState = DSBlocks.DOUBLE_SLAB.get().getStateForPlacement(context).setValue(DoubleSlabBlock.WATERLOGGED, fluidstate.getType() == Fluids.WATER && (horizontalSlabItemSupport.waterloggableWhenDouble(world, pos, slabState) || horizontalSlabSupport.waterloggableWhenDouble(world, pos, state)));

                    BlockEntity tileEntity = world.getBlockEntity(pos);

                    BlockState finalState1 = state;
                    if (placeSlab(world, pos, newState, offset ? context : getUseContext(player, hand, stack, pos), tile -> {
//                        half == SlabType.BOTTOM ? state : slabState, half == SlabType.TOP ? state : slabState
                        if (half == SlabType.BOTTOM) {
                            tile.getNegativeBlockInfo().setBlockState(finalState1);
                            tile.getNegativeBlockInfo().setBlockEntity(tileEntity);
                            tile.getPositiveBlockInfo().setBlockState(slabState);
                        } else {
                            tile.getNegativeBlockInfo().setBlockState(slabState);
                            tile.getPositiveBlockInfo().setBlockState(finalState1);
                            tile.getPositiveBlockInfo().setBlockEntity(tileEntity);
                        }
                    }))
                        finishBlockPlacement(world, pos, slabState, player, event.getItemStack(), cancel);
                }
            }
        }
    }

}
