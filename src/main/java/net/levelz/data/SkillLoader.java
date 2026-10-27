package net.levelz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.LevelzMain;
import net.levelz.init.ConfigInit;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.SkillAttribute;
import net.levelz.level.SkillBonus;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.*;

public class SkillLoader implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = LevelzMain.identifierOf("skill");

    private static final List<Integer> skillList = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        DatapackErrors.clear();
        LevelManager.SKILLS.clear();
        LevelManager.BONUSES.clear();
        skillList.clear();

        manager.findResources("skill", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            if (!ConfigInit.CONFIG.defaultSkills && id.getPath().endsWith("/default.json")) {
                return;
            }
            JsonObject data;
            try (InputStreamReader reader = new InputStreamReader(resourceRef.getInputStream())) {
                data = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                DatapackErrors.report("Skill file {} is not valid json: {}", id, e.getMessage());
                return;
            }
            for (String mapKey : data.keySet()) {
                String source = mapKey + " in " + id;
                try {
                    loadSkill(data.get(mapKey), source);
                } catch (Exception e) {
                    DatapackErrors.report("Skill {} could not be loaded: {}", source, e.getMessage());
                }
            }
        });

        Map<Integer, Skill> sortedMap = new TreeMap<>(LevelManager.SKILLS);
        int maxSkillId = sortedMap.isEmpty() ? -1 : Collections.max(sortedMap.keySet());
        for (int i = 0; i <= maxSkillId; i++) {
            if (!sortedMap.containsKey(i)) {
                DatapackErrors.report("Skill ids should be numbered without gaps, the skill with id {} is missing.", i);
            }
        }
        Set<Integer> attributeIds = new HashSet<>();
        for (Skill skill : sortedMap.values()) {
            for (SkillAttribute skillAttribute : skill.getAttributes()) {
                if (skillAttribute.getId() >= 0 && !attributeIds.add(skillAttribute.getId())) {
                    DatapackErrors.report("Attribute id {} is used more than once, only one of them is shown in the skill screen.", skillAttribute.getId());
                }
            }
        }
        int maxAttributeId = attributeIds.isEmpty() ? -1 : Collections.max(attributeIds);
        for (int i = 0; i <= maxAttributeId; i++) {
            if (!attributeIds.contains(i)) {
                DatapackErrors.report("Attribute ids should be numbered without gaps, the attribute with id {} is missing.", i);
            }
        }
        LevelManager.SKILLS.clear();
        LevelManager.SKILLS.putAll(sortedMap);
    }

    private static void loadSkill(JsonElement element, String source) {
        if (!element.isJsonObject()) {
            DatapackErrors.report("Skill {} has to be a json object.", source);
            return;
        }
        JsonObject skillJsonObject = element.getAsJsonObject();
        for (String requiredKey : List.of("id", "key", "level")) {
            if (!skillJsonObject.has(requiredKey)) {
                DatapackErrors.report("Skill {} is missing the required field \"{}\".", source, requiredKey);
                return;
            }
        }

        int identification = skillJsonObject.get("id").getAsInt();
        if (identification < 0) {
            DatapackErrors.report("Skill {} has the negative id {}, ids start at 0.", source, identification);
            return;
        }
        if (skillJsonObject.has("replace") && skillJsonObject.get("replace").getAsBoolean()) {
            skillList.add(identification);
            LevelManager.SKILLS.remove(identification);
            LevelManager.BONUSES.values().removeIf(skillBonus -> skillBonus.getId() == identification);
        } else if (skillList.contains(identification)) {
            return;
        } else if (LevelManager.SKILLS.containsKey(identification)) {
            DatapackErrors.report("Skill {} uses the id {} which is already used by skill {}, set \"replace\": true to override it.", source, identification,
                    LevelManager.SKILLS.get(identification).getKey());
        }

        String key = skillJsonObject.get("key").getAsString();
        int maxLevel = skillJsonObject.get("level").getAsInt();
        List<SkillAttribute> attributes = new ArrayList<>();

        if (skillJsonObject.has("attributes")) {
            for (JsonElement attributeElement : skillJsonObject.getAsJsonArray("attributes")) {
                SkillAttribute skillAttribute = loadAttribute(attributeElement.getAsJsonObject(), source);
                if (skillAttribute != null) {
                    attributes.add(skillAttribute);
                }
            }
        }

        if (skillJsonObject.has("bonus")) {
            for (JsonElement bonusElement : skillJsonObject.getAsJsonArray("bonus")) {
                JsonObject bonusJsonObject = bonusElement.getAsJsonObject();
                if (!bonusJsonObject.has("key") || !bonusJsonObject.has("level")) {
                    DatapackErrors.report("A bonus of skill {} is missing \"key\" or \"level\".", source);
                    continue;
                }
                String bonusKey = bonusJsonObject.get("key").getAsString();
                if (!SkillBonus.BONUS_KEYS.contains(bonusKey)) {
                    DatapackErrors.report("Skill {} contains the unknown bonus {}. Valid bonuses: {}", source, bonusKey, SkillBonus.BONUS_KEYS);
                    continue;
                }
                LevelManager.BONUSES.put(bonusKey, new SkillBonus(bonusKey, identification, bonusJsonObject.get("level").getAsInt()));
            }
        }

        LevelManager.SKILLS.put(identification, new Skill(identification, key, maxLevel, attributes));
    }

    private static SkillAttribute loadAttribute(JsonObject attributeJsonObject, String source) {
        for (String requiredKey : List.of("type", "value", "operation")) {
            if (!attributeJsonObject.has(requiredKey)) {
                DatapackErrors.report("An attribute of skill {} is missing the required field \"{}\".", source, requiredKey);
                return null;
            }
        }
        String type = attributeJsonObject.get("type").getAsString();
        Identifier attributeIdentifier = Identifier.tryParse(type);
        Optional<RegistryEntry.Reference<EntityAttribute>> entityAttribute = attributeIdentifier != null ? Registries.ATTRIBUTE.getEntry(attributeIdentifier) : Optional.empty();
        if (entityAttribute.isEmpty()) {
            DatapackErrors.report("Skill {} contains the unknown attribute {}.", source, type);
            return null;
        }
        String operationName = attributeJsonObject.get("operation").getAsString();
        EntityAttributeModifier.Operation operation;
        try {
            operation = EntityAttributeModifier.Operation.valueOf(operationName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            DatapackErrors.report("Skill {} contains the unknown attribute operation {}. Valid operations: {}", source, operationName, Arrays.toString(EntityAttributeModifier.Operation.values()));
            return null;
        }
        int attributeId = attributeJsonObject.has("id") ? attributeJsonObject.get("id").getAsInt() : -1;
        float baseValue = attributeJsonObject.has("base") ? attributeJsonObject.get("base").getAsFloat() : -10000.0f;
        return new SkillAttribute(attributeId, entityAttribute.get(), baseValue, attributeJsonObject.get("value").getAsFloat(), operation);
    }
}
