package net.levelz.level;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.levelz.init.ConfigInit;
import net.levelz.level.restriction.ComponentRestriction;
import net.levelz.level.restriction.PlayerRestriction;
import net.levelz.registry.EnchantmentRegistry;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelManager {

    public static final Map<Integer, Skill> SKILLS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> BLOCK_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> CRAFTING_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> ENTITY_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> ITEM_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> MINING_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, PlayerRestriction> ENCHANTMENT_RESTRICTIONS = new HashMap<>();
    public static final Map<Integer, List<ComponentRestriction>> COMPONENT_RESTRICTIONS = new HashMap<>();
    public static final Map<ResourceLocation, PlayerRestriction> DIMENSION_RESTRICTIONS = new HashMap<>();
    public static final Map<String, SkillBonus> BONUSES = new HashMap<>();

    private final Player playerEntity;
    private Map<Integer, PlayerSkill> playerSkills = new HashMap<>();

    // Level
    private int overallLevel;
    private int totalLevelExperience;
    private float levelProgress;
    private int skillPoints;

    public LevelManager(Player playerEntity) {
        this.playerEntity = playerEntity;
        this.refreshSkills();
    }

    public void refreshSkills() {
        for (Skill skill : SKILLS.values()) {
            PlayerSkill playerSkill = this.playerSkills.get(skill.getId());
            if (playerSkill == null) {
                this.playerSkills.put(skill.getId(), new PlayerSkill(skill.getId(), 0));
            } else if (playerSkill.getLevel() < 0) {
                playerSkill.setLevel(0);
            } else if (!ConfigInit.CONFIG.allowHigherSkillLevel && playerSkill.getLevel() > skill.getMaxLevel()) {
                this.skillPoints += playerSkill.getLevel() - skill.getMaxLevel();
                playerSkill.setLevel(skill.getMaxLevel());
            }
        }
    }

    public Player getPlayerEntity() {
        return playerEntity;
    }

    public void readNbt(CompoundTag nbt) {
        this.overallLevel = nbt.getInt("Level");
        this.levelProgress = nbt.getFloat("LevelProgress");
        this.totalLevelExperience = nbt.getInt("TotalLevelExperience");
        this.skillPoints = nbt.getInt("SkillPoints");

        ListTag skills = nbt.getList("Skills", Tag.TAG_COMPOUND);
        for (int i = 0; i < skills.size(); i++) {
            PlayerSkill skill = new PlayerSkill(skills.getCompound(i));
            if (!SKILLS.containsKey(skill.getId())) {
                continue;
            }
            playerSkills.put(skill.getId(), skill);
        }
        if (!Float.isFinite(this.levelProgress)) {
            this.levelProgress = 0.0F;
        }
        this.refreshSkills();
    }

    public void writeNbt(CompoundTag nbt) {
        nbt.putInt("Level", this.overallLevel);
        nbt.putFloat("LevelProgress", this.levelProgress);
        nbt.putInt("TotalLevelExperience", this.totalLevelExperience);
        nbt.putInt("SkillPoints", this.skillPoints);

        ListTag skills = new ListTag();
        for (PlayerSkill skill : this.playerSkills.values()) {
            skills.add(skill.writeDataToNbt());
        }
        nbt.put("Skills", skills);
    }

    public Map<Integer, PlayerSkill> getPlayerSkills() {
        return playerSkills;
    }

    public void setPlayerSkills(Map<Integer, PlayerSkill> playerSkills) {
        this.playerSkills = playerSkills;
    }

    public void setOverallLevel(int overallLevel) {
        this.overallLevel = overallLevel;
    }

    public int getOverallLevel() {
        return overallLevel;
    }

    public void setTotalLevelExperience(int totalLevelExperience) {
        this.totalLevelExperience = totalLevelExperience;
    }

    public int getTotalLevelExperience() {
        return totalLevelExperience;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setLevelProgress(float levelProgress) {
        this.levelProgress = levelProgress;
    }

    public float getLevelProgress() {
        return levelProgress;
    }

    public void setSkillLevel(int skillId, int level) {
        PlayerSkill playerSkill = this.playerSkills.get(skillId);
        if (playerSkill == null) {
            this.playerSkills.put(skillId, new PlayerSkill(skillId, level));
        } else {
            playerSkill.setLevel(level);
        }
    }

    public int getSkillLevel(int skillId) {
        PlayerSkill playerSkill = this.playerSkills.get(skillId);
        return playerSkill != null ? playerSkill.getLevel() : 0;
    }

    public void addExperienceLevels(int levels) {
        this.overallLevel += levels;
        this.skillPoints += ConfigInit.CONFIG.pointsPerLevel;
        if (this.overallLevel < 0) {
            this.overallLevel = 0;
            this.levelProgress = 0.0F;
            this.totalLevelExperience = 0;
        }
    }

    public boolean isMaxLevel() {
        if (ConfigInit.CONFIG.overallMaxLevel > 0) {
            return this.overallLevel >= ConfigInit.CONFIG.overallMaxLevel;
        } else {
            int maxLevel = 0;
            for (Skill skill : SKILLS.values()) {
                maxLevel += skill.getMaxLevel();
            }
            return this.overallLevel >= maxLevel;
        }
    }

    public boolean hasAvailableLevel() {
        return this.skillPoints > 0;
    }

    // Recommend to use https://www.geogebra.org/graphing
    public int getNextLevelExperience() {
        if (isMaxLevel()) {
            return 0;
        }
        int experienceCost = (int) (ConfigInit.CONFIG.xpBaseCost + ConfigInit.CONFIG.xpCostMultiplicator * Math.pow(this.overallLevel, ConfigInit.CONFIG.xpExponent));
        if (ConfigInit.CONFIG.xpMaxCost != 0) {
            return experienceCost >= ConfigInit.CONFIG.xpMaxCost ? ConfigInit.CONFIG.xpMaxCost : experienceCost;
        } else {
            return experienceCost;
        }
    }

    // block
    public boolean hasRequiredBlockLevel(Block block) {
        int itemId = BuiltInRegistries.BLOCK.getId(block);
        if (BLOCK_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = BLOCK_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredBlockLevel(Block block) {
        int itemId = BuiltInRegistries.BLOCK.getId(block);
        if (BLOCK_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = BLOCK_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // crafting
    public boolean hasRequiredCraftingLevel(Item item) {
        int itemId = BuiltInRegistries.ITEM.getId(item);
        if (CRAFTING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = CRAFTING_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredCraftingLevel(Item item) {
        int itemId = BuiltInRegistries.ITEM.getId(item);
        if (CRAFTING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = CRAFTING_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // entity
    public boolean hasRequiredEntityLevel(EntityType<?> entityType) {
        int entityId = BuiltInRegistries.ENTITY_TYPE.getId(entityType);
        if (ENTITY_RESTRICTIONS.containsKey(entityId)) {
            PlayerRestriction playerRestriction = ENTITY_RESTRICTIONS.get(entityId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredEntityLevel(EntityType<?> entityType) {
        int entityId = BuiltInRegistries.ENTITY_TYPE.getId(entityType);
        if (ENTITY_RESTRICTIONS.containsKey(entityId)) {
            PlayerRestriction playerRestriction = ENTITY_RESTRICTIONS.get(entityId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // item
    public boolean hasRequiredItemLevel(Item item) {
        int itemId = BuiltInRegistries.ITEM.getId(item);
        if (ITEM_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = ITEM_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasRequiredStackLevel(ItemStack stack) {
        if (!this.hasRequiredItemLevel(stack.getItem()) || !this.hasRequiredComponentLevel(stack)) {
            return false;
        }
        if (ConfigInit.CONFIG.restrictEnchantedItems) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : stack.getEnchantments().entrySet()) {
                if (!this.hasRequiredEnchantmentLevel(entry.getKey(), entry.getIntValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasRequiredComponentLevel(ItemStack stack) {
        List<ComponentRestriction> componentRestrictions = COMPONENT_RESTRICTIONS.get(BuiltInRegistries.ITEM.getId(stack.getItem()));
        if (componentRestrictions != null) {
            for (ComponentRestriction componentRestriction : componentRestrictions) {
                if (componentRestriction.matches(stack) && !this.hasRequiredSkillLevels(componentRestriction.getSkillLevelRestrictions())) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<ComponentRestriction> getComponentRestrictions(ItemStack stack) {
        List<ComponentRestriction> componentRestrictions = COMPONENT_RESTRICTIONS.get(BuiltInRegistries.ITEM.getId(stack.getItem()));
        if (componentRestrictions == null) {
            return List.of();
        }
        List<ComponentRestriction> matchingRestrictions = new ArrayList<>();
        for (ComponentRestriction componentRestriction : componentRestrictions) {
            if (componentRestriction.matches(stack)) {
                matchingRestrictions.add(componentRestriction);
            }
        }
        return matchingRestrictions;
    }

    public boolean hasRequiredDimensionLevel(ResourceKey<Level> dimension) {
        PlayerRestriction playerRestriction = DIMENSION_RESTRICTIONS.get(dimension.location());
        return playerRestriction == null || this.hasRequiredSkillLevels(playerRestriction.getSkillLevelRestrictions());
    }

    public Map<Integer, Integer> getRequiredDimensionLevel(ResourceKey<Level> dimension) {
        PlayerRestriction playerRestriction = DIMENSION_RESTRICTIONS.get(dimension.location());
        return playerRestriction != null ? playerRestriction.getSkillLevelRestrictions() : Map.of();
    }

    private boolean hasRequiredSkillLevels(Map<Integer, Integer> skillLevelRestrictions) {
        for (Map.Entry<Integer, Integer> entry : skillLevelRestrictions.entrySet()) {
            if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredItemLevel(Item item) {
        int itemId = BuiltInRegistries.ITEM.getId(item);
        if (ITEM_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = ITEM_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // mining
    public boolean hasRequiredMiningLevel(Block block) {
        int itemId = BuiltInRegistries.BLOCK.getId(block);
        if (MINING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = MINING_RESTRICTIONS.get(itemId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredMiningLevel(Block block) {
        int itemId = BuiltInRegistries.BLOCK.getId(block);
        if (MINING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = MINING_RESTRICTIONS.get(itemId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    // enchantment
    public boolean hasRequiredEnchantmentLevel(Holder<Enchantment> enchantment, int level) {
        if (ENCHANTMENT_RESTRICTIONS.isEmpty()) {
            return true;
        }
        int enchantmentId = EnchantmentRegistry.getId(enchantment, level);
        if (ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
            PlayerRestriction playerRestriction = ENCHANTMENT_RESTRICTIONS.get(enchantmentId);
            for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Integer, Integer> getRequiredEnchantmentLevel(Holder<Enchantment> enchantment, int level) {
        int enchantmentId = EnchantmentRegistry.getId(enchantment, level);
        if (ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
            PlayerRestriction playerRestriction = ENCHANTMENT_RESTRICTIONS.get(enchantmentId);
            return playerRestriction.getSkillLevelRestrictions();
        }
        return Map.of(0, 0);
    }

    public boolean resetSkill(int skillId) {
        int level = this.getSkillLevel(skillId);
        if (level > 0) {
            this.setSkillPoints(this.getSkillPoints() + level);
            this.setSkillLevel(skillId, 0);
            PacketHelper.updatePlayerSkills((ServerPlayer) this.playerEntity, null);
            LevelHelper.updateSkill((ServerPlayer) this.playerEntity, SKILLS.get(skillId));
            PacketHelper.updateLevels((ServerPlayer) this.playerEntity);
            return true;
        } else {
            return false;
        }
    }

}
