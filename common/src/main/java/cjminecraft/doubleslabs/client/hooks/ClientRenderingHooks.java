package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.api.state.Half;
import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ClientRenderingHooks {

    public static boolean renderBlockHighlight(PoseStack poseStack, double camX, double camY, double camZ, Supplier<VertexConsumer> vertexConsumer) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null || minecraft.hitResult == null) {
            return false;
        }

        final Player player = minecraft.player;

        // Only show the half slab highlight if we are in survival or if sneaking in creative
        if (player.isCreative() && !player.isCrouching()) {
            return false;
        }

        final BlockHitResult hitResult = (BlockHitResult) minecraft.hitResult;

        final BlockState state = minecraft.level.getBlockState(hitResult.getBlockPos());

        // TODO: It may be better to use the shapes of each half instead of manually defining a box
        if (state.is(DSBlocks.MIXED_SLAB.get())) {
            // Offset the position of the block for when we render
            final double x = hitResult.getBlockPos().getX() - camX;
            double y = hitResult.getBlockPos().getY() - camY;
            final double z = hitResult.getBlockPos().getZ() - camZ;

            // Check if we are looking at the top or bottom of the slab
            if (hitResult.getLocation().y - hitResult.getBlockPos().getY() > 0.5) {
                y += 0.5;
            }

            LevelRenderer.renderLineBox(poseStack, vertexConsumer.get(), x, y, z, x + 1, y + 0.5, z + 1, 0, 0, 0, 0.4f);
            return true;
        }

        return false;
    }

    public static void addTextToDebugScreenOverlay(List<String> text) {
        final Minecraft minecraft = Minecraft.getInstance();

        Preconditions.checkState(minecraft.getCameraEntity() != null, "The camera entity must be nonnull to be able " +
                "to add debug text");
        Preconditions.checkState(minecraft.level != null, "The level must be nonnull");

        final HitResult hitResult = minecraft.getCameraEntity().pick(20.0F, 0.0F, false);

        if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos hitPos = blockHitResult.getBlockPos();
            BlockEntity blockEntity = minecraft.level.getBlockEntity(hitPos);

            if (blockEntity instanceof DynamicSlabBlockEntity<?> dynamicSlabBlockEntity) {
                text.add("");
                text.add(ChatFormatting.UNDERLINE + "Dynamic Slab Types");
                dynamicSlabBlockEntity.runOnBlockStates((half, state) -> addBlockStateToDebugScreenOverlay(half,
                        state, text));
                dynamicSlabBlockEntity.runOnBlockEntities((half, entity) -> addBlockEntityToDebugScreenOverlay(half,
                        entity, text));
            }
        }
    }

    private static String getHalfPrefix(Half half) {
        return ChatFormatting.ITALIC + half.toString() + ChatFormatting.RESET + ": ";
    }

    private static void addBlockStateToDebugScreenOverlay(Half half, BlockState state, List<String> text) {
        text.add(getHalfPrefix(half) + BuiltInRegistries.BLOCK.getKey(state.getBlock()));
    }

    private static void addBlockEntityToDebugScreenOverlay(Half half, @Nullable BlockEntity blockEntity,
                                                           List<String> text) {
        if (blockEntity == null) {
            text.add(getHalfPrefix(half) + ChatFormatting.RED + "null");
        } else {
            text.add(getHalfPrefix(half) + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()));
        }
    }

}
