package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.blocks.properties.UnlistedPropertyBlockState;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockDoubleSlab extends Block {
    public static final UnlistedPropertyBlockState TOP = new UnlistedPropertyBlockState();
    public static final UnlistedPropertyBlockState BOTTOM = new UnlistedPropertyBlockState();

    public BlockDoubleSlab() {
        super(Material.ROCK);
        setRegistryName(DoubleSlabs.MODID, "double_slab");
//        setDefaultState(((IExtendedBlockState) this.getBlockState().getBaseState()).withProperty(TOP, Blocks.PURPUR_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)).withProperty(BOTTOM, Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK)));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
//        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
//        return extendedBlockState.getValue(TOP).isOpaqueCube() && extendedBlockState.getValue(BOTTOM).isOpaqueCube();
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedBlockState = ((IExtendedBlockState) getExtendedState(state, world, pos));
        return extendedBlockState.getValue(BOTTOM).isNormalCube() && extendedBlockState.getValue(TOP).isNormalCube();
    }

    @Override
    public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
        return ((IExtendedBlockState) state).getValue(TOP).canEntitySpawn(entityIn);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public Material getMaterial(IBlockState state) {
        return ((IExtendedBlockState) state).getValue(TOP) == null ? Material.ROCK : ((IExtendedBlockState) state).getValue(TOP).getMaterial();
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        IExtendedBlockState extendedState = ((IExtendedBlockState) getExtendedState(state, world, pos));
        return extendedState.getValue(TOP).getBlock().getSoundType(extendedState.getValue(TOP), world, pos, entity);
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        if (extendedBlockState == null || extendedBlockState.getValue(TOP) == null)
            return super.getAmbientOcclusionLightValue(state);
        return Math.max(extendedBlockState.getValue(TOP).getAmbientOcclusionLightValue(), extendedBlockState.getValue(BOTTOM).getAmbientOcclusionLightValue());
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        if (extendedBlockState == null || extendedBlockState.getValue(TOP) == null)
            return true;
        return extendedBlockState.getValue(TOP).causesSuffocation() || extendedBlockState.getValue(BOTTOM).causesSuffocation();
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
        return state.getValue(TOP).getBlock().canHarvestBlock(world, pos, player) || state.getValue(BOTTOM).getBlock().canHarvestBlock(world, pos, player);
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
        return Math.min(extendedBlockState.getValue(TOP).getPlayerRelativeBlockHardness(player, world, pos), extendedBlockState.getValue(BOTTOM).getPlayerRelativeBlockHardness(player, world, pos));
    }

//    @Override
//    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
//        IExtendedBlockState extendedState = ((IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos));
//        return Math.min(extendedState.getValue(TOP).getBlockHardness(world, pos), extendedState.getValue(BOTTOM).getBlockHardness(world, pos));
//    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        IExtendedBlockState extendedState = ((IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos));
        return Math.min(extendedState.getValue(TOP).getBlock().getExplosionResistance(world, pos, exploder, explosion), extendedState.getValue(BOTTOM).getBlock().getExplosionResistance(world, pos, exploder, explosion));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityDoubleSlab(((IExtendedBlockState) state).getValue(TOP), ((IExtendedBlockState) state).getValue(BOTTOM));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState.Builder(this).add(TOP, BOTTOM).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            TileEntityDoubleSlab tile = (TileEntityDoubleSlab) world.getTileEntity(pos);
            if (tile != null)
                return extendedState.withProperty(TOP, tile.getTopState()).withProperty(BOTTOM, tile.getBottomState());
        }
        return state;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        IExtendedBlockState extendedBlockState = ((IExtendedBlockState) getExtendedState(state, world, pos));
        if (target.hitVec.y - pos.getY() > 0.5)
            return extendedBlockState.getValue(TOP) != null ? extendedBlockState.getValue(TOP).getBlock().getPickBlock(extendedBlockState.getValue(TOP), target, world, pos, player) : ItemStack.EMPTY;
        return extendedBlockState.getValue(BOTTOM) != null ? extendedBlockState.getValue(BOTTOM).getBlock().getPickBlock(extendedBlockState.getValue(BOTTOM), target, world, pos, player) : ItemStack.EMPTY;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest)
            return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) world.getTileEntity(pos);
        if (tile != null) {
            tile.getTopState().getBlock().getDrops(drops, world, pos, tile.getTopState(), fortune);
            tile.getBottomState().getBlock().getDrops(drops, world, pos, tile.getBottomState(), fortune);
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        IExtendedBlockState extendedBlockState = ((IExtendedBlockState) getExtendedState(state, world, pos));
        float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
        double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
        int numOfParticles = (int) (150.0D * d0);
        world.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numOfParticles,
                0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(extendedBlockState.getValue(TOP)));
        return true;
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            IExtendedBlockState extendedBlockState = ((IExtendedBlockState) getExtendedState(state, world, pos));
            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                    entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                    entity.getEntityBoundingBox().minY + 0.1D,
                    entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                    -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, Block.getStateId(extendedBlockState.getValue(TOP)));
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        BlockPos pos = target.getBlockPos();
        EnumFacing side = target.sideHit;
        IExtendedBlockState extendedBlockState = ((IExtendedBlockState) getExtendedState(state, world, pos));

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
                0.0D, 0.0D, 0.0D, Block.getStateId(target.hitVec.y - pos.getY() > 0.5 ? extendedBlockState.getValue(TOP) : extendedBlockState.getValue(BOTTOM)));
        particle.setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
        manager.addEffect(particle);

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
        ParticleDigging.Factory factory = new ParticleDigging.Factory();
        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 4; ++k) {
                for (int l = 0; l < 4; ++l) {
                    double d0 = ((double) j + 0.5D) / 4.0D;
                    double d1 = ((double) k + 0.5D) / 4.0D;
                    double d2 = ((double) l + 0.5D) / 4.0D;

                    ParticleDigging particle1 = (ParticleDigging) factory.createParticle(0, world,
                            (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
                            d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, Block.getStateId(extendedBlockState.getValue(TOP)));
                    particle1.setBlockPos(pos);
                    manager.addEffect(particle1);

                    ParticleDigging particle2 = (ParticleDigging) factory.createParticle(0, world,
                            (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
                            d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, Block.getStateId(extendedBlockState.getValue(BOTTOM)));
                    particle2.setBlockPos(pos);
                    manager.addEffect(particle2);
                }
            }
        }
        return true;
    }

}
