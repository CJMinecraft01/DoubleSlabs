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
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.items.VerticalSlabItem;
import cjminecraft.doubleslabs.common.patches.DynamicSurroundings;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class PlacementHandler {

    private static boolean canPlace(World world, BlockPos pos, EnumFacing face, EntityPlayer player, EnumHand hand, ItemStack stack, float hitX, float hitY, float hitZ, Consumer<Boolean> cancelEventConsumer, boolean activateBlock) {
        if (!player.canPlayerEdit(pos, face, stack))
            return false;
        if (MathHelper.floor(player.posX) == pos.getX() && MathHelper.floor(player.posY) == pos.getY() && MathHelper.floor(player.posZ) == pos.getZ())
            return false;
        if (!activateBlock)
            return true;
        boolean flag = !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
        boolean flag1 = !(player.isSneaking() && flag) || (player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player));
        if (flag1) {
            boolean result = world.getBlockState(pos).getBlock().onBlockActivated(world, pos, world.getBlockState(pos), player, hand, face, hitX, hitY, hitZ);
            if (result)
                cancelEventConsumer.accept(result);
            return !result;
        }
        return true;
    }

    private static boolean activateBlock(World world, BlockPos pos, EntityPlayer player, EnumHand hand, Consumer<Boolean> cancelEventConsumer) {
        boolean flag = !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
        boolean flag1 = !(player.isSneaking() && flag) || (player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player));
        if (flag1) {
            RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);
            boolean result = world.getBlockState(pos).getBlock().onBlockActivated(world, pos, world.getBlockState(pos), player, hand, rayTraceResult.sideHit, (float) rayTraceResult.hitVec.x - pos.getX(), (float) rayTraceResult.hitVec.y - pos.getY(), (float) rayTraceResult.hitVec.z - pos.getZ());
            if (result)
                cancelEventConsumer.accept(result);
            return result;
        }
        return false;
    }

    public static IBlockState getStateFromSupport(World world, BlockPos pos, EntityPlayer player, EnumHand hand, ItemStack stack, BlockSlab.EnumBlockHalf half, IHorizontalSlabSupport support) {
        return support.getStateForHalf(world, pos, support.getStateFromStack(stack, world, pos, player, hand, RayTraceUtil.rayTrace(player)), half);
    }

    public static IBlockState getStateFromSupport(World world, BlockPos pos, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing direction, IVerticalSlabSupport support) {
        return support.getStateForDirection(world, pos, support.getStateFromStack(stack, world, pos, player, hand, RayTraceUtil.rayTrace(player)), direction);
    }

    private static void finishBlockPlacement(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack stack, Consumer<Boolean> cancel) {
        SoundType soundType = state.getBlock().getSoundType(state, world, pos, player);
        world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f);
        if (!player.isCreative())
            stack.shrink(1);
        cancel.accept(true);
        if (player instanceof EntityPlayerMP)
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
    }

    private static boolean placeSlab(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, Consumer<SlabTileEntity> setStates) {
        // todo add option check collision (used for vertical slab placement)
//        if (false && !world.mayPlace(state.getBlock(), pos, false, side, player))
//            return false;
        DynamicSurroundings.patchBlockState(state);
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

    private static boolean placeSlab(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, IBlockState negativeState, IBlockState positiveState) {
        return placeSlab(world, pos, state, player, side, tile -> {
            tile.getNegativeBlockInfo().setBlockState(negativeState);
            tile.getPositiveBlockInfo().setBlockState(positiveState);
        });
    }

    private static boolean shouldPlaceVerticalSlab(EntityPlayer player, EnumFacing face) {
        if (DSConfig.SERVER.disableVerticalSlabPlacement)
            return false;
        IPlayerConfig config = player.getCapability(PlayerConfigCapability.PLAYER_CONFIG, null);
        if (config == null)
            config = new PlayerConfig();

        return config.getVerticalSlabPlacementMethod().shouldPlace(player, face, config.placeVerticalSlabs());
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ItemStack stack = event.getItemStack();
            World world = event.getWorld();
            EntityPlayer player = event.getEntityPlayer();
            EnumHand hand = event.getHand();
            EnumFacing face = event.getFace();
            BlockPos pos = event.getPos();

            if (stack.getItem() == DSItems.VERTICAL_SLAB)
                stack = VerticalSlabItem.getStack(stack);
            IHorizontalSlabSupport horizontalSlabItemSupport = SlabSupport.getHorizontalSlabSupport(stack, player, hand);
            Consumer<Boolean> cancel = resultType -> {
                event.setCanceled(true);
                event.setCancellationResult(resultType ? EnumActionResult.SUCCESS : EnumActionResult.PASS);
            };

            IBlockState state = world.getBlockState(pos);

            RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);

            if (horizontalSlabItemSupport == null) {
                // The item we are holding is not a horizontal slab

                // Check if the item is a supported vertical slab
                IVerticalSlabSupport verticalSlabItemSupport = SlabSupport.getVerticalSlabSupport(stack, player, hand);

                // If not then don't do anything special
                if (verticalSlabItemSupport == null)
                    return;

                boolean offset = false;

                if (state.getBlock() != DSBlocks.VERTICAL_SLAB && world.getBlockState(pos.offset(face)).getBlock() == DSBlocks.VERTICAL_SLAB) {
                    pos = pos.offset(face);
                    state = world.getBlockState(pos);
                    TileEntity tileEntity = world.getTileEntity(pos);
                    offset = true;
                    if (tileEntity instanceof SlabTileEntity)
                        face = ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? state.getValue(VerticalSlabBlock.FACING).getOpposite() : state.getValue(VerticalSlabBlock.FACING);
                }

                // Check if the block that they clicked on is a vertical slab
                if (state.getBlock() == DSBlocks.VERTICAL_SLAB) {
                    // If we are trying to mix to one of our vertical slabs
                    if (!state.getValue(VerticalSlabBlock.DOUBLE) && face == state.getValue(VerticalSlabBlock.FACING).getOpposite()) {
                        TileEntity tileEntity = world.getTileEntity(pos);
                        // Check that the tile has been created and that the shift key isn't pressed and that we are clicking on the face that is inside of the block
                        if (tileEntity instanceof SlabTileEntity && !player.isSneaking() && (face != state.getValue(VerticalSlabBlock.FACING) || ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() == null)) {
//                                if (!canPlace(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event))
//                                    return;
                            // The new state for the vertical slab with the double property set
                            IBlockState newState = state.withProperty(VerticalSlabBlock.DOUBLE, true);
                            // Get the correct slab state for the vertical slab
                            IBlockState slabState = getStateFromSupport(world, pos, player, hand, stack, ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? face.getOpposite() : face, verticalSlabItemSupport);
//                            BlockState slabState = verticalSlabItemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), new BlockItemUseContext(event.getPlayer(), event.getHand(), event.getItemStack(), Utils.rayTrace(event.getPlayer())), ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() != null ? face.getOpposite() : face);
                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState))
                                return;

                            if (!offset && activateBlock(world, pos, player, hand, cancel))
                                return;

                            if (placeSlab(world, pos, newState, player, face, tile -> {
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

                    if (DSConfig.SERVER.isBlacklistedVerticalSlab(state))
                        return;

                    if (!offset && activateBlock(world, pos, player, hand, cancel))
                        return;

                    // Get the direction that the vertical slab block is facing
                    EnumFacing direction = blockSupport.getDirection(event.getWorld(), pos, state);

                    if (face == direction.getOpposite()) {
                        // Get the state for the vertical slab item using the direction of the already placed vertical slab
                        IBlockState slabState = getStateFromSupport(world, pos, player, hand, stack, direction.getOpposite(), verticalSlabItemSupport);
//                        BlockState slabState = itemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), new BlockItemUseContext(event.getPlayer(), event.getHand(), event.getItemStack(), Utils.rayTrace(event.getPlayer())), direction.getOpposite());
                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState))
                            return;
                        // Create the state for the vertical slab
                        IBlockState newState = DSBlocks.VERTICAL_SLAB.getDefaultState().withProperty(VerticalSlabBlock.FACING, direction).withProperty(VerticalSlabBlock.DOUBLE, true);

                        // Try to set the block state
                        IBlockState finalState = state;
                        if (placeSlab(world, pos, newState, player, face, tile -> {
                            tile.getPositiveBlockInfo().setBlockState(slabState);
                            tile.getNegativeBlockInfo().setBlockState(finalState);
                        }))
                            finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                    }
                }
            } else {
                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, state);

                boolean verticalSlab = state.getBlock() == DSBlocks.VERTICAL_SLAB && !state.getValue(VerticalSlabBlock.DOUBLE) && (((SlabTileEntity) world.getTileEntity(pos)).getPositiveBlockInfo().getBlockState() != null ? face == state.getValue(VerticalSlabBlock.FACING).getOpposite() : face == state.getValue(VerticalSlabBlock.FACING));

                boolean offset = false;

                if (horizontalSlabSupport == null && !verticalSlab) {
                    // The block at the position which was clicked is not a horizontal slab

                    // Check if the block is instead a vertical slab block
                    IVerticalSlabSupport verticalSlabSupport = SlabSupport.getVerticalSlabSupport(world, pos, state);
                    if (verticalSlabSupport != null) {
                        // If so try and combine a regular horizontal slab with a vertical slab

                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(state))
                            return;

                        // Get the direction of the vertical slab
                        EnumFacing direction = verticalSlabSupport.getDirection(world, pos, state);

                        // If we are placing on the front of the vertical slab
                        if (face == direction) {
                            if (activateBlock(world, pos, player, hand, cancel))
                                return;

                            IBlockState slabState = getStateFromSupport(world, pos, player, hand, stack, BlockSlab.EnumBlockHalf.BOTTOM, horizontalSlabItemSupport);
                            IBlockState newState = DSBlocks.VERTICAL_SLAB.getStateForPlacement(world, pos, face, (float)rayTraceResult.hitVec.x - pos.getX(), (float)rayTraceResult.hitVec.y - pos.getY(), (float)rayTraceResult.hitVec.z - pos.getZ(), 0, player, hand).withProperty(VerticalSlabBlock.DOUBLE, true).withProperty(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(world, pos, newState, player, face, state, slabState))
                                finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                            return;
                        }
                    }

//                    if (activateBlock(world, pos, player, hand, cancel))
//                        return;

                    BlockPos newPos = pos.offset(face);
                    IBlockState newState = world.getBlockState(newPos);

                    offset = true;

//                    if (!canPlace(world, newPos, face, player, hand, stack, cancel, false))
//                        return;

                    verticalSlab = newState.getBlock() == DSBlocks.VERTICAL_SLAB && !newState.getValue(VerticalSlabBlock.DOUBLE);

                    horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, newPos, newState);
                    if (horizontalSlabSupport == null && !verticalSlab) {
                        // If the offset block is not a horizontal slab and is not a dynamic vertical slab
                        verticalSlabSupport = SlabSupport.getVerticalSlabSupport(world, newPos, newState);
                        if (verticalSlabSupport != null) {
                            // The offset block is a vertical slab from another mod so we should try to combine them with a regular slab

                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(newState))
                                return;

                            EnumFacing direction = verticalSlabSupport.getDirection(world, newPos, newState);

                            IBlockState slabState = getStateFromSupport(world, newPos, player, hand, stack, BlockSlab.EnumBlockHalf.BOTTOM, horizontalSlabItemSupport);
                            IBlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.getStateForPlacement(world, pos, face, (float)rayTraceResult.hitVec.x - pos.getX(), (float) rayTraceResult.hitVec.y - pos.getY(), (float)rayTraceResult.hitVec.z - pos.getZ(), 0, player, hand).withProperty(VerticalSlabBlock.DOUBLE, true).withProperty(VerticalSlabBlock.FACING, direction);

                            if (placeSlab(world, newPos, verticalSlabState, player, face, newState, slabState))
                                finishBlockPlacement(world, pos, slabState, player, stack, cancel);
                        } else if (shouldPlaceVerticalSlab(player, face) && event.getItemStack().getItem() != DSItems.VERTICAL_SLAB) {
                            // We should place the horizontal slab as a vertical slab
//                            BlockRayTraceResult result = RayTraceUtil.rayTrace(player);
                            if (state.getBlock().isReplaceable(world, pos)) {
                                newState = state;
                                newPos = pos;
                                if (activateBlock(world, pos, player, hand, cancel))
                                    return;
                            } else if (!newState.getBlock().isReplaceable(world, pos))
                                return;

                            IBlockState slabState = getStateFromSupport(world, newPos, player, hand, stack, BlockSlab.EnumBlockHalf.BOTTOM, horizontalSlabItemSupport);
                            if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState))
                                return;

                            if (activateBlock(world, pos, player, hand, cancel))
                                return;

                            IBlockState verticalSlabState = DSBlocks.VERTICAL_SLAB.getStateForPlacement(world, newPos, face, (float)rayTraceResult.hitVec.x - pos.getX(), (float) rayTraceResult.hitVec.y - pos.getY(), (float)rayTraceResult.hitVec.z - pos.getZ(), 0, player, hand);

                            if (placeSlab(world, newPos, verticalSlabState, player, face, tile -> tile.getPositiveBlockInfo().setBlockState(slabState)))
                                finishBlockPlacement(world, newPos, slabState, player, stack, cancel);
                        }
                        return;
                    }
                    state = newState;
                    pos = newPos;
                    if (horizontalSlabSupport != null)
                        face = horizontalSlabSupport.getHalf(world, pos, newState) == BlockSlab.EnumBlockHalf.BOTTOM ? EnumFacing.UP : EnumFacing.DOWN;
                }

                // Check if the block is a dynamic vertical slab and try to join the two slabs together
                if (verticalSlab) {
                    if (!offset && activateBlock(world, pos, player, hand, cancel))
                        return;
                    TileEntity tileEntity = world.getTileEntity(pos);
                    if (tileEntity instanceof SlabTileEntity && !player.isSneaking() && (face != state.getValue(VerticalSlabBlock.FACING) || ((SlabTileEntity) tileEntity).getPositiveBlockInfo().getBlockState() == null)) {
                        SlabTileEntity tile = (SlabTileEntity) tileEntity;
                        IBlockState slabState = getStateFromSupport(world, pos, player, hand, stack, tile.getPositiveBlockInfo().getBlockState() != null ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM, horizontalSlabItemSupport);
                        IBlockState newState = state.withProperty(VerticalSlabBlock.DOUBLE, true);
                        if (DSConfig.SERVER.isBlacklistedVerticalSlab(slabState))
                            return;

                        if (placeSlab(world, pos, newState, player, face, t -> {
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

                if (DSConfig.SERVER.isBlacklistedHorizontalSlab(state))
                    return;

                BlockSlab.EnumBlockHalf half = horizontalSlabSupport.getHalf(world, pos, state);
                if (half == null)
                    return;

                if (!DSConfig.SERVER.replaceSameSlab && horizontalSlabItemSupport.equals(horizontalSlabSupport) && horizontalSlabSupport.areSame(world, pos, state, stack))
                    return;

                if ((face == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM) || (face == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP)) {
                    if (!offset && activateBlock(world, pos, player, hand, cancel))
                        return;
                    IBlockState slabState = getStateFromSupport(world, pos, player, hand, stack, half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM, horizontalSlabItemSupport);
                    if (DSConfig.SERVER.isBlacklistedHorizontalSlab(slabState))
                        return;

//                    if (slabState.hasProperty(BlockStateProperties.WATERLOGGED) && fluidstate.getFluid() == Fluids.WATER) {
//                        slabState = slabState.with(BlockStateProperties.WATERLOGGED, true);
//                        slabState = slabState.updatePostPlacement(Direction.DOWN, state, world, pos, pos.down());
//                    }
                    IBlockState newState = DSBlocks.DOUBLE_SLAB.getStateForPlacement(world, pos, face, (float)rayTraceResult.hitVec.x - pos.getX(), (float) rayTraceResult.hitVec.y - pos.getY(), (float)rayTraceResult.hitVec.z - pos.getZ(), 0, player, hand);

                    TileEntity tileEntity = world.getTileEntity(pos);

                    IBlockState finalState1 = state;
                    if (placeSlab(world, pos, newState, player, face, tile -> {
//                        half == SlabType.BOTTOM ? state : slabState, half == SlabType.TOP ? state : slabState
                        if (half == BlockSlab.EnumBlockHalf.BOTTOM) {
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
