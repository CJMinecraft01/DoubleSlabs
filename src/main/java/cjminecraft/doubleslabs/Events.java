package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.api.ISlabSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import cjminecraft.doubleslabs.patches.DynamicSurroundings;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(DoubleSlabs.MODID)) {
            ConfigManager.sync(DoubleSlabs.MODID, Config.Type.INSTANCE);
            DoubleSlabsConfig.SLAB_BLACKLIST = Arrays.asList(DoubleSlabsConfig.SLAB_BLACKLIST_ARRAY);
        }
    }

//    private static void tryPlace(BlockPos pos, ItemSlab slab, EnumFacing face, PlayerInteractEvent.RightClickBlock event) {
//        IBlockState state = event.getWorld().getBlockState(pos);
//        BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
//        IBlockState slabState = slab.singleSlab.getStateForPlacement(event.getWorld(), pos, face, (float) event.getHitVec().x, (float) event.getHitVec().y, (float) event.getHitVec().z, slab.getMetadata(event.getItemStack().getMetadata()), event.getEntityPlayer(), event.getHand()).cycleProperty(BlockSlab.HALF);
//        IBlockState newState = ((IExtendedBlockState) Registrar.DOUBLE_SLAB.getDefaultState()).withProperty(BlockDoubleSlab.TOP, half == BlockSlab.EnumBlockHalf.TOP ? state : slabState).withProperty(BlockDoubleSlab.BOTTOM, half == BlockSlab.EnumBlockHalf.BOTTOM ? state : slabState);
//
//        if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)) || DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(slabState)))
//            return;
//        AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(event.getWorld(), pos);
//
//        if (axisalignedbb != Block.NULL_AABB && event.getWorld().checkNoEntityCollision(axisalignedbb.offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
//            SoundType soundtype = slab.singleSlab.getSoundType(slabState, event.getWorld(), pos, event.getEntityPlayer());
//            event.getWorld().playSound(event.getEntityPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
//            if (!event.getEntityPlayer().isCreative())
//                event.getItemStack().shrink(1);
//            event.setCancellationResult(EnumActionResult.SUCCESS);
//            event.setCanceled(true);
//            if (event.getEntityPlayer() instanceof EntityPlayerMP)
//                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) event.getEntityPlayer(), pos, event.getItemStack());
//        }
//    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ISlabSupport itemSupport = SlabSupport.getSupport(event.getItemStack(), event.getEntityPlayer(), event.getHand());
            if (itemSupport == null)
                return;
            if (event.getEntityPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                BlockPos pos = event.getPos();
                EnumFacing face = event.getFace();
                ISlabSupport blockSupport = SlabSupport.getSupport(event.getWorld(), pos, event.getWorld().getBlockState(pos));
                if (blockSupport == null) {
                    pos = pos.offset(event.getFace());
                    face = event.getHitVec().y - pos.getY() > 0.5 ? EnumFacing.UP : EnumFacing.DOWN;
                    blockSupport = SlabSupport.getSupport(event.getWorld(), pos, event.getWorld().getBlockState(pos));
                }
                IBlockState state = event.getWorld().getBlockState(pos);

                if (!DoubleSlabsConfig.REPLACE_SAME_SLAB && blockSupport == itemSupport && blockSupport.areSame(event.getWorld(), pos, state, event.getItemStack()))
                    return;

                if (blockSupport != null) {
                    BlockSlab.EnumBlockHalf half = blockSupport.getHalf(event.getWorld(), pos, state);
                    if ((face == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM) || (face == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP)) {
                        IBlockState slabState = itemSupport.getStateForHalf(event.getWorld(), pos, event.getItemStack(), half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM);
                        if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)) || DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(slabState)))
                            return;

                        IBlockState newState = ((IExtendedBlockState) Registrar.DOUBLE_SLAB.getDefaultState()).withProperty(BlockDoubleSlab.TOP, half == BlockSlab.EnumBlockHalf.TOP ? state : slabState).withProperty(BlockDoubleSlab.BOTTOM, half == BlockSlab.EnumBlockHalf.BOTTOM ? state : slabState);
                        AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(event.getWorld(), pos);

                        DynamicSurroundings.patchBlockState(newState);

                        if (axisalignedbb != Block.NULL_AABB && event.getWorld().checkNoEntityCollision(axisalignedbb.offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
                            SoundType soundtype = state.getBlock().getSoundType(slabState, event.getWorld(), pos, event.getEntityPlayer());
                            event.getWorld().playSound(event.getEntityPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                            if (!event.getEntityPlayer().isCreative())
                                event.getItemStack().shrink(1);
                            event.setCancellationResult(EnumActionResult.SUCCESS);
                            event.setCanceled(true);
                            if (event.getEntityPlayer() instanceof EntityPlayerMP)
                                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) event.getEntityPlayer(), pos, event.getItemStack());
                        } else {
                            event.setUseItem(Event.Result.DENY);
                        }
                    }
                }
            }
        }
    }

    /*
    private static void tryPlace(IBlockState state, BlockSlab.EnumBlockHalf half, BlockSlab slab, BlockPos pos, PlayerInteractEvent.RightClickBlock event) {
        IBlockState slabState = slab.getDefaultState().withProperty(BlockSlab.HALF, half == BlockSlab.EnumBlockHalf.BOTTOM ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM);
        IBlockState newState = ((IExtendedBlockState) Registrar.DOUBLE_SLAB.getDefaultState()).withProperty(BlockDoubleSlab.TOP, half == BlockSlab.EnumBlockHalf.TOP ? state : slabState).withProperty(BlockDoubleSlab.BOTTOM, half == BlockSlab.EnumBlockHalf.BOTTOM ? state : slabState);

        if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)) || DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(slabState)))
            return;

        AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(event.getWorld(), pos);

        if (axisalignedbb != Block.NULL_AABB && event.getWorld().checkNoEntityCollision(axisalignedbb.offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
            SoundType soundtype = slab.getSoundType(slabState, event.getWorld(), pos, event.getEntityPlayer());
            event.getWorld().playSound(event.getEntityPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            if (!event.getEntityPlayer().isCreative())
                event.getItemStack().shrink(1);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
            if (event.getEntityPlayer() instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) event.getEntityPlayer(), pos, event.getItemStack());
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemSlab) {
            if (event.getEntityPlayer().canPlayerEdit(event.getPos().offset(event.getFace()), event.getFace(), event.getItemStack())) {
                BlockPos pos = event.getPos();
                EnumFacing face = event.getFace();
                if (!(event.getWorld().getBlockState(pos).getBlock() instanceof BlockSlab)) {
                    pos = pos.offset(event.getFace());
                    face = event.getHitVec().y - pos.getY() > 0.5 ? EnumFacing.UP : EnumFacing.DOWN;
                }
                IBlockState state = event.getWorld().getBlockState(pos);
                if (state.getBlock() instanceof BlockSlab) {
                    BlockSlab slab = (BlockSlab) ((ItemBlock) event.getItemStack().getItem()).getBlock();
                    if (((BlockSlab) state.getBlock()).isDouble())
                        return;
                    BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
                    if ((face == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM) || (face == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP))
                        tryPlace(state, half, slab, pos, event);
                }
            }
        }
    }
    */

}
