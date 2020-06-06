package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.blocks.BlockVerticalSlab;
import cjminecraft.doubleslabs.patches.DynamicSurroundings;
import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(DoubleSlabs.MODID)) {
            ConfigManager.sync(DoubleSlabs.MODID, Config.Type.INSTANCE);
            DoubleSlabsConfig.SLAB_BLACKLIST = Arrays.asList(DoubleSlabsConfig.SLAB_BLACKLIST_ARRAY);
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ISlabSupport itemSupport = SlabSupport.getSupport(event.getItemStack(), event.getEntityPlayer(), event.getHand());
            if (itemSupport == null) {
                // If not, check to see if the item is a vertical slab which is supported
                itemSupport = SlabSupport.getVerticalSlabSupport(event.getItemStack(), event.getEntityPlayer(), event.getHand());

                // If not, don't do anything special
                if (itemSupport == null)
                    return;

                // Check that the player can actually place a block here
                if (event.getEntityPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                    BlockPos pos = event.getPos();
                    // Check that the player isn't trying to place a block in the same position as them
                    if (MathHelper.floor(event.getEntityPlayer().posX) == pos.getX() && MathHelper.floor(event.getEntityPlayer().posY) == pos.getY() && MathHelper.floor(event.getEntityPlayer().posZ) == pos.getZ())
                        return;

                    IBlockState state = event.getWorld().getBlockState(pos);
                    EnumFacing face = event.getFace();

                    if (state.getBlock() != Registrar.VERTICAL_SLAB && event.getWorld().getBlockState(pos.offset(face)).getBlock() == Registrar.VERTICAL_SLAB) {
                        state = event.getWorld().getBlockState(pos.offset(face));
                        pos = pos.offset(face);
                        TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                        if (tile != null)
                            face = tile.getPositiveState() != null ? state.getValue(BlockVerticalSlab.FACING).getOpposite() : state.getValue(BlockVerticalSlab.FACING);
                    }

                    // Check if the block that they clicked on is a vertical slab
                    if (state.getBlock() == Registrar.VERTICAL_SLAB) {
                        // If we are trying to mix to one of our vertical slabs
                        if (!state.getValue(BlockVerticalSlab.DOUBLE)) {
                            TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                            // Check that the tile has been created and that the shift key isn't pressed and that we are clicking on the face that is inside of the block
                            if (tile != null && !event.getEntityPlayer().isSneaking() && (face != state.getValue(BlockVerticalSlab.FACING) || tile.getPositiveState() == null)) {
                                // The new state for the vertical slab with the double property set
                                IBlockState newState = state.withProperty(BlockVerticalSlab.DOUBLE, true);
                                // If we could set the block
                                if (event.getWorld().setBlockState(pos, newState, 11)) {
                                    // Get the correct slab state for the vertical slab
                                    IBlockState slabState = itemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), tile.getPositiveState() != null ? face.getOpposite() : face);

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
                            if (MathHelper.floor(event.getEntityPlayer().posX) == pos.getX() && MathHelper.floor(event.getEntityPlayer().posY) == pos.getY() && MathHelper.floor(event.getEntityPlayer().posZ) == pos.getZ())
                                return;
                            state = event.getWorld().getBlockState(pos);

                            if (!event.getEntityPlayer().canPlayerEdit(pos, face, event.getItemStack()))
                                return;

                            blockSupport = SlabSupport.getVerticalSlabSupport(event.getWorld(), pos, state);
                            if (blockSupport == null)
                                return;

                            face = blockSupport.getDirection(event.getWorld(), pos, state);
                        }

                        // Get the direction that the vertical slab block is facing
                        EnumFacing direction = blockSupport.getDirection(event.getWorld(), pos, state);

                        if (face == direction) {
                            // Get the state for the vertical slab item using the direction of the already placed vertical slab
                            IBlockState slabState = itemSupport.getStateForDirection(event.getWorld(), pos, event.getItemStack(), direction.getOpposite());
                            // Create the state for the vertical slab
                            IBlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().withProperty(BlockVerticalSlab.FACING, direction).withProperty(BlockVerticalSlab.DOUBLE, true);

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
            if (event.getEntityPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                BlockPos pos = event.getPos();
                EnumFacing face = event.getFace();
                IBlockState state = event.getWorld().getBlockState(pos);
                if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)))
                    return;

                boolean verticalSlab = state.getBlock() == Registrar.VERTICAL_SLAB && !state.getValue(BlockVerticalSlab.DOUBLE) && (((TileEntityVerticalSlab)event.getWorld().getTileEntity(pos)).getPositiveState() != null ? face == state.getValue(BlockVerticalSlab.FACING).getOpposite() : face == state.getValue(BlockVerticalSlab.FACING));

                ISlabSupport blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                if (blockSupport == null) {

                    // Check if the block is a vertical slab from another mod
                    blockSupport = SlabSupport.getVerticalSlabSupport(event.getWorld(), pos, state);
                    if (blockSupport != null) {
                        // We are trying to combine a mod vertical slab with a regular slab

                        EnumFacing direction = blockSupport.getDirection(event.getWorld(), pos, state);
                        if (face == direction) {
                            IBlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos,
                                    itemSupport.getStateFromStack(event.getItemStack(), event.getWorld(), pos, face,
                                            event.getHitVec(), event.getEntityPlayer(), event.getHand()),
                                    BlockSlab.EnumBlockHalf.BOTTOM);
                            IBlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().withProperty(BlockVerticalSlab.DOUBLE, true).withProperty(BlockVerticalSlab.FACING, direction);

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

                    pos = pos.offset(event.getFace());

                    if (MathHelper.floor(event.getEntityPlayer().posX) == pos.getX() && MathHelper.floor(event.getEntityPlayer().posY) == pos.getY() && MathHelper.floor(event.getEntityPlayer().posZ) == pos.getZ())
                        return;

                    state = event.getWorld().getBlockState(pos);
                    if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)))
                        return;
                    if (!event.getEntityPlayer().canPlayerEdit(pos.offset(face), face, event.getItemStack()))
                        return;

                    verticalSlab = state.getBlock() == Registrar.VERTICAL_SLAB && !state.getValue(BlockVerticalSlab.DOUBLE);

                    // Check if the offset state is a horizontal slab
                    blockSupport = SlabSupport.getHorizontalSlabSupport(event.getWorld(), pos, state);
                    if (blockSupport == null && !verticalSlab) {
                        // If not, check if it is a vertical slab
                        blockSupport = SlabSupport.getVerticalSlabSupport(event.getWorld(), pos, state);
                        if (blockSupport != null) {
                            // We are trying to combine a mod vertical slab with a regular slab

                            EnumFacing direction = blockSupport.getDirection(event.getWorld(), pos, state);

                            IBlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos,
                                    itemSupport.getStateFromStack(event.getItemStack(), event.getWorld(), pos, face,
                                            event.getHitVec(), event.getEntityPlayer(), event.getHand()),
                                    BlockSlab.EnumBlockHalf.BOTTOM);
                            IBlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().withProperty(BlockVerticalSlab.DOUBLE, true).withProperty(BlockVerticalSlab.FACING, direction);

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
                        if (event.getEntityPlayer().isSneaking() && !DoubleSlabsConfig.DISABLE_VERTICAL_SLAB_PLACEMENT) {
                            // Try to place a horizontal slab as a vertical slab
                            RayTraceResult result = Utils.rayTrace(event.getEntityPlayer());
                            if (face.getAxis() == EnumFacing.Axis.Y) {
                                EnumFacing direction = event.getEntityPlayer().getHorizontalFacing();

                                double distance;

                                if (direction.getAxis() == EnumFacing.Axis.X)
                                    distance = result.hitVec.x - pos.getX();
                                else
                                    distance = result.hitVec.z - pos.getZ();

                                if (direction.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE && distance < 0.5f)
                                    direction = direction.getOpposite();
                                else if (direction.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && distance > 0.5f)
                                    direction = direction.getOpposite();

                                IBlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos,
                                        itemSupport.getStateFromStack(event.getItemStack(), event.getWorld(), pos, face,
                                                event.getHitVec(), event.getEntityPlayer(), event.getHand()),
                                        BlockSlab.EnumBlockHalf.BOTTOM);
                                IBlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().withProperty(BlockVerticalSlab.FACING, direction);

                                if (event.getWorld().setBlockState(pos, newState, 11)) {
                                    TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                                    if (tile == null)
                                        return;
                                    tile.setPositiveState(slabState);

                                    finishBlockPlacement(event, pos, slabState);
                                }
                            } else {
                                IBlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos,
                                        itemSupport.getStateFromStack(event.getItemStack(), event.getWorld(), pos, face,
                                                event.getHitVec(), event.getEntityPlayer(), event.getHand()),
                                        BlockSlab.EnumBlockHalf.BOTTOM);
                                IBlockState newState = Registrar.VERTICAL_SLAB.getDefaultState().withProperty(BlockVerticalSlab.FACING, face.getOpposite());

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
                        face = blockSupport.getHalf(event.getWorld(), pos, state) == BlockSlab.EnumBlockHalf.BOTTOM ? EnumFacing.UP : EnumFacing.DOWN;
                } else if (state.getBlock().hasTileEntity(state) && state.getBlock().onBlockActivated(event.getWorld(), pos, state, event.getEntityPlayer(), event.getHand(), face, (float) event.getHitVec().x, (float) event.getHitVec().y, (float) event.getHitVec().z))
                    return;

                if (!DoubleSlabsConfig.REPLACE_SAME_SLAB && blockSupport == itemSupport && blockSupport.areSame(event.getWorld(), pos, state, event.getItemStack()))
                    return;

                if (blockSupport != null) {
                    BlockSlab.EnumBlockHalf half = blockSupport.getHalf(event.getWorld(), pos, state);
                    if ((face == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM) || (face == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP)) {
                        IBlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos,
                                itemSupport.getStateFromStack(event.getItemStack(), event.getWorld(), pos, face,
                                        event.getHitVec(), event.getEntityPlayer(), event.getHand()),
                                half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM);
                        if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(slabState)))
                            return;

                        IBlockState newState = ((IExtendedBlockState) Registrar.DOUBLE_SLAB.getDefaultState()).withProperty(BlockDoubleSlab.TOP, half == BlockSlab.EnumBlockHalf.TOP ? state : slabState).withProperty(BlockDoubleSlab.BOTTOM, half == BlockSlab.EnumBlockHalf.BOTTOM ? state : slabState);
                        AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(event.getWorld(), pos);

                        DynamicSurroundings.patchBlockState(newState);

                        if (axisalignedbb != Block.NULL_AABB && event.getWorld().checkNoEntityCollision(axisalignedbb.offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
                            TileEntityVerticalSlab tile = (TileEntityVerticalSlab) event.getWorld().getTileEntity(pos);
                            if (tile == null)
                                return;

                            tile.setPositiveState(half == BlockSlab.EnumBlockHalf.TOP ? state : slabState);
                            tile.setNegativeState(half == BlockSlab.EnumBlockHalf.TOP ? slabState : state);

                            SoundType soundtype = state.getBlock().getSoundType(slabState, event.getWorld(), pos, event.getEntityPlayer());
                            event.getWorld().playSound(event.getEntityPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                            if (!event.getEntityPlayer().isCreative())
                                event.getItemStack().shrink(1);
                            event.setCancellationResult(EnumActionResult.SUCCESS);
                            event.setCanceled(true);
                            if (event.getEntityPlayer() instanceof EntityPlayerMP)
                                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) event.getEntityPlayer(), pos, event.getItemStack());
                        } else {
                            event.setUseItem(Event.Result.DENY);
                        }
                    }
                }
            }
        }
    }

    private static void finishBlockPlacement(PlayerInteractEvent.RightClickBlock event, BlockPos pos, IBlockState slabState) {
        SoundType soundtype = slabState.getBlock().getSoundType(slabState, event.getWorld(), pos, event.getEntityPlayer());
        event.getWorld().playSound(event.getEntityPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        if (!event.getEntityPlayer().isCreative())
            event.getItemStack().shrink(1);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        event.setCanceled(true);
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) event.getEntityPlayer(), pos, event.getItemStack());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void renderBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK)
            return;
        // Reference net.minecraft.client.renderer.WorldRenderer#drawSelectionBox

        IBlockState state = Minecraft.getMinecraft().world.getBlockState(event.getTarget().getBlockPos());
        // We are trying to render the block highlight for the double slab
        if (state.getBlock() == Registrar.DOUBLE_SLAB && !Minecraft.getMinecraft().player.isCreative()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            if (Minecraft.getMinecraft().world.getWorldBorder().contains(event.getTarget().getBlockPos())) {
                // Offset the position of the block for when we render
                double x = event.getTarget().getBlockPos().getX() - (event.getPlayer().lastTickPosX + (event.getPlayer().posX - event.getPlayer().lastTickPosX) * (double) event.getPartialTicks());
                double y = event.getTarget().getBlockPos().getY() - (event.getPlayer().lastTickPosY + (event.getPlayer().posY - event.getPlayer().lastTickPosY) * (double) event.getPartialTicks());
                double z = event.getTarget().getBlockPos().getZ() - (event.getPlayer().lastTickPosZ + (event.getPlayer().posZ - event.getPlayer().lastTickPosZ) * (double) event.getPartialTicks());

                double expansionAmount = 0.0020000000949949026D;

                // Check if we are looking at the top slab or bottom slab
                if (event.getTarget().hitVec.y - event.getTarget().getBlockPos().getY() > 0.5) {
                    // Draw the top slab bounding box
                    RenderGlobal.drawBoundingBox(x - expansionAmount, y + 0.5f - expansionAmount, z - expansionAmount, x + 1 + expansionAmount, y + 1 + expansionAmount, z + 1 + expansionAmount, 0, 0, 0, 0.4f);
                } else {
                    // Draw the bottom slab bounding box
                    RenderGlobal.drawBoundingBox(x - expansionAmount, y - expansionAmount, z - expansionAmount, x + 1 + expansionAmount, y + 0.5f + expansionAmount, z + 1 + expansionAmount, 0, 0, 0, 0.4f);
                }
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            // Don't draw the default block highlight
            event.setCanceled(true);
        }
    }

}
