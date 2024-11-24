package cjminecraft.doubleslabs.fabric.mixin;

import cjminecraft.doubleslabs.common.hooks.MixedDoubleSlabBlockHooks;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow private Level level;

    @Redirect(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
    private SoundType playStepSound$getSoundType$doubleslabs(BlockState instance, BlockPos pos) {
        if (instance.is(DSBlocks.MIXED_SLAB.get())) {
            return MixedDoubleSlabBlockHooks.getSoundType(level, pos, (Entity) (Object) this).orElseGet(instance::getSoundType);
        }
        return instance.getSoundType();
    }

}
