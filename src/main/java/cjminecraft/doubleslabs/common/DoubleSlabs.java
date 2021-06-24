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
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
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

    public static final ItemGroup TAB = new ItemGroup("verticalslabs") {
        @Override
        public ItemStack createIcon() {
            ItemStack stack = new ItemStack(DSItems.VERTICAL_SLAB.get());
            stack.setTagInfo("item", Items.STONE_BRICK_SLAB.getDefaultInstance().write(new CompoundNBT()));
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
        mod.addListener(PROXY::loadComplete);

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
