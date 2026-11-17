package net.levelz.mixin.misc;

import net.levelz.init.ItemInit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void hasRecipeMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> info) {
        if (input.getItem() == Items.DRAGON_BREATH && ingredient.getItem() == Items.NETHER_STAR) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private  void isValidIngredientMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (stack.getItem() == Items.NETHER_STAR) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "mix", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private  void craftMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<ItemStack> info) {
        if (input.getItem() == Items.NETHER_STAR && ingredient.getItem() == Items.DRAGON_BREATH) {
            info.setReturnValue(new ItemStack(ItemInit.STRANGE_POTION));
        }
    }

}
