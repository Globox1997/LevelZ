package net.levelz.init;

import net.levelz.LevelzMain;
import net.levelz.screen.LevelScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import java.io.FileWriter;
import java.io.IOException;

public class KeyInit {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(LevelzMain.identifierOf("keybind"));
    public static KeyMapping screenKey = new KeyMapping("key.levelz.openskillscreen", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, CATEGORY);
    public static KeyMapping devKey = new KeyMapping("key.levelz.dev", InputConstants.Type.KEYBOARD, InputConstants.KEY_F8, CATEGORY);

    public static void onClientTick(Minecraft client) {
        if (screenKey.consumeClick()) {
            client.gui.setScreen(new LevelScreen());
        }
    }

    public static void writeId(String string) {
        try (FileWriter idFile = new FileWriter("idlist.json", true)) {
            idFile.append("\"" + string + "\",");
            idFile.append(System.lineSeparator());
        } catch (IOException ignored) {
        }
    }

}