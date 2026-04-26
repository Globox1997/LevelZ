package net.levelz.init;

import net.levelz.LevelzMain;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class TagInit {

    public static final TagKey<Item> RESTRICTED_FURNACE_EXPERIENCE_ITEMS = TagKey.of(RegistryKeys.ITEM, LevelzMain.identifierOf("restricted_furnace_experience_items"));
    public static final TagKey<Block> RESTRICTED_ORE_EXPERIENCE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, LevelzMain.identifierOf("restricted_ore_experience_blocks"));
    public static final TagKey<EntityType<?>> RESTRICTED_ENTITY_EXPERIENCE_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, LevelzMain.identifierOf("restricted_entity_experience_entities"));

    public static void init() {
    }
}
