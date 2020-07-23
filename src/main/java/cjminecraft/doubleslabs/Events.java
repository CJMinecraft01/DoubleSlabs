package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    private static BlockState removeWaterloggedPropertyFromState(BlockState state) {
        if (state.has(BlockStateProperties.WATERLOGGED))
            return state.with(BlockStateProperties.WATERLOGGED, false);
        return state;
    }

    public static boolean canPlace(World world, BlockPos pos, Direction face, PlayerEntity player, Hand hand, ItemStack stack, PlayerInteractEvent.RightClickBlock event, boolean activateBlock) {
        if (!player.canPlayerEdit(pos, face, stack))
            return false;
        if (!activateBlock)
            return true;
        boolean flag = !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
        boolean flag1 = !(player.isSneaking() && flag) || (player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) && player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player));
        if (flag1) {
            BlockRayTraceResult rayTraceResult = Utils.rayTrace(player);
            rayTraceResult = new BlockRayTraceResult(rayTraceResult.getHitVec(), rayTraceResult.getFace(), pos, rayTraceResult.isInside());
            boolean result = world.getBlockState(pos).onBlockActivated(world, player, hand, rayTraceResult);
            if (result) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
            return !result;
        }
        return true;
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        // Check we are holding an item
        if (!event.getItemStack().isEmpty()) {
            // Check that the item is a horizontal slab that is supported
            ISlabSupport itemSupport = SlabSupport.getHorizontalSlabSupport(event.getItemStack(), event.getPlayer(), event.getHand());
            if (itemSupport == null) {
                // If not, check to see if the item is a vertical slab which is supported
                itemSupport = SlabSupport.getVerticalSlabSupport(event.getItemStack(), event.getPlayer(), event.getHand());

                // If not, don't do anything special
                if (itemSupport == null)
                    return;

                // Check that the player can actually place a block here
                if (canPlace(event.getWorld(), event.getPos(), event.getFace(), event.getPlayer(), event.getHand(), event.getItemStack(), event, true)) {
                    BlockPos pos = event.getPos();
                    // Check that the player isn't trying to place a block in the same position as them
                    if (MathHelper.floor(event.getPlayer().posX) == pos.getX() && MathHelper.floor(event.getPlayer().posY) == pos.getY() && MathHelper.floor(event.getPlayer().posZ) == pos.getZ())
                        return;

                    BlockState state = event.getWorld().getBlockState(pos);
                    Direction face = event.getFace();

                    if (state.getBlock() != Registrar.VERTICAL_SLAB && event.getWorld().getBlockState(pos.offset(face)).getBlock() == Registrar.VERTICAL_SLAB) {
                        state = event.getWorld().getBlockState(pos.offset(face));
                        pos = pos.offset(face);
                        TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                        if (tile != null)
                            face = tile.getPositiveState() != null ? state.get(BlockVerticalSlab.FACING).getOpposite() : state.get(BlockVerticalSlab.FACING);

                        if (!canPlace(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event, false))
                            return;
                    }

                    // Check if the block that they clicked on is a vertical slab
                    if (state.getBlock() == Registrar.VERTICAL_SLAB) {
                        // If we are trying to mix to one of our vertical slabs
                        if (!state.get(BlockVerticalSlab.DOUBLE) && face == state.get(BlockVerticalSlab.FACING).getOpposite()) {
                            TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                            // Check that the tile has been created and that the shift key isn't pressed and that we are clicking on the face that is inside of the block
                            if (tile != null && !event.getPlayer().isSneaking() && (face != state.get(BlockVerticalSlab.FACING) || tile.getPositiveState() == null)) {
//                                if (!canPlace(tile.getPositiveState() != null ? tile.getPositiveWorld() : tile.getNegativeWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event))
//                                    return;
                                // The new state for the vertical slab with the double property set
                                BlockState newState = state.with(BlockVerticalSlab.DOUBLE, true).with(BlockVerticalSlab.WATERLOGGED, false);
                                // If we could set the block
                                if (event.getWorld().setBlockState(pos, newState, 11)) {
                                    // Get the correct slab state for the vertical slab
                                    BlockState slabState = itemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), tile.getPositiveState() != null ? face.getOpposite() : face);

                                    // Set the respective state within the tile entity
                                    if (tile.getPositiveState() != null)
                                        tile.setNegativeState(slabState);
                                    else
                                        tile.setPositiveState(slabState);

                                    finishBlockPlacement(event, pos, slabState);
                                    return;
                                }
                            }
                        }
                    } else {
                        // Otherwise check if we are trying to mix two vertical slabs from different mods

                        // Check that the block is a vertical slab
                        ISlabSupport blockSupport = SlabSupport.getVerticalSlabSupport(event.getWorld(), pos, state);

                        // If not, try offsetting by the face
                        if (blockSupport == null) {
                            // Offset the position
                            pos = pos.offset(face);
                            // Check the player isn't standing there
                            if (MathHelper.floor(event.getPlayer().posX) == pos.getX() && MathHelper.floor(event.getPlayer().posY) == pos.getY() && MathHelper.floor(event.getPlayer().posZ) == pos.getZ())
                                return;
                            state = event.getWorld().getBlockState(pos);

                            if (!canPlace(event.getWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event, false))
                                return;

                            blockSupport = SlabSupport.getVerticalSlabSupport(event.getWorld(), pos, state);
                            if (blockSupport == null)
                                return;

                            face = blockSupport.getDirection(event.getWorld(), pos, state).getOpposite();
                        }

                        state = removeWaterloggedPropertyFromState(state);

                        // Get the direction that the vertical slab block is facing
                        Direction direction = blockSupport.getDirection(event.getWorld(), pos, state);

                        if (face == direction.getOpposite()) {
                            // Get the state for the vertical slab item using the direction of the already placed vertical slab
                            BlockState slabState = itemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), direction.getOpposite());
                            // Create the state for the vertical slab
                            BlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().with(BlockVerticalSlab.FACING, direction).with(BlockVerticalSlab.DOUBLE, true).with(BlockVerticalSlab.WATERLOGGED, false);

                            // Try to set the block state
                            if (event.getWorld().setBlockState(pos, newState, 11)) {
                                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                                if (tile == null)
                                    return;

                                // Set the correct states
                                tile.setNegativeState(state);
                                tile.setPositiveState(slabState);

                                finishBlockPlacement(event, pos, slabState);
                            }
                        }
                    }
                }

                return;
            }

            // If the slab is a horizontal slab and we can edit the face that was clicked
            if (canPlace(event.getWorld(), event.getPos(), event.getFace(), event.getPlayer(), event.getHand(), event.getItemStack(), event, true)) {
                BlockPos pos = event.getPos();
                // Check the player is not standing in the same space as where was clicked
                if (MathHelper.floor(event.getPlayer().posX) == pos.getX() && MathHelper.floor(event.getPlayer().posY) == pos.getY() && MathHelper.floor(event.getPlayer().posZ) == pos.getZ())
                    return;
                Direction face = event.getFace();
                BlockState state = event.getWorld().getBlockState(pos);

                // Don't allow blacklisted slabs to be joined to
                if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)))
                    return;

                boolean verticalSlab = state.getBlock() == Registrar.VERTICAL_SLAB && !state.get(BlockVerticalSlab.DOUBLE) && (((TileEntityVerticalSlab) event.getWorld().getTileEntity(pos)).getPositiveState() != null ? face == state.get(BlockVerticalSlab.FACING).getOpposite() : face == state.get(BlockVerticalSlab.FACING));

                // Check if the block is a horizontal slab
                ISlabSupport blockSupport = SlabSupport.getHorizontalSlabSupport(event.getWorld(), pos, state);
                if (blockSupport == null && !verticalSlab) {
                    // If not, then the block is either a vertical slab or we should trying offsetting the pos by the face

                    // Check if the block is a vertical slab from another mod
                    blockSupport = SlabSupport.getVerticalSlabSupport(event.getWorld(), pos, state);
                    if (blockSupport != null) {
                        // We are trying to combine a mod vertical slab with a regular slab
//                        if (!canPlace(event.getWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event))
//                            return;

                        state = removeWaterloggedPropertyFromState(state);
                        Direction direction = blockSupport.getDirection(event.getWorld(), pos, state);
                        if (face == direction) {
                            BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), SlabType.BOTTOM);
                            BlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().with(BlockVerticalSlab.DOUBLE, true).with(BlockVerticalSlab.WATERLOGGED, false).with(BlockVerticalSlab.FACING, direction);

                            if (event.getWorld().setBlockState(pos, newState, 11)) {
                                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                                if (tile == null)
                                    return;
                                tile.setNegativeState(state);
                                tile.setPositiveState(slabState);

                                finishBlockPlacement(event, pos, slabState);
                            }

                            return;
                        }
                    }

                    // Offset the position
                    pos = pos.offset(face);
                    // Check the player isn't standing there
                    if (MathHelper.floor(event.getPlayer().posX) == pos.getX() && MathHelper.floor(event.getPlayer().posY) == pos.getY() && MathHelper.floor(event.getPlayer().posZ) == pos.getZ())
                        return;
                    state = event.getWorld().getBlockState(pos);
                    if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)))
                        return;
                    if (!canPlace(event.getWorld(), pos, face, event.getPlayer(), event.getHand(), event.getItemStack(), event, false))
                        return;

                    verticalSlab = state.getBlock() == Registrar.VERTICAL_SLAB && !state.get(BlockVerticalSlab.DOUBLE);

                    // Check if the offset state is a horizontal slab
                    blockSupport = SlabSupport.getHorizontalSlabSupport(event.getWorld(), pos, state);
                    if (blockSupport == null && !verticalSlab) {
                        // If not, check if it is a vertical slab
                        blockSupport = SlabSupport.getVerticalSlabSupport(event.getWorld(), pos, state);
                        if (blockSupport != null) {
                            // We are trying to combine a mod vertical slab with a regular slab

                            state = removeWaterloggedPropertyFromState(state);
                            Direction direction = blockSupport.getDirection(event.getWorld(), pos, state);

                            BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), SlabType.BOTTOM);
                            BlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().with(BlockVerticalSlab.DOUBLE, true).with(BlockVerticalSlab.FACING, direction).with(BlockVerticalSlab.WATERLOGGED, false);

                            if (event.getWorld().setBlockState(pos, newState, 11)) {
                                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                                if (tile == null)
                                    return;
                                tile.setNegativeState(state);
                                tile.setPositiveState(slabState);

                                finishBlockPlacement(event, pos, slabState);
                            }

                            return;
                        }
                        if (((event.getPlayer().isSneaking() && !Config.ALTERNATE_VERTICAL_SLAB_PLACEMENT.get()) || (Config.ALTERNATE_VERTICAL_SLAB_PLACEMENT.get() && ((event.getPlayer().isSneaking() && face.getAxis() == Direction.Axis.Y) || (!event.getPlayer().isSneaking() && face.getAxis() != Direction.Axis.Y)))) && !Config.DISABLE_VERTICAL_SLAB_PLACEMENT.get()) {
                            if (!state.isAir(event.getWorld(), pos))
                                return;
                            // Try to place a horizontal slab as a vertical slab
                            BlockRayTraceResult result = Utils.rayTrace(event.getPlayer());
                            if (face.getAxis() == Direction.Axis.Y) {
                                Direction direction = event.getPlayer().getHorizontalFacing();

                                double distance;

                                if (direction.getAxis() == Direction.Axis.X)
                                    distance = result.getHitVec().x - pos.getX();
                                else
                                    distance = result.getHitVec().z - pos.getZ();

                                if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE && distance < 0.5f)
                                    direction = direction.getOpposite();
                                else if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE && distance > 0.5f)
                                    direction = direction.getOpposite();

                                BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), SlabType.BOTTOM);
                                BlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().with(BlockVerticalSlab.FACING, direction);

                                if (event.getWorld().setBlockState(pos, newState, 11)) {
                                    TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                                    if (tile == null)
                                        return;
                                    tile.setPositiveState(slabState);

                                    finishBlockPlacement(event, pos, slabState);
                                }
                            } else {
                                BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), SlabType.BOTTOM);
                                BlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().with(BlockVerticalSlab.FACING, face.getOpposite());

                                if (event.getWorld().setBlockState(pos, newState, 11)) {
                                    TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                                    if (tile == null)
                                        return;
                                    tile.setPositiveState(slabState);

                                    finishBlockPlacement(event, pos, slabState);
                                }
                            }
                        }

                        return;
                    }
                    if (blockSupport != null)
                        face = blockSupport.getHalf(event.getWorld(), pos, state) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN;
                }

                // Check if the block is a vertical slab and try to join the two slabs together
                if (verticalSlab && state.getBlock() == Registrar.VERTICAL_SLAB && !state.get(BlockVerticalSlab.DOUBLE)) {
                    TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                    if (tile != null && !event.getPlayer().isSneaking() && (face != state.get(BlockVerticalSlab.FACING) || tile.getPositiveState() == null)) {
                        BlockState newState = state.with(BlockVerticalSlab.DOUBLE, true).with(BlockVerticalSlab.WATERLOGGED, false);
                        if (event.getWorld().setBlockState(pos, newState, 11)) {
                            BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), tile.getPositiveState() != null ? SlabType.TOP : SlabType.BOTTOM);
                            if (tile.getPositiveState() != null)
                                tile.setNegativeState(slabState);
                            else
                                tile.setPositiveState(slabState);

                            finishBlockPlacement(event, pos, slabState);
                            return;
                        }
                    }
                }

                if (blockSupport == null)
                    return;

                SlabType half = blockSupport.getHalf(event.getWorld(), pos, state);
                if (half == SlabType.DOUBLE)
                    return;

                if (!Config.REPLACE_SAME_SLAB.get() && blockSupport == itemSupport && blockSupport.areSame(event.getWorld(), pos, state, event.getItemStack()))
                    return;

                if ((face == Direction.UP && half == SlabType.BOTTOM) || (face == Direction.DOWN && half == SlabType.TOP)) {
                    state = removeWaterloggedPropertyFromState(state);
                    BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
                    BlockState newState = Registrar.DOUBLE_SLAB.getDefaultState();

                    if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(slabState)))
                        return;

