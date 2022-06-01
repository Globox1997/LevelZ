package net.levelz.compat;

import me.lizardofoz.inventorio.client.ui.InventorioScreen;
import me.lizardofoz.inventorio.player.InventorioScreenHandler;
import net.minecraft.client.MinecraftClient;

public class InventorioScreenCompatibility {

    public static void setInventorioScreen(MinecraftClient client) {
        client.setScreen(new InventorioScreen(new InventorioScreenHandler(0, client.player.getInventory()), client.player.getInventory()));
    }
}
