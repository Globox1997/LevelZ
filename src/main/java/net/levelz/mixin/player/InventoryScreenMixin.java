package net.levelz.mixin.player;

import net.levelz.init.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    protected void drawBackgroundMixin(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
        assert this.client != null;
        assert this.client.player != null;
        if (ConfigInit.CONFIG.inventorySkillLevel) {
            PlayerStatsManager playerStatsManager = (((PlayerStatsManagerAccess) this.client.player).getPlayerStatsManager());
            // 0xAARRGGBB Format
            int color = 0xFFFFFF;
            if (playerStatsManager.getSkillPoints() > 0)
                color = 1507303;

            context.getMatrices().push();
            context.getMatrices().scale(0.6F, 0.6F, 1F);
            context.getMatrices().translate((28 + ConfigInit.CONFIG.inventorySkillLevelPosX + this.x) / 0.6F,
                    (8 + ConfigInit.CONFIG.inventorySkillLevelPosY + this.y + textRenderer.fontHeight / 2F) / 0.6F, 70.0D);
            context.drawText(this.textRenderer, Text.translatable("text.levelz.gui.short_level", playerStatsManager.getOverallLevel()), 0, -textRenderer.fontHeight / 2, color, false);
            context.getMatrices().pop();
        }
    }
}
