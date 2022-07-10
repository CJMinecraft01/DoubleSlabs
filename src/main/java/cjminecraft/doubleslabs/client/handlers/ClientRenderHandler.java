package cjminecraft.doubleslabs.client.handlers;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;
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
    public static void renderBlockHighlight(DrawSelectionEvent.HighlightBlock event) {
        // Reference net.minecraft.client.renderer.WorldRenderer#drawSelectionBox

        BlockState state = Minecraft.getInstance().level.getBlockState(event.getTarget().getBlockPos());

        Player player = Minecraft.getInstance().player;

        if (player != null && (!player.isCreative() || (player.isCreative() && player.isCrouching()))) {
            // We are trying to render the block highlight for the double slab
            if (state.getBlock() == DSBlocks.DOUBLE_SLAB.get()) {
                // Offset the position of the block for when we render
                double x = (double) event.getTarget().getBlockPos().getX() - event.getCamera().getPosition().x;
                double y = (double) event.getTarget().getBlockPos().getY() - event.getCamera().getPosition().y;
                double z = (double) event.getTarget().getBlockPos().getZ() - event.getCamera().getPosition().z;
                // Check if we are looking at the top slab or bottom slab
                if (event.getTarget().getLocation().y - event.getTarget().getBlockPos().getY() > 0.5) {
                    // Draw the top slab bounding box
                    LevelRenderer.renderLineBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), x, y + 0.5f, z, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                } else {
                    // Draw the bottom slab bounding box
                    LevelRenderer.renderLineBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), x, y, z, x + 1, y + 0.5f, z + 1, 0, 0, 0, 0.4f);
                }
                // Don't draw the default block highlight
                event.setCanceled(true);
            }

            if (state.getBlock() == DSBlocks.VERTICAL_SLAB.get() && state.getValue(VerticalSlabBlock.DOUBLE)) {
                // Offset the position of the block for when we render
                double x = (double) event.getTarget().getBlockPos().getX() - event.getCamera().getPosition().x;
                double y = (double) event.getTarget().getBlockPos().getY() - event.getCamera().getPosition().y;
                double z = (double) event.getTarget().getBlockPos().getZ() - event.getCamera().getPosition().z;

                switch (state.getValue(VerticalSlabBlock.FACING).getAxis()) {
                    case X:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().getLocation().x - event.getTarget().getBlockPos().getX() > 0.5) {
                            // Draw the top slab bounding box
                            LevelRenderer.renderLineBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), x + 0.5f, y, z, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            LevelRenderer.renderLineBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), x, y, z, x + 0.5f, y + 1, z + 1, 0, 0, 0, 0.4f);
                        }
                        // Don't draw the default block highlight
                        event.setCanceled(true);
                        break;
                    case Z:
                        // Check if we are looking at the top slab or bottom slab
                        if (event.getTarget().getLocation().z - event.getTarget().getBlockPos().getZ() > 0.5) {
                            // Draw the top slab bounding box
                            LevelRenderer.renderLineBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), x, y, z + 0.5f, x + 1, y + 1, z + 1, 0, 0, 0, 0.4f);
                        } else {
                            // Draw the bottom slab bounding box
                            LevelRenderer.renderLineBox(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), x, y, z, x + 1, y + 1, z + 0.5f, 0, 0, 0, 0.4f);
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

    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> MAP_ENTRY_TO_STRING = new Function<Map.Entry<Property<?>, Comparable<?>>, String>() {
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                Property<?> prop = entry.getKey();
                return prop.getName() + "=" + this.getPropertyName(prop, entry.getValue());
            }
        }

        private <T extends Comparable<T>> String getPropertyName(Property<T> property, Comparable<?> entry) {
            return property.getName((T)entry);
        }
    };

    private static String stateToString(@Nullable BlockState state) {
        if (state == null)
            return ChatFormatting.RED + "null";
        return state.getBlock().getRegistryName().toString() + "[" + state.getValues().entrySet().stream().map(MAP_ENTRY_TO_STRING).collect(Collectors.joining(",")) + "]";
    }

    private static String tileToString(@Nullable BlockEntity tile) {
        if (tile == null)
            return ChatFormatting.RED + "null";
        String data = tile.getTileData().toString();
        return ForgeRegistries.BLOCK_ENTITIES.getKey(tile.getType()).toString() + data;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderOverlayText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getInstance().options.renderDebug) {
            if (Minecraft.getInstance().hitResult != null && Minecraft.getInstance().level != null && Minecraft.getInstance().hitResult.getType() == BlockHitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) Minecraft.getInstance().hitResult).getBlockPos();
                BlockEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(pos);
                if (tileEntity instanceof SlabTileEntity) {
                    SlabTileEntity tile = (SlabTileEntity) tileEntity;
                    event.getRight().add("");
                    event.getRight().add("Slab Types");
                    event.getRight().add("Positive Block: " + stateToString(tile.getPositiveBlockInfo().getBlockState()));
                    event.getRight().add("Positive Tile: " + tileToString(tile.getPositiveBlockInfo().getBlockEntity()));
                    event.getRight().add("Negative Block: " + stateToString(tile.getNegativeBlockInfo().getBlockState()));
                    event.getRight().add("Negative Tile: " + tileToString(tile.getNegativeBlockInfo().getBlockEntity()));
                }
            }
        }
    }

}
