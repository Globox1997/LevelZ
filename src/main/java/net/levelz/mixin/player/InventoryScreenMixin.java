package net.levelz.mixin.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

    public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    protected void drawBackgroundMixin(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo info) {
        assert this.minecraft != null;
        assert this.minecraft.player != null;
        if (ConfigInit.CONFIG.inventorySkillLevel) {
            LevelManager levelManager = (((LevelManagerAccess) this.minecraft.player).getLevelManager());
            // 0xAARRGGBB Format
            int color = 0xFFFFFF;
            if (levelManager.getSkillPoints() > 0) {
                color = 1507303;
            }

            context.pose().pushPose();
            context.pose().scale(0.6F, 0.6F, 1F);
            context.pose().translate((28 + ConfigInit.CONFIG.inventorySkillLevelPosX + this.leftPos) / 0.6F,
                    (8 + ConfigInit.CONFIG.inventorySkillLevelPosY + this.topPos + font.lineHeight / 2F) / 0.6F, 70.0D);
            context.drawString(this.font, Component.translatable("text.levelz.gui.short_level", levelManager.getOverallLevel()), 0, -font.lineHeight / 2, color, false);
            context.pose().popPose();
        }
    }
}
