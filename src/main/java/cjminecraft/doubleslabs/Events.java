package cjminecraft.doubleslabs;

import cjminecraft.doubleslabs.blocks.BlockDoubleSlab;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class Events {

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(DoubleSlabs.MODID))
        {
            ConfigManager.sync(DoubleSlabs.MODID, Config.Type.INSTANCE);
            DoubleSlabsConfig.slabBlacklist = Arrays.asList(DoubleSlabsConfig.slabBlacklistArray);
        }
    }

    private static void tryPlace(BlockPos pos, ItemSlab slab, EnumFacing face, PlayerInteractEvent.RightClickBlock event) {
        IBlockState state = event.getWorld().getBlockState(pos);
        BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
        IBlockState slabState = slab.singleSlab.getStateForPlacement(event.getWorld(), pos, face, (float) event.getHitVec().x, (float) event.getHitVec().y, (float) event.getHitVec().z, slab.getMetadata(event.getItemStack().getMetadata()), event.getEntityPlayer(), event.getHand()).cycleProperty(BlockSlab.HALF);
        IBlockState newState = ((IExtendedBlockState) Registrar.DOUBLE_SLAB.getDefaultState()).withProperty(BlockDoubleSlab.TOP, half == BlockSlab.EnumBlockHalf.TOP ? state : slabState).withProperty(BlockDoubleSlab.BOTTOM, half == BlockSlab.EnumBlockHalf.BOTTOM ? state : slabState);

        if (DoubleSlabsConfig.slabBlacklist.contains(DoubleSlabsConfig.slabToString(slabState)))
            return;
        AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(event.getWorld(), pos);

        if (axisalignedbb != Block.NULL_AABB && event.getWorld().checkNoEntityCollision(axisalignedbb.offset(pos)) && event.getWorld().setBlockState(pos, newState, 11))
        {
            SoundType soundtype = slab.singleSlab.getSoundType(slabState, event.getWorld(), pos, event.getEntityPlayer());
            event.getWorld().playSound(event.getEntityPlayer(), pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            if (!event.getEntityPlayer().isCreative())
                event.getItemStack().shrink(1);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            event.setCanceled(true);
            if (event.getEntityPlayer() instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)event.getEntityPlayer(), pos, event.getItemStack());
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
                    if (DoubleSlabsConfig.slabBlacklist.contains(DoubleSlabsConfig.slabToString(state)))
                        return;
                    BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
                    if ((face == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM || face == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP))
                        tryPlace(pos, slab, face, event);
//                    else if (event.getWorld().getBlockState(event.getPos().offset(event.getFace())).getBlock() instanceof BlockSlab) {
//                        // TODO work on placing when not directly hitting slab
//                        if (event.getHitVec().y - event.getPos().offset()event.getFace()).getY() < 0.5)
//                            tryPlace(event.getPos().offset(event.getFace()), slab, EnumFacing.DOWN, event);
//                        else
//                            tryPlace(event.getPos().offset(event.getFace()), slab, EnumFacing.UP, event);
//                    }
//                    BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
//                    if ((event.getFace() == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM || event.getFace() == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP))
//                    {
//                        IBlockState slabState = slab.singleSlab.getStateForPlacement(event.getWorld(), event.getPos(), event.getFace(), (float) event.getHitVec().x, (float) event.getHitVec().y, (float) event.getHitVec().z, slab.getMetadata(event.getItemStack().getMetadata()), event.getEntityPlayer(), event.getHand()).cycleProperty(BlockSlab.HALF);
//                        IBlockState newState = ((IExtendedBlockState) Registrar.DOUBLE_SLAB.getDefaultState()).withProperty(BlockDoubleSlab.TOP, half == BlockSlab.EnumBlockHalf.TOP ? state : slabState).withProperty(BlockDoubleSlab.BOTTOM, half == BlockSlab.EnumBlockHalf.BOTTOM ? state : slabState);
//
//                        AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(event.getWorld(), event.getPos());
//
//                        if (axisalignedbb != Block.NULL_AABB && event.getWorld().checkNoEntityCollision(axisalignedbb.offset(event.getPos())) && event.getWorld().setBlockState(event.getPos(), newState, 11))
//                        {
//                            SoundType soundtype = slab.singleSlab.getSoundType(slabState, event.getWorld(), event.getPos(), event.getEntityPlayer());
//                            event.getWorld().playSound(event.getEntityPlayer(), event.getPos(), soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
//                            if (!event.getEntityPlayer().isCreative())
//                                event.getItemStack().shrink(1);
//                            event.setCancellationResult(EnumActionResult.SUCCESS);
//                            event.setCanceled(true);
//                            if (event.getEntityPlayer() instanceof EntityPlayerMP)
//                                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)event.getEntityPlayer(), event.getPos(), event.getItemStack());
//                        }
//                    } else if (event.getWorld().getBlockState(event.getPos().offset(event.getFace())).getBlock() instanceof BlockSlab) {
//
//                    }
                }
            }
        }
    }

    // Taken from Klee Slabs

