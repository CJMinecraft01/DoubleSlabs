package cjminecraft.doubleslabs.forge.common.block;

import cjminecraft.doubleslabs.common.block.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.hooks.VerticalSlabBlockHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class ForgeVerticalSlabBlock extends VerticalSlabBlock {
    public ForgeVerticalSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        final var usePlayerDestroy = VerticalSlabBlockHooks.removeBlock(state, level, pos, player, willHarvest);
        return usePlayerDestroy || super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return VerticalSlabBlockHooks.getCloneItemStack(level, pos, state, target);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return VerticalSlabBlockHooks.getSoundType(level, state, pos, entity)
                .orElseGet(() -> super.getSoundType(state, level, pos, entity));
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return VerticalSlabBlockHooks.getParticleForLanding(level, state1, pos, entity).map(particle -> {
            level.sendParticles(particle, entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0, 0.0, 0.0, 0.15F);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return VerticalSlabBlockHooks.getParticleForLanding(level, state, pos, entity).map(particle -> {
            level.addParticle(particle, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
            return true;
        }).orElse(false);
    }
}
