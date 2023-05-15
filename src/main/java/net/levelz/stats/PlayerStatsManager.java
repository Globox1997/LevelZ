package net.levelz.stats;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStatsManager {

    private final PlayerEntity playerEntity;

    // Level
    public int overallLevel;
    private int totalLevelExperience;
    public float levelProgress;
    private int skillPoints;
    // Skill
    private final Map<Skill, Integer> skillLevel = new HashMap<>();

    public PlayerStatsManager(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    // Other
    public List<Integer> lockedBlockIds = new ArrayList<Integer>();
    public List<Integer> lockedbrewingItemIds = new ArrayList<Integer>();
    public List<Integer> lockedSmithingItemIds = new ArrayList<Integer>();
    public List<Integer> lockedCraftingItemIds = new ArrayList<Integer>();

    // Wood, Stone, Iron, Gold, Diamond, Netherite

    public void readNbt(NbtCompound tag) {
        if (tag.contains("SkillPoints", 99)) {
            // Level
            this.overallLevel = tag.getInt("Level");
            this.levelProgress = tag.getFloat("LevelProgress");
            this.totalLevelExperience = tag.getInt("TotalLevelExperience");
            this.skillPoints = tag.getInt("SkillPoints");
            // Skill
            for (Skill stats : Skill.values()) {
                skillLevel.put(stats, tag.getInt(stats.getNbt()));
            }
        }
    }

    public void writeNbt(NbtCompound tag) {
        // Level
        tag.putInt("Level", this.overallLevel);
        tag.putFloat("LevelProgress", this.levelProgress);
        tag.putInt("TotalLevelExperience", this.totalLevelExperience);
        tag.putInt("SkillPoints", this.skillPoints);
        // Skill
        skillLevel.forEach((k, v) -> tag.putInt(k.getNbt(), v));

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
        this.levelProgress = ConfigInit.CONFIG.useIndependentExp ? levelProgress : 0;
    }

    public float getLevelProgress() {
        if (!ConfigInit.CONFIG.useIndependentExp) {
            return Math.min(getNonIndependentExperience() / (float) this.getNextLevelExperience(), 1F);
        }
        return levelProgress;
    }

    public void setSkillLevel(Skill skill, int level) {
        skillLevel.put(skill, level);
    }

    public int getSkillLevel(Skill skill) {
        if (skillLevel.containsKey(skill)) {
            return skillLevel.get(skill);
        }
        return 0;
    }

    @Deprecated
    public void setLevel(String string, int level) {
        switch (string) {
            case "level" -> this.overallLevel = level;
            case "points" -> this.skillPoints = level;
            default -> setSkillLevel(Skill.valueOf(string.toUpperCase()), level);
        }
    }

    @Deprecated
    public int getLevel(String string) {
        return switch (string) {
            case "level" -> this.overallLevel;
            case "points" -> this.skillPoints;
            default -> getSkillLevel(Skill.valueOf(string.toUpperCase()));
        };
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
        if (ConfigInit.CONFIG.overallMaxLevel != 0) {
            return this.overallLevel >= ConfigInit.CONFIG.overallMaxLevel;
        }
        return this.overallLevel >= ConfigInit.CONFIG.maxLevel * 12;
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
        if (ConfigInit.CONFIG.xpMaxCost != 0)
            return experienceCost >= ConfigInit.CONFIG.xpMaxCost ? ConfigInit.CONFIG.xpMaxCost : experienceCost;
        else
            return experienceCost;
    }

    private int lastExperienceLevel = -1;
    private float lastExperienceProgress = -1;
    private long lastExperience = -1;

    public long getNonIndependentExperience() {
        int level = playerEntity.experienceLevel;
        float experienceProgress = playerEntity.experienceProgress;
        if (level == lastExperienceLevel && experienceProgress == lastExperienceProgress) {
            return lastExperience;
        }
        long exp = 0;
        for (int i = 0; i < level; i++) {
            if (i >= 30) {
                exp += 112 + (i - 30) * 9;
            } else {
                exp += i >= 15 ? 37 + (i - 15) * 5 : 7 + i * 2;
            }
        }
        exp += playerEntity.getNextLevelExperience() * experienceProgress;
        lastExperienceLevel = level;
        lastExperienceProgress = experienceProgress;
        lastExperience = exp;
        return exp;
    }

    public static boolean playerLevelisHighEnough(PlayerEntity playerEntity, List<Object> list, String string, boolean creativeRequired) {
        if (!playerEntity.isCreative() || !creativeRequired) {
            PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
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
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
        if (reference == 1) {
            if (playerStatsManager.getSkillLevel(Skill.MINING) < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedBlockIds.contains(id))
                return true;
        } else if (reference == 2) {
            if (playerStatsManager.getSkillLevel(Skill.ALCHEMY) < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedbrewingItemIds.contains(id))
                return true;
        } else if (reference == 3) {
            if (playerStatsManager.getSkillLevel(Skill.SMITHING) < ConfigInit.CONFIG.maxLevel && playerStatsManager.lockedSmithingItemIds.contains(id))
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

    public boolean resetSkill(Skill skill) {
        int sLevel = this.getSkillLevel(skill);
        if (sLevel > 0) {
            this.setSkillPoints(this.getSkillPoints() + sLevel);
            this.setSkillLevel(skill, 0);
            PlayerStatsServerPacket.writeS2CResetSkillPacket((ServerPlayerEntity) playerEntity, skill);
            return true;
        } else
            return false;
    }

    public static boolean resetSkill(PlayerEntity playerEntity, Skill skill) {
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager();
        return playerStatsManager.resetSkill(skill);
    }

    // Called on server only
    public static void onLevelUp(PlayerEntity playerEntity, int playerLevel) {
    }

}
