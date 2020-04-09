package cjminecraft.doubleslabs.blocks;

import cjminecraft.doubleslabs.DoubleSlabs;
import cjminecraft.doubleslabs.Registrar;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.FallingDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
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
    public boolean func_220074_n(BlockState state) {
        return false;
    }

    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
//        return layer == BlockRenderLayer.SOLID;
//    }


//    @Override
//    public boolean isSolid(BlockState state) {
//        return true;
//    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registrar.TILE_DOUBLE_SLAB.create();
    }

    public <T> T runOnDoubleSlab(IBlockReader world, BlockPos pos, Function<Pair<BlockState, BlockState>, T> func, Supplier<T> orElse) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityDoubleSlab) {
            BlockState topState = ((TileEntityDoubleSlab) te).getTopState();
            BlockState bottomState = ((TileEntityDoubleSlab) te).getBottomState();
            if (topState == null || bottomState == null)
                return orElse.get();
            return func.apply(Pair.of(topState, bottomState));
        }

        return orElse.get();
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().isNormalCube(world, pos) && states.getRight().isNormalCube(world, pos), () -> true);
    }

    @Override
    public boolean canEntitySpawn(BlockState state, IBlockReader world, BlockPos pos, EntityType<?> type) {
        return runOnDoubleSlab(world, pos, (states) -> states.getRight().canEntitySpawn(world, pos, type), () -> true);
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().getSoundType(), () -> super.getSoundType(state, world, pos, entity));
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return runOnDoubleSlab(world, pos, (states) -> Math.min(states.getLeft().getExplosionResistance(world, pos, exploder, explosion), states.getRight().getExplosionResistance(world, pos, exploder, explosion)), () -> super.getExplosionResistance(state, world, pos, exploder, explosion));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().propagatesSkylightDown(world, pos) && states.getRight().propagatesSkylightDown(world, pos), () -> false);
    }

    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnDoubleSlab(world, pos, (states) -> Math.max(states.getLeft().getAmbientOcclusionLightValue(world, pos), states.getRight().getAmbientOcclusionLightValue(world, pos)), () -> super.getAmbientOcclusionLightValue(state, world, pos));
    }

    @Override
    public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos) {
        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().with(SlabBlock.TYPE, SlabType.DOUBLE).causesSuffocation(world, pos) || states.getRight().with(SlabBlock.TYPE, SlabType.DOUBLE).causesSuffocation(world, pos), () -> true);
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return null;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return runOnDoubleSlab(world, pos, (states) -> states.getLeft().canHarvestBlock(world, pos, player) || states.getRight().canHarvestBlock(world, pos, player), () -> false);
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        return runOnDoubleSlab(world, pos, (states) -> Math.min(states.getLeft().getPlayerRelativeBlockHardness(player, world, pos), states.getRight().getPlayerRelativeBlockHardness(player, world, pos)), () -> super.getPlayerRelativeBlockHardness(state, player, world, pos));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return runOnDoubleSlab(world, pos, (states) -> target.getHitVec().y - pos.getY() > 0.5 ? states.getLeft().getPickBlock(target, world, pos, player) : states.getRight().getPickBlock(target, world, pos, player), () -> super.getPickBlock(state, target, world, pos, player));
    }

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

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return runOnDoubleSlab(worldserver, pos, (states) -> {
            float f = (float) MathHelper.ceil(entity.fallDistance - 3.0F);
            double d0 = Math.min((0.2F + f / 15.0F), 2.5D);
            int numOfParticles = (int) (150.0D * d0);
            worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()), entity.posX, entity.posY, entity.posZ, numOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            return true;
        }, () -> false);
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            runOnDoubleSlab(world, pos, (states) -> {
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()),
                        entity.posX + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                        entity.getBoundingBox().minY + 0.1D,
                        entity.posZ + ((double) world.rand.nextFloat() - 0.5D) * (double) entity.getWidth(),
                        -entity.getMotion().x * 4.0D, 1.5D, -entity.getMotion().z * 4.0D);
                return null;
            }, () -> null);
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult) target;
            return runOnDoubleSlab(world, result.getPos(), (states) -> {
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

                Particle particle = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, target.getHitVec().y > 0.5 ? states.getLeft() : states.getRight()), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                if (particle != null) {
                    ((DiggingParticle) particle).setBlockPos(pos);
                    particle = particle.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
                    manager.addEffect(particle);
                    return true;
                }

                return false;
            }, () -> false);
        }
        return false;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        return runOnDoubleSlab(world, pos, (states) -> {
            DiggingParticle.Factory factory = new DiggingParticle.Factory();
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        double d0 = ((double) j + 0.5D) / 4.0D + pos.getX();
                        double d1 = ((double) k + 0.5D) / 4.0D + pos.getY();
                        double d2 = ((double) l + 0.5D) / 4.0D + pos.getZ();

                        Particle particle1 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getLeft()), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                        if (particle1 != null)
                            manager.addEffect(particle1);

                        Particle particle2 = factory.makeParticle(new BlockParticleData(ParticleTypes.BLOCK, states.getRight()), world, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                        if (particle2 != null)
                            manager.addEffect(particle2);
                    }
                }
            }
            return true;
        }, () -> false);
    }

    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        return (state, world, pos, tintIndex) -> {
            if (world == null || pos == null)
                return -1;
            return runOnDoubleSlab(world, pos, (states) -> {
                int colourTop = Minecraft.getInstance().getBlockColors().getColor(states.getLeft(), world, pos, tintIndex);
                int colourBottom = Minecraft.getInstance().getBlockColors().getColor(states.getRight(), world, pos, tintIndex);
                if (colourTop < 0)
                    return colourBottom;
                if (colourBottom < 0)
                    return colourTop;
                return (colourBottom + colourTop) / 2;
            }, () -> -1);
        };
    }

}
