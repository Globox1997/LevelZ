package net.levelz.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LibGui;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.compat.InventorioScreenCompatibility;
import net.levelz.init.KeyInit;
import net.levelz.init.RenderInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class LevelzScreen extends CottonClientScreen {

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
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, RenderInit.GUI_ICONS);
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) this.client.player).getPlayerStatsManager(this.client.player);
            int m = (int) (playerStatsManager.levelProgress * 131.0F);
            int x = scaledWidth / 2 - 41;
            int n = scaledHeight / 2 - 45;
            LevelzScreen.drawTexture(matrices, x, n, 130, 5, 0F, 100F, 182, 5, 256, 256);
            if (m > 0) {
                this.drawTexture(matrices, x, n, 0, 105, m, 5);
            }
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
        }

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
        if (this.client != null && this.isPointWithinIconBounds(1, 23, (double) mouseX, (double) mouseY)) {
            assert this.client.player != null;
            if (RenderInit.isInventorioLoaded)
                InventorioScreenCompatibility.setInventorioScreen(client);
            else
                this.client.setScreen(new InventoryScreen(this.client.player));
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (KeyInit.screenKey.matchesKey(ch, keyCode) || Objects.requireNonNull(client).options.inventoryKey.matchesKey(ch, keyCode)) {
            this.close();
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