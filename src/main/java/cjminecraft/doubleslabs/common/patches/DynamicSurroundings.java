package cjminecraft.doubleslabs.common.patches;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DynamicSurroundings {

    private static boolean loaded;
    private static Method setStateData;
    private static Object defaultStateData;

    public static boolean isLoaded() {
        return loaded;
    }

    public static void prepare() {
        loaded = Loader.isModLoaded("dsurround");
        if (!loaded)
            return;

        DoubleSlabs.LOGGER.info("Detected Dynamic Surroundings is present, applying patch code");

        try {
            Class<?> blockStateUtil = Class.forName("org.orecruncher.dsurround.registry.blockstate.BlockStateUtil");
            Class<?> blockStateData = Class.forName("org.orecruncher.dsurround.registry.blockstate.BlockStateData");
            Field defaultStateDataField = blockStateData.getDeclaredField("DEFAULT");
            defaultStateDataField.setAccessible(true);
            defaultStateData = defaultStateDataField.get(null);
            setStateData = blockStateUtil.getDeclaredMethod("setStateData", IBlockState.class, defaultStateDataField.getType());
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            DoubleSlabs.LOGGER.error(e);
        }
    }

    public static void patchBlockState(IBlockState state) {
        if (!isLoaded())
            return;
        if (defaultStateData == null)
            return;
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT)
            return;
        try {
            setStateData.invoke(null, state, defaultStateData);
        } catch (IllegalAccessException | InvocationTargetException e) {
            DoubleSlabs.LOGGER.error(e);
        }
    }
}
