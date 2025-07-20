package cjminecraft.doubleslabs.neoforge.client.block;

import cjminecraft.doubleslabs.client.hooks.DynamicSlabBlockClientHooks;
import cjminecraft.doubleslabs.client.hooks.VerticalSlabBlockClientHooks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

public class VerticalSlabClientBlockExtensions implements IClientBlockExtensions {

    public static IClientBlockExtensions INSTANCE = new VerticalSlabClientBlockExtensions();

    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
        if (target.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        final var hitResult = (BlockHitResult) target;

        return VerticalSlabBlockClientHooks.addHitEffects((ClientLevel) level, state, hitResult.getBlockPos(), hitResult, hitResult.getDirection(), manager);
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        return DynamicSlabBlockClientHooks.addDestroyEffects(level, pos, manager);
    }
}
