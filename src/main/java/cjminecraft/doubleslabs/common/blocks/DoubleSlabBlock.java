package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.container.GuiHandler;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class DoubleSlabBlock extends DynamicSlabBlock {

    protected static Optional<IBlockInfo> getHalfState(IBlockAccess world, BlockPos pos, double y) {
        return getTile(world, pos).flatMap(tile -> tile.getNegativeBlockInfo().getBlockState() == null && tile.getPositiveBlockInfo().getBlockState() == null ? Optional.empty() :
                (y > 0.5 || tile.getNegativeBlockInfo().getBlockState() == null) && tile.getPositiveBlockInfo().getBlockState() != null ?
                        Optional.of(tile.getPositiveBlockInfo()) : Optional.of(tile.getNegativeBlockInfo()));
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3d hitVec = rayTraceResult.hitVec;
        if (hitVec == null)
            return minFloat(world, pos, i -> i.getBlockState().getPlayerRelativeBlockHardness(player, i.getWorld(), pos));
        return getHalfState(world, pos, hitVec.y - pos.getY())
                .map(i -> blockStrength(i.getBlockState(), player, world, pos))
                .orElseGet(() -> super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getHalfState(world, pos, target.hitVec.y - pos.getY()).map(i -> i.getBlockState().getBlock().getPickBlock(i.getBlockState(), target, i.getWorld(), i.getPos(), player)).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        return either(world, pos, i -> canHarvestBlock(i.getBlockState().getBlock(), player, i.getBlockState()));
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
        if (hitVec == null || te == null) {
            super.harvestBlock(world, player, pos, state, te, stack);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            world.removeTileEntity(pos);
        } else {
            SlabTileEntity tile = (SlabTileEntity) te;

            double y = hitVec.y - (double) pos.getY();

            IBlockInfo remainingBlock = y > 0.5 ? tile.getNegativeBlockInfo() : tile.getPositiveBlockInfo();
            IBlockInfo blockToRemove = y > 0.5 ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo();

            player.addStat(StatList.getBlockStats(blockToRemove.getBlockState().getBlock()));
            world.playEvent(2001, pos, Block.getStateId(blockToRemove.getBlockState()));
            player.addExhaustion(0.005F);

            if (!player.isCreative())
                blockToRemove.getBlockState().getBlock().harvestBlock(blockToRemove.getWorld(), player, blockToRemove.getPos(), blockToRemove.getBlockState(), blockToRemove.getTileEntity(), stack);

            if (!world.isRemote)
                blockToRemove.getBlockState().getBlock().breakBlock(blockToRemove.getWorld(), blockToRemove.getPos(), Blocks.AIR.getDefaultState());

            world.setBlockState(pos, remainingBlock.getBlockState(), Constants.BlockFlags.DEFAULT);
            world.setTileEntity(pos, remainingBlock.getTileEntity());
        }
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        return getTile(world, pos).map(tile -> {
            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            world.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numOfParticles,
                    0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(tile.getPositiveBlockInfo().getBlockState()));
            return true;
        }).orElse(false);
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            getTile(world, pos).ifPresent(tile -> {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                        entity.getEntityBoundingBox().minY + 0.1D,
                        entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                        -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, Block.getStateId(tile.getPositiveBlockInfo().getBlockState()));
            });
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            return getHalfState(world, target.getBlockPos(), target.hitVec.y).map(info -> {
                BlockPos pos = target.getBlockPos();
                EnumFacing side = target.sideHit;

                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();

                AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
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

                ParticleDigging.Factory factory = new ParticleDigging.Factory();
                ParticleDigging particle = (ParticleDigging) factory.createParticle(1, world, d0, d1, d2,
                        0.0D, 0.0D, 0.0D, Block.getStateId(info.getBlockState()));
                if (particle == null)
                    return false;
                particle.setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
                manager.addEffect(particle);

                return true;
            }).orElse(false);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private void createDestroyParticle(World world, BlockPos pos, ParticleManager manager, IBlockState state, ParticleDigging.Factory factory, double d0, double d1, double d2) {
        ParticleDigging particle = (ParticleDigging) factory.createParticle(0, world,
                (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
                d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, Block.getStateId(state));
        if (particle == null)
            return;
        particle.setBlockPos(pos);
        manager.addEffect(particle);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return getTile(world, pos).map(tile -> {
            ParticleDigging.Factory factory = new ParticleDigging.Factory();
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    for (int l = 0; l < 4; ++l) {
                        double d0 = ((double) j + 0.5D) / 4.0D;
                        double d1 = ((double) k + 0.5D) / 4.0D;
                        double d2 = ((double) l + 0.5D) / 4.0D;

                        if (tile.getPositiveBlockInfo().getBlockState() != null)
                            createDestroyParticle(world, pos, manager, tile.getPositiveBlockInfo().getBlockState(), factory, d0, d1, d2);
                        if (tile.getNegativeBlockInfo().getBlockState() != null)
                            createDestroyParticle(world, pos, manager, tile.getNegativeBlockInfo().getBlockState(), factory, d0, d1, d2);
                    }
                }
            }
            return true;
        }).orElse(false);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getBlock() != this)
            return false;
        return getHalfState(world, pos, hitY - pos.getY()).map(i -> {
            IContainerSupport containerSupport = ContainerSupport.getSupport(i.getWorld(), pos, i.getBlockState());
            ISlabSupport slabSupport = SlabSupport.getSlabSupport(world, pos, i.getBlockState());
            if (containerSupport != null) {
                if (!world.isRemote)
                    player.openGui(DoubleSlabs.instance, i.isPositive() ? GuiHandler.WRAPPER_POSITIVE : GuiHandler.WRAPPER_NEGATIVE, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            } else {
//            if (containerSupport != null && !world.isRemote)
//                    NetworkUtils.openGui((ServerPlayerEntity) player, containerSupport.getNamedContainerProvider(i.getWorld(), pos, state, player, hand, hit), pos, i.isPositive());
                try {
                    return slabSupport == null ? i.getBlockState().getBlock().onBlockActivated(i.getWorld(), i.getPos(), i.getBlockState(), player, hand, facing, hitX, hitY, hitZ) : slabSupport.onBlockActivated(i.getBlockState(), i.getWorld(), pos, player, hand, facing, hitX, hitY, hitZ);
                } catch (Exception e) {
                    return false;
                }
            }
        }).orElse(false);
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        RayTraceResult result = RayTraceUtil.rayTrace(player);
        if (result.hitVec != null)
            getHalfState(world, pos, result.hitVec.y - pos.getY()).ifPresent(i -> i.getBlockState().getBlock().onBlockClicked(i.getWorld(), i.getPos(), player));
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
    public void onLanded(World world, Entity entity) {
        BlockPos pos = new BlockPos(entity.getPositionVector()).down();
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
                tile.getPositiveBlockInfo().getBlockState().getBlock().onEntityWalk(world, pos, entity);
        });
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        getTile(world, pos).ifPresent(tile -> {
            if (tile.getPositiveBlockInfo().getBlockState() != null)
                tile.getPositiveBlockInfo().getBlockState().getBlock().onEntityCollision(world, pos, tile.getPositiveBlockInfo().getBlockState(), entity);
        });
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState().getBlock().getSoundType(tile.getPositiveBlockInfo().getBlockState(), tile.getPositiveBlockInfo().getWorld(), pos, entity)).orElseGet(() -> super.getSoundType(state, world, pos, entity));
    }

}
