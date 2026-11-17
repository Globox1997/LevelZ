package net.levelz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.commands.arguments.item.ItemInput;

public record RestrictionLoader(HolderLookup.Provider wrapperLookup) implements ResourceManagerReloadListener {

    public static final Identifier ID = LevelzMain.identifierOf("restriction");

    public static final List<RestrictionEntry> ENTRIES = new ArrayList<>();

    private static final Set<Integer> blockList = new HashSet<>();
    private static final Set<Integer> craftingList = new HashSet<>();
    private static final Set<Integer> entityList = new HashSet<>();
    private static final Set<Integer> itemList = new HashSet<>();
    private static final Set<Integer> miningList = new HashSet<>();
    private static final Set<Integer> enchantmentList = new HashSet<>();
    private static final Set<Identifier> dimensionList = new HashSet<>();
    private static final List<Consumer<RegistryAccess>> TAG_TASKS = new ArrayList<>();

    private enum Mode {
        DATAPACK, OVERRIDE, REMOVE
    }

    private interface EntryAction {
        void accept(int rawId, boolean fromTag);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
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
        ItemParser itemStringReader = new ItemParser(this.wrapperLookup());
        Map<String, Integer> skillKeyIdMap = getSkillKeyIdMap();

        manager.listResources("restriction", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            if (!ConfigInit.CONFIG.defaultRestrictions && id.getPath().endsWith("/default.json")) {
                return;
            }
            JsonObject data;
            try (InputStreamReader reader = new InputStreamReader(resourceRef.open())) {
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

    public static void onTagsLoaded(RegistryAccess registries) {
        for (Consumer<RegistryAccess> task : TAG_TASKS) {
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
        ItemParser itemStringReader = new ItemParser(server.registryAccess());
        Map<String, Integer> skillKeyIdMap = getSkillKeyIdMap();
        for (String mapKey : overrides.keySet()) {
            ENTRIES.add(new RestrictionEntry(RestrictionOverrides.SOURCE, mapKey, overrides.get(mapKey).toString()));
            loadRestrictionSafely(overrides.get(mapKey), getOverrideSource(mapKey), skillKeyIdMap, itemStringReader, true);
        }
        onTagsLoaded(server.registryAccess());
    }

    public static void reloadRestrictions(MinecraftServer server) {
        new RestrictionLoader(server.registryAccess()).onResourceManagerReload(server.getResourceManager());
        onTagsLoaded(server.registryAccess());
        applyOverrides(server);
        for (ServerPlayer serverPlayerEntity : server.getPlayerList().getPlayers()) {
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

    private static void loadRestrictionSafely(JsonElement element, String source, Map<String, Integer> skillKeyIdMap, ItemParser itemStringReader, boolean override) {
        try {
            loadRestriction(element, source, skillKeyIdMap, itemStringReader, override);
        } catch (Exception e) {
            DatapackErrors.report("Restriction {} could not be loaded: {}", source, e.getMessage());
        }
    }

    private static void loadRestriction(JsonElement element, String source, Map<String, Integer> skillKeyIdMap, ItemParser itemStringReader, boolean override) {
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

        loadEntries(restrictionJsonObject, "blocks", BuiltInRegistries.BLOCK, getAction(mode, LevelManager.BLOCK_RESTRICTIONS, blockList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "crafting", BuiltInRegistries.ITEM, getAction(mode, LevelManager.CRAFTING_RESTRICTIONS, craftingList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "entities", BuiltInRegistries.ENTITY_TYPE, getAction(mode, LevelManager.ENTITY_RESTRICTIONS, entityList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "items", BuiltInRegistries.ITEM, getAction(mode, LevelManager.ITEM_RESTRICTIONS, itemList, skillLevelRestrictions, replace), source);
        loadEntries(restrictionJsonObject, "mining", BuiltInRegistries.BLOCK, getAction(mode, LevelManager.MINING_RESTRICTIONS, miningList, skillLevelRestrictions, replace), source);
        loadEnchantments(restrictionJsonObject, getAction(mode, LevelManager.ENCHANTMENT_RESTRICTIONS, enchantmentList, skillLevelRestrictions, replace), source);
        loadComponents(restrictionJsonObject, mode, skillLevelRestrictions, source, itemStringReader);
        loadDimensions(restrictionJsonObject, mode, skillLevelRestrictions, replace, source);
    }

    private static EntryAction getAction(Mode mode, Map<Integer, PlayerRestriction> restrictions, Set<Integer> replaceList, Map<Integer, Integer> skillLevelRestrictions, boolean replace) {
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
                TagKey<T> tagKey = TagKey.create(registry.key(), tagIdentifier);
                TAG_TASKS.add(registries -> {
                    boolean found = false;
                    for (Holder<T> tagEntry : registry.getTagOrEmpty(tagKey)) {
                        found = true;
                        action.accept(registry.getId(tagEntry.value()), true);
                    }
                    if (!found) {
                        DatapackErrors.report("Restriction {} contains the tag {} in \"{}\" which is empty or does not exist.", source, entry, type);
                    }
                });
                continue;
            }
            Identifier identifier = Identifier.tryParse(entry);
            if (identifier == null || !registry.containsKey(identifier)) {
                DatapackErrors.report("Restriction {} contains the unknown id {} in \"{}\".", source, entry, type);
                continue;
            }
            action.accept(registry.getId(registry.getValue(identifier)), false);
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
                TagKey<Enchantment> tagKey = TagKey.create(Registries.ENCHANTMENT, tagIdentifier);
                TAG_TASKS.add(registries -> {
                    boolean found = false;
                    for (Holder<Enchantment> tagEntry : registries.lookupOrThrow(Registries.ENCHANTMENT).getTagOrEmpty(tagKey)) {
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

    private static void loadComponents(JsonObject restrictionJsonObject, Mode mode, Map<Integer, Integer> skillLevelRestrictions, String source, ItemParser itemStringReader) {
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
                ItemInput itemResult = itemStringReader.parse(new StringReader(itemString));
                if (itemResult.components().isEmpty()) {
                    DatapackErrors.report("Restriction {} contains {} in \"components\" without any component, use \"items\" instead.", source, itemString);
                    continue;
                }
                int itemRawId = BuiltInRegistries.ITEM.getId(itemResult.item().value());
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
            TAG_TASKS.add(registries -> registries.lookup(Registries.LEVEL_STEM).ifPresent(registry -> {
                if (!registry.containsKey(dimensionIdentifier)) {
                    DatapackErrors.report("Restriction {} contains the dimension {} which does not exist.", source, dimensionIdentifier);
                }
            }));
        }
    }

    private static void addRestriction(int rawId, Map<Integer, PlayerRestriction> restrictions, Set<Integer> replaceList, Map<Integer, Integer> skillLevelRestrictions, boolean replace,
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
