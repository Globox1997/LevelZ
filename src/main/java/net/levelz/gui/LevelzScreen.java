package net.levelz.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LibGui;
import me.lizardofoz.inventorio.api.InventorioAPI;
import me.lizardofoz.inventorio.client.ui.InventorioScreen;
import me.lizardofoz.inventorio.player.InventorioScreenHandler;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.init.RenderInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class LevelzScreen extends CottonClientScreen {

    private boolean sliderOpen = false;

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

        if (ConfigInit.CONFIG.inventoryButton) {
            RenderSystem.setShaderTexture(0, RenderInit.GUI_ICONS);
            if (LibGui.isDarkMode())
                this.drawTexture(matrices, this.left - 6, this.top + 6, 72, 80, 6, 20);
            else
                this.drawTexture(matrices, this.left - 6, this.top + 6, 48, 80, 6, 20);
            if (this.isPointWithinBounds(-18, 18, (double) mouseX, (double) mouseY)) {
                if (this.isPointWithinBounds(-6, 7, (double) mouseX, (double) mouseY))
                    this.sliderOpen = true;
                if (this.sliderOpen)
                    if (LibGui.isDarkMode())
                        this.drawTexture(matrices, this.left - 18, this.top + 6, 78, 80, 18, 20);
                    else
                        this.drawTexture(matrices, this.left - 18, this.top + 6, 54, 80, 18, 20);
            } else
                this.sliderOpen = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.sliderOpen) {
            assert this.client != null;
            assert this.client.player != null;
            if (this.client.player.currentScreenHandler.getClass().getName().equals("net.minecraft.screen.PlayerScreenHandler")) {
                this.client.setScreen(new InventoryScreen(this.client.player));
            } else if (this.client.player.currentScreenHandler.getClass().getName().equals("me.lizardofoz.inventorio.player.InventorioScreenHandler")) {
                this.client.setScreen(new InventorioScreen(new InventorioScreenHandler(0, this.client.player.getInventory()), this.client.player.getInventory()));
            }
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

    private boolean isPointWithinBounds(int x, int width, double pointX, double pointY) {
        int i = this.left;
        int j = this.top;
        return (pointX -= (double) i) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) j) >= (double) (6 - 1) && pointY < (double) (6 + 20 + 1);
    }

}