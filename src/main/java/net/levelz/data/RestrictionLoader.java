package net.levelz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.LevelzMain;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.level.PlayerRestriction;
import net.levelz.level.Skill;
import net.levelz.level.restriction.EnchantmentRestriction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class RestrictionLoader implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger("LevelZ");

    private List<Integer> blockList = new ArrayList<>();
    private List<Integer> craftingList = new ArrayList<>();
    private List<Integer> entityList = new ArrayList<>();
    private List<Integer> itemList = new ArrayList<>();
    private List<Integer> miningList = new ArrayList<>();
    private Map<String, List<Integer>> enchantmentList = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return LevelzMain.identifierOf("restriction");
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

                                    if (this.blockList.contains(blockRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        this.blockList.add(blockRawId);
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

                                    if (this.craftingList.contains(craftingRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        this.craftingList.add(craftingRawId);
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

                                    if (this.entityList.contains(entityRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        this.entityList.add(entityRawId);
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

                                    if (this.itemList.contains(itemRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        this.itemList.add(itemRawId);
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

                                    if (this.miningList.contains(miningRawId)) {
                                        continue;
                                    }
                                    if (replace) {
                                        this.miningList.add(miningRawId);
                                    }
                                    LevelManager.MINING_RESTRICTIONS.put(miningRawId, new PlayerRestriction(miningRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized mining id called {}.", mapKey, miningIdentifier);
                                }
                            }
                        }
                        // enchantments
                        if (restrictionJsonObject.has("enchantments")) {
                            Optional<RegistryWrapper.Impl<Enchantment>> wrapper = BuiltinRegistries.createWrapperLookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT);
                            if (wrapper.isPresent()) {
                                JsonObject enchantmentObject = restrictionJsonObject.getAsJsonObject("enchantments");
                                for (String enchantment : enchantmentObject.keySet()) {
                                    Identifier enchantmentIdentifier = Identifier.of(enchantment);
                                    Optional<RegistryEntry.Reference<Enchantment>> enchantmentReference = wrapper.get().getOptional(RegistryKey.of(RegistryKeys.ENCHANTMENT, enchantmentIdentifier));
                                    if (enchantmentReference.isPresent()) {
                                        int level = enchantmentObject.get(enchantment).getAsInt();
                                        if (this.enchantmentList.containsKey(enchantment) && this.enchantmentList.get(enchantment).contains(level)) {
                                            continue;
                                        }
                                        if (replace) {
                                            if (this.enchantmentList.containsKey(enchantment)) {
                                                this.enchantmentList.get(enchantment).add(level);
                                            } else {
                                                this.enchantmentList.put(enchantment, new ArrayList<>(level));
                                            }
                                        }

                                        if (LevelManager.ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentReference.get().getIdAsString())) {
                                            LevelManager.ENCHANTMENT_RESTRICTIONS.get(enchantmentReference.get().getIdAsString()).getSkillLevelRestrictions().put(level, skillLevelRestrictions);
                                        } else {
                                             Map<Integer, Map<Integer, Integer>> map = new HashMap<>();
                                            map.put(enchantmentObject.get(enchantment).getAsInt(), skillLevelRestrictions);
                                            LevelManager.ENCHANTMENT_RESTRICTIONS.put(enchantmentReference.get().getIdAsString(),
                                                    new EnchantmentRestriction(enchantmentReference.get(), map));
                                        }
                                    } else {
                                        LOGGER.warn("Restriction {} contains an unrecognized enchantment id called {}.", mapKey, enchantmentIdentifier);
                                    }
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
