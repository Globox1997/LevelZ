package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends EffectRenderingInventoryScreen<ItemPickerMenu> {

    public CreativeInventoryScreenMixin(ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "keyReleased", at = @At("HEAD"))
    private void keyReleasedMixin(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem() && ConfigInit.CONFIG.devMode && KeyInit.devKey.matches(keyCode, scanCode) && this.minecraft.player != null) {
            this.minecraft.player.sendSystemMessage(Component.translatable("text.levelz.dev.added_id", BuiltInRegistries.ITEM.getKey(this.hoveredSlot.getItem().getItem()).toString()));
            KeyInit.writeId(BuiltInRegistries.ITEM.getKey(this.hoveredSlot.getItem().getItem()).toString());
        }
    }

}
