package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.IBlockInfo;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.containers.IContainerSupport;
import cjminecraft.doubleslabs.api.support.ISlabSupport;
import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.properties.UnlistedPropertyBoolean;
import cjminecraft.doubleslabs.common.container.GuiHandler;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.common.util.RayTraceUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
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
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Optional;

public class VerticalSlabBlock extends DynamicSlabBlock {

    public static final UnlistedPropertyBoolean ROTATE_POSITIVE = new UnlistedPropertyBoolean();
    public static final UnlistedPropertyBoolean ROTATE_NEGATIVE = new UnlistedPropertyBoolean();
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool DOUBLE = PropertyBool.create("double");

    public VerticalSlabBlock() {
        super();
        setDefaultState(this.getBlockState().getBaseState().withProperty(DOUBLE, false).withProperty(FACING, EnumFacing.NORTH));
    }

    protected static Optional<IBlockInfo> getHalfState(IBlockAccess world, BlockPos pos, double x, double z) {
        IBlockState state = world.getBlockState(pos);

        return getTile(world, pos).flatMap(tile -> tile.getPositiveBlockInfo().getBlockState() == null && tile.getNegativeBlockInfo().getBlockState() == null ? Optional.empty() :
                ((state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ?
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) > 0.5 :
                        (state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? x : z) < 0.5)
                        || tile.getNegativeBlockInfo().getBlockState() == null) && tile.getPositiveBlockInfo().getBlockState() != null ?
                        Optional.of(tile.getPositiveBlockInfo()) : Optional.of(tile.getNegativeBlockInfo()));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(POSITIVE_BLOCK, NEGATIVE_BLOCK, CULL_INFO, ROTATE_POSITIVE, ROTATE_NEGATIVE).add(FACING, DOUBLE).build();
    }

    private boolean rotateModel(IBlockInfo info) {
        if (info.getBlockState() == null)
            return false;
        IVerticalSlabSupport support = SlabSupport.getVerticalSlabSupport(info.getWorld(), info.getPos(), info.getBlockState());
        if (support != null)
            return support.rotateModel(info.getWorld(), info.getPos(), info.getBlockState());
        return true;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState superState = super.getExtendedState(state, world, pos);
        if (superState instanceof IExtendedBlockState) {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState) superState;
            return getTile(world, pos).map(tile -> extendedBlockState.withProperty(ROTATE_POSITIVE, rotateModel(tile.getPositiveBlockInfo())).withProperty(ROTATE_NEGATIVE, rotateModel(tile.getNegativeBlockInfo()))).orElse(extendedBlockState);
        }
        return superState;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() == null || tile.getNegativeBlockInfo().getBlockState() == null).orElse(false))
            return 0;
        return min(world, pos, i -> i.getBlockState().getLightOpacity(i.getWorld(), i.getPos()));
    }

    //    @Override
