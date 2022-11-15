package net.levelz.config;

import java.util.ArrayList;
import java.util.Arrays;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "levelz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class LevelzConfig implements ConfigData {
    // Level settings
    @ConfigEntry.Category("level_settings")
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Maximum level per skills")
    public int maxLevel = 20;
    @ConfigEntry.Category("level_settings")
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Maximum level: 0 = disabled")
    public int overallMaxLevel = 0;
    @ConfigEntry.Category("level_settings")
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Applies if bonus chest world setting is enabled")
    public int startPoints = 5;
    @ConfigEntry.Category("level_settings")
    @Comment("Enables starter points for SERVER only")
    public boolean enableStartPoints = false;
    @ConfigEntry.Category("level_settings")
    public int pointsPerLevel = 1;
    @ConfigEntry.Category("level_settings")
    @Comment("If true will reset stats on death")
    public boolean hardMode = false;
    @ConfigEntry.Category("level_settings")
    public boolean disableMobFarms = true;
    @ConfigEntry.Category("level_settings")
    @Comment("Amount of allowed mob kills in a chunk")
    public int mobKillCount = 5;

    // Skill settings
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Attribute values - Bonus for each lvl")
    public double healthBase = 6D;
    @ConfigEntry.Gui.RequiresRestart
    public double healthBonus = 1D;
    @Comment("Absorption Bonus at max lvl")
    public float healthAbsorptionBonus = 6F;
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Levelz Screen Multiplies it by 10")
    public double movementBase = 0.09D;
    @ConfigEntry.Gui.RequiresRestart
    public double movementBonus = 0.001D;
    @Comment("Chance of damage misses player at max lvl")
    public float movementMissChance = 0.05F;
    @Comment("Reduces fall damage")
    public float movementFallBonus = 0.25F;
    @ConfigEntry.Gui.RequiresRestart
    public double attackBase = 1D;
    @ConfigEntry.Gui.RequiresRestart
    public double attackBonus = 0.2D;
    @Comment("Chance of double meele damage at max lvl")
    public float attackDoubleDamageChance = 0.03F;
    public float attackCritDmgBonus = 0.2F;
    @ConfigEntry.Gui.RequiresRestart
    public double defenseBase = 0D;
    @ConfigEntry.Gui.RequiresRestart
    public double defenseBonus = 0.2D;
    @Comment("Chance of damage reflection at max lvl")
    public float defenseReflectChance = 0.05F;
    @ConfigEntry.Gui.RequiresRestart
    public double luckBase = 0D;
    @ConfigEntry.Gui.RequiresRestart
    public double luckBonus = 0.05D;
    public float luckCritBonus = 0.01F;
    @Comment("Chance of not dying at max lvl")
    public float luckSurviveChance = 0.5F;
    public float staminaBase = 1.1F;
    public float staminaBonus = 0.02F;
    public float staminaHealthBonus = 0.05F;
    @Comment("Food is more nutritious at max lvl")
    public float staminaFoodBonus = 0.3F;
    @Comment("Price reduction in %")
    public double tradeBonus = 1.0D;
    public float tradeXPBonus = 0.5F;
    @Comment("Disables bad reputation possibility at max lvl")
    public boolean tradeReputation = true;
    public float smithingCostBonus = 0.015F;
    @Comment("Chance of no tool damage")
    public float smithingToolChance = 0.01F;
    @Comment("Chance of no xp usage on anvil at max lvl")
    public float smithingAnvilChance = 0.1F;
    @Comment("Min level to get chance of more crops drop")
    public int farmingBase = 10;
    @Comment("Chance of more crops drop")
    public float farmingChanceBonus = 0.01F;
    @Comment("Breeding twin chance at max lvl")
    public float farmingTwinChance = 0.2F;
    @Comment("Chance of increased enchantment strength")
    public float alchemyEnchantmentChance = 0.005F;
    @Comment("Chance of drinking potion with double value at max lvl")
    public float alchemyPotionChance = 0.05F;
    public float archeryBowExtraDamage = 0.2F;
    public float archeryCrossbowExtraDamage = 0.2F;
    @Comment("Chance of double range damage at max lvl")
    public float archeryDoubleDamageChance = 0.05F;
    @Comment("Chance of more ore drop")
    public float miningOreChance = 0.01F;
    @Comment("Tnt power increase at max lvl")
    public float miningTntBonus = 0.5F;
    @Comment("Locked blocks break slower factor")
    public float miningLockedMultiplicator = 2.0F;

    // Experience settings
    @ConfigEntry.Category("experience_settings")
    @Comment("XP equation: lvl^exponent * multiplicator + base")
    public float xpCostMultiplicator = 0.1F;
    public int xpExponent = 2;
    @ConfigEntry.Category("experience_settings")
    public int xpBaseCost = 50;
    @ConfigEntry.Category("experience_settings")
    @Comment("0 = no experience cap")
    public int xpMaxCost = 0;
    @ConfigEntry.Category("experience_settings")
    public boolean resetCurrentXP = true;
    @ConfigEntry.Category("experience_settings")
    public boolean dropPlayerXP = true;
    @ConfigEntry.Category("experience_settings")
    public boolean dropXPbasedOnLvl = false;
    @ConfigEntry.Category("experience_settings")
    @Comment("0.01 = 1% more xp per lvl")
    public float basedOnMultiplier = 0.01F;
    @ConfigEntry.Category("experience_settings")
    public float breedingXPMultiplier = 1.0F;
    @ConfigEntry.Category("experience_settings")
    public float bottleXPMultiplier = 1.0F;
    @ConfigEntry.Category("experience_settings")
    public float dragonXPMultiplier = 0.5F;
    @ConfigEntry.Category("experience_settings")
    public float fishingXPMultiplier = 0.8F;
    @ConfigEntry.Category("experience_settings")
    public float furnaceXPMultiplier = 0.1F;
    @ConfigEntry.Category("experience_settings")
    public float oreXPMultiplier = 1.0F;
    @ConfigEntry.Category("experience_settings")
    public float tradingXPMultiplier = 0.3F;
    @ConfigEntry.Category("experience_settings")
    public float mobXPMultiplier = 1.0F;

    @ConfigEntry.Category("gui_settings")
    @Comment("Show the skill gui button in the inventory")
    public boolean inventoryButton = true; // Client only
    @ConfigEntry.Category("gui_settings")
    @Comment("Highlight locked blocks in red.")
    public boolean highlightLocked = false; // Client only
    @ConfigEntry.Category("gui_settings")
    public boolean sortCraftingRecipesBySkill = false; // Client only
    @ConfigEntry.Category("gui_settings")
    public boolean inventorySkillLevel = true; // Client only
    @ConfigEntry.Category("gui_settings")
    @ConfigEntry.Gui.RequiresRestart
    public boolean showLevelList = true; // Client only
    @ConfigEntry.Category("gui_settings")
    public boolean showLevel = true; // Client only
    @ConfigEntry.Category("progression_settings")
    @ConfigEntry.Gui.RequiresRestart
    public boolean miningProgression = true;
    @ConfigEntry.Category("progression_settings")
    @ConfigEntry.Gui.RequiresRestart
    public boolean itemProgression = true;
    @ConfigEntry.Category("progression_settings")
    @ConfigEntry.Gui.RequiresRestart
    public boolean blockProgression = true;
    @ConfigEntry.Category("progression_settings")
    @ConfigEntry.Gui.RequiresRestart
    public boolean entityProgression = true;
    @ConfigEntry.Category("progression_settings")
    @ConfigEntry.Gui.RequiresRestart
    public boolean brewingProgression = true;
    @ConfigEntry.Category("progression_settings")
    @ConfigEntry.Gui.RequiresRestart
    public boolean smithingProgression = true;

    // List.of is immutable
    // Arrays.asList returns an ArrayList of the Arrays class which is different to the ArrayList class
    // Returns a list which should be synced to the client
    public ArrayList<Object> getConfigList() {
        return new ArrayList<Object>(Arrays.asList(maxLevel, overallMaxLevel, healthBase, healthBonus, healthAbsorptionBonus, movementBase, movementBonus, movementMissChance, movementFallBonus,
                attackBase, attackBonus, attackDoubleDamageChance, attackCritDmgBonus, defenseBase, defenseBonus, defenseReflectChance, luckBase, luckBonus, luckCritBonus, luckSurviveChance,
                staminaBase, staminaBonus, staminaHealthBonus, staminaFoodBonus, tradeBonus, tradeXPBonus, tradeReputation, smithingCostBonus, smithingToolChance, smithingAnvilChance, farmingBase,
                farmingChanceBonus, farmingTwinChance, alchemyEnchantmentChance, alchemyPotionChance, archeryBowExtraDamage, archeryCrossbowExtraDamage, archeryDoubleDamageChance, miningOreChance,
                miningTntBonus, xpCostMultiplicator, xpExponent, xpBaseCost, xpMaxCost, miningProgression, itemProgression, blockProgression, entityProgression, brewingProgression,
                smithingProgression));
    }

}