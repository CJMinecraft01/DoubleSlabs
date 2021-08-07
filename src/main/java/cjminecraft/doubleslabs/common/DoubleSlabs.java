package cjminecraft.doubleslabs.common;

import cjminecraft.doubleslabs.api.ContainerSupport;
import cjminecraft.doubleslabs.api.SlabSupport;
import cjminecraft.doubleslabs.client.proxy.ClientProxy;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.config.DSConfig;
import cjminecraft.doubleslabs.common.init.*;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.proxy.IProxy;
import cjminecraft.doubleslabs.server.proxy.ServerProxy;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DoubleSlabs.MODID)
public class DoubleSlabs {
    public static final String MODID = "doubleslabs";

    public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);

    public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static final CreativeModeTab TAB = new CreativeModeTab("verticalslabs") {
        @Override
        public ItemStack makeIcon() {
            ItemStack stack = new ItemStack(DSItems.VERTICAL_SLAB.get());
            stack.addTagElement("item", Items.STONE_BRICK_SLAB.getDefaultInstance().serializeNBT());
            return stack;
        }
    };

    public DoubleSlabs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, DSConfig.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DSConfig.CLIENT_SPEC);

        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forge = MinecraftForge.EVENT_BUS;

        SlabSupport.load();
        ContainerSupport.load();

        mod.addListener(this::commonSetup);

        DSBlocks.BLOCKS.register(mod);
        DSItems.ITEMS.register(mod);
        DSTiles.TILES.register(mod);
        DSRecipes.RECIPE_SERIALIZERS.register(mod);
        DSContainers.CONTAINER_TYPES.register(mod);

        PROXY.addListeners(mod, forge);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.registerPackets();
        PlayerConfigCapability.register();
    }
}
