package cjminecraft.doubleslabs.util;

import cjminecraft.doubleslabs.tileentitiy.TileEntityVerticalSlab;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.*;

public class WorldWrapper extends World {

    private World world;

    private TileEntityVerticalSlab verticalSlab;
    private boolean positive;

    public WorldWrapper(World world) {
        super(world.getSaveHandler(), world.getWorldInfo(), world.provider, world.profiler, world.isRemote);
        this.world = world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setVerticalSlab(TileEntityVerticalSlab verticalSlab, boolean positive) {
        this.verticalSlab = verticalSlab;
        this.positive = positive;
    }

    public boolean isPositive() {
        return this.positive;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        IBlockState state = pos.equals(this.verticalSlab.getPos()) ? (this.positive ? this.verticalSlab.getPositiveState() : this.verticalSlab.getNegativeState()) : this.world.getBlockState(pos);
        return state == null ? super.getBlockState(pos) : state;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (pos.equals(this.verticalSlab.getPos())) {
            if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
                return false;
            } else {
                if (this.positive)
                    this.verticalSlab.setPositiveState(newState);
                else
                    this.verticalSlab.setNegativeState(newState);
                return true;
            }
        }
        return this.world.setBlockState(pos, newState, flags);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return pos.equals(this.verticalSlab.getPos()) ? (this.positive ? this.verticalSlab.getPositiveTile() : this.verticalSlab.getNegativeTile()) : this.world.getTileEntity(pos);
    }

