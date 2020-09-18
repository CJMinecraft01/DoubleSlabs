package cjminecraft.doubleslabs.common;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.container.GuiHandler;
import cjminecraft.doubleslabs.common.init.DSBlocks;
import cjminecraft.doubleslabs.common.init.DSItems;
import cjminecraft.doubleslabs.common.init.DSRecipes;
import cjminecraft.doubleslabs.common.init.DSTiles;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.patches.DynamicSurroundings;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.common.util.AnnotationUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = DoubleSlabs.NAME, modid = DoubleSlabs.MODID, acceptedMinecraftVersions = DoubleSlabs.ACCEPTED_MC_VERSIONS, updateJSON = DoubleSlabs.UPDATE_URL, guiFactory = DoubleSlabs.GUI_FACTORY, useMetadata = true)
public class DoubleSlabs {
    public static final String NAME = "DoubleSlabs";
    public static final String MODID = "doubleslabs";
    //    public static final String VERSION = "${version}";
    public static final String ACCEPTED_MC_VERSIONS = "[1.12,1.12.2]";
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/CJMinecraft01/DoubleSlabs/1.12.x/update.json";
    public static final String GUI_FACTORY = "cjminecraft.doubleslabs.client.gui.DSGuiFactory";
    public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);
    public static final CreativeTabs TAB = new CreativeTabs("verticalslabs") {
        @Override
        public ItemStack createIcon() {
            ItemStack stack = new ItemStack(DSItems.VERTICAL_SLAB);
            stack.setTagInfo("item", new ItemStack(Blocks.STONE_SLAB).writeToNBT(new NBTTagCompound()));
            return stack;
        }
    };

    @SidedProxy(serverSide = "cjminecraft.doubleslabs.server.proxy.ServerProxy", clientSide = "cjminecraft.doubleslabs.client.proxy.ClientProxy")
    public static IProxy PROXY;
    @Mod.Instance(MODID)
    public static DoubleSlabs instance;

    public DoubleSlabs() {
        DSBlocks.BLOCKS.register(MinecraftForge.EVENT_BUS);
        DSItems.ITEMS.register(MinecraftForge.EVENT_BUS);
        DSTiles.TILES.register(MinecraftForge.EVENT_BUS);
        DSRecipes.RECIPES.register(MinecraftForge.EVENT_BUS);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        AnnotationUtil.prepare(event.getAsmData());
        SlabSupport.load();
        ContainerSupport.load();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        PacketHandler.registerPackets();
        PlayerConfigCapability.register();

        DSConfig.load(event.getSuggestedConfigurationFile());

        PROXY.preInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit();
        DynamicSurroundings.prepare();
    }
}
