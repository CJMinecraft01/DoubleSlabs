package cjminecraft.doubleslabs.api.state;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ISlabStateContainer {

    BlockState getBlockState();

    void setBlockState(BlockState state);

    default boolean hasBlockState() {
        return !getBlockState().isAir();
    }

    default void runOnBlockState(Consumer<BlockState> consumer) {
        if (hasBlockState()) {
            consumer.accept(getBlockState());
        }
    }

    default <T> T callOnBlockState(Function<BlockState, T> function, Supplier<T> orElse) {
        return hasBlockState() ? function.apply(getBlockState()) : orElse.get();
    }

    default <T> Optional<T> callOnBlockState(Function<BlockState, T> function) {
        return hasBlockState() ? Optional.of(function.apply(getBlockState())) : Optional.empty();
    }

    @Nullable
    BlockEntity getBlockEntity();

    void setBlockEntity(@Nullable BlockEntity blockEntity);

    default boolean hasBlockEntity() {
        return getBlockEntity() != null;
    }

    default void runOnBlockEntity(Consumer<BlockEntity> consumer) {
        if (hasBlockEntity()) {
            consumer.accept(getBlockEntity());
        }
    }

    default <T> T callOnBlockEntity(Function<BlockEntity, T> function, Supplier<T> orElse) {
        return hasBlockEntity() ? function.apply(getBlockEntity()) : orElse.get();
    }

    default <T> Optional<T> callOnBlockEntity(Function<BlockEntity, T> function) {
        return hasBlockEntity() ? Optional.of(function.apply(getBlockEntity())) : Optional.empty();
    }

    BlockPos getBlockPos();

    @Nullable
    Level getLevel();

    void setLevel(@Nullable Level level);

    default boolean hasLevel() {
        return getLevel() != null;
    }

    default CompoundTag serialize() {
        final var tag = new CompoundTag();
        runOnBlockState(state -> tag.put("state", NbtUtils.writeBlockState(state)));
        runOnBlockEntity(blockEntity -> tag.put("blockEntity", blockEntity.saveWithId()));
        return tag;
    }

    default void deserialize(CompoundTag tag) {
        if (tag.contains("state")) {
            final var blockGetter = hasLevel() ?
                    Objects.requireNonNull(getLevel()).holderLookup(Registries.BLOCK) :
                    BuiltInRegistries.BLOCK.asLookup();
            setBlockState(NbtUtils.readBlockState(blockGetter, tag.getCompound("state")));
        }
        if (tag.contains("blockEntity")) {
            setBlockEntity(BlockEntity.loadStatic(getBlockPos(), getBlockState(), tag.getCompound("blockEntity")));
        }
    }

}
