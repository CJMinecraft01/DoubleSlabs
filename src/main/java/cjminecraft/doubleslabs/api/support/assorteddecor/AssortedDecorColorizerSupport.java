package cjminecraft.doubleslabs.api.support.assorteddecor;

import cjminecraft.doubleslabs.api.support.SlabSupportProvider;
import cjminecraft.doubleslabs.api.support.minecraft.MinecraftSlabSupport;
import cjminecraft.doubleslabs.common.DoubleSlabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

import java.lang.reflect.Method;

@SlabSupportProvider(modid = "assorteddecor", priority = 1)
public class AssortedDecorColorizerSupport extends MinecraftSlabSupport {

    @ObjectHolder("assorteddecor:colorizer_brush")
    public static final Item COLORIZER_BRUSH = null;

    private final Class<?> iColorizer;
    private final Method getStoredState;
    private final Method setColorizer;

    public AssortedDecorColorizerSupport() {
        Class<?> icolorizer = null;
        Method getstoredstate = null;
        Method setcolorizer = null;
        try {
            icolorizer = Class.forName("com.grim3212.assorted.decor.common.block.IColorizer");
            getstoredstate = icolorizer.getMethod("getStoredState", IBlockReader.class, BlockPos.class);
            setcolorizer = icolorizer.getMethod("setColorizer", World.class, BlockPos.class, BlockState.class, PlayerEntity.class, Hand.class, boolean.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {

        }
        this.iColorizer = icolorizer;
        this.getStoredState = getstoredstate;
        this.setColorizer = setcolorizer;
    }

    /**
     * Initializes the NBT Tag Compound for the given ItemStack if it is null
     *
     * @param itemStack The ItemStack for which its NBT Tag Compound is being
     *                  checked for initialization
     */
    private static void initCompoundNBT(ItemStack itemStack) {
        if (itemStack.getTag() == null) {
            itemStack.setTag(new CompoundNBT());
        }
    }

    private static CompoundNBT getTag(ItemStack stack, String keyName) {
        initCompoundNBT(stack);

        if (!stack.getTag().contains(keyName)) {
            putTag(stack, keyName, new CompoundNBT());
        }

        return stack.getTag().getCompound(keyName);
    }

    private static void putTag(ItemStack stack, String keyName, CompoundNBT compound) {
        initCompoundNBT(stack);

        stack.getTag().put(keyName, compound);
    }

    @Override
    public boolean isHorizontalSlab(Item item) {
        return item instanceof BlockItem && iColorizer != null && getStoredState != null && setColorizer != null &&  iColorizer.isAssignableFrom(((BlockItem) item).getBlock().getClass());
    }

    @Override
    public boolean isHorizontalSlab(Block block) {
        return iColorizer != null && getStoredState != null && setColorizer != null && iColorizer.isAssignableFrom(block.getClass());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        DoubleSlabs.LOGGER.info("HERE");
        //noinspection ConstantConditions
        if (stack.getItem().equals(COLORIZER_BRUSH)) {
            try {
                BlockState stored = NBTUtil.readBlockState(getTag(stack, "stored_state"));
                Object colorizerBlock = iColorizer.cast(state.getBlock());

                if (getStoredState.invoke(colorizerBlock, world, pos) == stored) {
                    return ActionResultType.PASS;
                }

                setColorizer.invoke(colorizerBlock, world, pos, stored, player, hand, false);

                SoundType placeSound = stored.getSoundType(world, pos, player);
                world.playSound(player, pos, placeSound.getPlaceSound(), SoundCategory.BLOCKS, (placeSound.getVolume() + 1.0F) / 2.0F, placeSound.getPitch() * 0.8F);
                player.swingArm(hand);

                if (!player.isCreative()) {
                    int dmg = stack.getDamage() + 1;
                    if (stack.getMaxDamage() - dmg <= 0) {
                        player.setHeldItem(hand, new ItemStack(COLORIZER_BRUSH));
                    } else {
                        stack.setDamage(dmg);
                    }
                }

                return ActionResultType.SUCCESS;
            } catch (Exception ignored) {

            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }
}
