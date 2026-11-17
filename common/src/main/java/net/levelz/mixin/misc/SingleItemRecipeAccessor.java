package net.levelz.mixin.misc;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SingleItemRecipe.class)
public interface SingleItemRecipeAccessor {

    @Invoker("result")
    ItemStackTemplate callResult();
}
