package net.levelz.mixin.misc;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BrewingStandScreenHandler.class)
public class BrewingStandScreenHandlerMixin {

    @Inject(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canInsert(Lnet/minecraft/item/ItemStack;)Z", ordinal = 1, shift = Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void transferSlotMixin(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> info, ItemStack itemStack, Slot slot, ItemStack itemStack2) {
        if (player.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasRequiredCraftingLevel(itemStack2.getItem())) {
            info.setReturnValue(ItemStack.EMPTY);
        }
    }
}
