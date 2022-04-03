package net.levelz.init;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagInit {

    public static final TagKey<Item> FARM_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("levelz", "farm_items"));

    public static void init() {
    }
}
