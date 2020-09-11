package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.client.util.DoubleSlabBlockItemUseContext;
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
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class PlacementHandler {

    private static boolean canPlace(World world, BlockPos pos, Direction face, PlayerEntity player, Hand hand, ItemStack stack, Consumer<Boolean> cancelEventConsumer, boolean activateBlock) {
        if (!player.canPlayerEdit(pos, face, stack))
            return false;
        if (MathHelper.floor(player.posX) == pos.getX() && MathHelper.floor(player.posY) == pos.getY() && MathHelper.floor(player.posZ) == pos.getZ())
            return false;
        if (!activateBlock)
            return true;
        boolean flag = !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
        boolean flag1 = !(player.isSneaking() && flag) || (player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player));
        if (flag1) {
            boolean result = world.getBlockState(pos).onBlockActivated(world, player, hand, RayTraceUtil.rayTrace(player));
            if (result)
                cancelEventConsumer.accept(result);
            return !result;
        }
        return true;
    }

    private static boolean activateBlock(World world, BlockPos pos, PlayerEntity player, Hand hand, Consumer<Boolean> cancelEventConsumer) {
        boolean flag = !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
        boolean flag1 = !(player.isSneaking() && flag) || (player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player));
        if (flag1) {
            boolean result = world.getBlockState(pos).onBlockActivated(world, player, hand, RayTraceUtil.rayTrace(player));
            if (result)
                cancelEventConsumer.accept(result);
            return result;
        }
        return false;
    }

    private static BlockState prepareState(BlockState state) {
        if (state.has(BlockStateProperties.WATERLOGGED))
            return state.with(BlockStateProperties.WATERLOGGED, false);
        return state;
    }

    public static BlockItemUseContext getUseContext(PlayerEntity player, Hand hand) {
        return new BlockItemUseContext(new ItemUseContext(player, hand, RayTraceUtil.rayTrace(player)));
    }

    public static BlockItemUseContext getUseContext(PlayerEntity player, Hand hand, ItemStack stack, BlockPos pos) {
        return new DoubleSlabBlockItemUseContext(player, hand, stack, RayTraceUtil.rayTrace(player), pos);
    }

    public static BlockState getStateFromSupport(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, SlabType half, IHorizontalSlabSupport support) {
        return support.getStateForHalf(world, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack, pos)), half);
    }

    public static BlockState getStateFromSupport(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, Direction direction, IVerticalSlabSupport support) {
        return support.getStateForDirection(world, pos, support.getStateFromStack(stack, getUseContext(player, hand, stack, pos)), direction);
    }

    private static void finishBlockPlacement(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack stack, Consumer<Boolean> cancel) {
        SoundType soundType = state.getSoundType(world, pos, player);
        world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        if (!player.isCreative())
            stack.shrink(1);
        cancel.accept(true);
        if (player instanceof ServerPlayerEntity)
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
    }

    private static boolean canPlace(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ISelectionContext context = player == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(player);
        return (state.isValidPosition(world, pos)) && world.func_217350_a(state, pos, context);
    }

    private static boolean placeSlab(World world, BlockPos pos, BlockState state, PlayerEntity player, Consumer<SlabTileEntity> setStates) {
        if (!canPlace(world, pos, state, player))
            return false;
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

    private static boolean placeSlab(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockState negativeState, BlockState positiveState) {
        return placeSlab(world, pos, state, player, tile -> {
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

            if (stack.getItem() == DSItems.VERTICAL_SLAB.get())
                stack = VerticalSlabItem.getStack(stack);
            IHorizontalSlabSupport horizontalSlabItemSupport = SlabSupport.isHorizontalSlab(stack, player, hand);
            Consumer<Boolean> cancel = resultType -> {
                event.setCanceled(true);
                event.setCancellationResult(resultType ? ActionResultType.SUCCESS : ActionResultType.PASS);
            };

            BlockState state = world.getBlockState(pos);

            BlockItemUseContext context = getUseContext(player, hand);

            if (horizontalSlabItemSupport == null) {
                // The item we are holding is not a horizontal slab

                // Check if the item is a supported vertical slab
                IVerticalSlabSupport verticalSlabItemSupport = SlabSupport.getVerticalSlabSupport(stack, player, hand);

                // If not then don't do anything special
                if (verticalSlabItemSupport == null)
                    return;

                boolean offset = false;

                if (state.getBlock() != DSBlocks.VERTICAL_SLAB.get() && world.getBlockState(pos.offset(face)).getBlock() == DSBlocks.VERTICAL_SLAB.get()) {
                    pos = pos.offset(face);
                    state = world.getBlockState(pos);
                    TileEntity tileEntity = world.getTileEntity(pos);
                    offset = true;
                    if (tileEntity instanceof SlabTileEntity)
                        face = ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? state.get(VerticalSlabBlock.FACING).getOpposite() : state.get(VerticalSlabBlock.FACING);
                }

                // Check if the block that they clicked on is a vertical slab
                if (state.getBlock() == DSBlocks.VERTICAL_SLAB.get()) {
                    // If we are trying to mix to one of our vertical slabs
                    if (!state.get(VerticalSlabBlock.DOUBLE) && face == state.get(VerticalSlabBlock.FACING).getOpposite()) {
                        TileEntity tileEntity = world.getTileEntity(pos);
                        // Check that the tile has been created and that the shift key isn't pressed and that we are clicking on the face that is inside of the block
                        if (tileEntity instanceof SlabTileEntity && !event.getPlayer().isSneaking() && (face != state.get(VerticalSlabBlock.FACING) || ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() == null)) {
//                                if (!canPlace(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event))
//                                    return;
                            // The new state for the vertical slab with the double property set
                            BlockState newState = state.with(VerticalSlabBlock.DOUBLE, true);
                            // Get the correct slab state for the vertical slab
                            BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? face.getOpposite() : face, verticalSlabItemSupport);
//                            BlockState slabState = verticalSlabItemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), new BlockItemUseContext(event.getPlayer(), event.getHand(), event.getItemStack(), Utils.rayTrace(event.getPlayer())), ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? face.getOpposite() : face);
                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                                return;

                            if (!offset && activateBlock(world, pos, player, hand, cancel))
                                return;

                            if (placeSlab(world, pos, newState, player, tile -> {
                                if (tile.getPositiveBlockInfo().getBlockState() != null)
                                    tile.getNegativeBlockInfo().setBlockState(slabState);
                                else
                                    tile.getPositiveBlockInfo().setBlockState(slabState);
                            })) {
                                finishBlockPlacement(world, pos, slabState, player, stack, cancel);
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
                        pos = pos.offset(face);
                        // Check the player isn't standing there
//                        if (MathHelper.floor(event.getPlayer().posX) == pos.getX() && MathHelper.floor(event.getPlayer().posY) == pos.getY() && MathHelper.floor(event.getPlayer().posZ) == pos.getZ())
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
                        BlockState newState = DSBlocks.VERTICAL_SLAB.get().getDefaultState().with(VerticalSlabBlock.FACING, direction).with(VerticalSlabBlock.DOUBLE, true);

                        // Try to set the block state
                        BlockState finalState = state;
                        if (placeSlab(world, pos, newState, player, tile -> {
                            tile.getPositiveBlockInfo().setBlockState(slabState);
                            tile.getNegativeBlockInfo().setBlockState(finalState);
                        }))
                            finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                    }
                }
            } else {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.isHorizontalSlab(world, pos, state);

                boolean verticalSlab = state.getBlock() == DSBlocks.VERTICAL_SLAB.get() && !state.get(VerticalSlabBlock.DOUBLE) && (((SlabTileEntity) world.getTileEntity(pos)).getPositiveBlockInfo().getBlockState() != null ? face == state.get(VerticalSlabBlock.FACING).getOpposite() : face == state.get(VerticalSlabBlock.FACING));

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
                            BlockState newState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context).with(VerticalSlabBlock.DOUBLE, true).with(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(world, pos, newState, player, state, slabState))
                                finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                            return;
                        }
                    }

//                    if (activateBlock(world, pos, player, hand, cancel))
//                        return;

                    BlockPos newPos = pos.offset(face);
                    BlockState newState = world.getBlockState(newPos);

                    offset = true;

//                    if (!canPlace(world, newPos, face, player, hand, stack, cancel, false))
//                        return;

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

                            if (placeSlab(world, newPos, verticalSlabState, player, newState, slabState))
                                finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                        } else if (shouldPlaceVerticalSlab(player, face)) {
                            // We should place the horizontal slab as a vertical slab
//                            BlockRayTraceResult result = RayTraceUtil.rayTrace(player);
                            if (state.isReplaceable(context)) {
                                newState = state;
                                newPos = pos;
                                if (activateBlock(world, pos, player, hand, cancel))
                                    return;
                            } else if (!newState.isReplaceable(context))
                                return;

                            BlockState slabState = getStateFromSupport(world, newPos, player, hand, stack, SlabType.BOTTOM, horizontalSlabItemSupport);
                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                                return;

                            if (activateBlock(world, pos, player, hand, cancel))
                                return;

                            BlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.get().getStateForPlacement(context);

                            if (placeSlab(world, newPos, verticalSlabState, player, tile -> tile.getPositiveBlockInfo().setBlockState(slabState)))
                                finishBlockPlacement(world, newPos, slabState, player, stack, cancel);
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
                    TileEntity tileEntity = world.getTileEntity(pos);
                    if (tileEntity instanceof SlabTileEntity && !player.isSneaking() && (face != state.get(VerticalSlabBlock.FACING) || ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() == null)) {
                        SlabTileEntity tile = (SlabTileEntity) tileEntity;
                        IFluidState fluidstate = world.getFluidState(pos);
                        BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, tile.getPositiveBlockInfo().getBlockState() != null ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                        BlockState newState = state.with(VerticalSlabBlock.DOUBLE, true).with(VerticalSlabBlock.WATERLOGGED, fluidstate.getFluid() == Fluids.WATER && (VerticalSlabBlock.either(world, pos, i -> i.getSupport() != null && i.getSupport().waterloggableWhenDouble(i.getWorld(), i.getPos(), i.getBlockState())) || horizontalSlabItemSupport.waterloggableWhenDouble(world, pos, slabState)));
                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState.getBlock()))
                            return;

                        if (placeSlab(world, pos, newState, player, t -> {
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
                    if (!offset && activateBlock(world, pos, player, hand, cancel))
                        return;
                    BlockState slabState = getStateFromSupport(world, pos, player, hand, stack, half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM, horizontalSlabItemSupport);
                    if (DSConfig.SERVER.isBlacklistedHorizontalSlab(slabState.getBlock()))
                        return;

                    IFluidState fluidstate = world.getFluidState(pos);
//                    if (slabState.hasProperty(BlockStateProperties.WATERLOGGED) && fluidstate.getFluid() == Fluids.WATER) {
//                        slabState = slabState.with(BlockStateProperties.WATERLOGGED, true);
//                        slabState = slabState.updatePostPlacement(Direction.DOWN, state, world, pos, pos.down());
//                    }
                    BlockState newState = DSBlocks.DOUBLE_SLAB.get().getStateForPlacement(context).with(DoubleSlabBlock.WATERLOGGED, fluidstate.getFluid() == Fluids.WATER && (horizontalSlabItemSupport.waterloggableWhenDouble(world, pos, slabState) || horizontalSlabSupport.waterloggableWhenDouble(world, pos, state)));

                    TileEntity tileEntity = world.getTileEntity(pos);

                    BlockState finalState1 = state;
                    if (placeSlab(world, pos, newState, player, tile -> {
//                        half == SlabType.BOTTOM ? state : slabState, half == SlabType.TOP ? state : slabState
                        if (half == SlabType.BOTTOM) {
                            tile.getNegativeBlockInfo().setBlockState(finalState1);
                            tile.getNegativeBlockInfo().setTileEntity(tileEntity);
                            tile.getPositiveBlockInfo().setBlockState(slabState);
                        } else {
                            tile.getNegativeBlockInfo().setBlockState(slabState);
                            tile.getPositiveBlockInfo().setBlockState(finalState1);
                            tile.getPositiveBlockInfo().setTileEntity(tileEntity);
                        }
                    }))
                        finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                }
            }
        }
    }

}
