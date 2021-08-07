package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;

/**
 * With help from Cadiboo's NoCubes KeybindHandler
 */
@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID, value = Dist.CLIENT)
public class DSKeyBindings {

    private static final List<Pair<KeyMapping, Runnable>> KEYBINDINGS = new LinkedList<>();

    static {
        KEYBINDINGS.add(makeKeybind("toggleVerticalSlabPlacement", GLFW.GLFW_KEY_CAPS_LOCK, DSKeyBindings::toggleVerticalSlabPlacement));
    }

    public static void register() {
        for (Pair<KeyMapping, Runnable> keybind : KEYBINDINGS)
            ClientRegistry.registerKeyBinding(keybind.getKey());
    }

    private static Pair<KeyMapping, Runnable> makeKeybind(String name, int key, Runnable action) {
        KeyMapping keyBinding = new KeyMapping(DoubleSlabs.MODID + ".key." + name, key, DoubleSlabs.MODID + ".keycategory");
        return Pair.of(keyBinding, action);
    }

    private static void toggleVerticalSlabPlacement() {
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG).ifPresent(config -> {
            config.setPlaceVerticalSlabs(!config.placeVerticalSlabs());
            PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
        });
    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        for (Pair<KeyMapping, Runnable> keybind : KEYBINDINGS)
            if (keybind.getKey().isDown())
                keybind.getValue().run();
    }

}
