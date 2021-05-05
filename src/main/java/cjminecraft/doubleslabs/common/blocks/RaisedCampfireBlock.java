package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RaisedCampfireBlock extends CampfireBlock {

    private static final VoxelShape NEW_SHAPE = SHAPE.withOffset(0, 0.5d, 0);

    private final Block parent;

    public RaisedCampfireBlock(Block parent, boolean smokey, int fireDamage, Properties properties) {
        super(smokey, fireDamage, properties);
        this.parent = parent;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entityIn) {
        if (!entityIn.isImmuneToFire() && world.getBlockState(pos).get(LIT) && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn)) {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, (float)this.fireDamage);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return NEW_SHAPE;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new RaisedCampfireTileEntity();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return this.parent.getPickBlock(state, target, world, pos, player);
    }
}
