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
    public double attackBase = 1D;
    public double attackBonus = 0.2D;
    public double defenseBase = 0D;
    public double defenseBonus = 0.2D;
    public double luckBase = 0D;
    public double luckBonus = 0.05D;

    public double staminaBase = 1.1F;
    public double staminaBonus = 0.02F;
}