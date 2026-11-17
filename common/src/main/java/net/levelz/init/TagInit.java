package net.levelz.init;

import net.levelz.LevelzMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagInit {

    public static final TagKey<Item> RESTRICTED_FURNACE_EXPERIENCE_ITEMS = TagKey.create(Registries.ITEM, LevelzMain.identifierOf("restricted_furnace_experience_items"));
    public static final TagKey<Block> RESTRICTED_ORE_EXPERIENCE_BLOCKS = TagKey.create(Registries.BLOCK, LevelzMain.identifierOf("restricted_ore_experience_blocks"));
    public static final TagKey<EntityType<?>> RESTRICTED_ENTITY_EXPERIENCE_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, LevelzMain.identifierOf("restricted_entity_experience_entities"));

    public static final TagKey<Block> ORES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores"));
    public static final TagKey<Item> CROPS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "crops"));
}
