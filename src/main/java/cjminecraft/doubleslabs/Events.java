package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    private static void tryPlace(BlockState state, SlabType half, BlockPos pos, SlabBlock slab, PlayerInteractEvent.RightClickBlock event) {
        BlockState slabState = slab.getDefaultState().with(SlabBlock.TYPE, half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
        BlockState newState = Registrar.DOUBLE_SLAB.getDefaultState();

        if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)) || Config.SLAB_BLACKLIST.get().contains(Config.slabToString(slabState)))
            return;

        if (event.getWorld().checkNoEntityCollision(event.getPlayer(), VoxelShapes.create(event.getPlayer().getBoundingBox())) && event.getWorld().setBlockState(pos, newState, 11)) {
            TileEntityDoubleSlab tile = (TileEntityDoubleSlab) event.getWorld().getTileEntity(pos);
            if (tile == null)
                return;
            tile.setTopState(half == SlabType.TOP ? state : slabState);
            tile.setBottomState(half == SlabType.BOTTOM ? state : slabState);
            SoundType soundtype = slab.getSoundType(slabState, event.getWorld(), pos, event.getPlayer());
            event.getWorld().playSound(event.getPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            if (!event.getPlayer().isCreative())
                event.getItemStack().shrink(1);
            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
            if (event.getPlayer() instanceof ServerPlayerEntity)
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) event.getPlayer(), pos, event.getItemStack());
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof BlockItem && ((BlockItem) event.getItemStack().getItem()).getBlock() instanceof SlabBlock) {
            if (event.getPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                SlabBlock slab = (SlabBlock) ((BlockItem) event.getItemStack().getItem()).getBlock();
                BlockPos pos = event.getPos();
                Direction face = event.getFace();
                if (!(event.getWorld().getBlockState(pos).getBlock() instanceof SlabBlock)) {
                    pos = pos.offset(face);
                    if (event.getWorld().getBlockState(pos).getBlock() instanceof SlabBlock)
                        face = event.getWorld().getBlockState(pos).get(SlabBlock.TYPE) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN;
                }
                BlockState state = event.getWorld().getBlockState(pos);
                if (state.getBlock() instanceof SlabBlock && (face == Direction.UP || face == Direction.DOWN))
                    tryPlace(state, state.get(SlabBlock.TYPE), pos, slab, event);
            }
        }
    }

}
