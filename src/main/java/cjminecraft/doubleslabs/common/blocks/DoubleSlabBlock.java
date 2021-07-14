package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.container.WrappedContainer;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public class DoubleSlabBlock extends DynamicSlabBlock {

    protected static Optional<IBlockInfo> getHalfState(IBlockReader world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeBlockInfo().getBlockState() == null && tile.getPositiveBlockInfo().getBlockState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeBlockInfo().getBlockState() == null) && tile.getPositiveBlockInfo().getBlockState() != null ?
                        Optional.of(tile.getPositiveBlockInfo()) : Optional.of(tile.getNegativeBlockInfo()));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return both(world, pos, i -> i.getBlockState().propagatesSkylightDown(i.getWorld(), pos));
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
        if (hitVec == null)
            return minFloat(world, pos, i -> i.getBlockState().getPlayerRelativeBlockHardness(player, i.getWorld(), pos));
        return getHalfState(world, pos, hitVec.y - pos.getY())
                .map(i -> i.getBlockState().getPlayerRelativeBlockHardness(player, i.getWorld(), pos))
                .orElse(super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return getHalfState(world, pos, target.getHitVec().y - pos.getY()).map(i -> i.getBlockState().getPickBlock(target, i.getWorld(), pos, player)).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        BlockRayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3d hitVec = rayTraceResult.getHitVec();
        return getHalfState(world, pos, hitVec.y - pos.getY()).map(i -> i.getBlockState().canHarvestBlock(i.getWorld(), i.getPos(), player)).orElse(false);
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3d hitVec = rayTraceResult.getType() == RayTraceResult.Type.BLOCK ? rayTraceResult.getHitVec() : null;
        if (hitVec == null || te == null) {
            super.harvestBlock(world, player, pos, state, te, stack);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            world.removeTileEntity(pos);
        } else {
            SlabTileEntity tile = (SlabTileEntity) te;

            double y = hitVec.y - (double) pos.getY();

            IBlockInfo remainingBlock = y > 0.5 ? tile.getNegativeBlockInfo() : tile.getPositiveBlockInfo();
            IBlockInfo blockToRemove = y > 0.5 ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo();

            player.addStat(Stats.BLOCK_MINED.get(blockToRemove.getBlockState().getBlock()));
            world.playEvent(2001, pos, Block.getStateId(blockToRemove.getBlockState()));
            player.addExhaustion(0.005F);

            if (!player.abilities.isCreativeMode)
                spawnDrops(blockToRemove.getBlockState(), world, pos, null, player, stack);

            blockToRemove.getBlockState().onReplaced(blockToRemove.getWorld(), pos, Blocks.AIR.getDefaultState(), false);

            world.setBlockState(pos, remainingBlock.getBlockState(), Constants.BlockFlags.DEFAULT);
            world.setTileEntity(pos, remainingBlock.getTileEntity());
        }
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return getTile(worldserver, pos).map(tile -> {
            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getPositiveBlockInfo().getBlockState()), entity.posX, entity.posY, entity.posZ, numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            getTile(world, pos).ifPresent(tile -> {
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, tile.getPositiveBlockInfo().getBlockState()),
                        entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                        entity.getBoundingBox().minY + 0.1D,
                        entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                        -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D);
            });
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult) target;
            return getHalfState(world, result.getPos(), target.getHitVec().y).map(info -> {
                BlockPos pos = result.getPos();
                Direction side = result.getFace();
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();

                AxisAlignedBB axisalignedbb = state.getCollisionShape(world, pos).getBoundingBox();
                double d0 = (double) i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
                double d1 = (double) j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
                double d2 = (double) k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

                switch (side) {
                    case DOWN:
                        d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
                        break;
                    case UP:
                        d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
                        break;
                    case NORTH:
                        d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
                        break;
                    case SOUTH:
                        d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
                        break;
                    case WEST:
                        d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
                        break;
                    case EAST:
                        d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
                }

                DiggingParticle.Factory factory = new DiggingParticle.Factory();

                Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, info.getBlockState()), (ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                if (particle != null) {
                    ((DiggingParticle) particle).setBlockPos(pos);
                    particle = particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
                    manager.addEffect(particle);
                    return true;
                }

                return false;
            }).orElse(false);
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return getTile(world, pos).map(tile -> {
            DiggingParticle.Factory factory = new DiggingParticle.Factory();
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        double d0 = ((double) j + 0.5D) / 4.0D + pos.getX();
                        double d1 = ((double) k + 0.5D) / 4.0D + pos.getY();
                        double d2 = ((double) l + 0.5D) / 4.0D + pos.getZ();

                        runIfAvailable(world, pos, i -> {
                            Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, i.getBlockState()), (ClientWorld) world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                            if (particle != null)
                                manager.addEffect(particle);
                        });
                    }
                }
            }
            return true;
        }).orElse(false);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (state.getBlock() != this)
            return false;
        return getHalfState(world, pos, hit.getHitVec().y - pos.getY()).map(i -> {
            IContainerSupport containerSupport = ContainerSupport.getSupport(i.getWorld(), pos, i.getBlockState());
            ISlabSupport slabSupport = SlabSupport.getSlabSupport(world, pos, i.getBlockState());
            if (containerSupport != null) {
                if (!world.isRemote) {
                    INamedContainerProvider provider = containerSupport.getNamedContainerProvider(i.getWorld(), pos, state, player, hand, hit);
                    NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                        @Override
                        public ITextComponent getDisplayName() {
                            return provider.getDisplayName();
                        }

                        @Override
                        public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
                            return new WrappedContainer(windowId, playerInventory, player, provider, i);
                        }
                    }, buffer -> {
                        buffer.writeBlockPos(i.getPos());
                        buffer.writeBoolean(i.isPositive());
                        buffer.writeResourceLocation(containerSupport.getContainer(i.getWorld(), pos, state).getRegistryName());
                        containerSupport.writeExtraData(world, pos, state).accept(buffer);
                    });
                }
                return true;
            } else {
//            if (containerSupport != null && !world.isRemote)
//                    NetworkUtils.openGui((ServerPlayerEntity) player, containerSupport.getNamedContainerProvider(i.getWorld(), pos, state, player, hand, hit), pos, i.isPositive());
                try {
                    return slabSupport == null ? i.getBlockState().onBlockActivated(i.getWorld(), player, hand, hit) : slabSupport.onBlockActivated(i.getBlockState(), i.getWorld(), pos, player, hand, hit);
                } catch (Exception e) {
                    return false;
                }
            }
        }).orElse(false);
    }

    @Override
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        BlockRayTraceResult result = RayTraceUtil.rayTrace(player);
        if (result.getHitVec() != null)
            getHalfState(world, pos, result.getHitVec().y - pos.getY())
                    .ifPresent(pair -> pair.getBlockState().onBlockClicked(pair.getWorld(), pos, player));
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        if (!getTile(world, pos).map(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null) {
                tile.getPositiveBlockInfo().getBlockState().getBlock().onFallenUpon(tile.getPositiveBlockInfo().getWorld(), pos, entity, fallDistance);
                return true;
            }
            return false;
        }).orElse(false)) {
            super.onFallenUpon(world, pos, entity, fallDistance);
        }
    }

    @Override
    public void onLanded(IBlockReader world, Entity entity) {
        BlockPos pos = new BlockPos(entity.getPositionVec()).down();
        if (world.getBlockState(pos).getBlock() == this) {
            if (!getTile(world, pos).map(tile -> {
                if (tile.getPositiveBlockInfo().getBlockState() != null) {
                    tile.getPositiveBlockInfo().getBlockState().getBlock().onLanded(tile.getPositiveBlockInfo().getWorld(), entity);
                    return true;
                }
                return false;
            }).orElse(false)) {
                super.onLanded(world, entity);
            }
        }
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                tile.getPositiveBlockInfo().getBlockState().getBlock().onEntityWalk(tile.getPositiveBlockInfo().getWorld(), pos, entity);
        });
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                tile.getPositiveBlockInfo().getBlockState().onEntityCollision(world, pos, entity);
            if (tile.getNegativeBlockInfo().getBlockState() != null)
                tile.getNegativeBlockInfo().getBlockState().onEntityCollision(world, pos, entity);
        });
    }

    @Override
    public void onProjectileCollision(World world, BlockState state, BlockRayTraceResult hit, Entity projectile) {
        getHalfState(world, hit.getPos(), hit.getHitVec().y).ifPresent(i -> i.getBlockState().onProjectileCollision(i.getWorld(), i.getBlockState(), hit, projectile));
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState().getSoundType(tile.getPositiveBlockInfo().getWorld(), pos, entity)).orElse(super.getSoundType(state, world, pos, entity));
    }
}
