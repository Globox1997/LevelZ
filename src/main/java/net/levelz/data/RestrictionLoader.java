package net.levelz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.LevelzMain;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.restriction.PlayerRestriction;
import net.levelz.registry.EnchantmentRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RestrictionLoader(RegistryWrapper.WrapperLookup wrapperLookup) implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = LevelzMain.identifierOf("restriction");

    private static final Logger LOGGER = LogManager.getLogger("LevelZ");

    private static final List<Integer> blockList = new ArrayList<>();
    private static final List<Integer> craftingList = new ArrayList<>();
    private static final List<Integer> entityList = new ArrayList<>();
    private static final List<Integer> itemList = new ArrayList<>();
    private static final List<Integer> miningList = new ArrayList<>();
    private static final List<Integer> enchantmentList = new ArrayList<>();

//    private ItemStringReader itemStringReader = new ItemStringReader(BuiltinRegistries.createWrapperLookup());

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {

        LevelManager.BLOCK_RESTRICTIONS.clear();
        LevelManager.CRAFTING_RESTRICTIONS.clear();
        LevelManager.ENTITY_RESTRICTIONS.clear();
        LevelManager.ITEM_RESTRICTIONS.clear();
        LevelManager.MINING_RESTRICTIONS.clear();
        LevelManager.ENCHANTMENT_RESTRICTIONS.clear();

        if (!ConfigInit.CONFIG.restrictions) {
            return;
        }
        EnchantmentRegistry.updateEnchantments(this.wrapperLookup());

        manager.findResources("restriction", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                if (!ConfigInit.CONFIG.defaultRestrictions && id.getPath().endsWith("/default.json")) {
                    return;
                }
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                Map<String, Integer> skillKeyIdMap = new HashMap<>();
                for (Skill skill : LevelManager.SKILLS.values()) {
                    skillKeyIdMap.put(skill.getKey(), skill.getId());
                }

                for (String mapKey : data.keySet()) {
                    JsonObject restrictionJsonObject = data.getAsJsonObject(mapKey);
                    Map<Integer, Integer> skillLevelRestrictions = new HashMap<>();
                    boolean replace = restrictionJsonObject.has("replace") && restrictionJsonObject.get("replace").getAsBoolean();

                    JsonObject skillRestrictions = restrictionJsonObject.getAsJsonObject("skills");
                    for (String skillKey : skillRestrictions.keySet()) {
                        if (skillKeyIdMap.containsKey(skillKey)) {
                            skillLevelRestrictions.put(skillKeyIdMap.get(skillKey), skillRestrictions.get(skillKey).getAsInt());
                        } else {
                            LOGGER.warn("Restriction {} contains an unrecognized skill called {}.", mapKey, skillKey);
                        }
                    }

                    if (!skillLevelRestrictions.isEmpty()) {
                        // blocks
                        if (restrictionJsonObject.has("blocks")) {
                            for (JsonElement blockElement : restrictionJsonObject.getAsJsonArray("blocks")) {
                                Identifier blockIdentifier = Identifier.of(blockElement.getAsString());
                                if (Registries.BLOCK.containsId(blockIdentifier)) {
                                    int blockRawId = Registries.BLOCK.getRawId(Registries.BLOCK.get(blockIdentifier));

                                    if (blockList.contains(blockRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        blockList.add(blockRawId);
                                    }
                                    LevelManager.BLOCK_RESTRICTIONS.put(blockRawId, new PlayerRestriction(blockRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized block id called {}.", mapKey, blockIdentifier);
                                }
                            }
                        }
                        // crafting
                        if (restrictionJsonObject.has("crafting")) {
                            for (JsonElement craftingElement : restrictionJsonObject.getAsJsonArray("crafting")) {
                                Identifier craftingIdentifier = Identifier.of(craftingElement.getAsString());
                                if (Registries.ITEM.containsId(craftingIdentifier)) {
                                    int craftingRawId = Registries.ITEM.getRawId(Registries.ITEM.get(craftingIdentifier));

                                    if (craftingList.contains(craftingRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        craftingList.add(craftingRawId);
                                    }
                                    LevelManager.CRAFTING_RESTRICTIONS.put(craftingRawId, new PlayerRestriction(craftingRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized crafting id called {}.", mapKey, craftingIdentifier);
                                }
                            }
                        }
                        // entities
                        if (restrictionJsonObject.has("entities")) {
                            for (JsonElement entityElement : restrictionJsonObject.getAsJsonArray("entities")) {
                                Identifier entityIdentifier = Identifier.of(entityElement.getAsString());
                                if (Registries.ENTITY_TYPE.containsId(entityIdentifier)) {
                                    int entityRawId = Registries.ENTITY_TYPE.getRawId(Registries.ENTITY_TYPE.get(entityIdentifier));

                                    if (entityList.contains(entityRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        entityList.add(entityRawId);
                                    }
                                    LevelManager.ENTITY_RESTRICTIONS.put(entityRawId, new PlayerRestriction(entityRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized entity id called {}.", mapKey, entityIdentifier);
                                }
                            }
                        }
                        // items
                        if (restrictionJsonObject.has("items")) {
                            for (JsonElement itemElement : restrictionJsonObject.getAsJsonArray("items")) {
                                Identifier itemIdentifier = Identifier.of(itemElement.getAsString());
                                if (Registries.ITEM.containsId(itemIdentifier)) {
                                    int itemRawId = Registries.ITEM.getRawId(Registries.ITEM.get(itemIdentifier));

                                    if (itemList.contains(itemRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        itemList.add(itemRawId);
                                    }
                                    LevelManager.ITEM_RESTRICTIONS.put(itemRawId, new PlayerRestriction(itemRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized item id called {}.", mapKey, itemIdentifier);
                                }
                            }
                        }
                        // mining
                        if (restrictionJsonObject.has("mining")) {
                            for (JsonElement miningElement : restrictionJsonObject.getAsJsonArray("mining")) {
                                Identifier miningIdentifier = Identifier.of(miningElement.getAsString());
                                if (Registries.BLOCK.containsId(miningIdentifier)) {
                                    int miningRawId = Registries.BLOCK.getRawId(Registries.BLOCK.get(miningIdentifier));

                                    if (miningList.contains(miningRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        miningList.add(miningRawId);
                                    }
                                    LevelManager.MINING_RESTRICTIONS.put(miningRawId, new PlayerRestriction(miningRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized mining id called {}.", mapKey, miningIdentifier);
                                }
                            }
                        }
                        // enchantments
                        if (restrictionJsonObject.has("enchantments")) {
                            JsonObject enchantmentObject = restrictionJsonObject.getAsJsonObject("enchantments");
                            for (String enchantment : enchantmentObject.keySet()) {
                                Identifier enchantmentIdentifier = Identifier.of(enchantment);
                                int level = enchantmentObject.get(enchantment).getAsInt();
                                if (EnchantmentRegistry.containsId(enchantmentIdentifier, level)) {
                                    int enchantmentRawId = EnchantmentRegistry.getId(enchantmentIdentifier, level);
                                    if (enchantmentList.contains(enchantmentRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        enchantmentList.add(enchantmentRawId);
                                    }
                                    LevelManager.ENCHANTMENT_RESTRICTIONS.put(enchantmentRawId, new PlayerRestriction(enchantmentRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized enchantment id called {}.", mapKey, enchantmentIdentifier);
                                }
                            }
                        }
                        // Todo: Test
                        if (restrictionJsonObject.has("components")) {
//                            System.out.println(this.itemStringReader.consume(new StringReader("potion[potion_contents={potion:\"fire_resistance\"}]")));
//                            System.out.println(this.itemStringReader.consume(new StringReader("potion[potion_contents={potion:\"fire_resistance\"}]")).components());
//                            System.out.println(this.itemStringReader.consume(new StringReader("potion[potion_contents={potion:\"fire_resistance\"}]")).item().value());
//                            Registries.ENCHANTMENT.

                            JsonObject componentObject = restrictionJsonObject.getAsJsonObject("components");
                            for (String component : componentObject.keySet()) {
                                Identifier itemIdentifier = Identifier.of(component);
                                if (Registries.ITEM.containsId(itemIdentifier)) {
                                    if (Registries.DATA_COMPONENT_TYPE.containsId(Identifier.of(componentObject.get(component).getAsString()))) {
                                        int itemRawId = Registries.ITEM.getRawId(Registries.ITEM.get(itemIdentifier));
                                    } else {
                                        LOGGER.warn("Restriction {} contains an unrecognized component called {}.", mapKey, componentObject.get(component).getAsString());
                                    }
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized item id at component called {}.", mapKey, itemIdentifier);
                                }
                            }
                        }
                    } else {
                        LOGGER.warn("Restriction {} does not contain any valid skills.", mapKey);
                    }
                }

            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }
}
