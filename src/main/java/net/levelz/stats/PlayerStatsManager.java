package net.levelz.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class PlayerStatsManager {
    // Level
    public int overallLevel;
    public float levelProgress;
    public int totalLevelExperience;
    private int skillPoints;
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
    // Other
    public List<Integer> lockedBlockIds = new ArrayList<Integer>();
    public List<Integer> lockedbrewingItemIds = new ArrayList<Integer>();
    public List<Integer> lockedSmithingItemIds = new ArrayList<Integer>();
    public List<Integer> lockedCraftingItemIds = new ArrayList<Integer>();

    // Wood, Stone, Iron, Gold, Diamond, Netherite

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
        this.skillPoints += ConfigInit.CONFIG.pointsPerLevel;
        if (this.overallLevel < 0) {
            this.overallLevel = 0;
            this.levelProgress = 0.0F;
            this.totalLevelExperience = 0;
        }
    }

    public boolean isMaxLevel() {
        return this.overallLevel >= ConfigInit.CONFIG.maxLevel * 12;
    }

    // Recommend to use https://www.geogebra.org/graphing
    public int getNextLevelExperience() {
        if (isMaxLevel())
            return 0;
        int experienceCost = (int) (ConfigInit.CONFIG.xpBaseCost + ConfigInit.CONFIG.xpCostMultiplicator * MathHelper.square(this.overallLevel));
        if (ConfigInit.CONFIG.xpMaxCost != 0)
            return experienceCost >= ConfigInit.CONFIG.xpMaxCost ? ConfigInit.CONFIG.xpMaxCost : experienceCost;
        else
            return experienceCost;
    }

    public static boolean playerLevelisHighEnough(PlayerEntity playerEntity, List<Object> list, String string, boolean creativeRequired) {
        if (!playerEntity.isCreative() || !creativeRequired) {
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity);
            int playerLevel = 0;
            int maxLevel = ConfigInit.CONFIG.maxLevel;
            if (string != null) {
                if (!list.isEmpty() && list.contains(string)) {
                    playerLevel = playerStatsManager.getLevel(list.get(list.indexOf(string) + 1).toString());
                    if (playerLevel < maxLevel) {
                        if (playerLevel < (int) list.get(list.indexOf(string) + 2))
                            return false;
                    }
                }
                // else
                // System.out.println("Couldn't find " + string + " list");
            } else {
                if (!list.isEmpty()) {
                    playerLevel = playerStatsManager.getLevel(list.get(0).toString());
                    if (playerLevel < maxLevel && playerLevel < (int) list.get(1))
                        return false;
                }
            }
        }

        return true;
    }

    // 1 = mining, 2 = alchemy, 3 = smithing, 4 = crafting
    public static boolean listContainsItemOrBlock(PlayerEntity playerEntity, int id, int reference) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity);
        if (reference == 1) {
            if (playerStatsManager.getLevel("mining") < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedBlockIds.contains(id))
                return true;
        } else if (reference == 2) {
            if (playerStatsManager.getLevel("alchemy") < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedbrewingItemIds.contains(id))
                return true;
        } else if (reference == 3) {
            if (playerStatsManager.getLevel("smithing") < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedSmithingItemIds.contains(id))
                return true;
        } else if (reference == 4) {
            if (playerStatsManager.lockedCraftingItemIds.contains(id))
                return true;
        }
        return false;
    }

    // 1:mining; 2:brewing; 3:smithing; 4:crafting
    public static int getUnlockLevel(int id, int reference) {
        if (reference == 1) {
            for (int i = 0; i < LevelLists.miningBlockList.size(); i++) {
                if (LevelLists.miningBlockList.get(i).contains(id)) {
                    return LevelLists.miningLevelList.get(i);
                }
            }
            return 0;
        } else if (reference == 2) {
            for (int i = 0; i < LevelLists.brewingItemList.size(); i++) {
                if (LevelLists.brewingItemList.get(i).contains(id)) {
                    return LevelLists.brewingLevelList.get(i);
                }
            }
            return 0;
        } else if (reference == 3) {
            for (int i = 0; i < LevelLists.smithingItemList.size(); i++) {
                if (LevelLists.smithingItemList.get(i).contains(id)) {
                    return LevelLists.smithingLevelList.get(i);
                }
            }
            return 0;
        } else if (reference == 4) {
            for (int i = 0; i < LevelLists.craftingItemList.size(); i++) {
                if (LevelLists.craftingItemList.get(i).contains(id)) {
                    return LevelLists.craftingLevelList.get(i);
                }
            }
            return 0;
        }
        return 0;
    }

    public static boolean resetSkill(PlayerEntity playerEntity, String skill) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager(playerEntity);
        if (playerStatsManager.getLevel(skill) > 0) {
            playerStatsManager.setLevel("points", playerStatsManager.getLevel("points") + playerStatsManager.getLevel(skill));
            playerStatsManager.setLevel(skill, 0);
            PlayerStatsServerPacket.writeS2CResetSkillPacket((ServerPlayerEntity) playerEntity, skill);
            return true;
        } else
            return false;
    }

    public static List<String> getAllSkills() {
        List<String> list = new ArrayList<>();
        list.add("health");
        list.add("strength");
        list.add("agility");
        list.add("defense");
        list.add("stamina");
        list.add("luck");
        list.add("archery");
        list.add("trade");
        list.add("smithing");
        list.add("farming");
        list.add("alchemy");
        list.add("mining");
        return list;
    }

    public static String getRandomSkillString(Random random) {
        switch (random.nextInt(12)) {
        case 0:
            return "health";
        case 1:
            return "strength";
        case 2:
            return "agility";
        case 3:
            return "defense";
        case 4:
            return "stamina";
        case 5:
            return "luck";
        case 6:
            return "archery";
        case 7:
            return "trade";
        case 8:
            return "smithing";
        case 9:
            return "farming";
        case 10:
            return "alchemy";
        case 11:
            return "mining";
        default:
            return "health";
        }
    }

    // Called on server only
    public static void onLevelUp(PlayerEntity playerEntity, int playerLevel) {
    }

}