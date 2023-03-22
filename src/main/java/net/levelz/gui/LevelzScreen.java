package net.levelz.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.levelz.init.KeyInit;
import net.libz.api.Tab;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class LevelzScreen extends CottonClientScreen implements Tab {

    public LevelzScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);
        assert this.client != null;
        if (this.client.player != null) {
            int scaledWidth = this.client.getWindow().getScaledWidth();
            int scaledHeight = this.client.getWindow().getScaledHeight();
            InventoryScreen.drawEntity(scaledWidth / 2 - 75, scaledHeight / 2 - 40, 30, -28, 0, this.client.player);
        }
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (KeyInit.screenKey.matchesKey(ch, keyCode) || Objects.requireNonNull(client).options.inventoryKey.matchesKey(ch, keyCode)) {
            this.close();
            return true;
        } else
            return super.keyPressed(ch, keyCode, modifiers);
    }

}
