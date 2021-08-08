package net.levelz.init;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.levelz.gui.LevelzGui;
import net.levelz.gui.LevelzScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyInit {
    public static KeyBinding screenKey;
    private static boolean keyBoolean;

    public static void init() {
        // Keybinds
        screenKey = new KeyBinding("key.levelz.openskillscreen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.levelz.keybind");

        // Registering
        KeyBindingHelper.registerKeyBinding(screenKey);
        // Callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenKey.wasPressed()) {
                if (!keyBoolean) {
                    client.setScreen(new LevelzScreen(new LevelzGui(client.player)));
                }
                keyBoolean = true;
            } else
                keyBoolean = false;
        });
    }

}