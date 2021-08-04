package net.levelz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "levelz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class LevelzConfig implements ConfigData {
    // @Comment("Holder")
    // public int test1 = 0;
    // public int test2 = 0;
    // public int test3 = 0;
    // public int test4 = 0;

    // public int test5 = 0;
    // public int test6 = 0;
    // public int test7 = 0;
    // public int test8 = 0;

    // public float floattest8 = 28.125F;
    // public float floattest8x = 28.125F;

    // public int xpanelsize = 0;
    // public int ypanelsize = 0;
    public int maxLevel = 20;
    @Comment("If true will reset stats on death")
    public boolean hardMode = false;
    public boolean resetCurrentXP = true;

    @Comment("Attribute values - Bonus for each lvl")
    public double healthBase = 6D;
    public double healthBonus = 1D;
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
    public float critDmgBonus = 0.2F;
    public float staminaBase = 1.1F;
    public float staminaBonus = 0.02F;
    public float staminaHealthBonus = 0.05F;
    public double tradeBonus = 0.2D;
    public float smithingCostBonus = 0.015F;
    @Comment("Chance of no tool damage")
    public float smithingToolChance = 0.01F;
    @Comment("Chance of no xp usage on anvil at max lvl")
    public float smithingAnvilChance = 0.1F;
    @Comment("Min level to get chance of more crops drop")
    public int farmingBase = 10;
    @Comment("Chance of more crops drop")
    public float farmingChanceBonus = 0.01F;
    @Comment("Chance of drinking potion with double value at max lvl")
    public float alchemyPotionChance = 0.05F;
    @Comment("Chance of double range damage at max lvl")
    public float archeryDoubleDamageChance = 0.05F;
}