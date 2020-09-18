package cjminecraft.doubleslabs.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandResultStats;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.RecipeBookServer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.*;

public class ServerPlayerEntityWrapper extends EntityPlayerMP implements IPlayerWrapper<EntityPlayerMP> {
    private final EntityPlayerMP player;

    public ServerPlayerEntityWrapper(EntityPlayerMP player, WorldServer world) {
        super(player.server, world, player.getGameProfile(), player.interactionManager);
        player.interactionManager.player = player;
        this.connection = player.connection;
        this.player = player;
        this.world = world;
    }

    @Override
    public EntityPlayerMP getOriginalPlayer() {
        return this.player;
    }

    @Override
    public void addExperienceLevel(int levels) {
        this.player.addExperienceLevel(levels);
    }

    @Override
    public void onEnchant(ItemStack enchantedItem, int cost) {
        this.player.onEnchant(enchantedItem, cost);
    }

    @Override
    public void sendEnterCombat() {
        this.player.sendEnterCombat();
    }

    @Override
    public void sendEndCombat() {
        this.player.sendEndCombat();
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.player.onDeath(cause);
    }

    @Override
    public void awardKillScore(Entity killed, int scoreValue, DamageSource damageSource) {
        this.player.awardKillScore(killed, scoreValue, damageSource);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return this.player.attackEntityFrom(source, amount);
    }

    @Override
    public void onItemPickup(Entity entityIn, int quantity) {
        this.player.onItemPickup(entityIn, quantity);
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force) {
        return this.player.startRiding(entityIn, force);
    }

    @Override
    public void closeScreen() {
        this.player.closeScreen();
    }

    @Override
    public void unlockRecipes(ResourceLocation[] p_193102_1_) {
        this.player.unlockRecipes(p_193102_1_);
    }

