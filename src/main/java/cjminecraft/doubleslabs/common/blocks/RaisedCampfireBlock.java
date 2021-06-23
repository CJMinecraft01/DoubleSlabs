package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class RaisedCampfireBlock extends CampfireBlock {

    private static final VoxelShape NEW_SHAPE = SHAPE.withOffset(0, 0.5d, 0);

    private final int fireDamage;
    private Block parent;
    private ResourceLocation parentLocation;

    public RaisedCampfireBlock(Block parent, int fireDamage, Properties properties) {
        super(properties);
        this.parent = parent;
        this.fireDamage = fireDamage;
    }

    public RaisedCampfireBlock(ResourceLocation parentLocation, int fireDamage, Properties properties) {
        super(properties);
        this.parentLocation = parentLocation;
        this.fireDamage = fireDamage;
    }

    private Optional<Block> getParent() {
        if (this.parent == null && this.parentLocation != null)
            this.parent = ForgeRegistries.BLOCKS.getValue(this.parentLocation);
        return this.parent != null ? Optional.of(this.parent) : Optional.empty();
    }

    @Override
    public Item asItem() {
        return getParent().map(Block::asItem).orElseGet(super::asItem);
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return getParent().map(b -> b.getItem(worldIn, pos, state)).orElseGet(() -> super.getItem(worldIn, pos, state));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return getParent().map(b -> b.getDrops(state, builder)).orElseGet(() -> super.getDrops(state, builder));
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entityIn) {
        if (!entityIn.isImmuneToFire() && world.getBlockState(pos).get(LIT) && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entityIn)) {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, (float) this.fireDamage);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (!entityIn.isImmuneToFire() && state.get(LIT) && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn)) {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, (float) this.fireDamage);
        }

        super.onEntityCollision(state, worldIn, pos, entityIn);
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
        return getParent().map(b -> b.getPickBlock(state, target, world, pos, player)).orElseGet(() -> super.getPickBlock(state, target, world, pos, player));
    }
}
