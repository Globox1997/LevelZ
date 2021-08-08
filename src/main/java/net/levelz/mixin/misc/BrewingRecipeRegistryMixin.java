package net.levelz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.minecraft.item.Item;
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

}
