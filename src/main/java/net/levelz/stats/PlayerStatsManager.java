package net.levelz.stats;

import java.util.ArrayList;
import java.util.List;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerStatsManager {
    // Level
    public int overallLevel;
    public float levelProgress;
    public int totalLevelExperience;
    // Skill
    private int healthLevel;
    private int strengthLevel;
    private int agilityLevel;
    private int defenseLevel;
    private int staminaLevel;
    private int luckLevel;
    private int archeryLevel;
    private int tradeLevel;
    private int smithingLevel;
    private int miningLevel;
    private int farmingLevel;
    private int alchemyLevel;
    private int skillPoints;
    // Other
    public List<Integer> lockedBlockIds = new ArrayList<Integer>();
    public List<Integer> lockedbrewingItemIds = new ArrayList<Integer>();

    // public void add(int thirst) {
    // this.thirstLevel = Math.min(thirst + this.thirstLevel, 20);
    // }

    // public void update(PlayerEntity player) {
    // }

    // Health
    // Strength
    // Defense
    // Agility (chance of no damage)
    // Speed (walking speed)
    // Regeneration
    // Critical hit
    // Level bar#
    // Farm skill disables/enables farming + wool

    // Stamina reduces 2% exhaustion per level
    // Stamina increases regeneration by 5% per level
    // Luck increases crit chance by 1% per level
    // Luck increases loot value by 5% per level
    // Archery increases range damage by 0.2 per level
    // Strength increases meele damage by 0.2 per level
    // Strength unlocks weapons by material level
    // Defense increases armor by 0.2 per level
    // Defense unlocks shield at lvl 5
    // Defense unlocks armor by lvl
    // Health increases HP by 1 per level
    // Agility increases speed by 0.001 per level
    // Agility decreases fall damage by 1 per 4 level
    // Agility unlocks elytra at lvl 10
    // Trade decreases prices by x per level
    // Smithing deceases anvil xp price by 2.5% per level
    // Smithing level 15 caps anvil xp at 30
    // Farm level 5 enables shears use
    // Farm level 1 enables hoe item use
    // Farm level 8 enables flint and steel
    // Mining level 0-4 ca 20%slower, unlocks blocks via datapack
    // Mining unlocks tools by material level
    // Archery unlocks trident at lvl 16
    // Archery unlocks bow at lvl 1
    // Archery unlocks crossbow at lvl 8
    // Alchemy unlocks brewing stuff

    // Wood, Stone, Iron,Gold, Diamond, Netherite

    // Extra blocks tooltip
    // Locked block list
    // Locked list gui

    // Show unlock lvl at enchanting table screen

    public void readNbt(NbtCompound tag) {
        if (tag.contains("HealthLevel", 99)) {
            // Level
            this.overallLevel = tag.getInt("Level");
            this.levelProgress = tag.getFloat("LevelProgress");
            this.totalLevelExperience = tag.getInt("TotalLevelExperience");
            this.skillPoints = tag.getInt("SkillPoints");
            // Skill
            this.healthLevel = tag.getInt("HealthLevel");
            this.strengthLevel = tag.getInt("StrengthLevel");
            this.agilityLevel = tag.getInt("AgilityLevel");
            this.defenseLevel = tag.getInt("DefenseLevel");
            this.staminaLevel = tag.getInt("StaminaLevel");
            this.luckLevel = tag.getInt("LuckLevel");
            this.archeryLevel = tag.getInt("ArcheryLevel");
            this.tradeLevel = tag.getInt("TradeLevel");
            this.smithingLevel = tag.getInt("SmithingLevel");
            this.miningLevel = tag.getInt("MiningLevel");
            this.farmingLevel = tag.getInt("FarmingLevel");
            this.alchemyLevel = tag.getInt("AlchemyLevel");

        }
    }

    public void writeNbt(NbtCompound tag) {
        // Level
        tag.putInt("Level", this.overallLevel);
        tag.putFloat("LevelProgress", this.levelProgress);
        tag.putInt("TotalLevelExperience", this.totalLevelExperience);
        tag.putInt("SkillPoints", this.skillPoints);
        // Skill
        tag.putInt("HealthLevel", this.healthLevel);
        tag.putInt("StrengthLevel", this.strengthLevel);
        tag.putInt("AgilityLevel", this.agilityLevel);
        tag.putInt("DefenseLevel", this.defenseLevel);
        tag.putInt("StaminaLevel", this.staminaLevel);
        tag.putInt("LuckLevel", this.luckLevel);
        tag.putInt("ArcheryLevel", this.archeryLevel);
        tag.putInt("TradeLevel", this.tradeLevel);
        tag.putInt("SmithingLevel", this.smithingLevel);
        tag.putInt("MiningLevel", this.miningLevel);
        tag.putInt("FarmingLevel", this.farmingLevel);
        tag.putInt("AlchemyLevel", this.alchemyLevel);

    }

    public void setLevel(String string, int level) {
        switch (string) {
        case "level":
            this.overallLevel = level;
            break;
        case "health":
            this.healthLevel = level;
            break;
        case "strength":
            this.strengthLevel = level;
            break;
        case "agility":
            this.agilityLevel = level;
            break;
        case "defense":
            this.defenseLevel = level;
            break;
        case "stamina":
            this.staminaLevel = level;
            break;
        case "luck":
            this.luckLevel = level;
            break;
        case "archery":
            this.archeryLevel = level;
            break;
        case "trade":
            this.tradeLevel = level;
            break;
        case "smithing":
            this.smithingLevel = level;
            break;
        case "mining":
            this.miningLevel = level;
            break;
        case "farming":
            this.farmingLevel = level;
            break;
        case "alchemy":
            this.alchemyLevel = level;
            break;
        case "points":
            this.skillPoints = level;
            break;
        default:
            break;
        }
    }

    public int getLevel(String string) {
        switch (string) {
        case "level":
            return this.overallLevel;
        case "health":
            return this.healthLevel;
        case "strength":
            return this.strengthLevel;
        case "agility":
            return this.agilityLevel;
        case "defense":
            return this.defenseLevel;
        case "stamina":
            return this.staminaLevel;
        case "luck":
            return this.luckLevel;
        case "archery":
            return this.archeryLevel;
        case "trade":
            return this.tradeLevel;
        case "smithing":
            return this.smithingLevel;
        case "mining":
            return this.miningLevel;
        case "farming":
            return this.farmingLevel;
        case "alchemy":
            return this.alchemyLevel;
        case "points":
            return this.skillPoints;
        default:
            return 0;
        }
    }

    public void addExperienceLevels(int levels) {
        this.overallLevel += levels;
        this.skillPoints++;
        if (this.overallLevel < 0) {
            this.overallLevel = 0;
            this.levelProgress = 0.0F;
            this.totalLevelExperience = 0;
        }
    }

    public int getNextLevelExperience() {
        if (this.overallLevel >= 50) {
            return 300 + (this.overallLevel - 30) * 18;
        } else {
            return this.overallLevel >= 30 ? 112 + (this.overallLevel - 30) * 9 : 37 + this.overallLevel * 3;
        }
    }

    // Datapack order for objects in json file is important!!
    public static boolean playerLevelisHighEnough(PlayerEntity playerEntity, List<Object> list, String string, boolean creativeRequired) {
        if (!playerEntity.isCreative() || !creativeRequired) {
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity);
            int playerLevel = 0;
            int maxLevel = ConfigInit.CONFIG.maxLevel;
            if (string != null) {
                if (list.contains(string)) {
                    playerLevel = playerStatsManager.getLevel(list.get(list.indexOf(string) + 1).toString());
                    if (playerLevel < maxLevel) {
                        if (playerLevel < (int) list.get(list.indexOf(string) + 2)) {
                            return false;
                        }
                    }
                } else
                    System.out.println("Couldn't find " + string + " list");
            } else {
                playerLevel = playerStatsManager.getLevel(list.get(0).toString());
                if (playerLevel < maxLevel) {
                    if (playerLevel < (int) list.get(1)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean listContainsItemOrBlock(PlayerEntity playerEntity, int id, boolean isBlock) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity);
        if (isBlock) {
            int playerMiningLevel = playerStatsManager.getLevel("mining");
            if (playerMiningLevel < ConfigInit.CONFIG.maxLevel) {
                if (playerStatsManager.lockedBlockIds.contains(id)) {
                    return true;
                }
            }
        } else {
            int playerBrewingLevel = playerStatsManager.getLevel("alchemy");
            if (playerBrewingLevel < ConfigInit.CONFIG.maxLevel) {
                if (playerStatsManager.lockedbrewingItemIds.contains(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getUnlockLevel(int id, boolean isBlock) {
        if (isBlock) {
            for (int i = 0; i < LevelLists.miningBlockList.size(); i++) {
                if (LevelLists.miningBlockList.get(i).contains(id)) {
                    return LevelLists.miningLevelList.get(i);
                }
            }
            return 0;
        } else {
            for (int i = 0; i < LevelLists.brewingItemList.size(); i++) {
                if (LevelLists.brewingItemList.get(i).contains(id)) {
                    return LevelLists.brewingLevelList.get(i);
                }
            }
            return 0;
        }
    }

}