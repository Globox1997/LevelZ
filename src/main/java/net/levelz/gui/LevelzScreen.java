package net.levelz.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.KeyInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class LevelzScreen extends CottonClientScreen {
    public LevelzScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        super.render(matrices, mouseX, mouseY, partialTicks);
        if (this.client.player != null) {
            int scaledWidth = this.client.getWindow().getScaledWidth();
            int scaledHeight = this.client.getWindow().getScaledHeight();
            InventoryScreen.drawEntity(scaledWidth / 2 - 75, scaledHeight / 2 - 40, 30, -28, 0, this.client.player);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) this.client.player).getPlayerStatsManager(this.client.player);
            int m = (int) (playerStatsManager.levelProgress * 131.0F);
            int x = scaledWidth / 2 - 41;
            int n = scaledHeight / 2 - 45;
            LevelzScreen.drawTexture(matrices, x, n, 130, 5, 0F, 64F, 182, 5, 256, 256);
            if (m > 0) {
                this.drawTexture(matrices, x, n, 0, 69, m, 5);
            }
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
        }
    }

    @Override
    public boolean keyPressed(int ch, int keyCode, int modifiers) {
        if (KeyInit.screenKey.matchesKey(ch, keyCode) || client.options.keyInventory.matchesKey(ch, keyCode)) {
            this.onClose();
            return true;
        } else
            return super.keyPressed(ch, keyCode, modifiers);

    }

}