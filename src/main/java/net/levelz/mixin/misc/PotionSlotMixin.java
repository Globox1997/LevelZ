package net.levelz.mixin.misc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class PotionSlotMixin {

    @Inject(method = "mayPlaceItem(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void matchesMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (stack.getItem() == Items.DRAGON_BREATH) {
            info.setReturnValue(true);
        }
    }

}
