package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import com.mojang.math.Vector3d;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IBlockRenderProperties;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DoubleSlabBlock extends DynamicSlabBlock {

    public static Optional<IBlockInfo> getHalfState(BlockGetter world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeBlockInfo().getBlockState() == null && tile.getPositiveBlockInfo().getBlockState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeBlockInfo().getBlockState() == null) && tile.getPositiveBlockInfo().getBlockState() != null ?
                        Optional.of(tile.getPositiveBlockInfo()) : Optional.of(tile.getNegativeBlockInfo()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
        consumer.accept(new IBlockRenderProperties() {
            @Override
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                if (target.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult result = (BlockHitResult) target;
                    return getHalfState(level, result.getBlockPos(), result.getLocation().y - result.getBlockPos().getY()).map(i -> crack((ClientLevel) level, state, i.getBlockState(), result.getBlockPos(), result.getDirection(), manager)).orElse(false);
                }
                return false;
            }

            @Override
            public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
                AtomicBoolean result = new AtomicBoolean(false);
                runIfAvailable(level, pos, i -> {
                    manager.destroy(pos, i.getBlockState());
                    result.set(true);
                });
                return result.get();
            }
        });
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return both(world, pos, i -> i.getBlockState().propagatesSkylightDown(i.getWorld(), pos));
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        Vec3 hitVec = result.getType() == HitResult.Type.BLOCK ? result.getLocation() : null;
        if (hitVec == null)
            return minFloat(world, pos, i -> i.getBlockState().getDestroyProgress(player, i.getWorld(), pos));
        return getHalfState(world, pos, hitVec.y - pos.getY())
                .map(i -> i.getBlockState().getDestroyProgress(player, i.getWorld(), pos))
                .orElseGet(() -> super.getDestroyProgress(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return getHalfState(world, pos, target.getLocation().y - pos.getY()).map(i -> i.getBlockState().getPickBlock(target, world, pos, player)).orElse(ItemStack.EMPTY);
    }

    // todo test
//    @Override
//    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
//        BlockHitResult rayTraceResult = RayTraceUtil.rayTrace(player);
//        Vector3d hitVec = rayTraceResult.getHitVec();
//        return getHalfState(world, pos, hitVec.y - pos.getY()).map(i -> i.getBlockState().canHarvestBlock(i.getWorld(), i.getPos(), player)).orElse(false);
//    }


    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack stack) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        Vec3 hitVec = result.getType() == HitResult.Type.BLOCK ? result.getLocation() : null;
        if (hitVec == null || entity == null) {
            super.playerDestroy(world, player, pos, state, entity, stack);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
            world.removeBlockEntity(pos);
        } else {
            SlabTileEntity tile = (SlabTileEntity) entity;

            double y = hitVec.y - (double) pos.getY();

            IBlockInfo remainingBlock = y > 0.5 ? tile.getNegativeBlockInfo() : tile.getPositiveBlockInfo();
            IBlockInfo blockToRemove = y > 0.5 ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo();

            player.awardStat(Stats.BLOCK_MINED.get(blockToRemove.getBlockState().getBlock()));
            world.levelEvent(2001, pos, Block.getId(blockToRemove.getBlockState()));
            player.causeFoodExhaustion(0.005F);

            if (!player.isCreative())
                dropResources(blockToRemove.getBlockState(), world, pos, null, player, stack);

            blockToRemove.getBlockState().onRemove(blockToRemove.getWorld(), pos, Blocks.AIR.defaultBlockState(), false);

            world.setBlock(pos, remainingBlock.getBlockState(), Constants.BlockFlags.DEFAULT);
            if (remainingBlock.getBlockEntity() != null)
                world.setBlockEntity(remainingBlock.getBlockEntity());
            else
                world.removeBlockEntity(pos);
        }
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return getTile(worldserver, pos).map(tile -> {
            float f = (float) Math.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            worldserver.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, tile.getPositiveBlockInfo().getBlockState()), entity.getX(), entity.getY(), entity.getZ(), numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide()) {
            getTile(world, pos).ifPresent(tile ->
                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, tile.getPositiveBlockInfo().getBlockState()),
                        entity.getX() + ((double) world.random.nextFloat() - 0.5D) * (double) entity.getBbWidth(),
                        entity.getBoundingBox().minY + 0.1D,
                        entity.getZ() + ((double) world.random.nextFloat() - 0.5D) * (double) entity.getBbWidth(),
                        -entity.getDeltaMovement().x * 4.0D, 1.5D, -entity.getDeltaMovement().z * 4.0D)
            );
        }
        return true;
    }

