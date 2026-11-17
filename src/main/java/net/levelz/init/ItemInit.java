package net.levelz.init;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.levelz.LevelzMain;
import net.levelz.item.RareCandyItem;
import net.levelz.item.StrangePotionItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ItemInit {

    public static final Item STRANGE_POTION = register("strange_potion", StrangePotionItem::new, new Item.Properties().stacksTo(1), CreativeModeTabs.FOOD_AND_DRINKS);
    public static final Item RARE_CANDY = register("rare_candy", RareCandyItem::new, new Item.Properties(), CreativeModeTabs.TOOLS_AND_UTILITIES);

    private static Item register(String id, Function<Item.Properties, Item> factory, Item.Properties settings, ResourceKey<CreativeModeTab> itemGroup) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, LevelzMain.identifierOf(id));
        Item item = Registry.register(BuiltInRegistries.ITEM, key, factory.apply(settings.setId(key)));
        CreativeModeTabEvents.modifyOutputEvent(itemGroup).register(entries -> entries.accept(item));
        return item;
    }

    public static void init() {
//        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
//            builder.registerItemRecipe(Items.DRAGON_BREATH, Items.NETHER_STAR, STRANGE_POTION);
//        });
    }

}
