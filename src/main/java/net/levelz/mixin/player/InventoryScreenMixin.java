package net.levelz.mixin.player;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.gui.LevelzGui;
import net.levelz.gui.LevelzScreen;
import net.levelz.init.RenderInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    @Mutable
    @Final
    @Shadow
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();

    private boolean sliderOpen = false;

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
        if (this.sliderOpen)
            this.client.setScreen(new LevelzScreen(new LevelzGui(client)));
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    protected void drawBackgroundMixin(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo info) {

        if (!this.recipeBook.isOpen()) {
            RenderSystem.setShaderTexture(0, RenderInit.GUI_ICONS);
            this.drawTexture(matrices, this.x - 6, this.y + 6, 0, 80, 6, 20);
            if (this.isPointWithinBounds(-18, 6, 18, 20, (double) mouseX, (double) mouseY)) {
                if (this.isPointWithinBounds(-6, 6, 7, 20, (double) mouseX, (double) mouseY))
                    this.sliderOpen = true;
                if (this.sliderOpen)
                    this.drawTexture(matrices, this.x - 18, this.y + 6, 6, 80, 18, 20);
            } else
                this.sliderOpen = false;
        }

        PlayerStatsManager playerStatsManager = (((PlayerStatsManagerAccess) this.client.player).getPlayerStatsManager(this.client.player));
        // 0xAARRGGBB Format
        int color = 0xFFFFFF;
        if (playerStatsManager.getLevel("points") > 0)
            color = 1507303;

        matrices.push();
        matrices.scale(0.6F, 0.6F, 1F);
        matrices.translate((28 + this.x) / 0.6F, (8 + this.y + textRenderer.fontHeight / 2F) / 0.6F, 0);
        textRenderer.draw(matrices, new TranslatableText("text.levelz.gui.short_level", playerStatsManager.getLevel("level")), 0, -textRenderer.fontHeight / 2F, color);
        matrices.pop();
    }
}