//    public static RayTraceResult rayTrace(EntityLivingBase entity, double length) {
//        Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
//        Vec3d endPos = startPos.add(entity.getLookVec().x * length, entity.getLookVec().y * length, entity.getLookVec().z * length);
//        return entity.world.rayTraceBlocks(startPos, endPos);
//    }
//
//    @SubscribeEvent
//    public static void onBlockBreak(BlockEvent.BreakEvent event) {
//        if (event.getPlayer() instanceof FakePlayer)
//            return;
//        RayTraceResult mop = rayTrace(event.getPlayer(), 6);
//        Vec3d hitVec = mop != null ? mop.hitVec : null;
//        if (hitVec != null)
//            hitVec = hitVec.add(-event.getPos().getX(), -event.getPos().getY(), -event.getPos().getZ());
//        IBlockState state = event.getState();
//        if (state.getBlock() instanceof BlockDoubleSlab) {
//            IExtendedBlockState extendedBlockState = (IExtendedBlockState) state.getBlock().getExtendedState(state, event.getWorld(), event.getPos());
//            IBlockState dropState;
//            IBlockState newState;
//            if (hitVec != null && hitVec.y < 0.5f) {
//                dropState = extendedBlockState.getValue(BlockDoubleSlab.BOTTOM);
//                newState = extendedBlockState.getValue(BlockDoubleSlab.TOP);
//            } else {
//                dropState = extendedBlockState.getValue(BlockDoubleSlab.TOP);
//                newState = extendedBlockState.getValue(BlockDoubleSlab.BOTTOM);
//            }
//
//            if (!event.getWorld().isRemote && event.getPlayer().canHarvestBlock(event.getState()) && !event.getPlayer().isCreative()) {
//                Item slab = Item.getItemFromBlock(dropState.getBlock());
//                if (slab != Items.AIR)
//                    InventoryHelper.spawnItemStack(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new ItemStack(slab, 1, dropState.getBlock().damageDropped(dropState)));
//            }
//            event.getWorld().setBlockState(event.getPos(), newState, 1 | 2);
//            event.setCanceled(true);
//
//            IBlockState dropState = slabConverter.getSingleSlab(state, BlockSlab.EnumBlockHalf.BOTTOM);
//            if (!event.getWorld().isRemote && event.getPlayer().canHarvestBlock(event.getState()) && !event.getPlayer().capabilities.isCreativeMode) {
//                Item slabItem = Item.getItemFromBlock(dropState.getBlock());
//                if (slabItem != Items.AIR) {
//                    spawnItem(new ItemStack(slabItem, 1, dropState.getBlock().damageDropped(dropState)), event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
//                }
//            }
//            IBlockState newState;
//            if (hitVec != null && hitVec.y < 0.5f) {
//                newState = slabConverter.getSingleSlab(state, BlockSlab.EnumBlockHalf.TOP);
//            } else {
//                newState = slabConverter.getSingleSlab(state, BlockSlab.EnumBlockHalf.BOTTOM);
//            }
//            event.getWorld().setBlockState(event.getPos(), newState, 1 | 2);
//            event.setCanceled(true);
//        }
//    }

}
