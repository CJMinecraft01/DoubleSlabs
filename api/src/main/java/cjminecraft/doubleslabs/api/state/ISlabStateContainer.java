package cjminecraft.doubleslabs.api.state;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ISlabStateContainer {

    BlockState getBlockState();

    void setBlockState(BlockState state);

    default boolean hasBlockState() {
        return !getBlockState().isAir();
    }

    default void callOnBlockState(Consumer<BlockState> consumer) {
        if (hasBlockState()) {
            consumer.accept(getBlockState());
        }
    }

    default <T> T callOnBlockState(Function<BlockState, T> function, Supplier<T> orElse) {
        return hasBlockState() ? function.apply(getBlockState()) : orElse.get();
    }

    @Nullable
    BlockEntity getBlockEntity();

    void setBlockEntity(@Nullable BlockEntity blockEntity);

    default boolean hasBlockEntity() {
        return getBlockEntity() != null;
    }

    default void callOnBlockEntity(Consumer<BlockEntity> consumer) {
        if (hasBlockEntity()) {
            consumer.accept(getBlockEntity());
        }
    }

    default <T> T callOnBlockEntity(Function<BlockEntity, T> function, Supplier<T> orElse) {
        return hasBlockEntity() ? function.apply(getBlockEntity()) : orElse.get();
    }

    BlockPos getBlockPos();

    @Nullable
    Level getLevel();

    void setLevel(@Nullable Level level);

    default boolean hasLevel() {
        return getLevel() != null;
    }

    default CompoundTag serialize(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        callOnBlockState(state -> tag.put("state", NbtUtils.writeBlockState(state)));
        callOnBlockEntity(blockEntity -> tag.put("blockEntity", blockEntity.saveWithId(registries)));
        return tag;
    }

    default void deserialize(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("state")) {
            HolderGetter<Block> blockGetter = hasLevel() ?
                    Objects.requireNonNull(getLevel()).holderLookup(Registries.BLOCK) :
                    BuiltInRegistries.BLOCK.asLookup();
            setBlockState(NbtUtils.readBlockState(blockGetter, tag.getCompound("state")));
        }
        if (tag.contains("blockEntity")) {
            setBlockEntity(BlockEntity.loadStatic(getBlockPos(), getBlockState(), tag.getCompound("blockEntity"),
                    registries));
        }
    }

}
