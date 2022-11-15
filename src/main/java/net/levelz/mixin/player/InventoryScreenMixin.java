package net.levelz.mixin.player;

import com.mojang.blaze3d.systems.RenderSystem;

import net.levelz.init.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
        if (this.client != null && ConfigInit.CONFIG.inventoryButton && this.focusedSlot == null && this.isPointWithinBounds(26, -20, 22, 19, (double) mouseX, (double) mouseY))
            this.client.setScreen(new LevelzScreen(new LevelzGui(client)));
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    protected void drawBackgroundMixin(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo info) {
        if (ConfigInit.CONFIG.inventoryButton) {
            RenderSystem.setShaderTexture(0, RenderInit.GUI_ICONS);
            // bag icon
            this.drawTexture(matrices, this.x, this.y - 23, 0, 110, 24, 27);
            // skill icon
            this.drawTexture(matrices, this.x + 25, this.y - 21, 48, 110, 24, 21);

            if (this.isPointWithinBounds(26, -20, 22, 19, (double) mouseX, (double) mouseY))
                this.renderTooltip(matrices, new TranslatableText("screen.levelz.skill_screen"), mouseX, mouseY);
        }

        assert this.client != null;
        assert this.client.player != null;
        if (ConfigInit.CONFIG.inventorySkillLevel) {
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
}