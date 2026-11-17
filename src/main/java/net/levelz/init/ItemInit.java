package net.levelz.init;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.levelz.LevelzMain;
import net.levelz.item.RareCandyItem;
import net.levelz.item.StrangePotionItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ItemInit {

    public static final Item STRANGE_POTION = register("strange_potion", new StrangePotionItem(new Item.Properties().stacksTo(1)), CreativeModeTabs.FOOD_AND_DRINKS);
    public static final Item RARE_CANDY = register("rare_candy", new RareCandyItem(new Item.Properties()), CreativeModeTabs.TOOLS_AND_UTILITIES);

    private static Item register(String id, Item item, ResourceKey<CreativeModeTab> itemGroup) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.accept(item));
        return register(LevelzMain.identifierOf(id), item);
    }

    private static Item register(ResourceLocation id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void init() {
//        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
//            builder.registerItemRecipe(Items.DRAGON_BREATH, Items.NETHER_STAR, STRANGE_POTION);
//        });
    }
}
