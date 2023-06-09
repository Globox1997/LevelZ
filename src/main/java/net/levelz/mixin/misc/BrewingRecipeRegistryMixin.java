package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.init.ItemInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "registerPotionRecipe", at = @At(value = "HEAD"))
    private static void registerPotionRecipe(Potion input, Item item, Potion output, CallbackInfo info) {
        if (output != Potions.MUNDANE && output != Potions.THICK) {
            LevelLists.potionList.add(item);
            LevelLists.potionList.add(output);
        }
    }

    @Inject(method = "hasRecipe", at = @At("HEAD"), cancellable = true)
    private static void hasRecipeMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> info) {
        if (input.getItem() == Items.DRAGON_BREATH && ingredient.getItem() == Items.NETHER_STAR) {
            info.setReturnValue(true);
        }

    }

    @Inject(method = "isValidIngredient", at = @At("HEAD"), cancellable = true)
    private static void isValidIngredientMixin(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (stack.getItem() == Items.NETHER_STAR) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionUtil;getPotion(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;"), cancellable = true)
    private static void craftMixin(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<ItemStack> info) {
        if (input.getItem() == Items.NETHER_STAR && ingredient.getItem() == Items.DRAGON_BREATH) {
            info.setReturnValue(new ItemStack(ItemInit.STRANGE_POTION));
        }
    }

}
