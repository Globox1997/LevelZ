package net.levelz.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.gui.InfoScreen;
import net.levelz.gui.LevelzGui;
import net.levelz.gui.LevelzScreen;
import net.libz.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LevelzTab extends InventoryTab {

    public LevelzTab(Text title, Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public boolean canClick(Class<?> screenClass, MinecraftClient client) {
        if (screenClass.equals(InfoScreen.class))
            return true;
        return super.canClick(screenClass, client);
    }

    @Override
    public void onClick(MinecraftClient client) {
        client.setScreen(new LevelzScreen(new LevelzGui(client)));
    }

}
