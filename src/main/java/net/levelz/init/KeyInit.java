package net.levelz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.levelz.LevelzMain;
import net.levelz.screen.LevelScreen;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import java.io.FileWriter;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class KeyInit {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(LevelzMain.identifierOf("keybind"));
    public static KeyMapping screenKey = new KeyMapping("key.levelz.openskillscreen", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, CATEGORY);
    public static KeyMapping devKey = new KeyMapping("key.levelz.dev", InputConstants.Type.KEYBOARD, InputConstants.KEY_F8, CATEGORY);

    public static void init() {
        // Registering
        KeyMappingHelper.registerKeyMapping(screenKey);
        // Callback
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenKey.consumeClick()) {
                client.gui.setScreen(new LevelScreen());
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