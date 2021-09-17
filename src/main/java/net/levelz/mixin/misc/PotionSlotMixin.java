package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$PotionSlot")
public class PotionSlotMixin {

    @Inject(method = "Lnet/minecraft/screen/BrewingStandScreenHandler$PotionSlot;matches(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void matchesMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (stack.getItem() == Items.DRAGON_BREATH) {
            info.setReturnValue(true);
        }
    }

}
