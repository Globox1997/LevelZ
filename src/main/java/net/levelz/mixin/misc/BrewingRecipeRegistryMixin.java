package net.levelz.mixin.misc;

import net.levelz.init.ItemInit;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "hasRecipe", at = @At("HEAD"), cancellable = true)
    private void hasRecipeMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> info) {
        if (input.getItem() == Items.DRAGON_BREATH && ingredient.getItem() == Items.NETHER_STAR) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "isValidIngredient", at = @At("HEAD"), cancellable = true)
    private  void isValidIngredientMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (stack.getItem() == Items.NETHER_STAR) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private  void craftMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<ItemStack> info) {
        if (input.getItem() == Items.NETHER_STAR && ingredient.getItem() == Items.DRAGON_BREATH) {
            info.setReturnValue(new ItemStack(ItemInit.STRANGE_POTION));
        }
    }

}