// todo

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
//        if (target.getType() == RayTraceResult.Type.BLOCK) {
//            BlockRayTraceResult result = (BlockRayTraceResult) target;
//            return getHalfState(world, result.getPos(), target.getHitVec().y).map(info -> {
//                BlockPos pos = result.getPos();
//                Direction side = result.getFace();
//                int i = pos.getX();
//                int j = pos.getY();
//                int k = pos.getZ();
//
//                AxisAlignedBB axisalignedbb = state.getCollisionShape(world, pos).getBoundingBox();
//                double d0 = (double) i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
//                double d1 = (double) j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
//                double d2 = (double) k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;
//
//                switch (side) {
//                    case DOWN:
//                        d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
//                        break;
//                    case UP:
//                        d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
//                        break;
//                    case NORTH:
//                        d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
//                        break;
//                    case SOUTH:
//                        d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
//                        break;
//                    case WEST:
//                        d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
//                        break;
//                    case EAST:
//                        d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
//                }
//
//                DiggingParticle.Factory factory = new DiggingParticle.Factory();
//
//                Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, info.getBlockState()), (ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
//                if (particle != null) {
//                    ((DiggingParticle) particle).setBlockPos(pos);
//                    particle = particle.multiplyVelocity(0.2F).multiplyParticleScaleBy(0.6F);
//                    manager.addEffect(particle);
//                    return true;
//                }
//
//                return false;
//            }).orElse(false);
//        }
//        return false;
//    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
//        return getTile(world, pos).map(tile -> {
//            DiggingParticle.Factory factory = new DiggingParticle.Factory();
//            for (int j = 0; j < 4; j++) {
//                for (int k = 0; k < 4; k++) {
//                    for (int l = 0; l < 4; l++) {
//                        double d0 = ((double) j + 0.5D) / 4.0D + pos.getX();
//                        double d1 = ((double) k + 0.5D) / 4.0D + pos.getY();
//                        double d2 = ((double) l + 0.5D) / 4.0D + pos.getZ();
//
//                        runIfAvailable(world, pos, i -> {
//                            Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, i.getBlockState()), (ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
//                            if (particle != null)
//                                manager.addEffect(particle);
//                        });
//                    }
//                }
//            }
//            return true;
//        }).orElse(false);
//    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getBlock() != this)
            return InteractionResult.PASS;
        return getHalfState(world, pos, hit.getLocation().y - pos.getY()).map(i -> {
            IContainerSupport containerSupport = ContainerSupport.getSupport(i.getWorld(), pos, i.getBlockState());
            ISlabSupport slabSupport = SlabSupport.getSlabSupport(i.getWorld(), pos, i.getBlockState());
            if (containerSupport != null) {
                if (!world.isClientSide()) {
                    MenuProvider provider = containerSupport.getNamedContainerProvider(i.getWorld(), pos, i.getBlockState(), player, hand, hit);
                    NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return provider.getDisplayName();
                        }

                        @Nullable
                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                            return new WrappedContainer(windowId, inv, player, provider, i);
                        }
                    }, buffer -> {
                        buffer.writeBlockPos(i.getPos());
                        buffer.writeBoolean(i.isPositive());
                        buffer.writeResourceLocation(containerSupport.getContainer(i.getWorld(), pos, state).getRegistryName());
                        containerSupport.writeExtraData(world, pos, state).accept(buffer);
                    });
                }
                return InteractionResult.SUCCESS;
            } else {
                try {
                    return slabSupport == null ? i.getBlockState().use(i.getWorld(), player, hand, hit) : slabSupport.onBlockActivated(i.getBlockState(), i.getWorld(), pos, player, hand, hit);
                } catch (Exception e) {
                    return InteractionResult.PASS;
                }
            }
        }).orElse(InteractionResult.PASS);
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        BlockHitResult result = RayTraceUtil.rayTrace(player);
        if (result.getLocation() != null)
            getHalfState(world, pos, result.getLocation().y - pos.getY())
                    .ifPresent(i -> i.getBlockState().attack(i.getWorld(), pos, player));
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!getTile(world, pos).map(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null) {
                tile.getPositiveBlockInfo().getBlockState().getBlock().fallOn(tile.getPositiveBlockInfo().getWorld(), tile.getPositiveBlockInfo().getBlockState(), pos, entity, fallDistance);
                return true;
            }
            return false;
        }).orElse(false)) {
            super.fallOn(world, state, pos, entity, fallDistance);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter world, Entity entity) {
        BlockPos pos = new BlockPos(entity.position()).below();
        if (world.getBlockState(pos).getBlock() == this) {
            if (!getTile(world, pos).map(tile -> {
                if (tile.getPositiveBlockInfo().getBlockState() != null) {
                    tile.getPositiveBlockInfo().getBlockState().getBlock().updateEntityAfterFallOn(tile.getPositiveBlockInfo().getWorld(), entity);
                    return true;
                }
                return false;
            }).orElse(false)) {
                super.updateEntityAfterFallOn(world, entity);
            }
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                tile.getPositiveBlockInfo().getBlockState().getBlock().stepOn(tile.getPositiveBlockInfo().getWorld(), pos, tile.getPositiveBlockInfo().getBlockState(), entity);
        });
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                tile.getPositiveBlockInfo().getBlockState().entityInside(world, pos, entity);
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                tile.getNegativeBlockInfo().getBlockState().entityInside(world, pos, entity);
        });
    }

    @Override
    public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
        getHalfState(world, hit.getBlockPos(), hit.getLocation().y).ifPresent(i -> i.getBlockState().onProjectileHit(i.getWorld(), i.getBlockState(), hit, projectile));
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState().getSoundType(tile.getPositiveBlockInfo().getWorld(), pos, entity)).orElse(super.getSoundType(state, world, pos, entity));
    }

}