//    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
//        boolean positive = getTile(world, pos).map(tile -> tile.getPositiveBlockInfo().getBlockState() != null).orElse(true);
//        return !state.getValue(DOUBLE) && (face == (positive ? state.getValue(FACING).getOpposite() : state.getValue(FACING))) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
//    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DOUBLE, meta >= 4).withProperty(FACING, EnumFacing.byHorizontalIndex(meta % 4));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DOUBLE) ? state.getValue(FACING).getHorizontalIndex() + 4 : state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return state.getValue(DOUBLE);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(DOUBLE);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(DOUBLE);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this)
            return state.withProperty(DOUBLE, true);
        if (facing.getAxis().isVertical()) {
            Vec3d vec = new Vec3d(hitX, hitY, hitZ).subtract(0.5, 0, 0.5);
            double angle = Math.atan2(vec.x, vec.z) * -180 / Math.PI;
            return this.getDefaultState().withProperty(FACING, EnumFacing.fromAngle(angle));
        }
        float value = placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.X ? hitZ : hitX;
        if (value > 0.25 && value < 0.75)
            return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
        boolean positive = placer.getHorizontalFacing().getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? value > 0.5d : value < 0.5d;
        if (placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.Z)
            positive = !positive;

        return this.getDefaultState().withProperty(FACING, positive ? facing.rotateYCCW() : facing.rotateY());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(DOUBLE))
            return FULL_BLOCK_AABB;

        TileEntity tileEntity = world.getTileEntity(pos);

        double min = 0;
        double max = 8;
        if (state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
            min = 8;
            max = 16;
        }

        if (tileEntity instanceof SlabTileEntity) {
            SlabTileEntity tile = (SlabTileEntity) tileEntity;

            boolean positive = tile.getPositiveBlockInfo().getBlockState() != null;
            boolean negative = tile.getNegativeBlockInfo().getBlockState() != null;

            if ((state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE && positive) || (state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && negative)) {
                min = 8;
                max = 16;
            } else {
                min = 0;
                max = 8;
            }
        }

        if (state.getValue(FACING).getAxis() == EnumFacing.Axis.X)
            return new AxisAlignedBB(min / 16, 0, 0, max / 16, 1, 1);
        else
            return new AxisAlignedBB(0, 0, min / 16, 1, 1, max / 16);
    }

    // todo get block face shape

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(player);
        Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
        if (hitVec == null)
            return minFloat(world, pos, i -> blockStrength(i.getBlockState(), player, i.getWorld(), pos));
        return getHalfState(world, pos, hitVec.x - pos.getX(), hitVec.z - pos.getZ())
                .map(i -> blockStrength(i.getBlockState(), player, i.getWorld(), pos))
                .orElse(super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getHalfState(world, pos, target.hitVec.x - pos.getX(), target.hitVec.z - pos.getZ())
                .map(i -> i.getBlockState().getBlock().getPickBlock(i.getBlockState(), target, i.getWorld(), pos, player)).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest)
            return true;
        if (player.isCreative() && player.isSneaking() && state.getValue(DOUBLE)) {
            harvestBlock(world, player, pos, state, world.getTileEntity(pos), ItemStack.EMPTY);
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, false);
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
            if (state.getValue(DOUBLE)) {
                SlabTileEntity tile = (SlabTileEntity) te;

                double distance = state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? hitVec.x - (double) pos.getX() : hitVec.z - (double) pos.getZ();

                boolean positive = state.getValue(FACING).getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? distance > 0.5 : distance < 0.5;

                IBlockInfo blockToRemove = positive ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo();
//                IBlockState stateToRemove = positive ? tile.getPositiveBlockInfo().getBlockState() : tile.getNegativeBlockInfo().getBlockState();

                player.addStat(StatList.getBlockStats(blockToRemove.getBlockState().getBlock()));
                world.playEvent(2001, pos, Block.getStateId(blockToRemove.getBlockState()));
                player.addExhaustion(0.005F);

                if (!player.isCreative())
                    blockToRemove.getBlockState().getBlock().harvestBlock(blockToRemove.getWorld(), player, blockToRemove.getPos(), blockToRemove.getBlockState(), blockToRemove.getTileEntity(), stack);
                if (!world.isRemote)
                    blockToRemove.getBlockState().getBlock().breakBlock(blockToRemove.getWorld(), blockToRemove.getPos(), blockToRemove.getBlockState());

                blockToRemove.setBlockState(null);
//                if (positive)
//                    tile.getPositiveBlockInfo().setBlockState(null);
//                else
//                    tile.getNegativeBlockInfo().setBlockState(null);

                world.setBlockState(pos, getExtendedState(state.withProperty(DOUBLE, false), world, pos), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                world.checkLight(pos);
            } else {
                SlabTileEntity tile = (SlabTileEntity) te;
                boolean positive = tile.getPositiveBlockInfo().getBlockState() != null;
                IBlockInfo blockToRemove = positive ? tile.getPositiveBlockInfo() : tile.getNegativeBlockInfo();
//                IBlockState remainingState = positive ? tile.getPositiveBlockInfo().getBlockState() : tile.getNegativeBlockInfo().getBlockState();
                player.addStat(StatList.getBlockStats(blockToRemove.getBlockState().getBlock()));
                world.playEvent(2001, pos, Block.getStateId(blockToRemove.getBlockState()));
                player.addExhaustion(0.005F);

                if (!player.isCreative())
                    blockToRemove.getBlockState().getBlock().harvestBlock(blockToRemove.getWorld(), player, blockToRemove.getPos(), blockToRemove.getBlockState(), blockToRemove.getTileEntity(), stack);
                if (!world.isRemote)
                    blockToRemove.getBlockState().getBlock().breakBlock(blockToRemove.getWorld(), blockToRemove.getPos(), blockToRemove.getBlockState());

                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                world.removeTileEntity(pos);
            }
        }
    }

    @Override
    public boolean addLandingEffects(IBlockState state1, WorldServer worldServer, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
        double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
        final int numOfParticles = state1.getValue(DOUBLE) ? (int) (75.0D * d0) : (int) (150.0D * d0);
        runIfAvailable(worldServer, pos, i -> worldServer.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(i.getBlockState())));
        return true;
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            runIfAvailable(world, pos, i -> world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                    entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                    entity.getEntityBoundingBox().minY + 0.1D,
                    entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                    -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, Block.getStateId(i.getBlockState())));
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            return getHalfState(world, target.getBlockPos(), target.hitVec.x, target.hitVec.z).map(info -> {
                BlockPos pos = target.getBlockPos();
                EnumFacing side = target.sideHit;

                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();

                AxisAlignedBB axisalignedbb = state.getCollisionBoundingBox(world, pos);
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

                Particle particle = factory.createParticle(1, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(info.getBlockState()));
                if (particle != null) {
                    ((ParticleDigging) particle).setBlockPos(pos);
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
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return getTile(world, pos).map(tile -> {
            ParticleDigging.Factory factory = new ParticleDigging.Factory();
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        double d0 = ((double) j + 0.5D) / 4.0D + pos.getX();
                        double d1 = ((double) k + 0.5D) / 4.0D + pos.getY();
                        double d2 = ((double) l + 0.5D) / 4.0D + pos.getZ();

                        if (tile.getPositiveBlockInfo().getBlockState() != null) {
                            Particle particle1 = factory.createParticle(1, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(tile.getPositiveBlockInfo().getBlockState()));
                            if (particle1 != null)
                                manager.addEffect(particle1);
                        }

                        if (tile.getNegativeBlockInfo().getBlockState() != null) {
                            Particle particle2 = factory.createParticle(1, world, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(tile.getNegativeBlockInfo().getBlockState()));
                            if (particle2 != null)
                                manager.addEffect(particle2);
                        }
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
        return getHalfState(world, pos, hitX, hitZ).map(i -> {
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
            getHalfState(world, pos, result.hitVec.x - pos.getX(), result.hitVec.z - pos.getZ()).ifPresent(i -> i.getBlockState().getBlock().onBlockClicked(i.getWorld(), i.getPos(), player));
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        if (!getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(i -> {
            i.getBlockState().getBlock().onFallenUpon(i.getWorld(), pos, entity, fallDistance);
            return true;
        }).orElse(false))
            super.onFallenUpon(world, pos, entity, fallDistance);
    }

    @Override
    public void onLanded(World world, Entity entity) {
        BlockPos pos = new BlockPos(entity.getPositionVector()).down();
        if (world.getBlockState(pos).getBlock() == this)
            if (!getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(i -> {
                i.getBlockState().getBlock().onLanded(i.getWorld(), entity);
                return true;
            }).orElse(false))
                super.onLanded(world, entity);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(i -> {
            i.getBlockState().getBlock().onEntityWalk(i.getWorld(), pos, entity);
            return true;
        }).orElse(false))
            super.onEntityWalk(world, pos, entity);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).ifPresent(i -> i.getBlockState().getBlock().onEntityCollision(i.getWorld(), i.getPos(), i.getBlockState(), entity));
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (entity != null)
            return getHalfState(world, pos, entity.posX - pos.getX(), entity.posZ - pos.getZ()).map(i -> i.getBlockState().getBlock().getSoundType(i.getBlockState(), i.getWorld(), i.getPos(), entity)).orElseGet(() -> super.getSoundType(state, world, pos, entity));
        return getAvailable(world, pos).map(i -> i.getBlockState().getBlock().getSoundType(i.getBlockState(), i.getWorld(), i.getPos(), null)).orElseGet(() -> super.getSoundType(state, world, pos, null));
    }

    @Override
    public void fillWithRain(World world, BlockPos pos) {
        runIfAvailable(world, pos, i -> i.getBlockState().getBlock().fillWithRain(i.getWorld(), i.getPos()));
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return both(world, pos, i -> i.getBlockState().getBlock().canSustainPlant(i.getBlockState(), i.getWorld(), i.getPos(), direction, plantable));
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return either(world, pos, i -> canHarvestBlock(i.getBlockState().getBlock(), player, i.getBlockState()));
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return both(state, i -> i.getBlockState().causesSuffocation());
    }
}
