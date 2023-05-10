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
    public static final ArrayList<Object> beaconList = new ArrayList<Object>();
    public static final ArrayList<Object> craftingTableList = new ArrayList<Object>();
    public static final ArrayList<Object> fletchingTableList = new ArrayList<Object>();
    // Entity
    public static final ArrayList<Object> cowList = new ArrayList<Object>();
    public static final ArrayList<Object> mooshroomList = new ArrayList<Object>();
    public static final ArrayList<Object> sheepList = new ArrayList<Object>();
    public static final ArrayList<Object> snowGolemList = new ArrayList<Object>();
    public static final ArrayList<Object> wanderingTraderList = new ArrayList<Object>();
    public static final ArrayList<Object> villagerList = new ArrayList<Object>();
    public static final ArrayList<Object> wolfList = new ArrayList<Object>();
    public static final ArrayList<Object> breedingList = new ArrayList<Object>();
    public static final ArrayList<Object> axolotlList = new ArrayList<Object>();
    public static final ArrayList<Object> piglinList = new ArrayList<Object>();
    public static final ArrayList<Object> tadpoleList = new ArrayList<Object>();
    public static final ArrayList<Object> allayList = new ArrayList<Object>();
    public static final ArrayList<Object> goatList = new ArrayList<Object>();
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
    public static final ArrayList<Object> shearsList = new ArrayList<Object>();
    public static final ArrayList<Object> compassList = new ArrayList<Object>();
    // Misc
    public static final List<ArrayList<Object>> listOfAllLists = new ArrayList<>();
    // Player
    public static final List<List<Integer>> miningBlockList = new ArrayList<List<Integer>>();
    public static final List<Integer> miningLevelList = new ArrayList<Integer>();

    public static final List<List<Integer>> brewingItemList = new ArrayList<List<Integer>>();
    public static final List<Integer> brewingLevelList = new ArrayList<Integer>();
    public static final List<Object> potionList = new ArrayList<Object>();

    public static final List<List<Integer>> smithingItemList = new ArrayList<List<Integer>>();
    public static final List<Integer> smithingLevelList = new ArrayList<Integer>();

    public static final List<List<Integer>> craftingItemList = new ArrayList<List<Integer>>();
    public static final List<Integer> craftingLevelList = new ArrayList<Integer>();
    public static final List<String> craftingSkillList = new ArrayList<String>();

    // Custom
    public static final ArrayList<Object> customBlockList = new ArrayList<Object>();
    public static final ArrayList<Object> customItemList = new ArrayList<Object>();
    public static final ArrayList<Object> customEntityList = new ArrayList<Object>();

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
        case "minecraft:shears":
            return shearsList;
        case "minecraft:compass":
            return compassList;
        case "minecraft:custom_item":
            return customItemList;
        // Block
        case "minecraft:beehive":
            return beehiveList;
        case "minecraft:pumpkin":
            return pumpkinList;
        case "minecraft:anvil":
            return anvilList;
        case "minecraft:brewing_stand":
            return brewingStandList;
        case "minecraft:enchanting_table":
            return enchantingTableList;
        case "minecraft:smithing_table":
            return smithingTableList;
        case "minecraft:grindstone":
            return grindstoneList;
        case "minecraft:loom":
            return loomList;
        case "minecraft:smoker":
            return smokerList;
        case "minecraft:blast_furnace":
            return blastFurnaceList;
        case "minecraft:cartography_table":
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
        case "minecraft:beacon":
            return beaconList;
        case "minecraft:crafting_table":
            return craftingTableList;
        case "minecraft:fletching_table":
            return fletchingTableList;
        case "minecraft:custom_block":
            return customBlockList;
        // Entity
        case "minecraft:cow":
            return cowList;
        case "minecraft:mooshroom":
            return mooshroomList;
        case "minecraft:sheep":
            return sheepList;
        case "minecraft:snow_golem":
            return snowGolemList;
        case "minecraft:wandering_trader":
            return wanderingTraderList;
        case "minecraft:villager":
            return villagerList;
        case "minecraft:wolf":
            return wolfList;
        case "minecraft:breeding":
            return breedingList;
        case "minecraft:axolotl":
            return axolotlList;
        case "minecraft:piglin":
            return piglinList;
        case "minecraft:tadpole":
            return tadpoleList;
        case "minecraft:allay":
            return allayList;
        case "minecraft:goat":
            return goatList;
        case "minecraft:custom_entity":
            return customEntityList;
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
        listNames.add("minecraft:brewing_stand");
        listNames.add("minecraft:enchanting_table");
        listNames.add("minecraft:smithing_table");
        listNames.add("minecraft:grindstone");
        listNames.add("minecraft:loom");
        listNames.add("minecraft:smoker");
        listNames.add("minecraft:blast_furnace");
        listNames.add("minecraft:cartography_table");
        listNames.add("minecraft:barrel");
        listNames.add("minecraft:composter");
        listNames.add("minecraft:lectern");
        listNames.add("minecraft:stonecutter");
        listNames.add("minecraft:cauldron");
        listNames.add("minecraft:crafting_table");
        listNames.add("minecraft:fletching_table");
        listNames.add("minecraft:cow");
        listNames.add("minecraft:mooshroom");
        listNames.add("minecraft:sheep");
        listNames.add("minecraft:snow_golem");
        listNames.add("minecraft:wandering_trader");
        listNames.add("minecraft:villager");
        listNames.add("minecraft:wolf");
        listNames.add("minecraft:breeding");
        listNames.add("minecraft:furnace");
        listNames.add("minecraft:beacon");
        listNames.add("minecraft:axolotl");
        listNames.add("minecraft:piglin");
        listNames.add("minecraft:shears");
        listNames.add("minecraft:tadpole");
        listNames.add("minecraft:allay");
        listNames.add("minecraft:compass");
        listNames.add("minecraft:goat");
        // Custom
        listNames.add("minecraft:custom_block");
        listNames.add("minecraft:custom_item");
        listNames.add("minecraft:custom_entity");
        return listNames;
    }
}
