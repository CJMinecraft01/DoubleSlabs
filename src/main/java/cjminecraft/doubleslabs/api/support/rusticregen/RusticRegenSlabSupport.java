package cjminecraft.doubleslabs.api.support.rusticregen;

import cjminecraft.doubleslabs.api.support.IVerticalSlabSupport;
import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.minecraft.MinecraftSlabSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;

//@SlabSupportProvider(modid = "rusticregen", priority = 0) todo complete?
public class RusticRegenSlabSupport extends MinecraftSlabSupport {

    @ObjectHolder("rusticregen:crossed_logs")
    public static final Block CROSSED_LOGS = null;

    @Override
    public boolean isHorizontalSlab(Block block) {
        return CROSSED_LOGS != null && block == CROSSED_LOGS;
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return CROSSED_LOGS != null && item instanceof BlockItem && ((BlockItem) item).getBlock() == CROSSED_LOGS;
    }

    @Override
    public boolean isHorizontalSlab(BlockGetter world, BlockPos pos, BlockState state) {
        return isHorizontalSlab(state.getBlock());
    }

    @Override
    public boolean isHorizontalSlab(ItemStack stack, Player player, InteractionHand hand) {
        return isHorizontalSlab(stack.getItem());
    }

    @Override
    public boolean shouldCull(BlockState currentState, BlockState otherState) {
        return false;
    }

    @Override
    public boolean canCraft(Item item) {
        return false;
    }
}
