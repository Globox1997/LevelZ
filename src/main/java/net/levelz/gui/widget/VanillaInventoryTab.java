package net.levelz.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.compat.InventorioScreenCompatibility;
import net.levelz.init.RenderInit;
import net.libz.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VanillaInventoryTab extends InventoryTab {

    public VanillaInventoryTab(Text title, Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public void onClick(MinecraftClient client) {
        if (RenderInit.isInventorioLoaded)
            InventorioScreenCompatibility.setInventorioScreen(client);
        else
            client.setScreen(new InventoryScreen(client.player));
    }

}
