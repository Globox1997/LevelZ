package net.levelz.mixin.compat;

import com.mojang.blaze3d.systems.RenderSystem;

import net.levelz.init.ConfigInit;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.lizardofoz.inventorio.client.ui.InventorioScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.gui.LevelzGui;
import net.levelz.gui.LevelzScreen;
import net.levelz.init.RenderInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(InventorioScreen.class)
public abstract class InventorioScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    @Mutable
    @Final
    @Shadow
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();

    private boolean sliderOpen = false;

    private InventorioScreenMixin(PlayerScreenHandler playerScreenHandler, PlayerInventory playerInventory, Text text) {
        super(playerScreenHandler, playerInventory, text);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
        if (this.sliderOpen && this.focusedSlot == null) {
            assert this.client != null;
            this.client.setScreen(new LevelzScreen(new LevelzGui(client)));
        }
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    protected void drawBackgroundMixin(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo info) {
        if (!this.recipeBook.isOpen() && ConfigInit.CONFIG.inventoryButton) {
            RenderSystem.setShaderTexture(0, RenderInit.GUI_ICONS);
            this.drawTexture(matrices, this.x - 6, this.y + 6, 0, 80, 6, 20);
            if (this.isPointWithinBounds(-18, 6, 18, 20, mouseX, mouseY)) {
                if (this.isPointWithinBounds(-6, 6, 7, 20, mouseX, mouseY))
                    this.sliderOpen = true;
                if (this.sliderOpen)
                    this.drawTexture(matrices, this.x - 18, this.y + 6, 6, 80, 18, 20);
            } else
                this.sliderOpen = false;
        }

        assert this.client != null;
        assert this.client.player != null;
        PlayerStatsManager playerStatsManager = (((PlayerStatsManagerAccess) this.client.player).getPlayerStatsManager());
        // 0xAARRGGBB Format
        int color = 0xFFFFFF;
        if (playerStatsManager.getLevel("points") > 0)
            color = 1507303;

        matrices.push();
        matrices.scale(0.6F, 0.6F, 1F);
        matrices.translate((28 + this.x) / 0.6F, (8 + this.y + textRenderer.fontHeight / 2F) / 0.6F, 0);
        textRenderer.draw(matrices, Text.translatable("text.levelz.gui.short_level", playerStatsManager.getLevel("level")), 0, -textRenderer.fontHeight / 2F, color);
        matrices.pop();
    }
}
