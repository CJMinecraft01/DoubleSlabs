package cjminecraft.doubleslabs.common.placement;

import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.api.support.IHorizontalSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.blocks.VerticalSlabBlock;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.tileentity.SlabTileEntity;
import cjminecraft.doubleslabs.old.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID)
public class PlacementHandler {

    private static boolean canPlace(World world, BlockPos pos, Direction face, PlayerEntity player, Hand hand, ItemStack stack, Consumer<ActionResultType> cancelEventConsumer, boolean activateBlock) {
        if (!player.canPlayerEdit(pos, face, stack))
            return false;
        if (MathHelper.floor(player.getPosX()) == pos.getX() && MathHelper.floor(player.getPosY()) == pos.getY() && MathHelper.floor(player.getPosZ()) == pos.getZ())
            return false;
        if (!activateBlock)
            return true;
        boolean useItem = !player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player) || !player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player);
        boolean flag = player.isSecondaryUseActive() && useItem;
        if (!flag) {
            ActionResultType result = world.getBlockState(pos).onBlockActivated(world, player, hand, Utils.rayTrace(player).func_237485_a_(pos));
            if (result.isSuccessOrConsume())
                cancelEventConsumer.accept(result);
            return !result.isSuccessOrConsume();
        }
        return true;
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().isEmpty()) {
            ItemStack stack = event.getItemStack();
            World world = event.getWorld();
            PlayerEntity player = event.getPlayer();
            Hand hand = event.getHand();
            Direction face = event.getFace();
            BlockPos pos = event.getPos();

            IHorizontalSlabSupport horizontalSlabItemSupport = SlabSupport.getHorizontalSlabSupport(stack, player, hand);
            Consumer<ActionResultType> cancel = resultType -> {
                event.setCanceled(true);
                event.setCancellationResult(resultType);
            };

            IPlayerConfig config = player.getCapability(PlayerConfigCapability.PLAYER_CONFIG).orElse(new PlayerConfig());

            if (horizontalSlabItemSupport == null) {
                // The item we are holding is not a horizontal slab
            } else if (canPlace(world, pos, face, player, hand, stack, cancel, true)) {
                BlockState state = world.getBlockState(pos);

                IHorizontalSlabSupport horizontalSlabSupport = SlabSupport.getHorizontalSlabSupport(world, pos, state);

                boolean verticalSlab = state.getBlock() == DSBlocks.VERTICAL_SLAB.get() && !state.get(VerticalSlabBlock.DOUBLE) && (((SlabTileEntity) world.getTileEntity(pos)).getPositiveBlockInfo().getBlockState() != null ? face == state.get(VerticalSlabBlock.FACING).getOpposite() : face == state.get(VerticalSlabBlock.FACING));
                
                if (horizontalSlabSupport == null && !verticalSlab) {
                    // The block at the position which was clicked is not a horizontal slab

                }
            }
        }
    }

}
