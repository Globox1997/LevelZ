package net.levelz.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
//import me.shedaniel.rei.api.common.util.EntryIngredients;
//import me.shedaniel.rei.plugin.common.displays.brewing.DefaultBrewingDisplay;
//import net.levelz.init.ItemInit;
//import net.minecraft.world.item.Items;

public class LevelzReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
//        registry.add(new DefaultBrewingDisplay(EntryIngredients.of(Items.DRAGON_BREATH, 1), EntryIngredients.of(Items.NETHER_STAR, 1), EntryIngredients.of(ItemInit.STRANGE_POTION, 1)));
    }

}
