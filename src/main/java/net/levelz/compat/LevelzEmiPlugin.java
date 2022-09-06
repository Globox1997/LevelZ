package net.levelz.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiBrewingRecipe;
import net.levelz.init.ItemInit;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class LevelzEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipe(new EmiBrewingRecipe(EmiStack.of(Items.DRAGON_BREATH), EmiStack.of(Items.NETHER_STAR), EmiStack.of(ItemInit.STRANGE_POTION), new Identifier("levelz", "strange_potion")));
    }

}
