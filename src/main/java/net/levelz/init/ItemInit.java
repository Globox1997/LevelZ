package net.levelz.init;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.levelz.LevelzMain;
import net.levelz.item.RareCandyItem;
import net.levelz.item.StrangePotionItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ItemInit {

    public static final Item STRANGE_POTION = register("strange_potion", new StrangePotionItem(new Item.Settings().maxCount(1)), ItemGroups.FOOD_AND_DRINK);
    public static final Item RARE_CANDY = register("rare_candy", new RareCandyItem(new Item.Settings()), ItemGroups.TOOLS);

    private static Item register(String id, Item item, RegistryKey<ItemGroup> itemGroup) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
        return register(LevelzMain.identifierOf(id), item);
    }

    private static Item register(Identifier id, Item item) {
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void init() {
//        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
//            builder.registerItemRecipe(Items.DRAGON_BREATH, Items.NETHER_STAR, STRANGE_POTION);
//        });
    }
}