    @Override
    public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
        this.player.sendStatusMessage(chatComponent, actionBar);
    }

    @Override
    public void onCriticalHit(Entity entityHit) {
        this.player.onCriticalHit(entityHit);
    }

    @Override
    public void onEnchantmentCritical(Entity entityHit) {
        this.player.onEnchantmentCritical(entityHit);
    }

    @Override
    public void sendPlayerAbilities() {
        this.player.sendPlayerAbilities();
    }

    @Override
    public void setGameType(GameType gameType) {
        this.player.setGameType(gameType);
    }

    @Override
    public boolean isSpectator() {
        return this.player.isSpectator();
    }

    @Override
    public boolean isCreative() {
        return this.player.isCreative();
    }

    @Override
    public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
        this.player.attackTargetEntityWithCurrentItem(targetEntity);
    }

    @Override
    public int getMaxInPortalTime() {
        return this.player.getMaxInPortalTime();
    }

    @Override
    public int getPortalCooldown() {
        return this.player.getPortalCooldown();
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        this.player.playSound(soundIn, volume, pitch);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.player.getSoundCategory();
    }

    @Override
    public void handleStatusUpdate(byte id) {
        this.player.handleStatusUpdate(id);
    }

    @Override
    public void updateRidden() {
        this.player.updateRidden();
    }

    @Override
    public void preparePlayerToSpawn() {
        this.player.preparePlayerToSpawn();
    }

    @Override
    public int getScore() {
        return this.player.getScore();
    }

    @Override
    public void setScore(int scoreIn) {
        this.player.setScore(scoreIn);
    }

    @Override
    public void addScore(int scoreIn) {
        this.player.addScore(scoreIn);
    }

    @Override
    public double getYOffset() {
        return this.player.getYOffset();
    }

    @Override
    public void disableShield(boolean p_190777_1_) {
        this.player.disableShield(p_190777_1_);
    }

    @Override
    public void spawnSweepParticles() {
        this.player.spawnSweepParticles();
    }

    @Override
    public void respawnPlayer() {
        this.player.respawnPlayer();
    }

    @Override
    public boolean isUser() {
        return this.player.isUser();
    }

    @Override
    public GameProfile getGameProfile() {
        return this.player.getGameProfile();
    }

    @Override
    public boolean isPlayerFullyAsleep() {
        return this.player.isPlayerFullyAsleep();
    }

    @Override
    public int getSleepTimer() {
        return this.player.getSleepTimer();
    }

    @Override
    public void jump() {
        this.player.jump();
    }

    @Override
    public float getAIMoveSpeed() {
        return this.player.getAIMoveSpeed();
    }

    @Override
    public void setAIMoveSpeed(float speedIn) {
        this.player.setAIMoveSpeed(speedIn);
    }

    @Override
    public void addMovementStat(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
        this.player.addMovementStat(p_71000_1_, p_71000_3_, p_71000_5_);
    }

    @Override
    public int getXPSeed() {
        return this.player.getXPSeed();
    }

    @Override
    public int xpBarCap() {
        return this.player.xpBarCap();
    }

    @Override
    public void addExhaustion(float exhaustion) {
        this.player.addExhaustion(exhaustion);
    }

    @Override
    public FoodStats getFoodStats() {
        return this.player.getFoodStats();
    }

    @Override
    public boolean canEat(boolean ignoreHunger) {
        return this.player.canEat(ignoreHunger);
    }

    @Override
    public boolean shouldHeal() {
        return this.player.shouldHeal();
    }

    @Override
    public boolean isAllowEdit() {
        return this.player.isAllowEdit();
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return this.player.getAlwaysRenderNameTagForRender();
    }

    @Override
    public boolean addItemStackToInventory(ItemStack p_191521_1_) {
        return this.player.addItemStackToInventory(p_191521_1_);
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        return this.player.getHeldEquipment();
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return this.player.getArmorInventoryList();
    }

    @Override
    public boolean isPushedByWater() {
        return this.player.isPushedByWater();
    }

    @Override
    public Scoreboard getWorldScoreboard() {
        return this.player.getWorldScoreboard();
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.player.getDisplayName();
    }

    @Override
    public float getAbsorptionAmount() {
        return this.player.getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(float amount) {
        this.player.setAbsorptionAmount(amount);
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        return this.player.replaceItemInInventory(inventorySlot, itemStackIn);
    }

    @Override
    public boolean hasReducedDebug() {
        return this.player.hasReducedDebug();
    }

    @Override
    public void setReducedDebug(boolean reducedDebug) {
        this.player.setReducedDebug(reducedDebug);
    }

    @Override
    public float getCooldownPeriod() {
        return this.player.getCooldownPeriod();
    }

    @Override
    public float getCooledAttackStrength(float adjustTicks) {
        return this.player.getCooledAttackStrength(adjustTicks);
    }

    @Override
    public void resetCooldown() {
        this.player.resetCooldown();
    }

    @Override
    public CooldownTracker getCooldownTracker() {
        return this.player.getCooldownTracker();
    }

    @Override
    public float getLuck() {
        return this.player.getLuck();
    }

    @Override
    public boolean canUseCommandBlock() {
        return this.player.canUseCommandBlock();
    }

    @Override
    public void onKillCommand() {
        this.player.onKillCommand();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return this.player.canBreatheUnderwater();
    }

    @Override
    public boolean isChild() {
        return this.player.isChild();
    }

    @Override
    public Random getRNG() {
        return this.player.getRNG();
    }

    @Override
    public int getRevengeTimer() {
        return this.player.getRevengeTimer();
    }

    @Override
    public void setLastAttackedEntity(Entity entityIn) {
        this.player.setLastAttackedEntity(entityIn);
    }

    @Override
    public int getLastAttackedEntityTime() {
        return this.player.getLastAttackedEntityTime();
    }

    @Override
    public int getIdleTime() {
        return this.player.getIdleTime();
    }

    @Override
    public boolean isEntityUndead() {
        return this.player.isEntityUndead();
    }

    @Override
    public void heal(float healAmount) {
        this.player.heal(healAmount);
    }

    @Nullable
    @Override
    public DamageSource getLastDamageSource() {
        return this.player.getLastDamageSource();
    }

    @Override
    public boolean isOnLadder() {
        return this.player.isOnLadder();
    }

    @Override
    public void performHurtAnimation() {
        this.player.performHurtAnimation();
    }

    @Override
    public int getTotalArmorValue() {
        return this.player.getTotalArmorValue();
    }

    @Override
    public CombatTracker getCombatTracker() {
        return this.player.getCombatTracker();
    }

    @Override
    public ItemStack getHeldItemMainhand() {
        return this.player.getHeldItemMainhand();
    }

    @Override
    public ItemStack getHeldItemOffhand() {
        return this.player.getHeldItemOffhand();
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        this.player.applyEntityCollision(entityIn);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return this.player.attackEntityAsMob(entityIn);
    }

    @Override
    public void setJumping(boolean jumping) {
        this.player.setJumping(jumping);
    }

    @Override
    public boolean canEntityBeSeen(Entity entityIn) {
        return this.player.canEntityBeSeen(entityIn);
    }

    @Override
    public float getSwingProgress(float partialTickTime) {
        return this.player.getSwingProgress(partialTickTime);
    }

    @Override
    public boolean isServerWorld() {
        return this.player.isServerWorld();
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.player.canBeCollidedWith();
    }

    @Override
    public boolean canBePushed() {
        return this.player.canBePushed();
    }

    @Override
    public float getRotationYawHead() {
        return this.player.getRotationYawHead();
    }

    @Override
    public void setRotationYawHead(float rotation) {
        this.player.setRotationYawHead(rotation);
    }

    @Override
    public void setRenderYawOffset(float offset) {
        this.player.setRenderYawOffset(offset);
    }

    @Override
    public boolean isHandActive() {
        return this.player.isHandActive();
    }

    @Override
    public EnumHand getActiveHand() {
        return this.player.getActiveHand();
    }

    @Override
    public void setActiveHand(EnumHand hand) {
        this.player.setActiveHand(hand);
    }

    @Override
    public ItemStack getActiveItemStack() {
        return this.player.getActiveItemStack();
    }

    @Override
    public int getItemInUseCount() {
        return this.player.getItemInUseCount();
    }

    @Override
    public int getItemInUseMaxCount() {
        return this.player.getItemInUseMaxCount();
    }

    @Override
    public void stopActiveHand() {
        this.player.stopActiveHand();
    }

    @Override
    public void resetActiveHand() {
        this.player.resetActiveHand();
    }

    @Override
    public boolean isActiveItemStackBlocking() {
        return this.player.isActiveItemStackBlocking();
    }

    @Override
    public boolean isElytraFlying() {
        return this.player.isElytraFlying();
    }

    @Override
    public int getTicksElytraFlying() {
        return this.player.getTicksElytraFlying();
    }

    @Override
    public boolean canBeHitWithPotion() {
        return this.player.canBeHitWithPotion();
    }

    @Override
    public boolean attackable() {
        return this.player.attackable();
    }

    @Override
    public void setPartying(BlockPos pos, boolean isPartying) {
        this.player.setPartying(pos, isPartying);
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return this.player.isEntityInsideOpaqueBlock();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this.player.getRenderBoundingBox();
    }

    @Override
    public int getEntityId() {
        return this.player.getEntityId();
    }

    @Override
    public void setEntityId(int id) {
        this.player.setEntityId(id);
    }

    @Override
    public Set<String> getTags() {
        return this.player.getTags();
    }

    @Override
    public boolean addTag(String tag) {
        return this.player.addTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return this.player.removeTag(tag);
    }

    @Override
    public EntityDataManager getDataManager() {
        return this.player.getDataManager();
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return this.player.equals(p_equals_1_);
    }

    @Override
    public int hashCode() {
        return this.player.hashCode();
    }

    @Override
    public void setFire(int seconds) {
        this.player.setFire(seconds);
    }

    @Override
    public void extinguish() {
        this.player.extinguish();
    }

    @Override
    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        return this.player.isOffsetPositionInLiquid(x, y, z);
    }

    @Override
    public void resetPositionToBB() {
        this.player.resetPositionToBB();
    }


    @Override
    public boolean isSilent() {
        return this.player.isSilent();
    }

    @Override
    public void setSilent(boolean isSilent) {
        this.player.setSilent(isSilent);
    }

    @Override
    public boolean hasNoGravity() {
        return this.player.hasNoGravity();
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        this.player.setNoGravity(noGravity);
    }

    @Override
    public boolean isInWater() {
        return this.player.isInWater();
    }

    @Override
    public boolean isWet() {
        return this.player.isWet();
    }

    @Override
    public boolean isInLava() {
        return this.player.isInLava();
    }

    @Override
    public float getBrightness() {
        return this.player.getBrightness();
    }

    @Override
    public void setWorld(World worldIn) {
        this.player.setWorld(worldIn);
    }

    @Override
    public float getDistance(Entity entityIn) {
        return this.player.getDistance(entityIn);
    }

    @Override
    public double getDistanceSq(double x, double y, double z) {
        return this.player.getDistanceSq(x, y, z);
    }

    @Override
    public double getDistanceSq(Entity entityIn) {
        return this.player.getDistanceSq(entityIn);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        this.player.addVelocity(x, y, z);
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return this.player.isInRangeToRender3d(x, y, z);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return this.player.isInRangeToRenderDist(distance);
    }

    @Override
    public void updatePassenger(Entity passenger) {
        this.player.updatePassenger(passenger);
    }

    @Override
    public void applyOrientationToEntity(Entity entityToUpdate) {
        this.player.applyOrientationToEntity(entityToUpdate);
    }

    @Override
    public double getMountedYOffset() {
        return this.player.getMountedYOffset();
    }

    @Override
    public boolean startRiding(Entity entityIn) {
        return this.player.startRiding(entityIn);
    }

    @Override
    public void removePassengers() {
        this.player.removePassengers();
    }

    @Override
    public float getCollisionBorderSize() {
        return this.player.getCollisionBorderSize();
    }

    @Override
    public void setPortal(BlockPos pos) {
        this.player.setPortal(pos);
    }


    @Override
    public void setVelocity(double x, double y, double z) {
        this.player.setVelocity(x, y, z);
    }

    @Override
    public Iterable<ItemStack> getEquipmentAndArmor() {
        return this.player.getEquipmentAndArmor();
    }

    @Override
    public boolean isBurning() {
        return this.player.isBurning();
    }

    @Override
    public boolean isBeingRidden() {
        return this.player.isBeingRidden();
    }

    @Override
    public boolean isSneaking() {
        return this.player.isSneaking();
    }

    @Override
    public void setSneaking(boolean keyDownIn) {
        this.player.setSneaking(keyDownIn);
    }

    @Override
    public boolean isSprinting() {
        return this.player.isSprinting();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        this.player.setSprinting(sprinting);
    }

    @Override
    public boolean isGlowing() {
        return this.player.isGlowing();
    }

    @Override
    public void setGlowing(boolean glowingIn) {
        this.player.setGlowing(glowingIn);
    }

    @Override
    public boolean isInvisible() {
        return this.player.isInvisible();
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.player.setInvisible(invisible);
    }

    @Nullable
    @Override
    public Team getTeam() {
        return this.player.getTeam();
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return this.player.isOnSameTeam(entityIn);
    }

    @Override
    public boolean isOnScoreboardTeam(Team teamIn) {
        return this.player.isOnScoreboardTeam(teamIn);
    }

    @Override
    public int getAir() {
        return this.player.getAir();
    }

    @Override
    public void setAir(int air) {
        this.player.setAir(air);
    }

    @Override
    public boolean isEntityEqual(Entity entityIn) {
        return this.player.isEntityEqual(entityIn);
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return this.player.canBeAttackedWithItem();
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        return this.player.hitByEntity(entityIn);
    }

    @Override
    public String toString() {
        return this.player.toString();
    }

    @Override
    public void copyLocationAndAnglesFrom(Entity entityIn) {
        this.player.copyLocationAndAnglesFrom(entityIn);
    }

    @Override
    public boolean isNonBoss() {
        return this.player.isNonBoss();
    }

    @Override
    public int getMaxFallHeight() {
        return this.player.getMaxFallHeight();
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return this.player.doesEntityNotTriggerPressurePlate();
    }

    @Override
    public boolean canRenderOnFire() {
        return this.player.canRenderOnFire();
    }

    @Override
    public String getCachedUniqueIdString() {
        return this.player.getCachedUniqueIdString();
    }

    @Override
    public boolean hasCustomName() {
        return this.player.hasCustomName();
    }

    @Override
    public boolean isImmuneToExplosions() {
        return this.player.isImmuneToExplosions();
    }

    @Override
    public float getRotatedYaw(Rotation transformRotation) {
        return this.player.getRotatedYaw(transformRotation);
    }

    @Override
    public float getMirroredYaw(Mirror transformMirror) {
        return this.player.getMirroredYaw(transformMirror);
    }

    @Override
    public boolean ignoreItemEntityData() {
        return this.player.ignoreItemEntityData();
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.player.getControllingPassenger();
    }

    @Override
    public List<Entity> getPassengers() {
        return this.player.getPassengers();
    }

    @Override
    public boolean isPassenger(Entity entityIn) {
        return this.player.isPassenger(entityIn);
    }

    @Override
    public Collection<Entity> getRecursivePassengers() {
        return this.player.getRecursivePassengers();
    }

    @Override
    public Entity getLowestRidingEntity() {
        return this.player.getLowestRidingEntity();
    }

    @Override
    public boolean isRidingSameEntity(Entity entityIn) {
        return this.player.isRidingSameEntity(entityIn);
    }

    @Override
    public boolean isRidingOrBeingRiddenBy(Entity entityIn) {
        return this.player.isRidingOrBeingRiddenBy(entityIn);
    }

    @Override
    public boolean canPassengerSteer() {
        return this.player.canPassengerSteer();
    }

    @Nullable
    @Override
    public Entity getRidingEntity() {
        return this.player.getRidingEntity();
    }

    @Override
    public BlockPos getPosition() {
        return this.player.getPosition();
    }

    @Override
    public void onAddedToWorld() {
        this.player.onAddedToWorld();
    }

    @Override
    public void onRemovedFromWorld() {
        this.player.onRemovedFromWorld();
    }

    @Override
    public boolean shouldRiderSit() {
        return this.player.shouldRiderSit();
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return this.player.getPickedResult(target);
    }

    @Override
    public boolean canRiderInteract() {
        return this.player.canRiderInteract();
    }

    @Override
    public Collection<ITextComponent> getPrefixes() {
        return this.player.getPrefixes();
    }

    @Override
    public Collection<ITextComponent> getSuffixes() {
        return this.player.getSuffixes();
    }

    @Override
    public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
        this.player.knockBack(entityIn, strength, xRatio, zRatio);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.player.getCollisionBoundingBox();
    }

    @Override
    public boolean handleWaterMovement() {
        return this.player.handleWaterMovement();
    }

    @Override
    public void spawnRunningParticles() {
        this.player.spawnRunningParticles();
    }


    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return this.player.getCollisionBox(entityIn);
    }

    @Override
    public Vec3d getLookVec() {
        return this.player.getLookVec();
    }

    @Override
    public Vec2f getPitchYaw() {
        return this.player.getPitchYaw();
    }

    @Override
    public Vec3d getForward() {
        return this.player.getForward();
    }

    @Override
    public Vec3d getLastPortalVec() {
        return this.player.getLastPortalVec();
    }

    @Override
    public void sendMessage(ITextComponent component) {
        this.player.sendMessage(component);
    }

    @Override
    public Vec3d getPositionVector() {
        return this.player.getPositionVector();
    }

    @Override
    public boolean setPositionNonDirty() {
        return this.player.setPositionNonDirty();
    }

    @Override
    public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
        this.player.wakeUpPlayer(immediately, updateWorldFlag, setSpawn);
    }

    @Override
    public void setSpawnPoint(BlockPos pos, boolean forced) {
        this.player.setSpawnPoint(pos, forced);
    }


    @Override
    public void fall(float distance, float damageMultiplier) {
        this.player.fall(distance, damageMultiplier);
    }

    @Override
    public void dismountEntity(Entity entityIn) {
        this.player.dismountEntity(entityIn);
    }

    @Override
    public int getBrightnessForRender() {
        return this.player.getBrightnessForRender();
    }

    @Override
    public void onUpdate() {
        this.player.onUpdate();
    }

    @Override
    public void onLivingUpdate() {
        this.player.onLivingUpdate();
    }

    @Nullable
    @Override
    public EntityItem dropItem(boolean dropAll) {
        return this.player.dropItem(dropAll);
    }

    @Nullable
    @Override
    public EntityItem dropItem(ItemStack itemStackIn, boolean unused) {
        return this.player.dropItem(itemStackIn, unused);
    }

    @Nullable
    @Override
    public EntityItem dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
        return this.player.dropItem(droppedItem, dropAround, traceItem);
    }

    @Override
    public ItemStack dropItemAndGetStack(EntityItem p_184816_1_) {
        return this.player.dropItemAndGetStack(p_184816_1_);
    }

    @Override
    public float getDigSpeed(IBlockState state) {
        return this.player.getDigSpeed(state);
    }

    @Override
    public float getDigSpeed(IBlockState state, BlockPos pos) {
        return this.player.getDigSpeed(state, pos);
    }

    @Override
    public boolean canHarvestBlock(IBlockState state) {
        return this.player.canHarvestBlock(state);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.player.readEntityFromNBT(compound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        this.player.writeEntityToNBT(compound);
    }

    @Override
    public boolean canAttackPlayer(EntityPlayer other) {
        return this.player.canAttackPlayer(other);
    }

    @Override
    public float getArmorVisibility() {
        return this.player.getArmorVisibility();
    }

    @Override
    public void openEditSign(TileEntitySign signTile) {
        this.player.openEditSign(signTile);
    }

    @Override
    public void displayGuiEditCommandCart(CommandBlockBaseLogic commandBlock) {
        this.player.displayGuiEditCommandCart(commandBlock);
    }

    @Override
    public void displayGuiCommandBlock(TileEntityCommandBlock commandBlock) {
        this.player.displayGuiCommandBlock(commandBlock);
    }

    @Override
    public void openEditStructure(TileEntityStructure structure) {
        this.player.openEditStructure(structure);
    }

    @Override
    public void displayVillagerTradeGui(IMerchant villager) {
        this.player.displayVillagerTradeGui(villager);
    }

    @Override
    public void displayGUIChest(IInventory chestInventory) {
        this.player.displayGUIChest(chestInventory);
    }

    @Override
    public void openGuiHorseInventory(AbstractHorse horse, IInventory inventoryIn) {
        this.player.openGuiHorseInventory(horse, inventoryIn);
    }

    @Override
    public void displayGui(IInteractionObject guiOwner) {
        this.player.displayGui(guiOwner);
    }

    @Override
    public void openBook(ItemStack stack, EnumHand hand) {
        this.player.openBook(stack, hand);
    }

    @Override
    public EnumActionResult interactOn(Entity entityToInteractOn, EnumHand hand) {
        return this.player.interactOn(entityToInteractOn, hand);
    }

    @Override
    public void dismountRidingEntity() {
        this.player.dismountRidingEntity();
    }

    @Override
    public void setDead() {
        this.player.setDead();
    }

    @Override
    public SleepResult trySleep(BlockPos bedLocation) {
        return this.player.trySleep(bedLocation);
    }

    @Override
    public float getBedOrientationInDegrees() {
        return this.player.getBedOrientationInDegrees();
    }

    @Override
    public boolean isPlayerSleeping() {
        return this.player.isPlayerSleeping();
    }

    @Override
    public BlockPos getBedLocation() {
        return this.player.getBedLocation();
    }

    @Override
    public boolean isSpawnForced() {
        return this.player.isSpawnForced();
    }

    @Override
    public void addStat(StatBase stat) {
        this.player.addStat(stat);
    }

    @Override
    public void addStat(StatBase stat, int amount) {
        this.player.addStat(stat, amount);
    }

    @Override
    public void takeStat(StatBase stat) {
        this.player.takeStat(stat);
    }

    @Override
    public void unlockRecipes(List<IRecipe> p_192021_1_) {
        this.player.unlockRecipes(p_192021_1_);
    }

    @Override
    public void resetRecipes(List<IRecipe> p_192022_1_) {
        this.player.resetRecipes(p_192022_1_);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        this.player.travel(strafe, vertical, forward);
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        this.player.onKillEntity(entityLivingIn);
    }

    @Override
    public void setInWeb() {
        this.player.setInWeb();
    }

    @Override
    public void addExperience(int amount) {
        this.player.addExperience(amount);
    }

    @Override
    public boolean canPlayerEdit(BlockPos pos, EnumFacing facing, ItemStack stack) {
        return this.player.canPlayerEdit(pos, facing, stack);
    }

    @Override
    public InventoryEnderChest getInventoryEnderChest() {
        return this.player.getInventoryEnderChest();
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return this.player.getItemStackFromSlot(slotIn);
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        this.player.setItemStackToSlot(slotIn, stack);
    }

    @Override
    public boolean addShoulderEntity(NBTTagCompound p_192027_1_) {
        return this.player.addShoulderEntity(p_192027_1_);
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return this.player.isInvisibleToPlayer(player);
    }

    @Override
    public float getEyeHeight() {
        return this.player.getEyeHeight();
    }

    @Override
    public boolean canOpen(LockCode code) {
        return this.player.canOpen(code);
    }

    @Override
    public boolean isWearing(EnumPlayerModelParts part) {
        return this.player.isWearing(part);
    }

    @Override
    public boolean sendCommandFeedback() {
        return this.player.sendCommandFeedback();
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return this.player.getPrimaryHand();
    }

    @Override
    public void setPrimaryHand(EnumHandSide hand) {
        this.player.setPrimaryHand(hand);
    }

    @Override
    public NBTTagCompound getLeftShoulderEntity() {
        return this.player.getLeftShoulderEntity();
    }

    @Override
    public NBTTagCompound getRightShoulderEntity() {
        return this.player.getRightShoulderEntity();
    }

    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
        this.player.openGui(mod, modGuiId, world, x, y, z);
    }

    @Override
    public BlockPos getBedLocation(int dimension) {
        return this.player.getBedLocation(dimension);
    }

    @Override
    public boolean isSpawnForced(int dimension) {
        return this.player.isSpawnForced(dimension);
    }

    @Override
    public void setSpawnChunk(BlockPos pos, boolean forced, int dimension) {
        this.player.setSpawnChunk(pos, forced, dimension);
    }

    @Override
    public String getDisplayNameString() {
        return this.player.getDisplayNameString();
    }

    @Override
    public void refreshDisplayName() {
        this.player.refreshDisplayName();
    }

    @Override
    public void addPrefix(ITextComponent prefix) {
        this.player.addPrefix(prefix);
    }

    @Override
    public void addSuffix(ITextComponent suffix) {
        this.player.addSuffix(suffix);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return this.player == null ? super.getCapability(capability, facing) : this.player.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return this.player == null ? super.hasCapability(capability, facing) : this.player.hasCapability(capability, facing);
    }

    @Override
    public boolean hasSpawnDimension() {
        return this.player.hasSpawnDimension();
    }

    @Override
    public int getSpawnDimension() {
        return this.player.getSpawnDimension();
    }

    @Override
    public void setSpawnDimension(@Nullable Integer dimension) {
        this.player.setSpawnDimension(dimension);
    }

    @Override
    public void onEntityUpdate() {
        this.player.onEntityUpdate();
    }

    @Nullable
    @Override
    public EntityLivingBase getRevengeTarget() {
        return this.player.getRevengeTarget();
    }

    @Override
    public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
        this.player.setRevengeTarget(livingBase);
    }

    @Override
    public EntityLivingBase getLastAttackedEntity() {
        return this.player.getLastAttackedEntity();
    }

    @Override
    public void clearActivePotions() {
        this.player.clearActivePotions();
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return this.player.getActivePotionEffects();
    }

    @Override
    public Map<Potion, PotionEffect> getActivePotionMap() {
        return this.player.getActivePotionMap();
    }

    @Override
    public boolean isPotionActive(Potion potionIn) {
        return this.player.isPotionActive(potionIn);
    }

    @Nullable
    @Override
    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return this.player.getActivePotionEffect(potionIn);
    }

    @Override
    public void addPotionEffect(PotionEffect potioneffectIn) {
        this.player.addPotionEffect(potioneffectIn);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return this.player.isPotionApplicable(potioneffectIn);
    }

    @Nullable
    @Override
    public PotionEffect removeActivePotionEffect(@Nullable Potion potioneffectin) {
        return this.player.removeActivePotionEffect(potioneffectin);
    }

    @Override
    public void removePotionEffect(Potion potionIn) {
        this.player.removePotionEffect(potionIn);
    }

    @Override
    public void renderBrokenItemStack(ItemStack stack) {
        this.player.renderBrokenItemStack(stack);
    }

    @Override
    public boolean isEntityAlive() {
        return this.player.isEntityAlive();
    }

    @Nullable
    @Override
    public EntityLivingBase getAttackingEntity() {
        return this.player.getAttackingEntity();
    }

    @Override
    public void swingArm(EnumHand hand) {
        this.player.swingArm(hand);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return this.player.getCreatureAttribute();
    }

    @Override
    public ItemStack getHeldItem(EnumHand hand) {
        return this.player.getHeldItem(hand);
    }

    @Override
    public void setHeldItem(EnumHand hand, ItemStack stack) {
        this.player.setHeldItem(hand, stack);
    }

    @Override
    public boolean hasItemInSlot(EntityEquipmentSlot p_190630_1_) {
        return this.player.hasItemInSlot(p_190630_1_);
    }

    @Override
    public Vec3d getLook(float partialTicks) {
        return this.player.getLook(partialTicks);
    }

    @Override
    public void curePotionEffects(ItemStack curativeItem) {
        this.player.curePotionEffects(curativeItem);
    }

    @Override
    public boolean shouldRiderFaceForward(EntityPlayer player) {
        return this.player.shouldRiderFaceForward(player);
    }

    @Override
    public boolean attemptTeleport(double x, double y, double z) {
        return this.player.attemptTeleport(x, y, z);
    }

    @Override
    public void moveRelative(float strafe, float up, float forward, float friction) {
        this.player.moveRelative(strafe, up, forward, friction);
    }

    @Override
    public void setDropItemsWhenDead(boolean dropWhenDead) {
        this.player.setDropItemsWhenDead(dropWhenDead);
    }

    @Override
    public void turn(float yaw, float pitch) {
        this.player.turn(yaw, pitch);
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        this.player.move(type, x, y, z);
    }

    @Override
    public boolean isOverWater() {
        return this.player.isOverWater();
    }

    @Override
    public boolean isInsideOfMaterial(Material materialIn) {
        return this.player.isInsideOfMaterial(materialIn);
    }

    @Override
    public double getDistanceSq(BlockPos pos) {
        return this.player.getDistanceSq(pos);
    }

    @Override
    public double getDistanceSqToCenter(BlockPos pos) {
        return this.player.getDistanceSqToCenter(pos);
    }

    @Override
    public double getDistance(double x, double y, double z) {
        return this.player.getDistance(x, y, z);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        this.player.onCollideWithPlayer(entityIn);
    }

    @Override
    public Vec3d getPositionEyes(float partialTicks) {
        return this.player.getPositionEyes(partialTicks);
    }

    @Nullable
    @Override
    public RayTraceResult rayTrace(double blockReachDistance, float partialTicks) {
        return this.player.rayTrace(blockReachDistance, partialTicks);
    }

    @Override
    public boolean writeToNBTAtomically(NBTTagCompound compound) {
        return this.player.writeToNBTAtomically(compound);
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return this.player.writeToNBTOptional(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return this.player.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.player.readFromNBT(compound);
    }

    @Nullable
    @Override
    public EntityItem dropItem(Item itemIn, int size) {
        return this.player.dropItem(itemIn, size);
    }

    @Nullable
    @Override
    public EntityItem dropItemWithOffset(Item itemIn, int size, float offsetY) {
        return this.player.dropItemWithOffset(itemIn, size, offsetY);
    }

    @Nullable
    @Override
    public EntityItem entityDropItem(ItemStack stack, float offsetY) {
        return this.player.entityDropItem(stack, offsetY);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return this.player.processInitialInteract(player, hand);
    }

    @Override
    public boolean isRiding() {
        return this.player.isRiding();
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
        this.player.onStruckByLightning(lightningBolt);
    }

    @Nullable
    @Override
    public Entity[] getParts() {
        return this.player.getParts();
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return this.player.isEntityInvulnerable(source);
    }

    @Override
    public boolean getIsInvulnerable() {
        return this.player.getIsInvulnerable();
    }

    @Override
    public void setEntityInvulnerable(boolean isInvulnerable) {
        this.player.setEntityInvulnerable(isInvulnerable);
    }

    @Nullable
    @Override
    public Entity changeDimension(int dimensionIn) {
        return this.player.changeDimension(dimensionIn);
    }

    @Nullable
    @Override
    public Entity changeDimension(int dimensionIn, ITeleporter teleporter) {
        return this.player.changeDimension(dimensionIn, teleporter);
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
        return this.player.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn);
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float p_174816_5_) {
        return this.player.canExplosionDestroyBlock(explosionIn, worldIn, pos, blockStateIn, p_174816_5_);
    }

    @Override
    public EnumFacing getTeleportDirection() {
        return this.player.getTeleportDirection();
    }

    @Override
    public void addEntityCrashInfo(CrashReportCategory category) {
        this.player.addEntityCrashInfo(category);
    }

    @Override
    public void setCustomNameTag(String name) {
        this.player.setCustomNameTag(name);
    }

    @Override
    public String getCustomNameTag() {
        return this.player.getCustomNameTag();
    }

    @Override
    public void setAlwaysRenderNameTag(boolean alwaysRenderNameTag) {
        this.player.setAlwaysRenderNameTag(alwaysRenderNameTag);
    }

    @Override
    public boolean getAlwaysRenderNameTag() {
        return this.player.getAlwaysRenderNameTag();
    }

    @Override
    public EnumFacing getHorizontalFacing() {
        return this.player.getHorizontalFacing();
    }

    @Override
    public EnumFacing getAdjustedHorizontalFacing() {
        return this.player.getAdjustedHorizontalFacing();
    }

    @Override
    public boolean isSpectatedByPlayer(EntityPlayerMP player) {
        return this.player.isSpectatedByPlayer(player);
    }

    @Override
    public boolean canUseCommand(int permLevel, String commandName) {
        return this.player.canUseCommand(permLevel, commandName);
    }

    @Override
    public Entity getCommandSenderEntity() {
        return this.player.getCommandSenderEntity();
    }

    @Override
    public void setCommandStat(CommandResultStats.Type type, int amount) {
        this.player.setCommandStat(type, amount);
    }

    @Override
    public CommandResultStats getCommandStats() {
        return this.player.getCommandStats();
    }

    @Override
    public void setCommandStats(Entity entityIn) {
        this.player.setCommandStats(entityIn);
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        return this.player.applyPlayerInteraction(player, vec, hand);
    }

    @Override
    public NBTTagCompound getEntityData() {
        return this.player.getEntityData();
    }

    @Override
    public UUID getPersistentID() {
        return this.player.getPersistentID();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return this.player.shouldRenderInPass(pass);
    }

    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        return this.player.isCreatureType(type, forSpawnCount);
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return this.player.shouldDismountInWater(rider);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.player.deserializeNBT(nbt);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.player.serializeNBT();
    }

    @Override
    public boolean canTrample(World world, Block block, BlockPos pos, float fallDistance) {
        return this.player.canTrample(world, block, pos, fallDistance);
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        this.player.addTrackingPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        this.player.removeTrackingPlayer(player);
    }

    @Override
    public <T extends Entity> Collection<T> getRecursivePassengersByType(Class<T> entityClass) {
        return this.player.getRecursivePassengersByType(entityClass);
    }

    @Override
    public EnumPushReaction getPushReaction() {
        return this.player.getPushReaction();
    }

    @Override
    public void addSelfToInternalCraftingInventory() {
        this.player.addSelfToInternalCraftingInventory();
    }

    @Override
    public void onUpdateEntity() {
        this.player.onUpdateEntity();
    }

    @Override
    public void handleFalling(double y, boolean onGroundIn) {
        this.player.handleFalling(y, onGroundIn);
    }

    @Override
    public void getNextWindowId() {
        this.player.getNextWindowId();
    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        this.player.sendSlotContents(containerToSend, slotInd, stack);
    }

    @Override
    public void sendContainerToPlayer(Container containerIn) {
        this.player.sendContainerToPlayer(containerIn);
    }

    @Override
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
        this.player.sendAllContents(containerToSend, itemsList);
    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
        this.player.sendWindowProperty(containerIn, varToUpdate, newValue);
    }

    @Override
    public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
        this.player.sendAllWindowProperties(containerIn, inventory);
    }

    @Override
    public void updateHeldItem() {
        this.player.updateHeldItem();
    }

    @Override
    public void closeContainer() {
        this.player.closeContainer();
    }

    @Override
    public void setEntityActionState(float strafe, float forward, boolean jumping, boolean sneaking) {
        this.player.setEntityActionState(strafe, forward, jumping, sneaking);
    }

    @Override
    public void mountEntityAndWakeUp() {
        this.player.mountEntityAndWakeUp();
    }

    @Override
    public boolean hasDisconnected() {
        return this.player.hasDisconnected();
    }

    @Override
    public void setPlayerHealthUpdated() {
        this.player.setPlayerHealthUpdated();
    }

    @Override
    public void copyFrom(EntityPlayerMP that, boolean keepEverything) {
        this.player.copyFrom(that, keepEverything);
    }

    @Override
    public WorldServer getServerWorld() {
        return this.player.getServerWorld();
    }

    @Override
    public String getPlayerIP() {
        return this.player.getPlayerIP();
    }

    @Override
    public void handleClientSettings(CPacketClientSettings packetIn) {
        this.player.handleClientSettings(packetIn);
    }

    @Override
    public EnumChatVisibility getChatVisibility() {
        return this.player.getChatVisibility();
    }

    @Override
    public void loadResourcePack(String url, String hash) {
        this.player.loadResourcePack(url, hash);
    }

    @Override
    public void markPlayerActive() {
        this.player.markPlayerActive();
    }

    @Override
    public StatisticsManagerServer getStatFile() {
        return this.player.getStatFile();
    }

    @Override
    public RecipeBookServer getRecipeBook() {
        return this.player.getRecipeBook();
    }

    @Override
    public void removeEntity(Entity entityIn) {
        this.player.removeEntity(entityIn);
    }

    @Override
    public void addEntity(Entity entityIn) {
        this.player.addEntity(entityIn);
    }

    @Override
    public Entity getSpectatingEntity() {
        return this.player.getSpectatingEntity();
    }

    @Override
    public void setSpectatingEntity(Entity entityToSpectate) {
        this.player.setSpectatingEntity(entityToSpectate);
    }

    @Override
    public long getLastActiveTime() {
        return this.player.getLastActiveTime();
    }

    @Nullable
    @Override
    public ITextComponent getTabListDisplayName() {
        return this.player.getTabListDisplayName();
    }

    @Override
    public boolean isInvulnerableDimensionChange() {
        return this.player.isInvulnerableDimensionChange();
    }

    @Override
    public void clearInvulnerableDimensionChange() {
        this.player.clearInvulnerableDimensionChange();
    }

    @Override
    public void setElytraFlying() {
        this.player.setElytraFlying();
    }

    @Override
    public void clearElytraFlying() {
        this.player.clearElytraFlying();
    }

    @Override
    public PlayerAdvancements getAdvancements() {
        return this.player.getAdvancements();
    }

    @Nullable
    @Override
    public Vec3d getEnteredNetherPosition() {
        return this.player.getEnteredNetherPosition();
    }
}
