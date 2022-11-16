package net.levelz.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LibGui;
import net.fabricmc.api.Environment;
import net.levelz.compat.InventorioScreenCompatibility;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.init.RenderInit;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);
        assert this.client != null;

        RenderSystem.setShaderTexture(0, RenderInit.GUI_ICONS);
        if (LibGui.isDarkMode()) {
            // bag icon
            this.drawTexture(matrices, this.left, this.top - 21, 120, 110, 24, 25);
            // skill icon
            this.drawTexture(matrices, this.left + 25, this.top - 23, 168, 110, 24, 27);
        } else {
            // bag icon
            this.drawTexture(matrices, this.left, this.top - 21, 24, 110, 24, 25);
            // skill icon
            this.drawTexture(matrices, this.left + 25, this.top - 23, 72, 110, 24, 27);
        }
        if (this.isPointWithinIconBounds(1, 23, (double) mouseX, (double) mouseY))
            this.renderTooltip(matrices, new TranslatableText("container.inventory"), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.client != null) {
            assert this.client.player != null;
            if (this.isPointWithinIconBounds(1, 23, (double) mouseX, (double) mouseY)) {
                if (RenderInit.isInventorioLoaded)
                    InventorioScreenCompatibility.setInventorioScreen(client);
                else
                    this.client.setScreen(new InventoryScreen(this.client.player));
            } else if (this.isPointWithinIconBounds(26, 23, (double) mouseX, (double) mouseY)) {
                this.client.setScreen(new LevelzScreen(new LevelzGui(client)));
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (client.options.inventoryKey.matchesKey(ch, keyCode)) {
            if (ConfigInit.CONFIG.switch_screen)
                this.client.setScreen(new LevelzScreen(new LevelzGui(client)));
            else
                this.close();
            return true;
        } else if (KeyInit.screenKey.matchesKey(ch, keyCode)) {
            this.client.setScreen(new LevelzScreen(new LevelzGui(client)));
            return true;
        } else
            return super.keyPressed(ch, keyCode, modifiers);

    }

    private boolean isPointWithinIconBounds(int x, int width, double pointX, double pointY) {
        int i = this.left;
        int j = this.top;
        return (pointX -= (double) i) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) j) >= (double) (-20 - 1) && pointY < (double) (3 + 1);
    }

}