package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.tileentitiy.TileEntityDoubleSlab;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ISlabSupport itemSupport = SlabSupport.getSupport(event.getItemStack(), event.getPlayer(), event.getHand());
            if (itemSupport == null)
                return;

            if (event.getPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                BlockPos pos = event.getPos();
                Direction face = event.getFace();
                BlockState state = event.getWorld().getBlockState(pos);
                ISlabSupport blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                if (blockSupport == null) {
                    pos = pos.offset(face);
                    state = event.getWorld().getBlockState(pos);
                    if (!event.getPlayer().canPlayerEdit(pos.offset(face), face, event.getItemStack()))
                        return;
                    blockSupport = SlabSupport.getSupport(event.getWorld(), pos, state);
                    if (blockSupport == null)
                        return;
                    face = blockSupport.getHalf(event.getWorld(), pos, state) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN;
                }

                SlabType half = blockSupport.getHalf(event.getWorld(), pos, state);
                if (half == SlabType.DOUBLE)
                    return;

                if ((face == Direction.UP && half == SlabType.BOTTOM) || (face == Direction.DOWN && half == SlabType.TOP)) {
                    BlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), half == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
                    BlockState newState = Registrar.DOUBLE_SLAB.getDefaultState();

                    if (Config.SLAB_BLACKLIST.get().contains(Config.slabToString(state)) || Config.SLAB_BLACKLIST.get().contains(Config.slabToString(slabState)))
                        return;

                    if (!event.getWorld().checkBlockCollision(event.getPlayer().getBoundingBox().offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
                        TileEntityDoubleSlab tile = (TileEntityDoubleSlab) event.getWorld().getTileEntity(pos);
                        if (tile == null)
                            return;
                        tile.setTopState(half == SlabType.TOP ? state : slabState);
                        tile.setBottomState(half == SlabType.BOTTOM ? state : slabState);
                        SoundType soundtype = slabState.getSoundType(event.getWorld(), pos, event.getPlayer());
                        event.getWorld().playSound(event.getPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        if (!event.getPlayer().isCreative())
                            event.getItemStack().shrink(1);
                        event.setCancellationResult(ActionResultType.SUCCESS);
                        event.setCanceled(true);
                        if (event.getPlayer() instanceof ServerPlayerEntity)
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) event.getPlayer(), pos, event.getItemStack());
                    }
                }
            }
        }
    }

}
