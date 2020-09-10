package cjminecraft.doubleslabs.api;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stat;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PlayerEntityWrapper extends PlayerEntity implements IPlayerWrapper<PlayerEntity> {

    private final PlayerEntity player;

    public PlayerEntityWrapper(PlayerEntity player, World world) {
        super(world, player.getGameProfile());
//        super(player.server, player.getServerWorld(), player.getGameProfile(), player.interactionManager);
        this.player = player;
        this.world = world;
    }

    @Override
    public PlayerEntity getOriginalPlayer() {
        return this.player;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.player.readAdditional(compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        this.player.writeAdditional(compound);
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
    public void tick() {
        this.player.tick();
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
    public boolean canAttackPlayer(PlayerEntity other) {
        return this.player.canAttackPlayer(other);
    }

    @Override
    public boolean isSpectatedByPlayer(ServerPlayerEntity player) {
        return this.player.isSpectatedByPlayer(player);
    }

    @Override
    public void onItemPickup(Entity entityIn, int quantity) {
        this.player.onItemPickup(entityIn, quantity);
    }

    @Override
    public Either<SleepResult, Unit> trySleep(BlockPos at) {
        return this.player.trySleep(at);
    }

    @Override
    public void startSleeping(BlockPos pos) {
        this.player.startSleeping(pos);
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force) {
        return this.player.startRiding(entityIn, force);
    }

    @Override
    public void stopRiding() {
        this.player.stopRiding();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.player.isInvulnerableTo(source);
    }

    @Override
    public void openSignEditor(SignTileEntity signTile) {
        this.player.openSignEditor(signTile);
    }

    @Override
    public OptionalInt openContainer(@Nullable INamedContainerProvider p_213829_1_) {
        return this.player.openContainer(p_213829_1_);
    }

    @Override
    public void openMerchantContainer(int containerId, MerchantOffers offers, int level, int xp, boolean p_213818_5_, boolean p_213818_6_) {
        this.player.openMerchantContainer(containerId, offers, level, xp, p_213818_5_, p_213818_6_);
    }

    @Override
    public void openHorseInventory(AbstractHorseEntity horse, IInventory inventoryIn) {
        this.player.openHorseInventory(horse, inventoryIn);
    }

    @Override
    public void openBook(ItemStack stack, Hand hand) {
        this.player.openBook(stack, hand);
    }

    @Override
    public void openCommandBlock(CommandBlockTileEntity commandBlock) {
        this.player.openCommandBlock(commandBlock);
    }

    @Override
    public void closeScreen() {
        this.player.closeScreen();
    }

    @Override
    public void addStat(Stat<?> stat, int amount) {
        this.player.addStat(stat, amount);
    }

    @Override
    public void takeStat(Stat<?> stat) {
        this.player.takeStat(stat);
    }

    @Override
    public int unlockRecipes(Collection<IRecipe<?>> p_195065_1_) {
        return this.player.unlockRecipes(p_195065_1_);
    }

    @Override
    public void unlockRecipes(ResourceLocation[] p_193102_1_) {
        this.player.unlockRecipes(p_193102_1_);
    }

    @Override
    public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
        return this.player.resetRecipes(p_195069_1_);
    }

    @Override
    public void giveExperiencePoints(int p_195068_1_) {
        this.player.giveExperiencePoints(p_195068_1_);
    }

    @Override
    public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
        this.player.sendStatusMessage(chatComponent, actionBar);
    }

    @Override
    public void setPositionAndUpdate(double x, double y, double z) {
        this.player.setPositionAndUpdate(x, y, z);
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
    public void swingArm(Hand hand) {
        this.player.swingArm(hand);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return this.player.createSpawnPacket();
    }

    @Override
    public ItemEntity dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
        return this.player.dropItem(droppedItem, dropAround, traceItem);
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
    public void livingTick() {
        this.player.livingTick();
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

    @Nullable
    @Override
    public ItemEntity dropItem(ItemStack itemStackIn, boolean unused) {
        return this.player.dropItem(itemStackIn, unused);
    }

    @Override
    public float getDigSpeed(BlockState state) {
        return this.player.getDigSpeed(state);
    }

    @Override
    public float getDigSpeed(BlockState state, @Nullable BlockPos pos) {
        return this.player.getDigSpeed(state, pos);
    }

    @Override
    public void openMinecartCommandBlock(CommandBlockLogic commandBlock) {
        this.player.openMinecartCommandBlock(commandBlock);
    }

    @Override
    public void openStructureBlock(StructureBlockTileEntity structure) {
        this.player.openStructureBlock(structure);
    }

    @Override
    public ActionResultType interactOn(Entity entityToInteractOn, Hand hand) {
        return this.player.interactOn(entityToInteractOn, hand);
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
    public void remove(boolean keepData) {
        this.player.remove(keepData);
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
    public void wakeUp() {
        this.player.wakeUp();
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
    public void addStat(ResourceLocation stat) {
        this.player.addStat(stat);
    }

    @Override
    public void addStat(ResourceLocation p_195067_1_, int p_195067_2_) {
        this.player.addStat(p_195067_1_, p_195067_2_);
    }

    @Override
    public void addStat(Stat<?> stat) {
        this.player.addStat(stat);
    }

    @Override
    public void jump() {
        this.player.jump();
    }

    @Override
    public void updateSwimming() {
        this.player.updateSwimming();
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
    public boolean canPlayerEdit(BlockPos pos, Direction facing, ItemStack stack) {
        return this.player.canPlayerEdit(pos, facing, stack);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return this.player.getAlwaysRenderNameTagForRender();
    }

    @Override
    public ITextComponent getName() {
        return this.player.getName();
    }

    @Override
    public EnderChestInventory getInventoryEnderChest() {
        return this.player.getInventoryEnderChest();
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return this.player.getItemStackFromSlot(slotIn);
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        this.player.setItemStackToSlot(slotIn, stack);
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
    public boolean addShoulderEntity(CompoundNBT p_192027_1_) {
        return this.player.addShoulderEntity(p_192027_1_);
    }

    @Override
    public boolean isSwimming() {
        return this.player.isSwimming();
    }

    @Override
    public void setSwimming(boolean swimming) {
        this.player.setSwimming(swimming);
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
    public String getScoreboardName() {
        return this.player.getScoreboardName();
    }

    @Override
    public float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.player == null ? super.getStandingEyeHeight(poseIn, sizeIn) : this.player.getStandingEyeHeight(poseIn, sizeIn);
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
    public boolean isWearing(PlayerModelPart part) {
        return this.player.isWearing(part);
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
    public HandSide getPrimaryHand() {
        return this.player.getPrimaryHand();
    }

    @Override
    public void setPrimaryHand(HandSide hand) {
        this.player.setPrimaryHand(hand);
    }

    @Override
    public CompoundNBT getLeftShoulderEntity() {
        return this.player.getLeftShoulderEntity();
    }

    @Override
    public CompoundNBT getRightShoulderEntity() {
        return this.player.getRightShoulderEntity();
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
    public EntitySize getSize(Pose poseIn) {
        return this.player.getSize(poseIn);
    }

    @Override
    public ItemStack findAmmo(ItemStack shootable) {
        return this.player.findAmmo(shootable);
    }

    @Override
    public ItemStack onFoodEaten(World p_213357_1_, ItemStack p_213357_2_) {
        return this.player.onFoodEaten(p_213357_1_, p_213357_2_);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        return this.player.getCapability(capability, facing);
    }

    @Override
    public Brain<?> getBrain() {
        return this.player.getBrain();
    }

    @Override
    public void onKillCommand() {
        this.player.onKillCommand();
    }

    @Override
    public boolean canAttack(EntityType<?> typeIn) {
        return this.player.canAttack(typeIn);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return this.player.canBreatheUnderwater();
    }

    @Override
    public float getSwimAnimation(float partialTicks) {
        return this.player.getSwimAnimation(partialTicks);
    }

    @Override
    public void baseTick() {
        this.player.baseTick();
    }

    @Override
    public boolean isChild() {
        return this.player.isChild();
    }

    @Override
    public float getRenderScale() {
        return this.player.getRenderScale();
    }

    @Override
    public boolean canBeRiddenInWater() {
        return this.player.canBeRiddenInWater();
    }

    @Override
    public Random getRNG() {
        return this.player.getRNG();
    }

    @Nullable
    @Override
    public LivingEntity getRevengeTarget() {
        return this.player.getRevengeTarget();
    }

    @Override
    public void setRevengeTarget(@Nullable LivingEntity livingBase) {
        this.player.setRevengeTarget(livingBase);
    }

    @Override
    public int getRevengeTimer() {
        return this.player.getRevengeTimer();
    }

    @Nullable
    @Override
    public LivingEntity getLastAttackedEntity() {
        return this.player.getLastAttackedEntity();
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
    public void setIdleTime(int idleTimeIn) {
        this.player.setIdleTime(idleTimeIn);
    }

    @Override
    public double getVisibilityMultiplier(@Nullable Entity lookingEntity) {
        return this.player.getVisibilityMultiplier(lookingEntity);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.player.canAttack(target);
    }

    @Override
    public boolean canAttack(LivingEntity livingentityIn, EntityPredicate predicateIn) {
        return this.player.canAttack(livingentityIn, predicateIn);
    }

    @Override
    public boolean clearActivePotions() {
        return this.player.clearActivePotions();
    }

    @Override
    public Collection<EffectInstance> getActivePotionEffects() {
        return this.player.getActivePotionEffects();
    }

    @Override
    public Map<Effect, EffectInstance> getActivePotionMap() {
        return this.player.getActivePotionMap();
    }

    @Override
    public boolean isPotionActive(Effect potionIn) {
        return this.player.isPotionActive(potionIn);
    }

    @Nullable
    @Override
    public EffectInstance getActivePotionEffect(Effect potionIn) {
        return this.player.getActivePotionEffect(potionIn);
    }

    @Override
    public boolean addPotionEffect(EffectInstance effectInstanceIn) {
        return this.player.addPotionEffect(effectInstanceIn);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        return this.player.isPotionApplicable(potioneffectIn);
    }

    @Override
    public boolean isEntityUndead() {
        return this.player.isEntityUndead();
    }

    @Nullable
    @Override
    public EffectInstance removeActivePotionEffect(@Nullable Effect potioneffectin) {
        return this.player.removeActivePotionEffect(potioneffectin);
    }

    @Override
    public boolean removePotionEffect(Effect effectIn) {
        return this.player.removePotionEffect(effectIn);
    }

    @Override
    public void heal(float healAmount) {
        this.player.heal(healAmount);
    }

    @Override
    public float getHealth() {
        return this.player.getHealth();
    }

    @Override
    public void setHealth(float health) {
        if (this.player == null)
            super.setHealth(health);
        else
            this.player.setHealth(health);
    }

    @Nullable
    @Override
    public DamageSource getLastDamageSource() {
        return this.player.getLastDamageSource();
    }

    @Override
    public ResourceLocation getLootTableResourceLocation() {
        return this.player.getLootTableResourceLocation();
    }

    @Override
    public SoundEvent getEatSound(ItemStack itemStackIn) {
        return this.player.getEatSound(itemStackIn);
    }

    @Override
    public boolean isOnLadder() {
        return this.player.isOnLadder();
    }

    @Override
    public BlockState getBlockState() {
        return this.player.getBlockState();
    }

    @Override
    public boolean isAlive() {
        return this.player.isAlive();
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

    @Nullable
    @Override
    public LivingEntity getAttackingEntity() {
        return this.player.getAttackingEntity();
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return this.player.getCreatureAttribute();
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
    public ItemStack getHeldItem(Hand hand) {
        return this.player.getHeldItem(hand);
    }

    @Override
    public void setHeldItem(Hand hand, ItemStack stack) {
        this.player.setHeldItem(hand, stack);
    }

    @Override
    public boolean hasItemInSlot(EquipmentSlotType slotIn) {
        return this.player.hasItemInSlot(slotIn);
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
    public void startSpinAttack(int p_204803_1_) {
        this.player.startSpinAttack(p_204803_1_);
    }

    @Override
    public boolean isSpinAttacking() {
        return this.player.isSpinAttacking();
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.player.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, teleport);
    }

    @Override
    public void setHeadRotation(float yaw, int pitch) {
        this.player.setHeadRotation(yaw, pitch);
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
    public float getYaw(float partialTicks) {
        return this.player.getYaw(partialTicks);
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
    public Hand getActiveHand() {
        return this.player.getActiveHand();
    }

    @Override
    public void setActiveHand(Hand hand) {
        this.player.setActiveHand(hand);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (this.player == null)
            super.notifyDataManagerChange(key);
        else
            this.player.notifyDataManagerChange(key);
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
    public boolean isActualySwimming() {
        return this.player.isActualySwimming();
    }

    @Override
    public int getTicksElytraFlying() {
        return this.player.getTicksElytraFlying();
    }

    @Override
    public boolean attemptTeleport(double x, double y, double z, boolean p_213373_7_) {
        return this.player.attemptTeleport(x, y, z, p_213373_7_);
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
    public Optional<BlockPos> getBedPosition() {
        return this.player.getBedPosition();
    }

    @Override
    public void setBedPosition(BlockPos p_213369_1_) {
        this.player.setBedPosition(p_213369_1_);
    }

    @Override
    public void clearBedPosition() {
        this.player.clearBedPosition();
    }

    @Override
    public boolean isSleeping() {
        return this.player.isSleeping();
    }

    @Nullable
    @Override
    public Direction getBedDirection() {
        return this.player.getBedDirection();
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return this.player.isEntityInsideOpaqueBlock();
    }

    @Override
    public void sendBreakAnimation(EquipmentSlotType p_213361_1_) {
        this.player.sendBreakAnimation(p_213361_1_);
    }

    @Override
    public void sendBreakAnimation(Hand p_213334_1_) {
        this.player.sendBreakAnimation(p_213334_1_);
    }

    @Override
    public boolean curePotionEffects(ItemStack curativeItem) {
        return this.player.curePotionEffects(curativeItem);
    }

    @Override
    public boolean shouldRiderFaceForward(PlayerEntity player) {
        return this.player.shouldRiderFaceForward(player);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this.player.getRenderBoundingBox();
    }

    @Override
    public void setPacketCoordinates(double x, double y, double z) {
        this.player.setPacketCoordinates(x, y, z);
    }

    @Override
    public EntityType<?> getType() {
        return this.player.getType();
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
    public void remove() {
        this.player.remove();
    }

    @Override
    public Pose getPose() {
        return this.player.getPose();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        if (this.player == null)
            super.setPosition(x, y, z);
        else
            this.player.setPosition(x, y, z);
    }

    @Override
    public void rotateTowards(double yaw, double pitch) {
        this.player.rotateTowards(yaw, pitch);
    }

    @Override
    public void setFire(int seconds) {
        this.player.setFire(seconds);
    }

    @Override
    public int getFireTimer() {
        return this.player.getFireTimer();
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
    public boolean isInWaterRainOrBubbleColumn() {
        return this.player.isInWaterRainOrBubbleColumn();
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        return this.player.isInWaterOrBubbleColumn();
    }

    @Override
    public boolean canSwim() {
        return this.player.canSwim();
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
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.player.setPositionAndRotation(x, y, z, yaw, pitch);
    }

    @Override
    public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn) {
        this.player.moveToBlockPosAndAngles(pos, rotationYawIn, rotationPitchIn);
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        if (this.player == null)
            super.setLocationAndAngles(x, y, z, yaw, pitch);
        else
            this.player.setLocationAndAngles(x, y, z, yaw, pitch);
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
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        this.player.onCollideWithPlayer(entityIn);
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        this.player.addVelocity(x, y, z);
    }

    @Override
    public float getPitch(float partialTicks) {
        return this.player.getPitch(partialTicks);
    }

    @Override
    public RayTraceResult pick(double rayTraceDistance, float partialTicks, boolean p_213324_4_) {
        return this.player.pick(rayTraceDistance, partialTicks, p_213324_4_);
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
    public boolean writeUnlessRemoved(CompoundNBT compound) {
        return this.player.writeUnlessRemoved(compound);
    }

    @Override
    public boolean writeUnlessPassenger(CompoundNBT compound) {
        return this.player.writeUnlessPassenger(compound);
    }

    @Override
    public CompoundNBT writeWithoutTypeId(CompoundNBT compound) {
        return this.player.writeWithoutTypeId(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        this.player.read(compound);
    }


    @Nullable
    @Override
    public ItemEntity entityDropItem(IItemProvider itemIn) {
        return this.player.entityDropItem(itemIn);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(IItemProvider itemIn, int offset) {
        return this.player.entityDropItem(itemIn, offset);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(ItemStack stack) {
        return this.player.entityDropItem(stack);
    }

    @Nullable
    @Override
    public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
        return this.player.entityDropItem(stack, offsetY);
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
    public boolean isLiving() {
        return this.player.isLiving();
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
    public boolean isPassenger() {
        return this.player.isPassenger();
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
    public boolean isVisuallySwimming() {
        return this.player.isVisuallySwimming();
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

    @Override
    public boolean isInvisibleToPlayer(PlayerEntity player) {
        return this.player.isInvisibleToPlayer(player);
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
    public int getMaxAir() {
        return this.player == null ? super.getMaxAir() : this.player.getMaxAir();
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
    public void onEnterBubbleColumnWithAirAbove(boolean downwards) {
        this.player.onEnterBubbleColumnWithAirAbove(downwards);
    }

    @Override
    public void onEnterBubbleColumn(boolean downwards) {
        this.player.onEnterBubbleColumn(downwards);
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
    public boolean isInvulnerable() {
        return this.player.isInvulnerable();
    }

    @Override
    public void setInvulnerable(boolean isInvulnerable) {
        this.player.setInvulnerable(isInvulnerable);
    }

    @Override
    public void copyLocationAndAnglesFrom(Entity entityIn) {
        this.player.copyLocationAndAnglesFrom(entityIn);
    }

    @Override
    public void copyDataFromOld(Entity entityIn) {
        this.player.copyDataFromOld(entityIn);
    }

    @Override
    public boolean isNonBoss() {
        return this.player.isNonBoss();
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, float explosionPower) {
        return this.player.canExplosionDestroyBlock(explosionIn, worldIn, pos, blockStateIn, explosionPower);
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
    public void fillCrashReport(CrashReportCategory category) {
        this.player.fillCrashReport(category);
    }

    @Override
    public boolean canRenderOnFire() {
        return this.player.canRenderOnFire();
    }

    @Override
    public void setUniqueId(UUID uniqueIdIn) {
        if (this.player == null)
            super.setUniqueId(uniqueIdIn);
        else
            this.player.setUniqueId(uniqueIdIn);
    }

    @Override
    public UUID getUniqueID() {
        return this.player == null ? super.getUniqueID() : this.player.getUniqueID();
    }

    @Override
    public String getCachedUniqueIdString() {
        return this.player.getCachedUniqueIdString();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.player.getCustomName();
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        this.player.setCustomName(name);
    }

    @Override
    public boolean hasCustomName() {
        return this.player.hasCustomName();
    }

    @Override
    public boolean isCustomNameVisible() {
        return this.player.isCustomNameVisible();
    }

    @Override
    public void setCustomNameVisible(boolean alwaysRenderNameTag) {
        this.player.setCustomNameVisible(alwaysRenderNameTag);
    }

    @Override
    public void recalculateSize() {
        this.player.recalculateSize();
    }

    @Override
    public Direction getHorizontalFacing() {
        return this.player.getHorizontalFacing();
    }

    @Override
    public Direction getAdjustedHorizontalFacing() {
        return this.player.getAdjustedHorizontalFacing();
    }


    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.player.getBoundingBox();
    }


    @Override
    public void setBoundingBox(AxisAlignedBB bb) {
        if (this.player == null)
            super.setBoundingBox(bb);
        else
            this.player.setBoundingBox(bb);
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return this.player.getEyeHeight(pose);
    }

    @Override
    public World getEntityWorld() {
        return this.player.getEntityWorld();
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return this.player.getServer();
    }

    @Override
    public boolean isImmuneToExplosions() {
        return this.player.isImmuneToExplosions();
    }

    @Override
    public void addTrackingPlayer(ServerPlayerEntity player) {
        this.player.addTrackingPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayerEntity player) {
        this.player.removeTrackingPlayer(player);
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
    public boolean isPassenger(Class<? extends Entity> entityClazz) {
        return this.player.isPassenger(entityClazz);
    }

    @Override
    public Collection<Entity> getRecursivePassengers() {
        return this.player.getRecursivePassengers();
    }

    @Override
    public boolean isOnePlayerRiding() {
        return this.player.isOnePlayerRiding();
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
    public PushReaction getPushReaction() {
        return this.player.getPushReaction();
    }

    @Override
    public CommandSource getCommandSource() {
        return this.player.getCommandSource();
    }

    @Override
    public boolean hasPermissionLevel(int level) {
        return this.player.hasPermissionLevel(level);
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return this.player.shouldReceiveFeedback();
    }

    @Override
    public boolean shouldReceiveErrors() {
        return this.player.shouldReceiveErrors();
    }

    @Override
    public boolean allowLogging() {
        return this.player.allowLogging();
    }

    @Override
    public BlockPos getPosition() {
        return this.player.getPosition();
    }

    @Override
    public void setMotion(double x, double y, double z) {
        this.player.setMotion(x, y, z);
    }

    @Override
    public void canUpdate(boolean value) {
        this.player.canUpdate(value);
    }

    @Override
    public boolean canUpdate() {
        return this.player.canUpdate();
    }

    @Override
    public Collection<ItemEntity> captureDrops() {
        return this.player.captureDrops();
    }

    @Override
    public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
        return this.player.captureDrops(value);
    }

    @Override
    public CompoundNBT getPersistentData() {
        return this.player.getPersistentData();
    }

    @Override
    public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
        return this.player.canTrample(state, pos, fallDistance);
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
    public void revive() {
        this.player.revive();
    }


    @Override
    public Entity getEntity() {
        return this.player.getEntity();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.player.deserializeNBT(nbt);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return this.player.serializeNBT();
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
    public boolean canBeRiddenInWater(Entity rider) {
        return this.player.canBeRiddenInWater(rider);
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        return this.player.getClassification(forSpawnCount);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return this.player.getCapability(cap);
    }

    // todo complete

    @Override
    public boolean canHarvestBlock(BlockState state) {
        return this.player.canHarvestBlock(state);
    }

    @Override
    public BlockPos getBedLocation() {
        return this.player.getBedLocation();
    }

    @Override
    public BlockPos getBedLocation(DimensionType dim) {
        return this.player.getBedLocation(dim);
    }

    @Override
    public boolean isSpawnForced() {
        return this.player.isSpawnForced();
    }

    @Override
    public boolean isSpawnForced(DimensionType dim) {
        return this.player.isSpawnForced(dim);
    }

    @Override
    public void travel(Vec3d positionIn) {
        this.player.travel(positionIn);
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        this.player.onKillEntity(entityLivingIn);
    }

    @Override
    public void setMotionMultiplier(BlockState state, Vec3d motionMultiplierIn) {
        this.player.setMotionMultiplier(state, motionMultiplierIn);
    }

    @Override
    public ITextComponent getDisplayNameAndUUID() {
        return this.player.getDisplayNameAndUUID();
    }

    @Override
    public DimensionType getSpawnDimension() {
        return this.player.getSpawnDimension();
    }

    @Override
    public void setSpawnDimenion(DimensionType dim) {
        this.player.setSpawnDimenion(dim);
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

    @Override
    public IAttributeInstance getAttribute(IAttribute attribute) {
        return this.player == null ? super.getAttribute(attribute) : this.player.getAttribute(attribute);
    }

    @Override
    public AbstractAttributeMap getAttributes() {
        return this.player == null ? super.getAttributes() : this.player.getAttributes();
    }

    @Override
    public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
        this.player.lookAt(p_200602_1_, p_200602_2_);
    }

    @Override
    public void setFireTimer(int p_223308_1_) {
        this.player.setFireTimer(p_223308_1_);
    }

    @Override
    public void move(MoverType typeIn, Vec3d pos) {
        this.player.move(typeIn, pos);
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

    @Override
    public boolean areEyesInFluid(Tag<Fluid> tagIn) {
        return this.player.areEyesInFluid(tagIn);
    }

    @Override
    public boolean areEyesInFluid(Tag<Fluid> p_213290_1_, boolean checkChunkLoaded) {
        return this.player.areEyesInFluid(p_213290_1_, checkChunkLoaded);
    }

    @Override
    public void setInLava() {
        this.player.setInLava();
    }

    @Override
    public void moveRelative(float p_213309_1_, Vec3d relative) {
        this.player.moveRelative(p_213309_1_, relative);
    }

    @Override
    public double getDistanceSq(Vec3d p_195048_1_) {
        return this.player.getDistanceSq(p_195048_1_);
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        return this.player.processInitialInteract(player, hand);
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
    public void onStruckByLightning(LightningBoltEntity lightningBolt) {
        this.player.onStruckByLightning(lightningBolt);
    }

    @Nullable
    @Override
    public Entity changeDimension(DimensionType destination) {
        return this.player.changeDimension(destination);
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, IFluidState p_180428_5_, float p_180428_6_) {
        return this.player.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn, p_180428_5_, p_180428_6_);
    }

    @Override
    public Vec3d getLastPortalVec() {
        return this.player.getLastPortalVec();
    }

    @Override
    public Direction getTeleportDirection() {
        return this.player.getTeleportDirection();
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
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        return this.player.applyPlayerInteraction(player, vec, hand);
    }

    @Override
    public boolean setPositionNonDirty() {
        return this.player.setPositionNonDirty();
    }

    @Override
    public boolean handleFluidAcceleration(Tag<Fluid> p_210500_1_) {
        return this.player.handleFluidAcceleration(p_210500_1_);
    }

    @Override
    public double getSubmergedHeight() {
        return this.player.getSubmergedHeight();
    }

    @Override
    public Vec3d getPositionVec() {
        return this.player.getPositionVec();
    }

    @Override
    public Vec3d getMotion() {
        return this.player.getMotion();
    }

    @Override
    public void setMotion(Vec3d motionIn) {
        this.player.setMotion(motionIn);
    }

    @Override
    public boolean func_223729_a(World p_223729_1_, BlockPos p_223729_2_, GameType p_223729_3_) {
        return this.player.func_223729_a(p_223729_1_, p_223729_2_, p_223729_3_);
    }

    @Override
    public void func_213823_a(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
        this.player.func_213823_a(p_213823_1_, p_213823_2_, p_213823_3_, p_213823_4_);
    }

    @Nullable
    @Override
    public ItemEntity dropItem(boolean dropAll) {
        return this.player.dropItem(dropAll);
    }

    @Override
    public void func_213826_a(JigsawTileEntity p_213826_1_) {
        this.player.func_213826_a(p_213826_1_);
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
    public void setSpawnPoint(@Nullable BlockPos pos, boolean forced, DimensionType dim) {
        this.player.setSpawnPoint(pos, forced, dim);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        this.player.fall(distance, damageMultiplier);
    }

    @Override
    public boolean func_213365_e(ItemStack itemstackIn) {
        return this.player.func_213365_e(itemstackIn);
    }

    @Override
    public float func_213343_cS() {
        return this.player.func_213343_cS();
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
    public Vec3d getEyePosition(float partialTicks) {
        return this.player.getEyePosition(partialTicks);
    }

    @Override
    public boolean shouldRenderSneaking() {
        return this.player.shouldRenderSneaking();
    }
}
