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
    public static final ArrayList<Object> furnaceList = new ArrayList<Object>();
    // Entity
    public static final ArrayList<Object> cowList = new ArrayList<Object>();
    public static final ArrayList<Object> mooshroomList = new ArrayList<Object>();
    public static final ArrayList<Object> sheepList = new ArrayList<Object>();
    public static final ArrayList<Object> snowGolemList = new ArrayList<Object>();
    public static final ArrayList<Object> wanderingTraderList = new ArrayList<Object>();
    public static final ArrayList<Object> villagerList = new ArrayList<Object>();
    public static final ArrayList<Object> wolfList = new ArrayList<Object>();
    public static final ArrayList<Object> breedingList = new ArrayList<Object>();
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
    public static final ArrayList<Object> axeList = new ArrayList<Object>();
    public static final ArrayList<Object> bucketList = new ArrayList<Object>();
    public static final ArrayList<Object> fishingList = new ArrayList<Object>();
    public static final ArrayList<Object> dragonBreathList = new ArrayList<Object>();
    public static final ArrayList<Object> totemList = new ArrayList<Object>();
    // Misc
    public static final List<ArrayList<Object>> listOfAllLists = new ArrayList<>();
    // Player
    public static final List<List<Integer>> miningBlockList = new ArrayList<>();
    public static final List<Integer> miningLevelList = new ArrayList<>();

    public static final List<List<Integer>> brewingItemList = new ArrayList<>();
    public static final List<Integer> brewingLevelList = new ArrayList<>();
    public static final List<Object> potionList = new ArrayList<>();
    // Custom
    public static final ArrayList<Object> customBlockList = new ArrayList<Object>();
    public static final ArrayList<Object> customItemList = new ArrayList<Object>();

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
        case "minecraft:axe":
            return axeList;
        case "minecraft:custom_item":
            return customItemList;
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
        case "minecraft:furnace":
            return furnaceList;
        case "minecraft:custom_block":
            return customBlockList;
        // Entity
        case "minecraft:cow":
            return cowList;
        case "minecraft:mooshroom":
            return mooshroomList;
        case "minecraft:sheep":
            return sheepList;
        case "minecraft:snowgolem":
            return snowGolemList;
        case "minecraft:wanderingtrader":
            return wanderingTraderList;
        case "minecraft:villager":
            return villagerList;
        case "minecraft:wolf":
            return wolfList;
        case "minecraft:breeding":
            return breedingList;
        default:
            return new ArrayList<Object>();
        }
    }

    // Used for datapack to client sync (can't get filled inside the LevelLoader)
    public static ArrayList<String> getListNames() {
        ArrayList<String> listNames = new ArrayList<String>();
        listNames.add("minecraft:elytra");
        listNames.add("minecraft:armor");
        listNames.add("minecraft:bow");
        listNames.add("minecraft:crossbow");
        listNames.add("minecraft:flint_and_steel");
        listNames.add("minecraft:hoe");
        listNames.add("minecraft:shield");
        listNames.add("minecraft:trident");
        listNames.add("minecraft:tool");
        listNames.add("minecraft:sword");
        listNames.add("minecraft:bucket");
        listNames.add("minecraft:fishing_rod");
        listNames.add("minecraft:dragon_breath");
        listNames.add("minecraft:totem_of_undying");
        listNames.add("minecraft:axe");
        listNames.add("minecraft:beehive");
        listNames.add("minecraft:pumpkin");
        listNames.add("minecraft:anvil");
        listNames.add("minecraft:brewing");
        listNames.add("minecraft:enchanting");
        listNames.add("minecraft:smithing");
        listNames.add("minecraft:grindstone");
        listNames.add("minecraft:loom");
        listNames.add("minecraft:smoker");
        listNames.add("minecraft:blastfurnace");
        listNames.add("minecraft:cartography");
        listNames.add("minecraft:barrel");
        listNames.add("minecraft:composter");
        listNames.add("minecraft:lectern");
        listNames.add("minecraft:stonecutter");
        listNames.add("minecraft:cauldron");
        listNames.add("minecraft:cow");
        listNames.add("minecraft:mooshroom");
        listNames.add("minecraft:sheep");
        listNames.add("minecraft:snowgolem");
        listNames.add("minecraft:wanderingtrader");
        listNames.add("minecraft:villager");
        listNames.add("minecraft:wolf");
        listNames.add("minecraft:breeding");
        listNames.add("minecraft:furnace");
        listNames.add("minecraft:custom_block");
        listNames.add("minecraft:custom_item");
        return listNames;
    }
}
