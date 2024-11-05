package cjminecraft.doubleslabs.client.hooks;

import cjminecraft.doubleslabs.common.block.entity.DynamicSlabBlockEntity;
import com.google.common.base.Preconditions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class ClientRenderingHooks {

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
        return ChatFormatting.ITALIC + half.getSerializedName() + ChatFormatting.RESET + ": ";
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
