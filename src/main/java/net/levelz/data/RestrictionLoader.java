package net.levelz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.LevelzMain;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.restriction.ComponentRestriction;
import net.levelz.level.restriction.PlayerRestriction;
import net.levelz.registry.EnchantmentRegistry;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;

public record RestrictionLoader(RegistryWrapper.WrapperLookup wrapperLookup) implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = LevelzMain.identifierOf("restriction");

    private static final List<Integer> blockList = new ArrayList<>();
    private static final List<Integer> craftingList = new ArrayList<>();
    private static final List<Integer> entityList = new ArrayList<>();
    private static final List<Integer> itemList = new ArrayList<>();
    private static final List<Integer> miningList = new ArrayList<>();
    private static final List<Integer> enchantmentList = new ArrayList<>();
    private static final List<Identifier> dimensionList = new ArrayList<>();
    private static final List<Consumer<DynamicRegistryManager>> TAG_TASKS = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(SkillLoader.ID);
    }

    @Override
    public void reload(ResourceManager manager) {
        LevelManager.BLOCK_RESTRICTIONS.clear();
        LevelManager.CRAFTING_RESTRICTIONS.clear();
        LevelManager.ENTITY_RESTRICTIONS.clear();
        LevelManager.ITEM_RESTRICTIONS.clear();
        LevelManager.MINING_RESTRICTIONS.clear();
        LevelManager.ENCHANTMENT_RESTRICTIONS.clear();
        LevelManager.COMPONENT_RESTRICTIONS.clear();
        LevelManager.DIMENSION_RESTRICTIONS.clear();
        blockList.clear();
        craftingList.clear();
        entityList.clear();
        itemList.clear();
        miningList.clear();
        enchantmentList.clear();
        dimensionList.clear();
        TAG_TASKS.clear();

        if (!ConfigInit.CONFIG.restrictions) {
            return;
        }
        EnchantmentRegistry.updateEnchantments(this.wrapperLookup());
        ItemStringReader itemStringReader = new ItemStringReader(this.wrapperLookup());

        Map<String, Integer> skillKeyIdMap = new HashMap<>();
        for (Skill skill : LevelManager.SKILLS.values()) {
            skillKeyIdMap.put(skill.getKey(), skill.getId());
        }

        manager.findResources("restriction", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            if (!ConfigInit.CONFIG.defaultRestrictions && id.getPath().endsWith("/default.json")) {
                return;
            }
            JsonObject data;
            try (InputStreamReader reader = new InputStreamReader(resourceRef.getInputStream())) {
                data = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                DatapackErrors.report("Restriction file {} is not valid json: {}", id, e.getMessage());
                return;
            }
            for (String mapKey : data.keySet()) {
                String source = mapKey + " in " + id;
                try {
                    loadRestriction(data.get(mapKey), source, skillKeyIdMap, itemStringReader);
                } catch (Exception e) {
                    DatapackErrors.report("Restriction {} could not be loaded: {}", source, e.getMessage());
                }
            }
        });
    }

    public static void onTagsLoaded(DynamicRegistryManager registries) {
        for (Consumer<DynamicRegistryManager> task : TAG_TASKS) {
            task.accept(registries);
        }
        TAG_TASKS.clear();
    }

    private static void loadRestriction(JsonElement element, String source, Map<String, Integer> skillKeyIdMap, ItemStringReader itemStringReader) {
        if (!element.isJsonObject()) {
            DatapackErrors.report("Restriction {} has to be a json object.", source);
            return;
        }
        JsonObject restrictionJsonObject = element.getAsJsonObject();
        if (!restrictionJsonObject.has("skills") || !restrictionJsonObject.get("skills").isJsonObject()) {
            DatapackErrors.report("Restriction {} is missing the \"skills\" object.", source);
            return;
        }
        Map<Integer, Integer> skillLevelRestrictions = new HashMap<>();
        JsonObject skillRestrictions = restrictionJsonObject.getAsJsonObject("skills");
        for (String skillKey : skillRestrictions.keySet()) {
            if (skillKeyIdMap.containsKey(skillKey)) {
                skillLevelRestrictions.put(skillKeyIdMap.get(skillKey), skillRestrictions.get(skillKey).getAsInt());
            } else {
                DatapackErrors.report("Restriction {} contains the unknown skill {}. Known skills: {}", source, skillKey, skillKeyIdMap.keySet());
            }
        }
        if (skillLevelRestrictions.isEmpty()) {
            DatapackErrors.report("Restriction {} does not contain any valid skills.", source);
            return;
        }
        boolean replace = restrictionJsonObject.has("replace") && restrictionJsonObject.get("replace").getAsBoolean();

        loadEntries(restrictionJsonObject, "blocks", Registries.BLOCK, LevelManager.BLOCK_RESTRICTIONS, blockList, skillLevelRestrictions, replace, source);
        loadEntries(restrictionJsonObject, "crafting", Registries.ITEM, LevelManager.CRAFTING_RESTRICTIONS, craftingList, skillLevelRestrictions, replace, source);
        loadEntries(restrictionJsonObject, "entities", Registries.ENTITY_TYPE, LevelManager.ENTITY_RESTRICTIONS, entityList, skillLevelRestrictions, replace, source);
        loadEntries(restrictionJsonObject, "items", Registries.ITEM, LevelManager.ITEM_RESTRICTIONS, itemList, skillLevelRestrictions, replace, source);
        loadEntries(restrictionJsonObject, "mining", Registries.BLOCK, LevelManager.MINING_RESTRICTIONS, miningList, skillLevelRestrictions, replace, source);
        loadEnchantments(restrictionJsonObject, skillLevelRestrictions, replace, source);
        loadComponents(restrictionJsonObject, skillLevelRestrictions, source, itemStringReader);
        loadDimensions(restrictionJsonObject, skillLevelRestrictions, replace, source);
    }

    private static <T> void loadEntries(JsonObject restrictionJsonObject, String type, Registry<T> registry, Map<Integer, PlayerRestriction> restrictions, List<Integer> replaceList,
                                        Map<Integer, Integer> skillLevelRestrictions, boolean replace, String source) {
        if (!restrictionJsonObject.has(type)) {
            return;
        }
        if (!restrictionJsonObject.get(type).isJsonArray()) {
            DatapackErrors.report("\"{}\" of restriction {} has to be a list.", type, source);
            return;
        }
        for (JsonElement entryElement : restrictionJsonObject.getAsJsonArray(type)) {
            String entry = entryElement.getAsString();
            if (entry.startsWith("#")) {
                Identifier tagIdentifier = Identifier.tryParse(entry.substring(1));
                if (tagIdentifier == null) {
                    DatapackErrors.report("Restriction {} contains the invalid tag {} in \"{}\".", source, entry, type);
                    continue;
                }
                TagKey<T> tagKey = TagKey.of(registry.getKey(), tagIdentifier);
                TAG_TASKS.add(registries -> {
                    boolean found = false;
                    for (RegistryEntry<T> tagEntry : registry.iterateEntries(tagKey)) {
                        found = true;
                        addRestriction(registry.getRawId(tagEntry.value()), restrictions, replaceList, skillLevelRestrictions, replace, true);
                    }
                    if (!found) {
                        DatapackErrors.report("Restriction {} contains the tag {} in \"{}\" which is empty or does not exist.", source, entry, type);
                    }
                });
                continue;
            }
            Identifier identifier = Identifier.tryParse(entry);
            if (identifier == null || !registry.containsId(identifier)) {
                DatapackErrors.report("Restriction {} contains the unknown id {} in \"{}\".", source, entry, type);
                continue;
            }
            addRestriction(registry.getRawId(registry.get(identifier)), restrictions, replaceList, skillLevelRestrictions, replace, false);
        }
    }

    private static void loadEnchantments(JsonObject restrictionJsonObject, Map<Integer, Integer> skillLevelRestrictions, boolean replace, String source) {
        if (!restrictionJsonObject.has("enchantments")) {
            return;
        }
        if (!restrictionJsonObject.get("enchantments").isJsonObject()) {
            DatapackErrors.report("\"enchantments\" of restriction {} has to be an object like {\"minecraft:sharpness\": 1}.", source);
            return;
        }
        JsonObject enchantmentObject = restrictionJsonObject.getAsJsonObject("enchantments");
        for (String enchantment : enchantmentObject.keySet()) {
            int level = enchantmentObject.get(enchantment).getAsInt();
            if (enchantment.startsWith("#")) {
                Identifier tagIdentifier = Identifier.tryParse(enchantment.substring(1));
                if (tagIdentifier == null) {
                    DatapackErrors.report("Restriction {} contains the invalid enchantment tag {}.", source, enchantment);
                    continue;
                }
                TagKey<Enchantment> tagKey = TagKey.of(RegistryKeys.ENCHANTMENT, tagIdentifier);
                TAG_TASKS.add(registries -> {
                    boolean found = false;
                    for (RegistryEntry<Enchantment> tagEntry : registries.get(RegistryKeys.ENCHANTMENT).iterateEntries(tagKey)) {
                        found = true;
                        if (EnchantmentRegistry.containsId(tagEntry, level)) {
                            addRestriction(EnchantmentRegistry.getId(tagEntry, level), LevelManager.ENCHANTMENT_RESTRICTIONS, enchantmentList, skillLevelRestrictions, replace, true);
                        }
                    }
                    if (!found) {
                        DatapackErrors.report("Restriction {} contains the enchantment tag {} which is empty or does not exist.", source, enchantment);
                    }
                });
                continue;
            }
            Identifier enchantmentIdentifier = Identifier.tryParse(enchantment);
            if (enchantmentIdentifier != null && EnchantmentRegistry.containsId(enchantmentIdentifier, level)) {
                addRestriction(EnchantmentRegistry.getId(enchantmentIdentifier, level), LevelManager.ENCHANTMENT_RESTRICTIONS, enchantmentList, skillLevelRestrictions, replace, false);
            } else {
                DatapackErrors.report("Restriction {} contains the unknown enchantment {} or level {} is above its max level.", source, enchantment, level);
            }
        }
    }

    private static void loadComponents(JsonObject restrictionJsonObject, Map<Integer, Integer> skillLevelRestrictions, String source, ItemStringReader itemStringReader) {
        if (!restrictionJsonObject.has("components")) {
            return;
        }
        JsonElement componentElement = restrictionJsonObject.get("components");
        if (componentElement.isJsonObject() && componentElement.getAsJsonObject().isEmpty()) {
            return;
        }
        if (!componentElement.isJsonArray()) {
            DatapackErrors.report("\"components\" of restriction {} has to be a list of items like \"minecraft:potion[potion_contents={potion:'minecraft:strength'}]\".", source);
            return;
        }
        for (JsonElement element : componentElement.getAsJsonArray()) {
            String itemString = element.getAsString();
            try {
                ItemStringReader.ItemResult itemResult = itemStringReader.consume(new StringReader(itemString));
                if (itemResult.components().isEmpty()) {
                    DatapackErrors.report("Restriction {} contains {} in \"components\" without any component, use \"items\" instead.", source, itemString);
                    continue;
                }
                int itemRawId = Registries.ITEM.getRawId(itemResult.item().value());
                LevelManager.COMPONENT_RESTRICTIONS.computeIfAbsent(itemRawId, id -> new ArrayList<>()).add(new ComponentRestriction(itemRawId, itemResult.components(), skillLevelRestrictions));
            } catch (CommandSyntaxException e) {
                DatapackErrors.report("Restriction {} contains the invalid component item {}: {}", source, itemString, e.getMessage());
            }
        }
    }

    private static void loadDimensions(JsonObject restrictionJsonObject, Map<Integer, Integer> skillLevelRestrictions, boolean replace, String source) {
        if (!restrictionJsonObject.has("dimensions")) {
            return;
        }
        if (!restrictionJsonObject.get("dimensions").isJsonArray()) {
            DatapackErrors.report("\"dimensions\" of restriction {} has to be a list.", source);
            return;
        }
        for (JsonElement dimensionElement : restrictionJsonObject.getAsJsonArray("dimensions")) {
            Identifier dimensionIdentifier = Identifier.tryParse(dimensionElement.getAsString());
            if (dimensionIdentifier == null) {
                DatapackErrors.report("Restriction {} contains the invalid dimension id {}.", source, dimensionElement.getAsString());
                continue;
            }
            if (dimensionList.contains(dimensionIdentifier)) {
                continue;
            }
            if (replace) {
                dimensionList.add(dimensionIdentifier);
            }
            LevelManager.DIMENSION_RESTRICTIONS.put(dimensionIdentifier, new PlayerRestriction(0, skillLevelRestrictions));
            TAG_TASKS.add(registries -> registries.getOptional(RegistryKeys.DIMENSION).ifPresent(registry -> {
                if (!registry.containsId(dimensionIdentifier)) {
                    DatapackErrors.report("Restriction {} contains the dimension {} which does not exist.", source, dimensionIdentifier);
                }
            }));
        }
    }

    private static void addRestriction(int rawId, Map<Integer, PlayerRestriction> restrictions, List<Integer> replaceList, Map<Integer, Integer> skillLevelRestrictions, boolean replace,
                                       boolean fromTag) {
        if (replaceList.contains(rawId)) {
            return;
        }
        if (replace) {
            replaceList.add(rawId);
        } else if (fromTag && restrictions.containsKey(rawId)) {
            return;
        }
        restrictions.put(rawId, new PlayerRestriction(rawId, skillLevelRestrictions));
    }
}