//                    if (!event.getWorld().checkBlockCollision(event.getPlayer().getBoundingBox().offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
                    if (event.getWorld().setBlockState(pos, newState, 11)) {
                        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) event.getWorld().getTileEntity(pos);
                        if (tile == null)
                            return;
                        tile.setTopState(half == SlabType.TOP ? state : slabState);
                        tile.setBottomState(half == SlabType.BOTTOM ? state : slabState);
                        finishBlockPlacement(event, pos, slabState);
                    }
                }
            }
        }
    }

    private static void finishBlockPlacement(PlayerInteractEvent.RightClickBlock event, BlockPos pos, BlockState slabState) {
        SoundType soundtype = slabState.getSoundType(event.getWorld(), pos, event.getPlayer());
        event.getWorld().playSound(event.getPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        if (!event.getPlayer().isCreative())
            event.getItemStack().shrink(1);
        event.setCancellationResult(ActionResultType.SUCCESS);
        event.setCanceled(true);
        if (event.getPlayer() instanceof ServerPlayerEntity)
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) event.getPlayer(), pos, event.getItemStack());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderBlockHighlight(DrawBlockHighlightEvent.HighlightBlock event) {
        // Reference net.minecraft.client.renderer.WorldRenderer#drawSelectionBox

        BlockState state = Minecraft.getInstance().world.getBlockState(event.getTarget().getPos());

        if (!Minecraft.getInstance().player.isCreative() || (Minecraft.getInstance().player.isCreative() && Minecraft.getInstance().player.isSneaking())) {

            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float) Minecraft.getInstance().mainWindow.getFramebufferWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 0.999F);

            // We are trying to render the block highlight for the double slab
            if (state.getBlock() == Registrar.DOUBLE_SLAB) {
                // Offset the position of the block for when we render
                double x = (double) event.getTarget().getPos().getX() - event.getInfo().getProjectedView().x;
                double y = (double) event.getTarget().getPos().getY() - event.getInfo().getProjectedView().y;
                double z = (double) event.getTarget().getPos().getZ() - event.getInfo().getProjectedView().z;
                // Check if we are looking at the top slab or bottom slab
                if (event.getTarget().getHitVec().y - event.getTarget().getPos().getY() > 0.5) {
                    // Draw the top slab bounding box
                    WorldRenderer.drawBoundingBox(x, y + 0.5f, z, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                } else {
                    // Draw the bottom slab bounding box
                    WorldRenderer.drawBoundingBox(x, y, z, x + 1, y + 0.5f, z + 1, 0, 0, 0, 0.4f);
                }
                // Don't draw the default block highlight
                event.setCanceled(true);
            }

            if (state.getBlock() == Registrar.VERTICAL_SLAB && state.get(BlockVerticalSlab.DOUBLE)) {
                // Offset the position of the block for when we render
                double x = (double) event.getTarget().getPos().getX() - event.getInfo().getProjectedView().x;
                double y = (double) event.getTarget().getPos().getY() - event.getInfo().getProjectedView().y;
                double z = (double) event.getTarget().getPos().getZ() - event.getInfo().getProjectedView().z;

                switch (state.get(BlockVerticalSlab.FACING).getAxis()) {
                    case X:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().getHitVec().x - event.getTarget().getPos().getX() > 0.5) {
                            // Draw the top slab bounding box
                            WorldRenderer.drawBoundingBox(x + 0.5f, y, z, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            WorldRenderer.drawBoundingBox(x, y, z, x + 0.5f, y + 1, z + 1, 0, 0, 0, 0.4f);
                        }
                        // Don't draw the default block highlight
                        event.setCanceled(true);
                        break;
                    case Z:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().getHitVec().z - event.getTarget().getPos().getZ() > 0.5) {
                            // Draw the top slab bounding box
                            WorldRenderer.drawBoundingBox(x, y, z + 0.5f, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            WorldRenderer.drawBoundingBox(x, y, z, x + 1, y + 1, z + 0.5f, 0, 0, 0, 0.4f);
                        }
                        // Don't draw the default block highlight
                        event.setCanceled(true);
                        break;
                    default:
                        break;
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();

        }

    }

}
