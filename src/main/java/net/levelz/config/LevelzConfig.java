package net.levelz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "levelz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class LevelzConfig implements ConfigData {
    public int maxLevel = 20;
    @Comment("If true will reset stats on death")
    public boolean hardMode = false;
    public boolean resetCurrentXP = true;
    public boolean disableMobFarms = true;
    @Comment("Attribute values - Bonus for each lvl")
    public double healthBase = 6D;
    public double healthBonus = 1D;
    @Comment("Absorption Bonus at max lvl")
    public float healthAbsorptionBonus = 6F;
    public double movementBase = 0.09D;
    public double movementBonus = 0.001D;
    @Comment("Chance of damage misses player at max lvl")
    public float movementMissChance = 0.05F;
    @Comment("Reduces fall damage")
    public float movementFallBonus = 0.25F;
    public double attackBase = 1D;
    public double attackBonus = 0.2D;
    @Comment("Chance of double meele damage at max lvl")
    public float attackDoubleDamageChance = 0.03F;
    public double defenseBase = 0D;
    public double defenseBonus = 0.2D;
    @Comment("Chance of damage reflection at max lvl")
    public float defenseReflectChance = 0.05F;
    public double luckBase = 0D;
    public double luckBonus = 0.05D;
    public float luckCritBonus = 0.01F;
    @Comment("Chance of not dying at max lvl")
    public float luckSurviveChance = 0.5F;
    public float critDmgBonus = 0.2F;
    public float staminaBase = 1.1F;
    public float staminaBonus = 0.02F;
    public float staminaHealthBonus = 0.05F;
    @Comment("Food is more nutritious at max lvl")
    public float staminaFoodBonus = 0.3F;
    public double tradeBonus = 0.2D;
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

}