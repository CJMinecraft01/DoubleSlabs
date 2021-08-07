package cjminecraft.doubleslabs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class WorldWrapper extends Level implements IWorldWrapper<Level> {

    private Level world;
    
    private BlockPos pos;
    private IStateContainer container;
    private boolean positive;

    public WorldWrapper(Level level) {
        super((WritableLevelData) level.getLevelData(), level.dimension(), level.dimensionType(), level.getProfilerSupplier(), level.isClientSide, level.isDebug(), 0L);
        this.world = level;
    }

    @Override
    public void blockEntityChanged(BlockPos pos) {
        if (this.pos.equals(pos))
            this.container.markDirty();
        else
            super.blockEntityChanged(pos);
    }

    @Override
    public boolean isPositive() {
        return this.positive;
    }

    @Override
    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public IStateContainer getStateContainer() {
        return this.container;
    }

    @Override
    public void setStateContainer(IStateContainer container) {
        this.container = container;
    }

    @Override
    public void setWorld(Level world) {
        this.world = world;
    }

    @Override
    public Level getWorld() {
        return this.world;
    }

    @Override
    @Nonnull
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.pos)) {
            BlockState state = this.positive ? this.container.getPositiveBlockInfo().getBlockState() : this.container.getNegativeBlockInfo().getBlockState();
            if (state != null)
                return state;
        }
        return super.getBlockState(pos);
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        return this.world.addFreshEntity(entity);
    }

    @Override
    public void playSound(@Nullable Player p_46543_, double p_46544_, double p_46545_, double p_46546_, SoundEvent p_46547_, SoundSource p_46548_, float p_46549_, float p_46550_) {
        this.world.playSound(p_46543_, p_46544_, p_46545_, p_46546_, p_46547_, p_46548_, p_46549_, p_46550_);
    }

    @Override
    public void playSound(@Nullable Player p_46551_, Entity p_46552_, SoundEvent p_46553_, SoundSource p_46554_, float p_46555_, float p_46556_) {
        this.world.playSound(p_46551_, p_46552_, p_46553_, p_46554_, p_46555_, p_46556_);
    }

    @Override
    public String gatherChunkSourceStats() {
        return this.world.gatherChunkSourceStats();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return pos.equals(this.pos) ? (this.positive ? this.container.getPositiveBlockInfo().getBlockEntity() : this.container.getNegativeBlockInfo().getBlockEntity()) : super.getBlockEntity(pos);
    }

    @Override
    public void setBlockEntity(BlockEntity entity) {
        if (entity != null && entity.getBlockPos().equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockEntity(entity);
            else
                this.container.getNegativeBlockInfo().setBlockEntity(entity);
        } else {
            super.setBlockEntity(entity);
        }
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int p) {
        return this.setBlock(pos, state, p, 512);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int p, int height) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockState(state);
            else
                this.container.getNegativeBlockInfo().setBlockState(state);
            return true;
        }
        return this.world.setBlock(pos, state, p, height);
    }

    @Override
    public void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_) {
        this.world.sendBlockUpdated(p_46612_, p_46613_, p_46614_, p_46615_);
    }

    @Override
    public void blockUpdated(BlockPos pos, Block block) {
        super.blockUpdated(pos, block);
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
        if (pos.equals(this.pos))
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockEntity(null);
            else
                this.container.getNegativeBlockInfo().setBlockEntity(null);
        else
            this.world.removeBlockEntity(pos);
    }

    @Nullable
    @Override
    public Entity getEntity(int p_46492_) {
        return this.getEntity(p_46492_);
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(String p_46650_) {
        return this.getMapData(p_46650_);
    }

    @Override
    public void setMapData(String p_151533_, MapItemSavedData p_151534_) {
        this.setMapData(p_151533_, p_151534_);
    }

    @Override
    public int getFreeMapId() {
        return this.world.getFreeMapId();
    }

    @Override
    public void destroyBlockProgress(int p_46506_, BlockPos p_46507_, int p_46508_) {
        this.world.destroyBlockProgress(p_46506_, p_46507_, p_46508_);
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.world.getRecipeManager();
    }

    @Override
    public TagContainer getTagManager() {
        return this.world.getTagManager();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return new LevelEntityGetter<>() {
            @Nullable
            @Override
            public Entity get(int p_156931_) {
                return null;
            }

            @Nullable
            @Override
            public Entity get(UUID p_156939_) {
                return null;
            }

            @Override
            public Iterable<Entity> getAll() {
                return () -> new Iterator<Entity>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public Entity next() {
                        return null;
                    }
                };
            }

            @Override
            public <U extends Entity> void get(EntityTypeTest<Entity, U> p_156935_, Consumer<U> p_156936_) {
            }

            @Override
            public void get(AABB p_156937_, Consumer<Entity> p_156938_) {
            }

            @Override
            public <U extends Entity> void get(EntityTypeTest<Entity, U> p_156932_, AABB p_156933_, Consumer<U> p_156934_) {
            }
        };
    }

    @Override
    public TickList<Block> getBlockTicks() {
        return this.world.getBlockTicks();
    }

    @Override
    public TickList<Fluid> getLiquidTicks() {
        return this.world.getLiquidTicks();
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.world.getChunkSource();
    }

    @Override
    public void levelEvent(@Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
        this.world.levelEvent(p_46771_, p_46772_, p_46773_, p_46774_);
    }

    @Override
    public void gameEvent(@Nullable Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
        this.world.gameEvent(p_151549_, p_151550_, p_151551_);
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.world.registryAccess();
    }

    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return this.world.getShade(p_45522_, p_45523_);
    }

    @Override
    public List<? extends Player> players() {
        return this.world.players();
    }

    @Override
    public Biome getUncachedNoiseBiome(int p_46809_, int p_46810_, int p_46811_) {
        return this.world.getUncachedNoiseBiome(p_46809_, p_46810_, p_46811_);
    }
}
