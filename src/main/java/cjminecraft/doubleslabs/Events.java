package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ISlabSupport itemSupport = SlabSupport.getSupport(event.getItemStack(), event.getPlayer(), event.getHand());
            if (itemSupport == null)
                return;

            if (event.getPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                BlockPos pos = event.getPos();
                if (MathHelper.floor(event.getPlayer().getPosX()) == pos.getX() && MathHelper.floor(event.getPlayer().getPosY()) == pos.getY() && MathHelper.floor(event.getPlayer().getPosZ()) == pos.getZ())
                    return;
                Direction face = event.getFace();
                BlockState state = event.getWorld().getBlockState(pos);
                if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)))
                    return;
                ISlabSupport blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                if (blockSupport == null) {

                    boolean foundVerticalSlab = false;
                    Direction verticalSlabFacing = null;

                    if (state.getBlock() == Registrar.VERTICAL_SLAB && !state.get(BlockVerticalSlab.DOUBLE)) {
                        foundVerticalSlab = true;
                        verticalSlabFacing = state.get(BlockVerticalSlab.FACING);
                        if (!event.getPlayer().isCrouching()) {
                            BlockState newState = state.with(BlockVerticalSlab.DOUBLE, true);
                            if (event.getWorld().setBlockState(pos, newState, 11)) {
                                BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), SlabType.TOP);
                                TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                                if (tile == null)
                                    return;
                                tile.setNegativeState(slabState);

                                event.getWorld().markBlockRangeForRenderUpdate(pos, state, newState);

                                finishBlockPlacement(event, pos, slabState);
                                return;
                            }
                        }
                    }

                    pos = pos.offset(face);
                    if (MathHelper.floor(event.getPlayer().getPosX()) == pos.getX() && MathHelper.floor(event.getPlayer().getPosY()) == pos.getY() && MathHelper.floor(event.getPlayer().getPosZ()) == pos.getZ())
                        return;
                    state = event.getWorld().getBlockState(pos);
                    if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)))
                        return;
                    if (!event.getPlayer().canPlayerEdit(pos.offset(face), face, event.getItemStack()))
                        return;
                    blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                    if (blockSupport == null) {
                        if (event.getPlayer().isCrouching() && (!foundVerticalSlab || face == verticalSlabFacing)) {
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
                    face = blockSupport.getHalf(event.getWorld(), pos, state) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN;
                }

                SlabType half = blockSupport.getHalf(event.getWorld(), pos, state);
                if (half == SlabType.DOUBLE)
                    return;

                if (!Config.REPLACE_SAME_SLAB.get() && blockSupport == itemSupport && blockSupport.areSame(event.getWorld(), pos, state, event.getItemStack()))
                    return;

                if ((face == Direction.UP && half == SlabType.BOTTOM) || (face == Direction.DOWN && half == SlabType.TOP)) {
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

    @OnlyIn(Dist.CLIENT)
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
    public static void renderBlockHighlight(DrawHighlightEvent.HighlightBlock event) {
        // Reference net.minecraft.client.renderer.WorldRenderer#drawSelectionBox

        BlockState state = Minecraft.getInstance().world.getBlockState(event.getTarget().getPos());
        // We are trying to render the block highlight for the double slab
        if (state.getBlock() == Registrar.DOUBLE_SLAB && !Minecraft.getInstance().player.abilities.isCreativeMode) {
            // Offset the position of the block for when we render
            double x = (double)event.getTarget().getPos().getX() - event.getInfo().getProjectedView().x;
            double y = (double)event.getTarget().getPos().getY() - event.getInfo().getProjectedView().y;
            double z = (double)event.getTarget().getPos().getZ() - event.getInfo().getProjectedView().z;
            // Check if we are looking at the top slab or bottom slab
            if (event.getTarget().getHitVec().y - event.getTarget().getPos().getY() > 0.5) {
                // Draw the top slab bounding box
                WorldRenderer.drawBoundingBox(event.getMatrix(), event.getBuffers().getBuffer(RenderType.getLines()), x, y + 0.5f, z, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
            } else {
                // Draw the bottom slab bounding box
                WorldRenderer.drawBoundingBox(event.getMatrix(), event.getBuffers().getBuffer(RenderType.getLines()), x, y, z, x + 1, y + 0.5f, z + 1, 0, 0, 0, 0.4f);
            }
            // Don't draw the default block highlight
            event.setCanceled(true);
        }
    }

}
