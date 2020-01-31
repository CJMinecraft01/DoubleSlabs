package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDoubleSlab extends Block {

    public BlockDoubleSlab() {
        super(Properties.create(Material.ROCK));
        setRegistryName(DoubleSlabs.MODID, "double_slab");
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isSolid(BlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registrar.TILE_DOUBLE_SLAB.create();
    }


    public <T> T runOnDoubleSlab(BlockState state, IBlockReader world, BlockPos pos, Function<Pair<BlockState, BlockState>, T> func, Supplier<T> orElse) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityDoubleSlab) {
            BlockState topState = ((TileEntityDoubleSlab) te).getTopState();
            BlockState bottomState = ((TileEntityDoubleSlab) te).getBottomState();
            return func.apply(Pair.of(topState, bottomState));
        }

        return orElse.get();
    }

//    @Override
//    public Material getMaterial(BlockState state) {
//        return ((IExtendedBlockState) state).getValue(TOP) == null ? Material.ROCK : ((IExtendedBlockState) state).getValue(TOP).getMaterial();
//    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return runOnDoubleSlab(state, world, pos, (states) -> states.getLeft().getSoundType(), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getBlockHardness(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnDoubleSlab(state, world, pos, (states) -> Math.min(states.getLeft().getBlockHardness(world, pos), states.getRight().getBlockHardness(world, pos)), () -> super.getBlockHardness(state, world, pos));
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return runOnDoubleSlab(state, world, pos, (states) -> Math.min(states.getLeft().getExplosionResistance(world, pos, exploder, explosion), states.getRight().getExplosionResistance(world, pos, exploder, explosion)), () -> super.getExplosionResistance(state, world, pos, exploder, explosion));
    }

    //    @Override
//    public int getHarvestLevel(IBlockState state) {
//        IExtendedBlockState extendedState = (IExtendedBlockState) state;
//        return Math.min(extendedState.getValue(TOP).getBlock().getHarvestLevel(extendedState.getValue(TOP)), extendedState.getValue(BOTTOM).getBlock().getHarvestLevel(extendedState.getValue(BOTTOM)));
//    }

//    @Nullable
//    @Override
//    public String getHarvestTool(IBlockState state) {
//        // TODO use the correct harvest tool
//        return "pickaxe";
//    }

//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new ExtendedBlockState.Builder(this).add(TOP, BOTTOM).build();
//    }


//    @Override
//    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
//        if (state instanceof IExtendedBlockState) {
//            IExtendedBlockState extendedState = (IExtendedBlockState) state;
//            TileEntityDoubleSlab tile = (TileEntityDoubleSlab) world.getTileEntity(pos);
//            if (tile != null)
//                return extendedState.withProperty(TOP, tile.getTopState()).withProperty(BOTTOM, tile.getBottomState());
//        }
//        return state;
//    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return runOnDoubleSlab(state, world, pos, (states) -> target.getHitVec().y - pos.getY() > 0.5 ? states.getLeft().getPickBlock(target, world, pos, player) : states.getRight().getPickBlock(target, world, pos, player), () -> super.getPickBlock(state, target, world, pos, player));
    }

//    @Override
//    public void breakBlock(World world, BlockPos pos, IBlockState state) {
//        if (world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 6, false).isCreative()) {
//            super.breakBlock(world, pos, state);
//            return;
//        }
//        IExtendedBlockState extendedBlockState = ((IExtendedBlockState) getExtendedState(state, world, pos));
//        NonNullList<ItemStack> drops = NonNullList.create();
//        extendedBlockState.getValue(TOP).getBlock().getDrops(drops, world, pos, extendedBlockState.getValue(TOP), 0);
//        extendedBlockState.getValue(BOTTOM).getBlock().getDrops(drops, world, pos, extendedBlockState.getValue(BOTTOM), 0);
//        for (ItemStack stack : drops)
//            if (!stack.isEmpty())
//                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
//        super.breakBlock(world, pos, state);
//    }


    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        if (willHarvest)
            return true;
        return super.removedByPlayer(state, world, pos, player, false, fluid);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) builder.get(LootParameters.BLOCK_ENTITY);
        List<ItemStack> drops = new ArrayList<>();
        if (tile != null) {
            drops.addAll(tile.getTopState().getDrops(builder));
            drops.addAll(tile.getBottomState().getDrops(builder));
        }
        return drops;
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        world.removeTileEntity(pos);
    }

