package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
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

    private static void tryPlace(BlockPos pos, ItemSlab slab, EnumFacing face, PlayerInteractEvent.RightClickBlock event) {
        IBlockState state = event.getWorld().getBlockState(pos);
        BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
        IBlockState slabState = slab.singleSlab.getStateForPlacement(event.getWorld(), pos, face, (float) event.getHitVec().x, (float) event.getHitVec().y, (float) event.getHitVec().z, slab.getMetadata(event.getItemStack().getMetadata()), event.getEntityPlayer(), event.getHand()).cycleProperty(BlockSlab.HALF);
        IBlockState newState = ((IExtendedBlockState) Registrar.DOUBLE_SLAB.getDefaultState()).withProperty(BlockDoubleSlab.TOP, half == BlockSlab.EnumBlockHalf.TOP ? state : slabState).withProperty(BlockDoubleSlab.BOTTOM, half == BlockSlab.EnumBlockHalf.BOTTOM ? state : slabState);

        if (DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(state)) || DoubleSlabsConfig.SLAB_BLACKLIST.contains(DoubleSlabsConfig.slabToString(slabState)))
            return;
        AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(event.getWorld(), pos);

        if (axisalignedbb != Block.NULL_AABB && event.getWorld().checkNoEntityCollision(axisalignedbb.offset(pos)) && event.getWorld().setBlockState(pos, newState, 11)) {
            SoundType soundtype = slab.singleSlab.getSoundType(slabState, event.getWorld(), pos, event.getEntityPlayer());
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
                ItemSlab slab = (ItemSlab) event.getItemStack().getItem();
                BlockPos pos = event.getPos();
                EnumFacing face = event.getFace();
                if (!(event.getWorld().getBlockState(pos).getBlock() instanceof BlockSlab)) {
                    pos = pos.offset(event.getFace());
                    face = event.getHitVec().y - pos.getY() > 0.5 ? EnumFacing.UP : EnumFacing.DOWN;
                }
                IBlockState state = event.getWorld().getBlockState(pos);
                if (state.getBlock() instanceof BlockSlab) {
                    if (((BlockSlab) state.getBlock()).isDouble())
                        return;
                    BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
                    if ((face == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM) || (face == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP))
                        tryPlace(pos, slab, face, event);
                }
            }
        }
    }

}
