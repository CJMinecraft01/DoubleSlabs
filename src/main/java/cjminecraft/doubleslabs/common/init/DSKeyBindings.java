package cjminecraft.doubleslabs.common.init;

import cjminecraft.doubleslabs.common.DoubleSlabs;
import cjminecraft.doubleslabs.common.capability.config.IPlayerConfig;
import cjminecraft.doubleslabs.common.capability.config.PlayerConfigCapability;
import cjminecraft.doubleslabs.common.network.PacketHandler;
import cjminecraft.doubleslabs.common.network.packet.config.UpdateServerPlayerConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;
import java.util.List;

/**
 * With help from Cadiboo's NoCubes KeybindHandler
 */
@Mod.EventBusSubscriber(modid = DoubleSlabs.MODID, value = Side.CLIENT)
public class DSKeyBindings {

    private static final List<Pair<KeyBinding, Runnable>> KEYBINDINGS = new LinkedList<>();

    static {
        KEYBINDINGS.add(makeKeybind("toggleVerticalSlabPlacement", Keyboard.KEY_CAPITAL, DSKeyBindings::toggleVerticalSlabPlacement));
    }

    public static void register() {
        for (Pair<KeyBinding, Runnable> keybind : KEYBINDINGS)
            ClientRegistry.registerKeyBinding(keybind.getKey());
    }

    private static Pair<KeyBinding, Runnable> makeKeybind(String name, int key, Runnable action) {
        KeyBinding keyBinding = new KeyBinding(DoubleSlabs.MODID + ".key." + name, key, DoubleSlabs.MODID + ".keycategory");
        return Pair.of(keyBinding, action);
    }

    private static void toggleVerticalSlabPlacement() {
        IPlayerConfig config = Minecraft.getMinecraft().player.getCapability(PlayerConfigCapability.PLAYER_CONFIG, null);
        if (config == null)
            return;
        config.setPlaceVerticalSlabs(!config.placeVerticalSlabs());
        PacketHandler.INSTANCE.sendToServer(new UpdateServerPlayerConfigPacket(config));
    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        for (Pair<KeyBinding, Runnable> keybind : KEYBINDINGS)
            if (keybind.getKey().isPressed())
                keybind.getValue().run();
    }

}
