package net.levelz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.levelz.screen.LevelScreen;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import java.io.FileWriter;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class KeyInit {
    public static KeyMapping screenKey = new KeyMapping("key.levelz.openskillscreen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.levelz.keybind");
    public static KeyMapping devKey = new KeyMapping("key.levelz.dev", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, "category.levelz.keybind");

    public static void init() {
        // Registering
        KeyBindingHelper.registerKeyBinding(screenKey);
        // Callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenKey.consumeClick()) {
                client.setScreen(new LevelScreen());
                return;
            }
        });
    }

    public static void writeId(String string) {
        try (FileWriter idFile = new FileWriter("idlist.json", true)) {
            idFile.append("\"" + string + "\",");
            idFile.append(System.lineSeparator());
        } catch (IOException ignored) {
        }
    }

}