    @Override
    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        if (pos.equals(this.verticalSlab.getPos())) {
            if (this.positive)
                this.verticalSlab.setPositiveTile(tileEntityIn);
            else
                this.verticalSlab.setNegativeTile(tileEntityIn);
        } else {
            this.world.setTileEntity(pos, tileEntityIn);
        }
    }

    @Override
    public World init() {
        return this.world.init();
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return this.world.getBiome(pos);
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

    @Nullable
    @Override
    public MinecraftServer getMinecraftServer() {
        return this.world.getMinecraftServer();
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
    public boolean isValid(BlockPos pos) {
        return this.world.isValid(pos);
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return this.world.isOutsideBuildHeight(pos);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.world.isAirBlock(pos);
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos) {
        return this.world.isBlockLoaded(pos);
    }

    @Override
    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
        return this.world.isBlockLoaded(pos, allowEmpty);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int radius) {
        return this.world.isAreaLoaded(center, radius);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
        return this.world.isAreaLoaded(center, radius, allowEmpty);
    }

    @Override
    public boolean isAreaLoaded(BlockPos from, BlockPos to) {
        return this.world.isAreaLoaded(from, to);
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
    public Chunk getChunk(int chunkX, int chunkZ) {
        return this.world.getChunk(chunkX, chunkZ);
    }

    @Override
    public boolean isChunkGeneratedAt(int x, int z) {
        return this.world.isChunkGeneratedAt(x, z);
    }

    @Override
    public void markAndNotifyBlock(BlockPos pos, @Nullable Chunk chunk, IBlockState iblockstate, IBlockState newState, int flags) {
        this.world.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
    }

    @Override
    public boolean setBlockToAir(BlockPos pos) {
        return this.world.setBlockToAir(pos);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return this.world.destroyBlock(pos, dropBlock);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state) {
        return this.world.setBlockState(pos, state);
    }

    @Override
    public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        this.world.notifyBlockUpdate(pos, oldState, newState, flags);
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
    public void neighborChanged(BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.world.neighborChanged(pos, blockIn, fromPos);
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
    public int getLight(BlockPos pos) {
        return this.world.getLight(pos);
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

    @Override
    public boolean isDaytime() {
        return this.world.isDaytime();
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
    public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        this.world.playSound(player, pos, soundIn, category, volume, pitch);
    }

    @Override
    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        this.world.playSound(player, x, y, z, soundIn, category, volume, pitch);
    }

    @Override
    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
        this.world.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
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
    public int calculateSkylightSubtracted(float partialTicks) {
        return this.world.calculateSkylightSubtracted(partialTicks);
    }

    @Override
    public float getSunBrightnessFactor(float partialTicks) {
        return this.world.getSunBrightnessFactor(partialTicks);
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
    public Vec3d getSkyColor(Entity entityIn, float partialTicks) {
        return this.world.getSkyColor(entityIn, partialTicks);
    }

    @Override
    public Vec3d getSkyColorBody(Entity entityIn, float partialTicks) {
        return this.world.getSkyColorBody(entityIn, partialTicks);
    }

    @Override
    public float getCelestialAngle(float partialTicks) {
        return this.world.getCelestialAngle(partialTicks);
    }

    @Override
    public int getMoonPhase() {
        return this.world.getMoonPhase();
    }

    @Override
    public float getCurrentMoonPhaseFactor() {
        return this.world.getCurrentMoonPhaseFactor();
    }

    @Override
    public float getCurrentMoonPhaseFactorBody() {
        return this.world.getCurrentMoonPhaseFactorBody();
    }

    @Override
    public float getCelestialAngleRadians(float partialTicks) {
        return this.world.getCelestialAngleRadians(partialTicks);
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
    public BlockPos getPrecipitationHeight(BlockPos pos) {
        return this.world.getPrecipitationHeight(pos);
    }

    @Override
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
        return this.world.getTopSolidOrLiquidBlock(pos);
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
    protected void tickPlayers() {
        super.tickPlayers();
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
    public boolean checkBlockCollision(AxisAlignedBB bb) {
        return this.world.checkBlockCollision(bb);
    }

    @Override
    public boolean containsAnyLiquid(AxisAlignedBB bb) {
        return this.world.containsAnyLiquid(bb);
    }

    @Override
    public boolean isFlammableWithin(AxisAlignedBB bb) {
        return this.world.isFlammableWithin(bb);
    }

    @Override
    public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn) {
        return this.world.handleMaterialAcceleration(bb, materialIn, entityIn);
    }

    @Override
    public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
        return this.world.isMaterialInBB(bb, materialIn);
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
    public String getProviderName() {
        return this.world.getProviderName();
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        if (pos == this.verticalSlab.getPos())
            if (this.positive)
                this.verticalSlab.setPositiveTile(null);
            else
                this.verticalSlab.setNegativeTile(null);
        else
            this.world.removeTileEntity(pos);
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
    public void calculateInitialSkylight() {
        this.world.calculateInitialSkylight();
    }

    @Override
    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
        this.world.setAllowedSpawnTypes(hostile, peaceful);
    }

    @Override
    public void tick() {
        this.world.tick();
    }

    @Override
    protected void calculateInitialWeather() {
        this.world.provider.calculateInitialWeather();
    }

    @Override
    public void calculateInitialWeatherBody() {
        this.world.calculateInitialWeatherBody();
    }

    @Override
    protected void updateWeather() {
        this.world.provider.updateWeather();
    }

    @Override
    public void updateWeatherBody() {
        this.world.updateWeatherBody();
    }

    @Override
    protected void playMoodSoundAndCheckLight(int x, int z, Chunk chunkIn) {
        super.playMoodSoundAndCheckLight(x, z, chunkIn);
    }

    @Override
    protected void updateBlocks() {
        super.updateBlocks();
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
    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb) {
        return this.world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
        return this.world.getEntities(entityType, filter);
    }

    @Override
    public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
        return this.world.getPlayers(playerType, filter);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
        return this.world.getEntitiesWithinAABB(classEntity, bb);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
        return this.world.getEntitiesWithinAABB(clazz, aabb, filter);
    }

    @Nullable
    @Override
    public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
        return this.world.findNearestEntityWithinAABB(entityType, aabb, closestTo);
    }

    @Nullable
    @Override
    public Entity getEntityByID(int id) {
        return this.world.getEntityByID(id);
    }

    @Override
    public List<Entity> getLoadedEntityList() {
        return this.world.getLoadedEntityList();
    }

    @Override
    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        this.world.markChunkDirty(pos, unusedTileEntity);
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
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public void setSeaLevel(int seaLevelIn) {
        this.world.setSeaLevel(seaLevelIn);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.world.getStrongPower(pos, direction);
    }

    @Override
    public WorldType getWorldType() {
        return this.world.getWorldType();
    }

    @Override
    public int getStrongPower(BlockPos pos) {
        return this.world.getStrongPower(pos);
    }

    @Override
    public boolean isSidePowered(BlockPos pos, EnumFacing side) {
        return this.world.isSidePowered(pos, side);
    }

    @Override
    public int getRedstonePower(BlockPos pos, EnumFacing facing) {
        return this.world.getRedstonePower(pos, facing);
    }

    @Override
    public boolean isBlockPowered(BlockPos pos) {
        return this.world.isBlockPowered(pos);
    }

    @Override
    public int getRedstonePowerFromNeighbors(BlockPos pos) {
        return this.world.getRedstonePowerFromNeighbors(pos);
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
    public EntityPlayer getClosestPlayer(double posX, double posY, double posZ, double distance, boolean spectator) {
        return this.world.getClosestPlayer(posX, posY, posZ, distance, spectator);
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
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
    public void sendQuittingDisconnectingPacket() {
        this.world.sendQuittingDisconnectingPacket();
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
    public long getSeed() {
        return this.world.getSeed();
    }

    @Override
    public long getTotalWorldTime() {
        return this.world.getTotalWorldTime();
    }

    @Override
    public long getWorldTime() {
        return this.world.getWorldTime();
    }

    @Override
    public void setWorldTime(long time) {
        this.world.setWorldTime(time);
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
    public void setEntityState(Entity entityIn, byte state) {
        this.world.setEntityState(entityIn, state);
    }

    @Override
    public IChunkProvider getChunkProvider() {
        return this.world.getChunkProvider();
    }

    @Override
    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        this.world.addBlockEvent(pos, blockIn, eventID, eventParam);
    }

    @Override
    public ISaveHandler getSaveHandler() {
        return this.world.getSaveHandler();
    }

    @Override
    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    @Override
    public GameRules getGameRules() {
        return this.world.getGameRules();
    }

    @Override
    public void updateAllPlayersSleepingFlag() {
        this.world.updateAllPlayersSleepingFlag();
    }

    @Override
    public float getThunderStrength(float delta) {
        return this.world.getThunderStrength(delta);
    }

    @Override
    public void setThunderStrength(float strength) {
        this.world.setThunderStrength(strength);
    }

    @Override
    public float getRainStrength(float delta) {
        return this.world.getRainStrength(delta);
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
    public void playBroadcastSound(int id, BlockPos pos, int data) {
        this.world.playBroadcastSound(id, pos, data);
    }

    @Override
    public void playEvent(int type, BlockPos pos, int data) {
        this.world.playEvent(type, pos, data);
    }

    @Override
    public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data) {
        this.world.playEvent(player, type, pos, data);
    }

    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    @Override
    public int getActualHeight() {
        return this.world.getActualHeight();
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
    public double getHorizon() {
        return this.world.getHorizon();
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        this.world.sendBlockBreakProgress(breakerId, pos, progress);
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
    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
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
    public EnumDifficulty getDifficulty() {
        return this.world.getDifficulty();
    }

    @Override
    public int getSkylightSubtracted() {
        return this.world.getSkylightSubtracted();
    }

    @Override
    public void setSkylightSubtracted(int newSkylightSubtracted) {
        this.world.setSkylightSubtracted(newSkylightSubtracted);
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
    public VillageCollection getVillageCollection() {
        return this.world.getVillageCollection();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
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
    protected void initCapabilities() {
        super.initCapabilities();
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

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return false;
    }
}
