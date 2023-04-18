package net.levelz.init;

import net.levelz.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {

    public static final Item STRANGE_POTION = new StrangePotionItem(new Item.Settings().group(ItemGroup.BREWING).maxCount(1));
    public static final Item RARE_CANDY = new RareCandyItem(new Item.Settings().group(ItemGroup.MISC));

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("levelz", "strange_potion"), STRANGE_POTION);
        Registry.register(Registry.ITEM, new Identifier("levelz", "rare_candy"), RARE_CANDY);
    }
}