//    @Override
//    public boolean addLandingEffects(BlockState state1, ServerWorld world, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
//        return runOnDoubleSlab(state1, world, pos, (states) -> {
//            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
//            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
//            int numOfParticles = (int) (150.0D * d0);
//            world.spawnParticle(new RedstoneParticleData(0, 0, 0, 1), entity.posX, entity.posY, entity.posZ, numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
//            return true;
//        }, () -> false);
//    }

//    @Override
//    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
//        return runOnDoubleSlab(state, world, pos, (states) -> {
//            if (world.isRemote) {
//                world.part
//                world.spawnParticle(ParticleTypes.BLOCK_CRACK,
//                        entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
//                        entity.getEntityBoundingBox().minY + 0.1D,
//                        entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
//                        -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, Block.getStateId(extendedBlockState.getValue(TOP)));
//                return true;
//            }
//            return false;
//        }, () -> false);
//    }

    /* TODO add
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

//                    RayTraceResult result = rayTrace(Objects.requireNonNull(world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 6, false)), 6);
//                    Vec3d hitVec = result != null ? result.hitVec : null;
//                    if (hitVec != null) {
//                        hitVec = hitVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
//                        ParticleDigging particle = (ParticleDigging) factory.createParticle(0, world,
//                                (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
//                                d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, Block.getStateId(extendedBlockState.getValue(hitVec.y > 0.5 ? TOP : BOTTOM)));
//                        particle.setBlockPos(pos);
//                        manager.addEffect(particle);
//                    } else {
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
//                    }

//                    ParticleDigging particle2 = (ParticleDigging) factory.createParticle(0, world,
//                            (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2,
//                            d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, Block.getStateId(extendedBlockState.getValue(BOTTOM)));
//                    particle2.setBlockPos(pos);
//                    manager.addEffect(particle2);
                }
            }
        }
        return true;
    }*/

//    @Override
//    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
//        this.onBlockHarvested(world, pos, state, player);
//        return true;
//    }

//    @Override
//    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
//        IExtendedBlockState extendedBlockState = ((IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos));
//        NonNullList<ItemStack> drops = NonNullList.create();
//        extendedBlockState.getValue(TOP).getBlock().getDrops(drops, world, pos, extendedBlockState.getValue(TOP), 0);
//        extendedBlockState.getValue(BOTTOM).getBlock().getDrops(drops, world, pos, extendedBlockState.getValue(BOTTOM), 0);
//        for (ItemStack stack : drops)
//            if (!stack.isEmpty())
//                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
//        super.onBlockExploded(world, pos, explosion);
//    }
//
//    @Override
//    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
//        if (player instanceof FakePlayer)
//            return;
//        RayTraceResult mop = rayTrace(player, 6);
//        Vec3d hitVec = mop != null ? mop.hitVec : null;
//        if (hitVec != null)
//            hitVec = hitVec.add(-pos.getX(), -pos.getY(), -pos.getZ());
//        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state.getBlock().getExtendedState(state, world, pos);
//        IBlockState dropState;
//        IBlockState newState;
//        if (hitVec != null && hitVec.y < 0.5f) {
//            dropState = extendedBlockState.getValue(BlockDoubleSlab.BOTTOM);
//            newState = extendedBlockState.getValue(BlockDoubleSlab.TOP);
//        } else {
//            dropState = extendedBlockState.getValue(BlockDoubleSlab.TOP);
//            newState = extendedBlockState.getValue(BlockDoubleSlab.BOTTOM);
//        }
//
//        if (!world.isRemote && player.canHarvestBlock(state) && !player.isCreative()) {
//            Item slab = Item.getItemFromBlock(dropState.getBlock());
//            if (slab != Items.AIR)
//                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(slab, 1, dropState.getBlock().damageDropped(dropState)));
//        }
//        world.setBlockState(pos, newState, world.isRemote ? 11 : 3);
//    }
//
//    public static RayTraceResult rayTrace(EntityLivingBase entity, double length) {
//        Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
//        Vec3d endPos = startPos.add(entity.getLookVec().x * length, entity.getLookVec().y * length, entity.getLookVec().z * length);
//        return entity.world.rayTraceBlocks(startPos, endPos);
//    }
}
