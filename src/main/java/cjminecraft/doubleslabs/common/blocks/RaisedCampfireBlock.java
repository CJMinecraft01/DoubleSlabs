package cjminecraft.doubleslabs.common.blocks;

import cjminecraft.doubleslabs.common.tileentity.RaisedCampfireTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class RaisedCampfireBlock extends CampfireBlock {

    private static final VoxelShape NEW_SHAPE = SHAPE.move(0, 0.5d, 0);

    private Block parent;
    private ResourceLocation parentLocation;

    public RaisedCampfireBlock(Block parent, boolean smokey, int fireDamage, Properties properties) {
        super(smokey, fireDamage, properties);
        this.parent = parent;
    }

    public RaisedCampfireBlock(ResourceLocation parentLocation, boolean smokey, int fireDamage, Properties properties) {
        super(smokey, fireDamage, properties);
        this.parentLocation = parentLocation;
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
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return getParent().map(b -> b.getCloneItemStack(world, pos, state)).orElseGet(() -> super.getCloneItemStack(world, pos, state));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return getParent().map(b -> b.getDrops(state, builder)).orElseGet(() -> super.getDrops(state, builder));
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.fireImmune() && world.getBlockState(pos).getValue(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity)) {
            entity.hurt(DamageSource.IN_FIRE, this.fireDamage);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return NEW_SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RaisedCampfireTileEntity(pos, state);
    }

    // ticker?


    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return getParent().map(b -> b.getCloneItemStack(state, target, world, pos, player)).orElseGet(() -> super.getCloneItemStack(state, target, world, pos, player));
    }
}
