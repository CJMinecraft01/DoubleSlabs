package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Utils;
import cjminecraft.doubleslabs.blocks.properties.UnlistedPropertyBlockState;
import cjminecraft.doubleslabs.client.model.DoubleSlabBakedModel;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDoubleSlab extends Block {
    public static final UnlistedPropertyBlockState TOP = new UnlistedPropertyBlockState();
    public static final UnlistedPropertyBlockState BOTTOM = new UnlistedPropertyBlockState();

    public BlockDoubleSlab() {
        super(Material.ROCK);
        setRegistryName(DoubleSlabs.MODID, "double_slab");
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    @Nonnull
//    public BlockRenderLayer getRenderLayer() {
//        return BlockRenderLayer.CUTOUT_MIPPED;
//    }

    public static boolean canHarvestBlock(@Nonnull Block block, @Nonnull EntityPlayer player, @Nonnull IBlockState state) {
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            String tool = block.getHarvestTool(state);
            if (!stack.isEmpty() && tool != null) {
                int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
                if (toolLevel < 0) {
                    return player.canHarvestBlock(state);
                } else {
                    return toolLevel >= block.getHarvestLevel(state);
                }
            } else {
                return player.canHarvestBlock(state);
            }
        }
    }

    public static float blockStrength(@Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F) {
            return 0.0F;
        } else {
            return !canHarvestBlock(state.getBlock(), player, state) ? player.getDigSpeed(state, pos) / hardness / 100.0F : player.getDigSpeed(state, pos) / hardness / 30.0F;
        }
    }

    private <T> T runOnDoubleSlab(IBlockState state, Function<Pair<IBlockState, IBlockState>, T> func, Supplier<T> orElse) {
        if (state.getBlock() != this)
            return orElse.get();
        if (state instanceof IExtendedBlockState) {
            IBlockState topState = ((IExtendedBlockState) state).getValue(TOP);
            IBlockState bottomState = ((IExtendedBlockState) state).getValue(BOTTOM);
            if (topState == null || bottomState == null)
                return orElse.get();
            return func.apply(Pair.of(topState, bottomState));
        }
        return orElse.get();
    }

    private <T> T runOnDoubleSlab(IBlockState state, IBlockAccess world, BlockPos pos, Function<Pair<IBlockState, IBlockState>, T> func, Supplier<T> orElse) {
        if (state.getBlock() != this)
            return orElse.get();
        IBlockState extendedState = getExtendedState(state, world, pos);
        return runOnDoubleSlab(extendedState, func, orElse);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return runOnDoubleSlab(state, states -> states.getLeft().isTopSolid(), () -> true);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return runOnDoubleSlab(state, world, pos, states -> {
            if (face == EnumFacing.UP)
                return states.getLeft().getBlockFaceShape(world, pos, EnumFacing.UP);
            if (face == EnumFacing.DOWN)
                return states.getRight().getBlockFaceShape(world, pos, EnumFacing.DOWN);
            BlockFaceShape topFace = states.getLeft().getBlockFaceShape(world, pos, EnumFacing.UP);
            BlockFaceShape bottomFace = states.getRight().getBlockFaceShape(world, pos, EnumFacing.DOWN);
            if (topFace == BlockFaceShape.SOLID && bottomFace == BlockFaceShape.SOLID)
                return BlockFaceShape.SOLID;
            return BlockFaceShape.UNDEFINED;
        }, () -> BlockFaceShape.SOLID);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState other = world.getBlockState(pos.offset(side));
        return runOnDoubleSlab(state, world, pos, states -> runOnDoubleSlab(other, world, pos.offset(side), otherStates -> {
            boolean selfTransparent = Utils.isTransparent(states.getLeft()) || Utils.isTransparent(states.getRight());
            boolean otherTransparent = Utils.isTransparent(otherStates.getLeft()) || Utils.isTransparent(otherStates.getRight());

            return selfTransparent ^ otherTransparent;
        }, () -> super.shouldSideBeRendered(state, world, pos, side)), () -> super.shouldSideBeRendered(state, world, pos, side));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return runOnDoubleSlab(state, world, pos, states -> states.getLeft().isNormalCube() && states.getRight().isNormalCube(), () -> true);
    }

    @Override
    public boolean canEntitySpawn(IBlockState state, Entity entity) {
        return runOnDoubleSlab(state, states -> states.getLeft().canEntitySpawn(entity), () -> true);
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    @Nonnull
    public Material getMaterial(IBlockState state) {
        return runOnDoubleSlab(state, states -> states.getLeft().getMaterial(), () -> Material.ROCK);
    }

    @Override
    @Nonnull
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return runOnDoubleSlab(state, states -> states.getLeft().getBlock().getSoundType(state, world, pos, entity), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return runOnDoubleSlab(state, states -> Math.max(states.getLeft().getAmbientOcclusionLightValue(), states.getRight().getAmbientOcclusionLightValue()), () -> super.getAmbientOcclusionLightValue(state));
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return runOnDoubleSlab(state, states -> states.getLeft().causesSuffocation() || states.getRight().causesSuffocation(), () -> true);
    }

    @Override
    @Nullable
    public String getHarvestTool(@Nonnull IBlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        return runOnDoubleSlab(world.getBlockState(pos), world, pos, states -> canHarvestBlock(states.getLeft().getBlock(), player, states.getLeft()) || canHarvestBlock(states.getRight().getBlock(), player, states.getRight()), () -> true);
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
        return runOnDoubleSlab(state, world, pos, states -> {
            RayTraceResult rayTraceResult = Utils.rayTrace(player);
            Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
            if (hitVec == null)
                return Math.min(blockStrength(states.getLeft(), player, world, pos), blockStrength(states.getRight(), player, world, pos));
            return (hitVec.y - pos.getY()) > 0.5 ? blockStrength(states.getLeft(), player, world, pos) : blockStrength(states.getRight(), player, world, pos);
        }, () -> super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return runOnDoubleSlab(state, world, pos, states -> Math.max(states.getLeft().getBlockHardness(world, pos), states.getRight().getBlockHardness(world, pos)), () -> super.getBlockHardness(state, world, pos));
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return runOnDoubleSlab(world.getBlockState(pos), world, pos, states -> Math.min(states.getLeft().getBlock().getExplosionResistance(world, pos, exploder, explosion), states.getRight().getBlock().getExplosionResistance(world, pos, exploder, explosion)), () -> super.getExplosionResistance(world, pos, exploder, explosion));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return runOnDoubleSlab(state, world, pos, states -> Math.max(states.getLeft().getLightValue(world, pos), states.getRight().getLightValue(world, pos)), () -> super.getLightValue(state, world, pos));
    }

//    @Override
//    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
//        return runOnDoubleSlab(state, world, pos, states -> Math.min(states.getLeft().getLightOpacity(world, pos), states.getRight().getLightOpacity(world, pos)), () -> super.getLightOpacity(state, world, pos));
//    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityDoubleSlab(((IExtendedBlockState) state).getValue(TOP), ((IExtendedBlockState) state).getValue(BOTTOM));
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState.Builder(this).add(TOP, BOTTOM).build();
    }

    @Override
    @Nonnull
    public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState actualState = getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityDoubleSlab && actualState instanceof IExtendedBlockState)
            return ((IExtendedBlockState) actualState).withProperty(TOP, ((TileEntityDoubleSlab) tile).getTopState()).withProperty(BOTTOM, ((TileEntityDoubleSlab) tile).getBottomState());
        return state;
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return runOnDoubleSlab(state, world, pos, states -> target.hitVec.y - pos.getY() > 0.5 ? states.getLeft().getBlock().getPickBlock(states.getLeft(), target, world, pos, player) : states.getRight().getBlock().getPickBlock(states.getRight(), target, world, pos, player), () -> ItemStack.EMPTY);
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        if (willHarvest)
            return true;
        return super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) world.getTileEntity(pos);
        if (tile != null) {
            tile.getTopState().getBlock().getDrops(drops, world, pos, tile.getTopState(), fortune);
            tile.getBottomState().getBlock().getDrops(drops, world, pos, tile.getBottomState(), fortune);
        }
    }

    @Override
    public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        RayTraceResult rayTraceResult = Utils.rayTrace(player);
        Vec3d hitVec = rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK ? rayTraceResult.hitVec : null;
        if (hitVec == null || te == null) {
            super.harvestBlock(world, player, pos, state, te, stack);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else {
            TileEntityDoubleSlab tile = (TileEntityDoubleSlab) te;

            double y = hitVec.y - (double) pos.getY();

            IBlockState remainingState = y > 0.5 ? tile.getBottomState() : tile.getTopState();
            IBlockState stateToRemove = y > 0.5 ? tile.getTopState() : tile.getBottomState();

            player.addStat(StatList.getBlockStats(stateToRemove.getBlock()));
            player.addExhaustion(0.005F);
            world.playEvent(2001, pos, Block.getStateId(stateToRemove));

            if (!player.isCreative())
                stateToRemove.getBlock().harvestBlock(world, player, pos, stateToRemove, null, stack);

            world.setBlockState(pos, remainingState, 11);
        }
        world.removeTileEntity(pos);
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        return runOnDoubleSlab(state, world, pos, states -> {
            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            world.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ, numOfParticles,
                    0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(states.getLeft()));
            return true;
        }, () -> true);
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            return runOnDoubleSlab(state, world, pos, states -> {
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                        entity.getEntityBoundingBox().minY + 0.1D,
                        entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.width,
                        -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, Block.getStateId(states.getLeft()));
                return true;
            }, () -> true);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return runOnDoubleSlab(state, world, target.getBlockPos(), states -> {
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
            IBlockState blockState = target.hitVec.y - pos.getY() > 0.5 ? states.getLeft() : states.getRight();
            if (blockState == null)
                return false;

            ParticleDigging.Factory factory = new ParticleDigging.Factory();
            ParticleDigging particle = (ParticleDigging) factory.createParticle(1, world, d0, d1, d2,
                    0.0D, 0.0D, 0.0D, Block.getStateId(blockState));
            if (particle == null)
                return false;
            particle.setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
            manager.addEffect(particle);

            return true;
        }, () -> false);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return runOnDoubleSlab(world.getBlockState(pos), world, pos, states -> {
            ParticleDigging.Factory factory = new ParticleDigging.Factory();
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    for (int l = 0; l < 4; ++l) {
                        double d0 = ((double) j + 0.5D) / 4.0D;
                        double d1 = ((double) k + 0.5D) / 4.0D;
                        double d2 = ((double) l + 0.5D) / 4.0D;

                        createDestroyParticle(world, pos, manager, states.getLeft(), factory, d0, d1, d2);
                        createDestroyParticle(world, pos, manager, states.getRight(), factory, d0, d1, d2);
                    }
                }
            }
            return true;
        }, () -> true);
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

    @SideOnly(Side.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> runOnDoubleSlab(state, world, pos, states -> {
            if (tintIndex < DoubleSlabBakedModel.TINT_OFFSET)
                return Minecraft.getMinecraft().getBlockColors().colorMultiplier(states.getLeft(), world, pos, tintIndex);
            return Minecraft.getMinecraft().getBlockColors().colorMultiplier(states.getRight(), world, pos, tintIndex - DoubleSlabBakedModel.TINT_OFFSET);
        }, () -> -1);
    }
}
