package net.levelz.mixin.player;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {

    public InventoryScreenMixin(InventoryMenu screenHandler, RecipeBookComponent<?> recipeBookComponent, Inventory playerInventory, Component text) {
        super(screenHandler, recipeBookComponent, playerInventory, text);
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    protected void drawBackgroundMixin(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        assert this.minecraft != null;
        assert this.minecraft.player != null;
        if (ConfigInit.CONFIG.inventorySkillLevel) {
            LevelManager levelManager = (((LevelManagerAccess) this.minecraft.player).getLevelManager());
            // 0xAARRGGBB Format
            int color = 0xFFFFFFFF;
            if (levelManager.getSkillPoints() > 0) {
                color = 0xFF16FFE7;
            }

            context.pose().pushMatrix();
            context.pose().scale(0.6F, 0.6F);
            context.pose().translate((28 + ConfigInit.CONFIG.inventorySkillLevelPosX + this.leftPos) / 0.6F,
                    (8 + ConfigInit.CONFIG.inventorySkillLevelPosY + this.topPos + font.lineHeight / 2F) / 0.6F);
            context.text(this.font, Component.translatable("text.levelz.gui.short_level", levelManager.getOverallLevel()), 0, -font.lineHeight / 2, color, false);
            context.pose().popMatrix();
        }
    }
}
