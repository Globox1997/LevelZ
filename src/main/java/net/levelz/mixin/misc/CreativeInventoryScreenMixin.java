package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeScreenHandler> {

    public CreativeInventoryScreenMixin(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "keyReleased", at = @At("HEAD"))
    private void keyReleasedMixin(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (this.focusedSlot != null && this.focusedSlot.hasStack() && ConfigInit.CONFIG.devMode && KeyInit.devKey.matchesKey(keyCode, scanCode) && this.client.player != null) {
            this.client.player.sendMessage(new TranslatableText("Added ID: " + Registry.ITEM.getId(this.focusedSlot.getStack().getItem()).toString()), true);
            KeyInit.writeId(Registry.ITEM.getId(this.focusedSlot.getStack().getItem()).toString());
        }
    }

}