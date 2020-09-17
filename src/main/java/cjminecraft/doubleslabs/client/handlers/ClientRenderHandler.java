package cjminecraft.doubleslabs.client.handlers;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID, value = Side.CLIENT)
public class ClientRenderHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void renderBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK)
            return;
        // Reference net.minecraft.client.renderer.WorldRenderer#drawSelectionBox

        IBlockState state = Minecraft.getMinecraft().world.getBlockState(event.getTarget().getBlockPos());

        if (!Minecraft.getMinecraft().player.isCreative() || (Minecraft.getMinecraft().player.isCreative() && Minecraft.getMinecraft().player.isSneaking())) {

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            // Offset the position of the block for when we render
            double x = event.getTarget().getBlockPos().getX() - (event.getPlayer().lastTickPosX + (event.getPlayer().posX - event.getPlayer().lastTickPosX) * (double) event.getPartialTicks());
            double y = event.getTarget().getBlockPos().getY() - (event.getPlayer().lastTickPosY + (event.getPlayer().posY - event.getPlayer().lastTickPosY) * (double) event.getPartialTicks());
            double z = event.getTarget().getBlockPos().getZ() - (event.getPlayer().lastTickPosZ + (event.getPlayer().posZ - event.getPlayer().lastTickPosZ) * (double) event.getPartialTicks());

            // We are trying to render the block highlight for the double slab
            if (state.getBlock() == DSBlocks.DOUBLE_SLAB) {
                if (Minecraft.getMinecraft().world.getWorldBorder().contains(event.getTarget().getBlockPos())) {

                    final double expansionAmount = 0.0020000000949949026D;

                    // Check if we are looking at the top slab or bottom slab
                    if (event.getTarget().hitVec.y - event.getTarget().getBlockPos().getY() > 0.5) {
                        // Draw the top slab bounding box
                        RenderGlobal.drawBoundingBox(x - expansionAmount, y + 0.5f - expansionAmount, z - expansionAmount, x + 1 + expansionAmount, y + 1 + expansionAmount, z + 1 + expansionAmount, 0, 0, 0, 0.4f);
                    } else {
                        // Draw the bottom slab bounding box
                        RenderGlobal.drawBoundingBox(x - expansionAmount, y - expansionAmount, z - expansionAmount, x + 1 + expansionAmount, y + 0.5f + expansionAmount, z + 1 + expansionAmount, 0, 0, 0, 0.4f);
                    }
                }

                // Don't draw the default block highlight
                event.setCanceled(true);
            }

            if (state.getBlock() == DSBlocks.VERTICAL_SLAB && state.getValue(VerticalSlabBlock.DOUBLE)) {

                final double expansionAmount = 0.0020000000949949026D;

                switch (state.getValue(VerticalSlabBlock.FACING).getAxis()) {
                    case X:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().hitVec.x - event.getTarget().getBlockPos().getX() > 0.5) {
                            // Draw the top slab bounding box
                            RenderGlobal.drawBoundingBox(x + 0.5f - expansionAmount, y - expansionAmount, z - expansionAmount, x + 1 + expansionAmount, y + 1 + expansionAmount, z + 1 + expansionAmount, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            RenderGlobal.drawBoundingBox(x - expansionAmount, y - expansionAmount, z - expansionAmount, x + 0.5f + expansionAmount, y + 1 + expansionAmount, z + 1 + expansionAmount, 0, 0, 0, 0.4f);
                        }
                        // Don't draw the default block highlight
                        event.setCanceled(true);
                        break;
                    case Z:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().hitVec.z - event.getTarget().getBlockPos().getZ() > 0.5) {
                            // Draw the top slab bounding box
                            RenderGlobal.drawBoundingBox(x - expansionAmount, y - expansionAmount, z + 0.5f - expansionAmount, x + 1 + expansionAmount, y + 1 + expansionAmount, z + 1 + expansionAmount, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            RenderGlobal.drawBoundingBox(x - expansionAmount, y - expansionAmount, z - expansionAmount, x + 1 + expansionAmount, y + 1 + expansionAmount, z + 0.5f + expansionAmount, 0, 0, 0, 0.4f);
                        }
                        // Don't draw the default block highlight
                        event.setCanceled(true);
                        break;
                    default:
                        break;
                }
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    private static final Function<Map.Entry<IProperty<?>, Comparable<?>>, String> MAP_ENTRY_TO_STRING = new Function<Map.Entry<IProperty<?>, Comparable<?>>, String>() {
        public String apply(@Nullable Map.Entry<IProperty<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                IProperty<?> prop = entry.getKey();
                return prop.getName() + "=" + this.getPropertyName(prop, entry.getValue());
            }
        }

        private <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> entry) {
            return property.getName((T)entry);
        }
    };

    private static <T extends Comparable<T>> String stateToString(@Nullable IBlockState state) {
        if (state == null)
            return TextFormatting.RED + "null";
        return state.getBlock().getRegistryName().toString() + "[" + state.getProperties().entrySet().stream().map(MAP_ENTRY_TO_STRING).collect(Collectors.joining(",")) + "]";
    }

    private static String tileToString(@Nullable TileEntity tile) {
        if (tile == null)
            return TextFormatting.RED + "null";
        String data = tile.getTileData().toString();
        return TileEntity.getKey(tile.getClass()) + data;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void renderOverlayText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            if (Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
                TileEntity tileEntity = Minecraft.getMinecraft().world.getTileEntity(pos);
                if (tileEntity instanceof SlabTileEntity) {
                    SlabTileEntity tile = (SlabTileEntity) tileEntity;
                    event.getRight().add("");
                    event.getRight().add("Slab Types");
                    event.getRight().add("Positive Block: " + stateToString(tile.getPositiveBlockInfo().getBlockState()));
                    event.getRight().add("Positive Tile: " + tileToString(tile.getPositiveBlockInfo().getTileEntity()));
                    event.getRight().add("Negative Block: " + stateToString(tile.getNegativeBlockInfo().getBlockState()));
                    event.getRight().add("Negative Tile: " + tileToString(tile.getNegativeBlockInfo().getTileEntity()));
                }
            }
        }
    }

}
