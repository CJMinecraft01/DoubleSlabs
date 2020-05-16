package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.patches.DynamicSurroundings;
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
            if (itemSupport == null)
                return;
            if (event.getEntityPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                BlockPos pos = event.getPos();
                EnumFacing face = event.getFace();
                IBlockState state = event.getWorld().getBlockState(pos);
                if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)))
                    return;
                ISlabSupport blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                if (blockSupport == null) {
                    pos = pos.offset(event.getFace());
                    state = event.getWorld().getBlockState(pos);
                    if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)))
                        return;
                    face = event.getHitVec().y - pos.getY() > 0.5 ? EnumFacing.UP : EnumFacing.DOWN;
                    blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
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
