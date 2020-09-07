package cjminecraft.doubleslabs.client.handlers;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.state.IProperty;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class ClientRenderHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderBlockHighlight(DrawHighlightEvent.HighlightBlock event) {
        // Reference net.minecraft.client.renderer.WorldRenderer#drawSelectionBox

        BlockState state = Minecraft.getInstance().world.getBlockState(event.getTarget().getPos());

        if (!Minecraft.getInstance().player.abilities.isCreativeMode || (Minecraft.getInstance().player.abilities.isCreativeMode && Minecraft.getInstance().player.isSneaking())) {
            // We are trying to render the block highlight for the double slab
            if (state.getBlock() == DSBlocks.DOUBLE_SLAB.get()) {
                // Offset the position of the block for when we render
                double x = (double) event.getTarget().getPos().getX() - event.getInfo().getProjectedView().x;
                double y = (double) event.getTarget().getPos().getY() - event.getInfo().getProjectedView().y;
                double z = (double) event.getTarget().getPos().getZ() - event.getInfo().getProjectedView().z;
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

            if (state.getBlock() == DSBlocks.VERTICAL_SLAB.get() && state.get(VerticalSlabBlock.DOUBLE)) {
                // Offset the position of the block for when we render
                double x = (double) event.getTarget().getPos().getX() - event.getInfo().getProjectedView().x;
                double y = (double) event.getTarget().getPos().getY() - event.getInfo().getProjectedView().y;
                double z = (double) event.getTarget().getPos().getZ() - event.getInfo().getProjectedView().z;

                switch (state.get(VerticalSlabBlock.FACING).getAxis()) {
                    case X:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().getHitVec().x - event.getTarget().getPos().getX() > 0.5) {
                            // Draw the top slab bounding box
                            WorldRenderer.drawBoundingBox(event.getMatrix(), event.getBuffers().getBuffer(RenderType.getLines()), x + 0.5f, y, z, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            WorldRenderer.drawBoundingBox(event.getMatrix(), event.getBuffers().getBuffer(RenderType.getLines()), x, y, z, x + 0.5f, y + 1, z + 1, 0, 0, 0, 0.4f);
                        }
                        // Don't draw the default block highlight
                        event.setCanceled(true);
                        break;
                    case Z:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().getHitVec().z - event.getTarget().getPos().getZ() > 0.5) {
                            // Draw the top slab bounding box
                            WorldRenderer.drawBoundingBox(event.getMatrix(), event.getBuffers().getBuffer(RenderType.getLines()), x, y, z + 0.5f, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            WorldRenderer.drawBoundingBox(event.getMatrix(), event.getBuffers().getBuffer(RenderType.getLines()), x, y, z, x + 1, y + 1, z + 0.5f, 0, 0, 0, 0.4f);
                        }
                        // Don't draw the default block highlight
                        event.setCanceled(true);
                        break;
                    default:
                        break;
                }
            }
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

    private static String stateToString(@Nullable BlockState state) {
        if (state == null)
            return TextFormatting.RED + "null";
        return state.getBlock().getRegistryName().toString() + "[" + state.getValues().entrySet().stream().map(MAP_ENTRY_TO_STRING).collect(Collectors.joining(",")) + "]";
    }

    private static String tileToString(@Nullable TileEntity tile) {
        if (tile == null)
            return TextFormatting.RED + "null";
        String data = tile.getTileData().toString();
        return ForgeRegistries.TILE_ENTITIES.getKey(tile.getType()).toString() + data;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderOverlayText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getInstance().gameSettings.showDebugInfo) {
            if (Minecraft.getInstance().objectMouseOver != null && Minecraft.getInstance().world != null && Minecraft.getInstance().objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
                BlockPos pos = ((BlockRayTraceResult) Minecraft.getInstance().objectMouseOver).getPos();
                TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
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
