package net.levelz.init;

import net.levelz.LevelzMain;
import net.levelz.item.RareCandyItem;
import net.levelz.item.StrangePotionItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemInit {

    public static Item STRANGE_POTION;
    public static Item RARE_CANDY;

    public static void register(BiConsumer<Identifier, Item> registrar) {
        STRANGE_POTION = register(registrar, "strange_potion", StrangePotionItem::new, new Item.Properties().stacksTo(1));
        RARE_CANDY = register(registrar, "rare_candy", RareCandyItem::new, new Item.Properties());
//        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
//            builder.registerItemRecipe(Items.DRAGON_BREATH, Items.NETHER_STAR, STRANGE_POTION);
//        });
    }

    private static Item register(BiConsumer<Identifier, Item> registrar, String id, Function<Item.Properties, Item> factory, Item.Properties settings) {
        Identifier identifier = LevelzMain.identifierOf(id);
        Item item = factory.apply(settings.setId(ResourceKey.create(Registries.ITEM, identifier)));
        registrar.accept(identifier, item);
        return item;
    }

    private static final ResourceKey<CreativeModeTab> FOOD_AND_DRINKS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("food_and_drinks"));
    private static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("tools_and_utilities"));

    public static void addToCreativeTab(ResourceKey<CreativeModeTab> tab, Consumer<Item> output) {
        if (FOOD_AND_DRINKS.equals(tab)) {
            output.accept(STRANGE_POTION);
        }
        if (TOOLS_AND_UTILITIES.equals(tab)) {
            output.accept(RARE_CANDY);
        }
    }

}
