package cjminecraft.doubleslabs.api;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ServerWorldWrapper extends WorldServer implements IWorldWrapper<WorldServer> {

    private static final Field WEAK_WORLD_MAP;

    static {
        Field weakWorldMap;
        try {
            weakWorldMap = DimensionManager.class.getDeclaredField("weakWorldMap");
            weakWorldMap.setAccessible(true);
        } catch (NoSuchFieldException e) {
            weakWorldMap = null;
        }
        WEAK_WORLD_MAP = weakWorldMap;
    }

    private static void patch(ServerWorldWrapper instance) {
        if (WEAK_WORLD_MAP == null)
            return;
        try {
            ConcurrentMap<World, World> map = (ConcurrentMap<World, World>) WEAK_WORLD_MAP.get(null);
            map.remove(instance);
        } catch (IllegalAccessException ignored) {

        }
    }

    private WorldServer world;
    private boolean positive;
    private BlockPos pos;
    private IStateContainer container;

    public ServerWorldWrapper(WorldServer world) {
        super(world.getMinecraftServer(), world.getSaveHandler(), world.getWorldInfo(), world.provider.getDimension(), world.profiler);
        this.world = world;
        DimensionManager.setWorld(world.provider.getDimension(), world, world.getMinecraftServer());
        patch(this);
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
    public void setWorld(World world) {
        this.world = (WorldServer) world;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.pos)) {
            IBlockState state = (this.positive ? this.container.getPositiveBlockInfo().getBlockState() : this.container.getNegativeBlockInfo().getBlockState());
            return state != null ? state : this.world.getBlockState(pos);
        }
        return this.world.getBlockState(pos);
    }

    @Override
    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        this.world.playSound(player, x, y, z, soundIn, category, volume, pitch);
    }

    @Override
    public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        this.world.notifyBlockUpdate(pos, oldState, newState, flags);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return pos.equals(this.pos) ? (this.positive ? this.container.getPositiveBlockInfo().getTileEntity() : this.container.getNegativeBlockInfo().getTileEntity()) : this.world.getTileEntity(pos);
    }

    @Override
    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setTileEntity(tileEntityIn);
            else
                this.container.getNegativeBlockInfo().setTileEntity(tileEntityIn);
        } else {
            this.world.setTileEntity(pos, tileEntityIn);
        }
    }

    @Nullable
    @Override
    public Entity getEntityByID(int id) {
        return this.world.getEntityByID(id);
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        this.world.sendBlockBreakProgress(breakerId, pos, progress);
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data) {
        this.world.playEvent(player, type, pos, data);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.world.getChunk(chunkX, chunkZ);
    }

    @Override
    public void markAndNotifyBlock(BlockPos pos, @Nullable Chunk chunk, IBlockState IBlockState, IBlockState newState, int flags) {
        this.world.markAndNotifyBlock(pos, chunk, IBlockState, newState, flags);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockState(state);
            else
                this.container.getNegativeBlockInfo().setBlockState(state);
            return true;
        }
        return this.world.setBlockState(pos, state);
    }

    @Override
    public void neighborChanged(BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.world.neighborChanged(pos, blockIn, fromPos);
    }

    @Override
    public boolean isDaytime() {
        return this.world.isDaytime();
    }

    @Override
    public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        this.world.playSound(player, pos, soundIn, category, volume, pitch);
    }

    @Override
    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
        this.world.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
    }

    @Override
    public float getCelestialAngleRadians(float partialTicks) {
        return this.world.getCelestialAngleRadians(partialTicks);
    }

    @Override
    public boolean addTileEntity(TileEntity tile) {
        return this.world.addTileEntity(tile);
    }

    @Override
    public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
        this.world.addTileEntities(tileEntityCollection);
    }

    @Override
    public String getProviderName() {
        return this.world.getProviderName();
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        if (pos.equals(this.pos))
            if (this.positive)
                this.container.getPositiveBlockInfo().setTileEntity(null);
            else
                this.container.getNegativeBlockInfo().setTileEntity(null);
        else
            this.world.removeTileEntity(pos);
    }

    @Override
    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
        this.world.setAllowedSpawnTypes(hostile, peaceful);
    }

    @Override
    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        this.world.markChunkDirty(pos, unusedTileEntity);
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public int getStrongPower(BlockPos pos) {
        return this.world.getStrongPower(pos);
    }

    @Override
    public boolean isBlockPowered(BlockPos pos) {
        return this.world.isBlockPowered(pos);
    }

    @Override
    public int getRedstonePowerFromNeighbors(BlockPos pos) {
        return this.world.getRedstonePowerFromNeighbors(pos);
    }

    @Override
    public void sendQuittingDisconnectingPacket() {
        this.world.sendQuittingDisconnectingPacket();
    }

    @Override
    public void setEntityState(Entity entityIn, byte state) {
        this.world.setEntityState(entityIn, state);
    }

    @Override
    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        this.world.addBlockEvent(pos, blockIn, eventID, eventParam);
    }

    @Override
    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    @Override
    public void setThunderStrength(float strength) {
        this.world.setThunderStrength(strength);
    }

    @Override
    public void setRainStrength(float strength) {
        this.world.setRainStrength(strength);
    }

    @Override
    public boolean isThundering() {
        return this.world.isThundering();
    }

    @Override
    public boolean isRaining() {
        return this.world.isRaining();
    }

    @Override
    public boolean isRainingAt(BlockPos position) {
        return this.world.isRainingAt(position);
    }

    @Override
    public boolean isBlockinHighHumidity(BlockPos pos) {
        return this.world.isBlockinHighHumidity(pos);
    }

    @Override
    public void playBroadcastSound(int id, BlockPos pos, int data) {
        this.world.playBroadcastSound(id, pos, data);
    }

    @Override
    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
        this.world.updateComparatorOutputLevel(pos, blockIn);
    }

    @Override
    public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
        return this.world.getDifficultyForLocation(pos);
    }

    @Override
    public int getSkylightSubtracted() {
        return this.world.getSkylightSubtracted();
    }

    @Override
    public void playEvent(int type, BlockPos pos, int data) {
        this.world.playEvent(type, pos, data);
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb) {
        return this.world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_) {
        return this.world.getEntitiesWithinAABB(p_217357_1_, p_217357_2_);
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, boolean creativePlayers) {
        return this.world.getClosestPlayer(x, y, z, distance, creativePlayers);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return this.world.getBiome(pos);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.world.isAirBlock(pos);
    }

    @Override
    public boolean containsAnyLiquid(AxisAlignedBB bb) {
        return this.world.containsAnyLiquid(bb);
    }

    @Override
    public int getLight(BlockPos pos) {
        return this.world.getLight(pos);
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos) {
        return this.world.isBlockLoaded(pos);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int range) {
        return this.world.isAreaLoaded(center, range);
    }

    @Override
    public boolean isAreaLoaded(BlockPos from, BlockPos to) {
        return this.world.isAreaLoaded(from, to);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return this.world.destroyBlock(pos, dropBlock);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (pos.equals(this.pos)) {
            if (this.positive)
                this.container.getPositiveBlockInfo().setBlockState(newState);
            else
                this.container.getNegativeBlockInfo().setBlockState(newState);
            return true;
        } else {
            return this.world.setBlockState(pos, newState, flags);
        }
    }

    @Override
    public void setInitialSpawnLocation() {
        this.world.setInitialSpawnLocation();
    }

    @Override
    public IBlockState getGroundAboveSeaLevel(BlockPos pos) {
        return this.world.getGroundAboveSeaLevel(pos);
    }

    @Override
    public boolean checkBlockCollision(AxisAlignedBB bb) {
        return this.world.checkBlockCollision(bb);
    }

    @Override
    public boolean isFlammableWithin(AxisAlignedBB bb) {
        return this.world.isFlammableWithin(bb);
    }

    @Override
    public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
        return this.world.isMaterialInBB(bb, materialIn);
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public WorldType getWorldType() {
        return this.world.getWorldType();
    }

    @Override
    public BlockPos getSpawnPoint() {
        return this.world.getSpawnPoint();
    }

    @Override
    public void setSpawnPoint(BlockPos pos) {
        this.world.setSpawnPoint(pos);
    }

    @Override
    public int getActualHeight() {
        return this.world.getActualHeight();
    }

    @Override
    public float getCurrentMoonPhaseFactor() {
        return this.world.getCurrentMoonPhaseFactor();
    }

    @Override
    public int getMoonPhase() {
        return this.world.getMoonPhase();
    }

    @Override
    public float getSunBrightness(float partialTicks) {
        return this.world.getSunBrightness(partialTicks);
    }

    @Override
    public float getSunBrightnessBody(float partialTicks) {
        return this.world.getSunBrightnessBody(partialTicks);
    }

    @Override
    public Vec3d getCloudColour(float partialTicks) {
        return this.world.getCloudColour(partialTicks);
    }

    @Override
    public Vec3d getCloudColorBody(float partialTicks) {
        return this.world.getCloudColorBody(partialTicks);
    }

    @Override
    public Vec3d getFogColor(float partialTicks) {
        return this.world.getFogColor(partialTicks);
    }

    @Override
    public float getStarBrightness(float partialTicks) {
        return this.world.getStarBrightness(partialTicks);
    }

    @Override
    public float getStarBrightnessBody(float partialTicks) {
        return this.world.getStarBrightnessBody(partialTicks);
    }

    @Override
    public double getHorizon() {
        return this.world.getHorizon();
    }

    @Override
    public int getLastLightningBolt() {
        return this.world.getLastLightningBolt();
    }

    @Override
    public void setLastLightningBolt(int lastLightningBoltIn) {
        this.world.setLastLightningBolt(lastLightningBoltIn);
    }

    @Override
    public World init() {
        return this.world.init();
    }

    @Override
    public Biome getBiomeForCoordsBody(BlockPos pos) {
        return this.world.getBiomeForCoordsBody(pos);
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return this.world.getBiomeProvider();
    }

    @Override
    public void initialize(WorldSettings settings) {
        this.world.initialize(settings);
    }

    @Override
    public boolean isValid(BlockPos pos) {
        return this.world.isValid(pos);
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return this.world.isOutsideBuildHeight(pos);
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
        return this.world.isBlockLoaded(pos, allowEmpty);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
        return this.world.isAreaLoaded(center, radius, allowEmpty);
    }

    @Override
    public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty) {
        return this.world.isAreaLoaded(from, to, allowEmpty);
    }

    @Override
    public boolean isAreaLoaded(StructureBoundingBox box) {
        return this.world.isAreaLoaded(box);
    }

    @Override
    public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty) {
        return this.world.isAreaLoaded(box, allowEmpty);
    }

    @Override
    public Chunk getChunk(BlockPos pos) {
        return this.world.getChunk(pos);
    }

    @Override
    public boolean isChunkGeneratedAt(int x, int z) {
        return this.world.isChunkGeneratedAt(x, z);
    }

    @Override
    public boolean setBlockToAir(BlockPos pos) {
        return this.world.setBlockToAir(pos);
    }

    @Override
    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType, boolean updateObservers) {
        this.world.notifyNeighborsRespectDebug(pos, blockType, updateObservers);
    }

    @Override
    public void markBlocksDirtyVertical(int x, int z, int y1, int y2) {
        this.world.markBlocksDirtyVertical(x, z, y1, y2);
    }

    @Override
    public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
        this.world.markBlockRangeForRenderUpdate(rangeMin, rangeMax);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void updateObservingBlocksAt(BlockPos pos, Block blockType) {
        this.world.updateObservingBlocksAt(pos, blockType);
    }

    @Override
    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType, boolean updateObservers) {
        this.world.notifyNeighborsOfStateChange(pos, blockType, updateObservers);
    }

    @Override
    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
        this.world.notifyNeighborsOfStateExcept(pos, blockType, skipSide);
    }

    @Override
    public void observedNeighborChanged(BlockPos pos, Block changedBlock, BlockPos changedBlockPos) {
        this.world.observedNeighborChanged(pos, changedBlock, changedBlockPos);
    }

    @Override
    public boolean isBlockTickPending(BlockPos pos, Block blockType) {
        return this.world.isBlockTickPending(pos, blockType);
    }

    @Override
    public boolean canSeeSky(BlockPos pos) {
        return this.world.canSeeSky(pos);
    }

    @Override
    public boolean canBlockSeeSky(BlockPos pos) {
        return this.world.canBlockSeeSky(pos);
    }

    @Override
    public int getLightFromNeighbors(BlockPos pos) {
        return this.world.getLightFromNeighbors(pos);
    }

    @Override
    public int getLight(BlockPos pos, boolean checkNeighbors) {
        return this.world.getLight(pos, checkNeighbors);
    }

    @Override
    public BlockPos getHeight(BlockPos pos) {
        return this.world.getHeight(pos);
    }

    @Override
    public int getHeight(int x, int z) {
        return this.world.getHeight(x, z);
    }

    @Override
    public int getChunksLowestHorizon(int x, int z) {
        return this.world.getChunksLowestHorizon(x, z);
    }

    @Override
    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
        return this.world.getLightFromNeighborsFor(type, pos);
    }

    @Override
    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        return this.world.getLightFor(type, pos);
    }

    @Override
    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue) {
        this.world.setLightFor(type, pos, lightValue);
    }

    @Override
    public void notifyLightSet(BlockPos pos) {
        this.world.notifyLightSet(pos);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return this.world.getCombinedLight(pos, lightValue);
    }

    @Override
    public float getLightBrightness(BlockPos pos) {
        return this.world.getLightBrightness(pos);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end) {
        return this.world.rayTraceBlocks(start, end);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid) {
        return this.world.rayTraceBlocks(start, end, stopOnLiquid);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        return this.world.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
    }

    @Override
    public void playRecord(BlockPos blockPositionIn, @Nullable SoundEvent soundEventIn) {
        this.world.playRecord(blockPositionIn, soundEventIn);
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnAlwaysVisibleParticle(int id, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.world.spawnAlwaysVisibleParticle(id, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.world.spawnParticle(particleType, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public boolean addWeatherEffect(Entity entityIn) {
        return this.world.addWeatherEffect(entityIn);
    }

    @Override
    public boolean spawnEntity(Entity entityIn) {
        return this.world.spawnEntity(entityIn);
    }

    @Override
    public void onEntityAdded(Entity entityIn) {
        this.world.onEntityAdded(entityIn);
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
        this.world.onEntityRemoved(entityIn);
    }

    @Override
    public void removeEntity(Entity entityIn) {
        this.world.removeEntity(entityIn);
    }

    @Override
    public void removeEntityDangerously(Entity entityIn) {
        this.world.removeEntityDangerously(entityIn);
    }

    @Override
    public void addEventListener(IWorldEventListener listener) {
        this.world.addEventListener(listener);
    }

    @Override
    public List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, AxisAlignedBB aabb) {
        return this.world.getCollisionBoxes(entityIn, aabb);
    }

    @Override
    public void removeEventListener(IWorldEventListener listener) {
        this.world.removeEventListener(listener);
    }

    @Override
    public boolean isInsideWorldBorder(Entity entityToCheck) {
        return this.world.isInsideWorldBorder(entityToCheck);
    }

    @Override
    public boolean collidesWithAnyBlock(AxisAlignedBB bbox) {
        return this.world.collidesWithAnyBlock(bbox);
    }


    @Override
    public Vec3d getSkyColor(Entity entityIn, float partialTicks) {
        return this.world.getSkyColor(entityIn, partialTicks);
    }

    @Override
    public Vec3d getSkyColorBody(Entity entityIn, float partialTicks) {
        return this.world.getSkyColorBody(entityIn, partialTicks);
    }

    @Override
    public float getCurrentMoonPhaseFactorBody() {
        return this.world.getCurrentMoonPhaseFactorBody();
    }

    @Override
    public BlockPos getPrecipitationHeight(BlockPos pos) {
        return this.world.getPrecipitationHeight(pos);
    }

    @Override
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
        return this.world.getTopSolidOrLiquidBlock(pos);
    }

    @Override
    public boolean isUpdateScheduled(BlockPos pos, Block blk) {
        return this.world.isUpdateScheduled(pos, blk);
    }

    @Override
    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
        this.world.scheduleUpdate(pos, blockIn, delay);
    }

    @Override
    public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
        this.world.updateBlockTick(pos, blockIn, delay, priority);
    }

    @Override
    public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
        this.world.scheduleBlockUpdate(pos, blockIn, delay, priority);
    }

    @Override
    public void updateEntities() {
        this.world.updateEntities();
    }

    @Override
    public void updateEntity(Entity ent) {
        this.world.updateEntity(ent);
    }

    @Override
    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
        this.world.updateEntityWithOptionalForce(entityIn, forceUpdate);
    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB bb) {
        return this.world.checkNoEntityCollision(bb);
    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB bb, @Nullable Entity entityIn) {
        return this.world.checkNoEntityCollision(bb, entityIn);
    }

    @Override
    public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn) {
        return this.world.handleMaterialAcceleration(bb, materialIn, entityIn);
    }

    @Override
    public Explosion createExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean damagesTerrain) {
        return this.world.createExplosion(entityIn, x, y, z, strength, damagesTerrain);
    }

    @Override
    public Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain) {
        return this.world.newExplosion(entityIn, x, y, z, strength, causesFire, damagesTerrain);
    }

    @Override
    public float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        return this.world.getBlockDensity(vec, bb);
    }

    @Override
    public boolean extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side) {
        return this.world.extinguishFire(player, pos, side);
    }

    @Override
    public String getDebugLoadedEntities() {
        return this.world.getDebugLoadedEntities();
    }

    @Override
    public void markTileEntityForRemoval(TileEntity tileEntityIn) {
        this.world.markTileEntityForRemoval(tileEntityIn);
    }

    @Override
    public boolean isBlockFullCube(BlockPos pos) {
        return this.world.isBlockFullCube(pos);
    }

    @Override
    public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
        return this.world.isBlockNormalCube(pos, _default);
    }

    @Override
    public void tick() {
        this.world.tick();
    }

    @Override
    public void updateWeatherBody() {
        this.world.updateWeatherBody();
    }

    @Override
    public void immediateBlockTick(BlockPos pos, IBlockState state, Random random) {
        this.world.immediateBlockTick(pos, state, random);
    }

    @Override
    public boolean canBlockFreezeWater(BlockPos pos) {
        return this.world.canBlockFreezeWater(pos);
    }

    @Override
    public boolean canBlockFreezeNoWater(BlockPos pos) {
        return this.world.canBlockFreezeNoWater(pos);
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj) {
        return this.world.canBlockFreeze(pos, noWaterAdj);
    }

    @Override
    public boolean canBlockFreezeBody(BlockPos pos, boolean noWaterAdj) {
        return this.world.canBlockFreezeBody(pos, noWaterAdj);
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        return this.world.canSnowAt(pos, checkLight);
    }

    @Override
    public boolean canSnowAtBody(BlockPos pos, boolean checkLight) {
        return this.world.canSnowAtBody(pos, checkLight);
    }

    @Override
    public boolean checkLight(BlockPos pos) {
        return this.world.checkLight(pos);
    }

    @Override
    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
        return this.world.checkLightFor(lightType, pos);
    }

    @Override
    public boolean tickUpdates(boolean runAllPending) {
        return this.world.tickUpdates(runAllPending);
    }

    @Nullable
    @Override
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean remove) {
        return this.world.getPendingBlockUpdates(chunkIn, remove);
    }

    @Nullable
    @Override
    public List<NextTickListEntry> getPendingBlockUpdates(StructureBoundingBox structureBB, boolean remove) {
        return this.world.getPendingBlockUpdates(structureBB, remove);
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable com.google.common.base.Predicate<? super Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, com.google.common.base.Predicate<? super T> filter) {
        return this.world.getEntities(entityType, filter);
    }

    @Override
    public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, com.google.common.base.Predicate<? super T> filter) {
        return this.world.getPlayers(playerType, filter);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable com.google.common.base.Predicate<? super T> filter) {
        return this.world.getEntitiesWithinAABB(clazz, aabb, filter);
    }

    @Nullable
    @Override
    public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
        return this.world.findNearestEntityWithinAABB(entityType, aabb, closestTo);
    }

    @Override
    public List<Entity> getLoadedEntityList() {
        return this.world.getLoadedEntityList();
    }

    @Override
    public int countEntities(Class<?> entityType) {
        return this.world.countEntities(entityType);
    }

    @Override
    public void loadEntities(Collection<Entity> entityCollection) {
        this.world.loadEntities(entityCollection);
    }

    @Override
    public void unloadEntities(Collection<Entity> entityCollection) {
        this.world.unloadEntities(entityCollection);
    }

    @Override
    public boolean mayPlace(Block blockIn, BlockPos pos, boolean skipCollisionCheck, EnumFacing sidePlacedOn, @Nullable Entity placer) {
        return this.world.mayPlace(blockIn, pos, skipCollisionCheck, sidePlacedOn, placer);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.world.getStrongPower(pos, direction);
    }

    @Override
    public boolean isSidePowered(BlockPos pos, EnumFacing side) {
        return this.world.isSidePowered(pos, side);
    }

    @Override
    public int getRedstonePower(BlockPos pos, EnumFacing facing) {
        return this.world.getRedstonePower(pos, facing);
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
        return this.world.getClosestPlayerToEntity(entityIn, distance);
    }

    @Nullable
    @Override
    public EntityPlayer getNearestPlayerNotCreative(Entity entityIn, double distance) {
        return this.world.getNearestPlayerNotCreative(entityIn, distance);
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, com.google.common.base.Predicate<Entity> predicate) {
        return this.world.getClosestPlayer(x, y, z, distance, predicate);
    }

    @Override
    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
        return this.world.isAnyPlayerWithinRangeAt(x, y, z, range);
    }

    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance) {
        return this.world.getNearestAttackablePlayer(entityIn, maxXZDistance, maxYDistance);
    }

    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(BlockPos pos, double maxXZDistance, double maxYDistance) {
        return this.world.getNearestAttackablePlayer(pos, maxXZDistance, maxYDistance);
    }

    @Nullable
    @Override
    public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> predicate) {
        return this.world.getNearestAttackablePlayer(posX, posY, posZ, maxXZDistance, maxYDistance, playerToDouble, predicate);
    }

    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByName(String name) {
        return this.world.getPlayerEntityByName(name);
    }

    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
        return this.world.getPlayerEntityByUUID(uuid);
    }

    @Override
    public void checkSessionLock() throws MinecraftException {
        this.world.checkSessionLock();
    }

    @Override
    public void setTotalWorldTime(long worldTime) {
        this.world.setTotalWorldTime(worldTime);
    }

    @Override
    public long getTotalWorldTime() {
        return this.world.getTotalWorldTime();
    }

    @Override
    public void setWorldTime(long time) {
        this.world.setWorldTime(time);
    }

    @Override
    public void joinEntityInSurroundings(Entity entityIn) {
        this.world.joinEntityInSurroundings(entityIn);
    }

    @Override
    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
        return this.world.isBlockModifiable(player, pos);
    }

    @Override
    public boolean canMineBlockBody(EntityPlayer player, BlockPos pos) {
        return this.world.canMineBlockBody(player, pos);
    }

    @Override
    public ISaveHandler getSaveHandler() {
        return this.world.getSaveHandler();
    }

    @Override
    public void updateAllPlayersSleepingFlag() {
        this.world.updateAllPlayersSleepingFlag();
    }

    @Nullable
    @Override
    public MapStorage getMapStorage() {
        return this.world.getMapStorage();
    }

    @Override
    public void setData(String dataID, WorldSavedData worldSavedDataIn) {
        this.world.setData(dataID, worldSavedDataIn);
    }

    @Nullable
    @Override
    public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataID) {
        return this.world.loadData(clazz, dataID);
    }

    @Override
    public int getUniqueDataId(String key) {
        return this.world.getUniqueDataId(key);
    }

    @Override
    public Random setRandomSeed(int seedX, int seedY, int seedZ) {
        return this.world.setRandomSeed(seedX, seedY, seedZ);
    }

    @Override
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
        return this.world.addWorldInfoToCrashReport(report);
    }

    @Override
    public Calendar getCurrentDate() {
        return this.world.getCurrentDate();
    }

    @Override
    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compound) {
        this.world.makeFireworks(x, y, z, motionX, motionY, motionZ, compound);
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.world.getDifficulty();
    }

    @Override
    public void setSkylightSubtracted(int newSkylightSubtracted) {
        this.world.setSkylightSubtracted(newSkylightSubtracted);
    }

    @Override
    public VillageCollection getVillageCollection() {
        return this.world.getVillageCollection();
    }

    @Override
    public boolean isSpawnChunk(int x, int z) {
        return this.world.isSpawnChunk(x, z);
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side) {
        return this.world.isSideSolid(pos, side);
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return this.world.isSideSolid(pos, side, _default);
    }

    @Override
    public ImmutableSetMultimap<ChunkPos, ForgeChunkManager.Ticket> getPersistentChunks() {
        return this.world.getPersistentChunks();
    }

    @Override
    public Iterator<Chunk> getPersistentChunkIterable(Iterator<Chunk> chunkIterator) {
        return this.world.getPersistentChunkIterable(chunkIterator);
    }

    @Override
    public int getBlockLightOpacity(BlockPos pos) {
        return this.world.getBlockLightOpacity(pos);
    }

    @Override
    public int countEntities(EnumCreatureType type, boolean forSpawnCount) {
        return this.world.countEntities(type, forSpawnCount);
    }

    @Override
    public void markTileEntitiesInChunkForRemoval(Chunk chunk) {
        this.world.markTileEntitiesInChunkForRemoval(chunk);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return this.world.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return this.world.getCapability(capability, facing);
    }

    @Override
    public MapStorage getPerWorldStorage() {
        return this.world.getPerWorldStorage();
    }

    @Override
    public void sendPacketToServer(Packet<?> packetIn) {
        this.world.sendPacketToServer(packetIn);
    }

    @Override
    public LootTableManager getLootTableManager() {
        return this.world.getLootTableManager();
    }

    @Nullable
    @Override
    public BlockPos findNearestStructure(String structureName, BlockPos position, boolean findUnexplored) {
        return this.world.findNearestStructure(structureName, position, findUnexplored);
    }

    @Nullable
    @Override
    public Biome.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
        return this.world.getSpawnListEntryForTypeAt(creatureType, pos);
    }

    @Override
    public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, Biome.SpawnListEntry spawnListEntry, BlockPos pos) {
        return this.world.canCreatureTypeSpawnHere(creatureType, spawnListEntry, pos);
    }

    @Override
    public boolean areAllPlayersAsleep() {
        return this.world.areAllPlayersAsleep();
    }

    @Override
    public void resetUpdateEntityTick() {
        this.world.resetUpdateEntityTick();
    }

    @Nullable
    @Override
    public BlockPos getSpawnCoordinate() {
        return this.world.getSpawnCoordinate();
    }

    @Override
    public void saveAllChunks(boolean all, @Nullable IProgressUpdate progressCallback) throws MinecraftException {
        this.world.saveAllChunks(all, progressCallback);
    }

    @Override
    public void flushToDisk() {
        this.world.flushToDisk();
    }

    @Override
    public void flush() {
        this.world.flush();
    }

    @Override
    public EntityTracker getEntityTracker() {
        return this.world.getEntityTracker();
    }

    @Override
    public PlayerChunkMap getPlayerChunkMap() {
        return this.world.getPlayerChunkMap();
    }

    @Override
    public Teleporter getDefaultTeleporter() {
        return this.world.getDefaultTeleporter();
    }

    @Override
    public TemplateManager getStructureTemplateManager() {
        return this.world.getStructureTemplateManager();
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed, int... particleArguments) {
        this.world.spawnParticle(particleType, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, boolean longDistance, double xCoord, double yCoord, double zCoord, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed, int... particleArguments) {
        this.world.spawnParticle(particleType, longDistance, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
    }

    @Override
    public void spawnParticle(EntityPlayerMP player, EnumParticleTypes particle, boolean longDistance, double x, double y, double z, int count, double xOffset, double yOffset, double zOffset, double speed, int... arguments) {
        this.world.spawnParticle(player, particle, longDistance, x, y, z, count, xOffset, yOffset, zOffset, speed, arguments);
    }

    @Nullable
    @Override
    public Entity getEntityFromUuid(UUID uuid) {
        return this.world.getEntityFromUuid(uuid);
    }

    @Override
    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
        return this.world.addScheduledTask(runnableToSchedule);
    }

    @Override
    public boolean isCallingFromMinecraftThread() {
        return this.world.isCallingFromMinecraftThread();
    }

    @Override
    public AdvancementManager getAdvancementManager() {
        return this.world.getAdvancementManager();
    }

    @Override
    public FunctionManager getFunctionManager() {
        return this.world.getFunctionManager();
    }
}
