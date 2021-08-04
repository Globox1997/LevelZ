package net.levelz.data;

import java.util.ArrayList;
import java.util.List;

public class LevelLists {

    // Block
    public static final ArrayList<Object> beehiveList = new ArrayList<Object>();
    public static final ArrayList<Object> pumpkinList = new ArrayList<Object>();
    public static final ArrayList<Object> anvilList = new ArrayList<Object>();
    public static final ArrayList<Object> brewingStandList = new ArrayList<Object>();
    public static final ArrayList<Object> enchantingTableList = new ArrayList<Object>();
    public static final ArrayList<Object> smithingTableList = new ArrayList<Object>();
    public static final ArrayList<Object> grindstoneList = new ArrayList<Object>();
    public static final ArrayList<Object> loomList = new ArrayList<Object>();
    public static final ArrayList<Object> smokerList = new ArrayList<Object>();
    public static final ArrayList<Object> blastFurnaceList = new ArrayList<Object>();
    public static final ArrayList<Object> cartographyList = new ArrayList<Object>();
    public static final ArrayList<Object> barrelList = new ArrayList<Object>();
    public static final ArrayList<Object> composterList = new ArrayList<Object>();
    public static final ArrayList<Object> lecternList = new ArrayList<Object>();
    public static final ArrayList<Object> stonecutterList = new ArrayList<Object>();
    public static final ArrayList<Object> cauldronList = new ArrayList<Object>();
    // Entity
    public static final ArrayList<Object> cowList = new ArrayList<Object>();
    public static final ArrayList<Object> mooshroomList = new ArrayList<Object>();
    public static final ArrayList<Object> sheepList = new ArrayList<Object>();
    public static final ArrayList<Object> snowGolemList = new ArrayList<Object>();
    // Item
    public static final ArrayList<Object> elytraList = new ArrayList<Object>();
    public static final ArrayList<Object> armorList = new ArrayList<Object>();
    public static final ArrayList<Object> bowList = new ArrayList<Object>();
    public static final ArrayList<Object> crossbowList = new ArrayList<Object>();
    public static final ArrayList<Object> flintAndSteelList = new ArrayList<Object>();
    public static final ArrayList<Object> hoeList = new ArrayList<Object>();
    public static final ArrayList<Object> shieldList = new ArrayList<Object>();
    public static final ArrayList<Object> tridentList = new ArrayList<Object>();
    public static final ArrayList<Object> toolList = new ArrayList<Object>();
    public static final ArrayList<Object> swordList = new ArrayList<Object>();
    public static final ArrayList<Object> bucketList = new ArrayList<Object>();
    public static final ArrayList<Object> fishingList = new ArrayList<Object>();
    public static final ArrayList<Object> dragonBreathList = new ArrayList<Object>();
    public static final ArrayList<Object> totemList = new ArrayList<Object>();
    // Misc
    public static final ArrayList<Object> explosionList = new ArrayList<Object>();
    // Player
    public static final List<List<Integer>> miningBlockList = new ArrayList<>();
    public static final List<Integer> miningLevelList = new ArrayList<>();

    public static final List<List<Integer>> brewingItemList = new ArrayList<>();
    public static final List<Integer> brewingLevelList = new ArrayList<>();

    public static ArrayList<Object> getList(String string) {
        switch (string) {
        // Item
        case "minecraft:elytra":
            return elytraList;
        case "minecraft:armor":
            return armorList;
        case "minecraft:bow":
            return bowList;
        case "minecraft:crossbow":
            return crossbowList;
        case "minecraft:flint_and_steel":
            return flintAndSteelList;
        case "minecraft:hoe":
            return hoeList;
        case "minecraft:shield":
            return shieldList;
        case "minecraft:trident":
            return tridentList;
        case "minecraft:tool":
            return toolList;
        case "minecraft:sword":
            return swordList;
        case "minecraft:bucket":
            return bucketList;
        case "minecraft:fishing_rod":
            return fishingList;
        case "minecraft:dragon_breath":
            return dragonBreathList;
        case "minecraft:totem_of_undying":
            return totemList;
        // Block
        case "minecraft:beehive":
            return beehiveList;
        case "minecraft:pumpkin":
            return pumpkinList;
        case "minecraft:anvil":
            return anvilList;
        case "minecraft:brewing":
            return brewingStandList;
        case "minecraft:enchanting":
            return enchantingTableList;
        case "minecraft:smithing":
            return smithingTableList;
        case "minecraft:grindstone":
            return grindstoneList;
        case "minecraft:loom":
            return loomList;
        case "minecraft:smoker":
            return smokerList;
        case "minecraft:blastfurnace":
            return blastFurnaceList;
        case "minecraft:cartography":
            return cartographyList;
        case "minecraft:barrel":
            return barrelList;
        case "minecraft:composter":
            return composterList;
        case "minecraft:lectern":
            return lecternList;
        case "minecraft:stonecutter":
            return stonecutterList;
        case "minecraft:cauldron":
            return cauldronList;
        // Entity
        case "minecraft:cow":
            return cowList;
        case "minecraft:mooshroom":
            return mooshroomList;
        case "minecraft:sheep":
            return sheepList;
        case "minecraft:snowgolem":
            return snowGolemList;
        default:
            return new ArrayList<Object>();
        }
    }
}
