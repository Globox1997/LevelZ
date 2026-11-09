package net.levelz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.LevelzMain;
import net.levelz.editor.RestrictionEntry;
import net.levelz.editor.RestrictionOverrides;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.restriction.ComponentRestriction;
import net.levelz.level.restriction.PlayerRestriction;
import net.levelz.registry.EnchantmentRegistry;
import net.levelz.util.PacketHelper;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;

public record RestrictionLoader(RegistryWrapper.WrapperLookup wrapperLookup) implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = LevelzMain.identifierOf("restriction");

    public static final List<RestrictionEntry> ENTRIES = new ArrayList<>();

    private static final List<Integer> blockList = new ArrayList<>();
    private static final List<Integer> craftingList = new ArrayList<>();
    private static final List<Integer> entityList = new ArrayList<>();
    private static final List<Integer> itemList = new ArrayList<>();
    private static final List<Integer> miningList = new ArrayList<>();
    private static final List<Integer> enchantmentList = new ArrayList<>();
    private static final List<Identifier> dimensionList = new ArrayList<>();
    private static final List<Consumer<DynamicRegistryManager>> TAG_TASKS = new ArrayList<>();

    private enum Mode {
        DATAPACK, OVERRIDE, REMOVE
    }

    private interface EntryAction {
        void accept(int rawId, boolean fromTag);
    }

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
        ENTRIES.clear();
        blockList.clear();
        craftingList.clear();
        entityList.clear();
        itemList.clear();
        miningList.clear();
        enchantmentList.clear();
        dimensionList.clear();
        TAG_TASKS.clear();
        DatapackErrors.clearRestrictionErrors();

        if (!ConfigInit.CONFIG.restrictions) {
            return;
        }
        EnchantmentRegistry.updateEnchantments(this.wrapperLookup());
        ItemStringReader itemStringReader = new ItemStringReader(this.wrapperLookup());
        Map<String, Integer> skillKeyIdMap = getSkillKeyIdMap();

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
                ENTRIES.add(new RestrictionEntry(id.toString(), mapKey, data.get(mapKey).toString()));
                loadRestrictionSafely(data.get(mapKey), mapKey + " in " + id, skillKeyIdMap, itemStringReader, false);
            }
        });
    }

    public static void onTagsLoaded(DynamicRegistryManager registries) {
        for (Consumer<DynamicRegistryManager> task : TAG_TASKS) {
            task.accept(registries);
        }
        TAG_TASKS.clear();
    }

    public static void applyOverrides(MinecraftServer server) {
        if (!ConfigInit.CONFIG.restrictions) {
            return;
        }
        JsonObject overrides = RestrictionOverrides.read(server);
        if (overrides == null) {
            return;
        }
        ItemStringReader itemStringReader = new ItemStringReader(server.getRegistryManager());
        Map<String, Integer> skillKeyIdMap = getSkillKeyIdMap();
        for (String mapKey : overrides.keySet()) {
            ENTRIES.add(new RestrictionEntry(RestrictionOverrides.SOURCE, mapKey, overrides.get(mapKey).toString()));
            loadRestrictionSafely(overrides.get(mapKey), getOverrideSource(mapKey), skillKeyIdMap, itemStringReader, true);
        }
        onTagsLoaded(server.getRegistryManager());
    }

    public static void reloadRestrictions(MinecraftServer server) {
        new RestrictionLoader(server.getRegistryManager()).reload(server.getResourceManager());
        onTagsLoaded(server.getRegistryManager());
        applyOverrides(server);
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            PacketHelper.syncEnchantments(serverPlayerEntity);
            PacketHelper.updateRestrictions(serverPlayerEntity);
        }
    }

    public static String getOverrideSource(String mapKey) {
        return mapKey + " in " + RestrictionOverrides.SOURCE;
    }

    private static Map<String, Integer> getSkillKeyIdMap() {
        Map<String, Integer> skillKeyIdMap = new HashMap<>();
        for (Skill skill : LevelManager.SKILLS.values()) {
            skillKeyIdMap.put(skill.getKey(), skill.getId());
        }
        return skillKeyIdMap;
    }

    private static void loadRestrictionSafely(JsonElement element, String source, Map<String, Integer> skillKeyIdMap, ItemStringReader itemStringReader, boolean override) {
        try {
            loadRestriction(element, source, skillKeyIdMap, itemStringReader, override);
        } catch (Exception e) {
            DatapackErrors.report("Restriction {} could not be loaded: {}", source, e.getMessage());
        }
    }

    private static void loadRestriction(JsonElement element, String source, Map<String, Integer> skillKeyIdMap, ItemStringReader itemStringReader, boolean override) {
        if (!element.isJsonObject()) {
            DatapackErrors.report("Restriction {} has to be a json object.", source);
            return;
        }
        JsonObject restrictionJsonObject = element.getAsJsonObject();
        boolean remove = restrictionJsonObject.has("remove") && restrictionJsonObject.get("remove").getAsBoolean();
        if (remove && !override) {
            DatapackErrors.report("Restriction {} uses \"remove\" which only works in the world overrides file.", source);
            return;
        }
        Map<Integer, Integer> skillLevelRestrictions = new HashMap<>();
        if (!remove) {
            if (!restrictionJsonObject.has("skills") || !restrictionJsonObject.get("skills").isJsonObject()) {
                DatapackErrors.report("Restriction {} is missing the \"skills\" object.", source);
                return;
            }
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
        }
        Mode mode = remove ? Mode.REMOVE : override ? Mode.OVERRIDE : Mode.DATAPACK;
        boolean replace = restrictionJsonObject.has("replace") && restrictionJsonObject.get("replace").getAsBoolean();

        loadEntries(restrictionJsonObject, "blocks", Registries.BLOCK, getAction(mode, LevelManager.BLOCK_RESTRICTIONS, blockList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "crafting", Registries.ITEM, getAction(mode, LevelManager.CRAFTING_RESTRICTIONS, craftingList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "entities", Registries.ENTITY_TYPE, getAction(mode, LevelManager.ENTITY_RESTRICTIONS, entityList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "items", Registries.ITEM, getAction(mode, LevelManager.ITEM_RESTRICTIONS, itemList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "mining", Registries.BLOCK, getAction(mode, LevelManager.MINING_RESTRICTIONS, miningList, skillLevelRestrictions, replace), source);
        loadEnchantments(restrictionJsonObject, getAction(mode, LevelManager.ENCHANTMENT_RESTRICTIONS, enchantmentList, skillLevelRestrictions, replace), source);
        loadComponents(restrictionJsonObject, mode, skillLevelRestrictions, source, itemStringReader);
        loadDimensions(restrictionJsonObject, mode, skillLevelRestrictions, replace, source);
    }

    private static EntryAction getAction(Mode mode, Map<Integer, PlayerRestriction> restrictions, List<Integer> replaceList, Map<Integer, Integer> skillLevelRestrictions, boolean replace) {
        return switch (mode) {
            case DATAPACK -> (rawId, fromTag) -> addRestriction(rawId, restrictions, replaceList, skillLevelRestrictions, replace, fromTag);
            case OVERRIDE -> (rawId, fromTag) -> restrictions.put(rawId, new PlayerRestriction(rawId, skillLevelRestrictions));
            case REMOVE -> (rawId, fromTag) -> restrictions.remove(rawId);
        };
    }

    private static <T> void loadEntries(JsonObject restrictionJsonObject, String type, Registry<T> registry, EntryAction action, String source) {
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
                        action.accept(registry.getRawId(tagEntry.value()), true);
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
            action.accept(registry.getRawId(registry.get(identifier)), false);
        }
    }

    private static void loadEnchantments(JsonObject restrictionJsonObject, EntryAction action, String source) {
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
                            action.accept(EnchantmentRegistry.getId(tagEntry, level), true);
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
                action.accept(EnchantmentRegistry.getId(enchantmentIdentifier, level), false);
            } else {
                DatapackErrors.report("Restriction {} contains the unknown enchantment {} or level {} is above its max level.", source, enchantment, level);
            }
        }
    }

    private static void loadComponents(JsonObject restrictionJsonObject, Mode mode, Map<Integer, Integer> skillLevelRestrictions, String source, ItemStringReader itemStringReader) {
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
                List<ComponentRestriction> componentRestrictions = LevelManager.COMPONENT_RESTRICTIONS.computeIfAbsent(itemRawId, id -> new ArrayList<>());
                if (mode != Mode.DATAPACK) {
                    componentRestrictions.removeIf(componentRestriction -> componentRestriction.getComponents().equals(itemResult.components()));
                }
                if (mode != Mode.REMOVE) {
                    componentRestrictions.add(new ComponentRestriction(itemRawId, itemResult.components(), skillLevelRestrictions));
                }
            } catch (CommandSyntaxException e) {
                DatapackErrors.report("Restriction {} contains the invalid component item {}: {}", source, itemString, e.getMessage());
            }
        }
    }

    private static void loadDimensions(JsonObject restrictionJsonObject, Mode mode, Map<Integer, Integer> skillLevelRestrictions, boolean replace, String source) {
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
            if (mode == Mode.REMOVE) {
                LevelManager.DIMENSION_RESTRICTIONS.remove(dimensionIdentifier);
                continue;
            }
            if (mode == Mode.DATAPACK) {
                if (dimensionList.contains(dimensionIdentifier)) {
                    continue;
                }
                if (replace) {
                    dimensionList.add(dimensionIdentifier);
                }
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
