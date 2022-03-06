package net.levelz.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.Environment;
import net.levelz.init.KeyInit;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class InfoScreen extends CottonClientScreen {

    public InfoScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (client.options.inventoryKey.matchesKey(ch, keyCode)) {
            this.close();
            return true;
        } else if (KeyInit.screenKey.matchesKey(ch, keyCode)) {
            this.client.setScreen(new LevelzScreen(new LevelzGui(client)));
            return true;
        } else
            return super.keyPressed(ch, keyCode, modifiers);

    }

}
