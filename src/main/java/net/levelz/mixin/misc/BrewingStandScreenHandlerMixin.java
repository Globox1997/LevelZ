package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.registry.Registry;

@Mixin(BrewingStandScreenHandler.class)
public class BrewingStandScreenHandlerMixin {

    @Inject(method = "transferSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canInsert(Lnet/minecraft/item/ItemStack;)Z", ordinal = 1, shift = Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void transferSlotMixin(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> info, ItemStack itemStack, Slot slot, ItemStack itemStack2) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager(player);
        int playerBrewingLevel = playerStatsManager.getLevel("alchemy");
        if (playerBrewingLevel < ConfigInit.CONFIG.maxLevel) {
            if (playerStatsManager.lockedbrewingItemIds.contains(Registry.ITEM.getRawId(itemStack2.getItem()))) {
                info.setReturnValue(ItemStack.EMPTY);
            }
        }
    }
}
