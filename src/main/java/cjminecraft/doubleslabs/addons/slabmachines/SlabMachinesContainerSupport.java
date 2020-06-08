package cjminecraft.doubleslabs.addons.slabmachines;

import cjminecraft.doubleslabs.api.IContainerSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.InvocationTargetException;

@GameRegistry.ObjectHolder("slabmachines")
public class SlabMachinesContainerSupport implements IContainerSupport {

    public static final Block FURNACE_SLAB = null;
    public static final Block WORKBENCH_SLAB = null;
    public static final Block CHEST_SLAB = null;
    public static final Block TRAPPED_CHEST_SLAB = null;

    // Tinkers compat
    public static final Block CRAFTING_STATION_SLAB = null;
    public static final Block PART_BUILDER_SLAB = null;
    public static final Block PART_CHEST_SLAB = null;
    public static final Block PATTERN_CHEST_SLAB = null;
    public static final Block STENCIL_TABLE_SLAB = null;
    public static final Block TOOL_FORGE_SLAB = null;
    public static final Block TOOL_STATION_SLAB = null;

    private final Object modInstance;
    private final boolean fastFurnace;

    private final Class<?> baseContainer;

    public SlabMachinesContainerSupport() {
        Object modInstance;
        Class<?> baseContainer;
        try {
            modInstance = Class.forName("com.mrbysco.slabmachines.SlabMachines").getField("instance").get(null);
            if (Loader.isModLoaded("mantle"))
                baseContainer = Class.forName("slimeknights.mantle.inventory.BaseContainer");
            else
                baseContainer = null;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException  ignored) {
            modInstance = null;
            baseContainer = null;
        }
        this.modInstance = modInstance;
        this.baseContainer = baseContainer;
        this.fastFurnace = Loader.isModLoaded("fastfurnace");
    }

    @Override
    public boolean isValid(World world, BlockPos pos, IBlockState state) {
        return modInstance != null && (state.getBlock() == FURNACE_SLAB || state.getBlock() == WORKBENCH_SLAB || state.getBlock() == CHEST_SLAB || state.getBlock() == TRAPPED_CHEST_SLAB || state.getBlock() == CRAFTING_STATION_SLAB || state.getBlock() == PART_BUILDER_SLAB || state.getBlock() == PART_CHEST_SLAB || state.getBlock() == PATTERN_CHEST_SLAB || state.getBlock() == STENCIL_TABLE_SLAB || state.getBlock() == TOOL_FORGE_SLAB || state.getBlock() == TOOL_STATION_SLAB);
    }

    @Override
    public Object getMod() {
        return this.modInstance;
    }

    @Override
    public int getGuiId(World world, BlockPos pos, IBlockState state) {
        int id = -1;
        if (state.getBlock() == WORKBENCH_SLAB)
            if (fastFurnace)
                id = 8;
            else
                id = 0;
        else if (state.getBlock() == FURNACE_SLAB)
            if (fastFurnace)
                id = 10;
            else
                id = 2;
        else if (state.getBlock() == CHEST_SLAB || state.getBlock() == TRAPPED_CHEST_SLAB)
            id = 4;
        else if (state.getBlock() == CRAFTING_STATION_SLAB)
            id = 12;
        else if (state.getBlock() == STENCIL_TABLE_SLAB)
            id = 14;
        else if (state.getBlock() == PART_BUILDER_SLAB)
            id = 16;
        else if (state.getBlock() == TOOL_STATION_SLAB)
            id = 18;
        else if (state.getBlock() == PATTERN_CHEST_SLAB)
            id = 20;
        else if (state.getBlock() == TOOL_FORGE_SLAB)
            id = 22;
        else if (state.getBlock() == PART_CHEST_SLAB)
            id = 24;

        if (state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM)
            id += 1;
        return id;
    }

    @Override
    public void onClicked(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing) {
        if (state.getBlock() == WORKBENCH_SLAB)
            player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
        else if (state.getBlock() == FURNACE_SLAB)
            player.addStat(StatList.FURNACE_INTERACTION);
        else if (state.getBlock() == CHEST_SLAB)
            player.addStat(StatList.CHEST_OPENED);
        else if (state.getBlock() == TRAPPED_CHEST_SLAB)
            player.addStat(StatList.TRAPPED_CHEST_TRIGGERED);
        else if (baseContainer != null && baseContainer.isAssignableFrom(player.openContainer.getClass())) {
            Object baseContainerObj = baseContainer.cast(player.openContainer);
            try {
                baseContainer.getMethod("syncOnOpen", EntityPlayerMP.class).invoke(baseContainerObj, (EntityPlayerMP)player);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {

            }
        }

    }
}
