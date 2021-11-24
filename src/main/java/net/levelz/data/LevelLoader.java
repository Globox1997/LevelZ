package net.levelz.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class LevelLoader implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger("LevelZ");
    // For mining and brewing
    // List of lists which get filled and cleared while loading data
    private List<List<Integer>> objectList = new ArrayList<>();
    private List<Integer> levelList = new ArrayList<>();
    // Check if list is already replacing any other list
    private List<Boolean> replaceList = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("levelz", "level_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        clearEveryList();
        // Mining
        for (Identifier id : manager.findResources("mining", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                if (levelList.contains(data.get("level").getAsInt())) {
                    int index = levelList.indexOf(data.get("level").getAsInt());
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        levelList.remove(index);
                        objectList.remove(index);
                        replaceList.remove(index);
                        fillLists(data, false, 1);
                    } else if (!replaceList.get(index)) {
                        fillLists(data, true, 1);
                    }
                } else {
                    fillLists(data, false, 1);
                }

            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
        // Fill mining list
        sortAndFillLists(levelList, objectList, 1);

        // Item
        for (Identifier id : manager.findResources("item", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                ArrayList<Object> list = LevelLists.getList(data.get("item").getAsString());
                if (data.get("item").getAsString().equals("minecraft:armor") || data.get("item").getAsString().equals("minecraft:tool") || data.get("item").getAsString().equals("minecraft:hoe")
                        || data.get("item").getAsString().equals("minecraft:sword") || data.get("item").getAsString().equals("minecraft:axe")) {
                    if (list.contains(data.get("material").getAsString())) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            int removeLines = list.indexOf(data.get("material").getAsString());
                            for (int i = 0; i < 5; i++) {
                                list.remove(removeLines);
                            }
                        } else {
                            if (!(boolean) list.get(list.indexOf(data.get("material")) + 4)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    list.add(data.get("material").getAsString());
                    list.add(data.get("skill").getAsString());
                    list.add(data.get("level").getAsInt());
                    list.add(data.get("item").getAsString());
                    list.add(JsonHelper.getBoolean(data, "replace", false));
                } else if (data.get("item").getAsString().equals("minecraft:custom_item")) {
                    ArrayList<Object> customList = LevelLists.getList(data.get("item").getAsString());
                    if (customList.contains(data.get("object").getAsString())) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            int removeLines = list.indexOf(data.get("object").getAsString());
                            for (int i = 0; i < 5; i++) {
                                list.remove(removeLines);
                            }
                        } else {
                            if (!(boolean) list.get(list.indexOf(data.get("object")) + 4)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    if (Registry.ITEM.get(new Identifier(data.get("object").getAsString())).toString().equals("air")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid item identifier", id.toString(), data.get("object").getAsString());
                        continue;
                    }
                    customList.add(data.get("object").getAsString());
                    customList.add(data.get("skill").getAsString());
                    customList.add(data.get("level").getAsInt());
                    customList.add(data.get("item").getAsString());
                    customList.add(JsonHelper.getBoolean(data, "replace", false));
                } else {
                    if (!list.isEmpty()) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            list.clear();
                        } else {
                            if (!(boolean) list.get(3)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    if (Registry.ITEM.get(new Identifier(data.get("item").getAsString())).toString().equals("air")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid item identifier", id.toString(), data.get("item").getAsString());
                        continue;
                    }
                    list.add(data.get("skill").getAsString());
                    list.add(data.get("level").getAsInt());
                    list.add(data.get("item").getAsString());
                    list.add(JsonHelper.getBoolean(data, "replace", false));
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }

        // Block
        for (Identifier id : manager.findResources("block", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                ArrayList<Object> list = LevelLists.getList(data.get("block").getAsString());

                if (data.get("block").getAsString().equals("minecraft:custom_block")) {
                    ArrayList<Object> customList = LevelLists.getList(data.get("block").getAsString());
                    if (customList.contains(data.get("object").getAsString())) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            int removeLines = list.indexOf(data.get("object").getAsString());
                            for (int i = 0; i < 5; i++) {
                                list.remove(removeLines);
                            }
                        } else {
                            if (!(boolean) list.get(list.indexOf(data.get("object")) + 4)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    if (Registry.BLOCK.get(new Identifier(data.get("object").getAsString())).toString().equals("air")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid block identifier", id.toString(), data.get("object").getAsString());
                        continue;
                    }
                    customList.add(data.get("object").getAsString());
                    customList.add(data.get("skill").getAsString());
                    customList.add(data.get("level").getAsInt());
                    customList.add(data.get("block").getAsString());
                    customList.add(JsonHelper.getBoolean(data, "replace", false));
                } else {
                    if (!list.isEmpty()) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            list.clear();
                        } else {
                            if (!(boolean) list.get(3)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    if (Registry.BLOCK.get(new Identifier(data.get("block").getAsString())).toString().equals("air")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid block identifier", id.toString(), data.get("block").getAsString());
                        continue;
                    }
                    list.add(data.get("skill").getAsString());
                    list.add(data.get("level").getAsInt());
                    list.add(data.get("block").getAsString());
                    list.add(JsonHelper.getBoolean(data, "replace", false));
                    // EnchantingTable
                    if (data.get("enchanting") != null) {
                        for (int i = 0; i < data.getAsJsonArray("enchanting").size(); i++) {
                            list.add(data.get("enchanting").getAsJsonArray().get(i).getAsInt());
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }

        // Entity
        for (Identifier id : manager.findResources("entity", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                ArrayList<Object> list = LevelLists.getList(data.get("entity").getAsString());

                if (data.get("entity").getAsString().equals("minecraft:custom_entity")) {
                    ArrayList<Object> customList = LevelLists.getList(data.get("entity").getAsString());
                    if (customList.contains(data.get("object").getAsString())) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            int removeLines = list.indexOf(data.get("object").getAsString());
                            for (int i = 0; i < 5; i++) {
                                list.remove(removeLines);
                            }
                        } else {
                            if (!(boolean) list.get(list.indexOf(data.get("object")) + 4)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    if (Registry.ENTITY_TYPE.get(new Identifier(data.get("object").getAsString())).toString().equals("air")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid entity identifier", id.toString(), data.get("object").getAsString());
                        continue;
                    }
                    customList.add(data.get("object").getAsString());
                    customList.add(data.get("skill").getAsString());
                    customList.add(data.get("level").getAsInt());
                    customList.add(data.get("entity").getAsString());
                    customList.add(JsonHelper.getBoolean(data, "replace", false));
                } else {
                    if (!list.isEmpty()) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            list.clear();
                        } else {
                            if (!(boolean) list.get(3)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    if (Registry.ENTITY_TYPE.get(new Identifier(data.get("entity").getAsString())).toString().equals("air")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid entity identifier", id.toString(), data.get("entity").getAsString());
                        continue;
                    }
                    list.add(data.get("skill").getAsString());
                    list.add(data.get("level").getAsInt());
                    list.add(data.get("entity").getAsString());
                    list.add(JsonHelper.getBoolean(data, "replace", false));
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }

        // Brewing
        for (Identifier id : manager.findResources("brewing", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                if (levelList.contains(data.get("level").getAsInt())) {
                    int index = levelList.indexOf(data.get("level").getAsInt());
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        levelList.remove(index);
                        objectList.remove(index);
                        replaceList.remove(index);
                        fillLists(data, false, 2);
                    } else if (!replaceList.get(index)) {
                        fillLists(data, true, 2);
                    }
                } else {
                    fillLists(data, false, 2);
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
        // Fill brewing list
        sortAndFillLists(levelList, objectList, 2);

        addAllInOneList();

        // Test check here:
        // System.out.println(LevelLists.miningBlockList);
        // System.out.println(LevelLists.miningLevelList);
    }

    private void fillLists(JsonObject data, boolean addToExisting, int type) {
        List<Integer> idList = new ArrayList<Integer>();
        if (type == 1) {
            for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
                if (Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString())).toString().equals("air")) {
                    LOGGER.info("{} is not a valid block identifier", data.getAsJsonArray("block").get(i).getAsString());
                }
                idList.add(Registry.BLOCK.getRawId(Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString()))));
            }
        } else if (type == 2) {
            for (int i = 0; i < data.getAsJsonArray("item").size(); i++) {
                if (Registry.ITEM.get(new Identifier(data.getAsJsonArray("item").get(i).getAsString())).toString().equals("air")) {
                    LOGGER.info("{} is not a valid item identifier", data.getAsJsonArray("item").get(i).getAsString());
                }
                idList.add(Registry.ITEM.getRawId(Registry.ITEM.get(new Identifier(data.getAsJsonArray("item").get(i).getAsString()))));
            }
        }

        if (addToExisting) {
            int index = levelList.indexOf(data.get("level").getAsInt());
            for (int u = 0; u < idList.size(); u++) {
                objectList.get(index).add(idList.get(u));
            }
        } else {
            levelList.add(data.get("level").getAsInt());
            objectList.add(idList);
            replaceList.add(JsonHelper.getBoolean(data, "replace", false));
        }
    }

    // type: 1 = mining; 2 = brewing
    private void sortAndFillLists(List<Integer> levelList, List<List<Integer>> objectList, int type) {
        // clear replace list for next usage
        replaceList.clear();

        if (type != 0) {
            if (type == 1) {
                LevelLists.miningLevelList.addAll(levelList);
                LevelLists.miningLevelList.sort(Comparator.naturalOrder());
                for (int i = 0; i < levelList.size(); i++) {
                    LevelLists.miningBlockList.add(i, objectList.get(levelList.indexOf(LevelLists.miningLevelList.get(i))));
                }
            } else if (type == 2) {
                LevelLists.brewingLevelList.addAll(levelList);
                LevelLists.brewingLevelList.sort(Comparator.naturalOrder());
                for (int i = 0; i < levelList.size(); i++) {
                    LevelLists.brewingItemList.add(i, objectList.get(levelList.indexOf(LevelLists.brewingLevelList.get(i))));
                }
            }
            this.objectList.clear();
            this.levelList.clear();
        }

    }

    public static void addAllInOneList() {
        LevelLists.listOfAllLists.add(LevelLists.anvilList);
        LevelLists.listOfAllLists.add(LevelLists.armorList);
        LevelLists.listOfAllLists.add(LevelLists.axeList);
        LevelLists.listOfAllLists.add(LevelLists.barrelList);
        LevelLists.listOfAllLists.add(LevelLists.beehiveList);
        LevelLists.listOfAllLists.add(LevelLists.blastFurnaceList);
        LevelLists.listOfAllLists.add(LevelLists.bowList);
        LevelLists.listOfAllLists.add(LevelLists.brewingStandList);
        LevelLists.listOfAllLists.add(LevelLists.bucketList);
        LevelLists.listOfAllLists.add(LevelLists.cartographyList);
        LevelLists.listOfAllLists.add(LevelLists.cauldronList);
        LevelLists.listOfAllLists.add(LevelLists.composterList);
        LevelLists.listOfAllLists.add(LevelLists.cowList);
        LevelLists.listOfAllLists.add(LevelLists.crossbowList);
        LevelLists.listOfAllLists.add(LevelLists.dragonBreathList);
        LevelLists.listOfAllLists.add(LevelLists.elytraList);
        LevelLists.listOfAllLists.add(LevelLists.enchantingTableList);
        LevelLists.listOfAllLists.add(LevelLists.fishingList);
        LevelLists.listOfAllLists.add(LevelLists.flintAndSteelList);
        LevelLists.listOfAllLists.add(LevelLists.grindstoneList);
        LevelLists.listOfAllLists.add(LevelLists.hoeList);
        LevelLists.listOfAllLists.add(LevelLists.lecternList);
        LevelLists.listOfAllLists.add(LevelLists.loomList);
        LevelLists.listOfAllLists.add(LevelLists.mooshroomList);
        LevelLists.listOfAllLists.add(LevelLists.pumpkinList);
        LevelLists.listOfAllLists.add(LevelLists.sheepList);
        LevelLists.listOfAllLists.add(LevelLists.shieldList);
        LevelLists.listOfAllLists.add(LevelLists.smithingTableList);
        LevelLists.listOfAllLists.add(LevelLists.smokerList);
        LevelLists.listOfAllLists.add(LevelLists.snowGolemList);
        LevelLists.listOfAllLists.add(LevelLists.stonecutterList);
        LevelLists.listOfAllLists.add(LevelLists.swordList);
        LevelLists.listOfAllLists.add(LevelLists.toolList);
        LevelLists.listOfAllLists.add(LevelLists.totemList);
        LevelLists.listOfAllLists.add(LevelLists.tridentList);
        LevelLists.listOfAllLists.add(LevelLists.villagerList);
        LevelLists.listOfAllLists.add(LevelLists.wanderingTraderList);
        LevelLists.listOfAllLists.add(LevelLists.wolfList);
        LevelLists.listOfAllLists.add(LevelLists.breedingList);
        LevelLists.listOfAllLists.add(LevelLists.furnaceList);
        LevelLists.listOfAllLists.add(LevelLists.beaconList);
        LevelLists.listOfAllLists.add(LevelLists.customBlockList);
        LevelLists.listOfAllLists.add(LevelLists.customItemList);
        LevelLists.listOfAllLists.add(LevelLists.customEntityList);
    }

    public static void clearEveryList() {
        LevelLists.anvilList.clear();
        LevelLists.armorList.clear();
        LevelLists.axeList.clear();
        LevelLists.barrelList.clear();
        LevelLists.beehiveList.clear();
        LevelLists.blastFurnaceList.clear();
        LevelLists.bowList.clear();
        LevelLists.brewingStandList.clear();
        LevelLists.bucketList.clear();
        LevelLists.cartographyList.clear();
        LevelLists.cauldronList.clear();
        LevelLists.composterList.clear();
        LevelLists.cowList.clear();
        LevelLists.crossbowList.clear();
        LevelLists.dragonBreathList.clear();
        LevelLists.elytraList.clear();
        LevelLists.enchantingTableList.clear();
        LevelLists.fishingList.clear();
        LevelLists.flintAndSteelList.clear();
        LevelLists.grindstoneList.clear();
        LevelLists.hoeList.clear();
        LevelLists.lecternList.clear();
        LevelLists.loomList.clear();
        LevelLists.mooshroomList.clear();
        LevelLists.pumpkinList.clear();
        LevelLists.sheepList.clear();
        LevelLists.shieldList.clear();
        LevelLists.smithingTableList.clear();
        LevelLists.smokerList.clear();
        LevelLists.snowGolemList.clear();
        LevelLists.stonecutterList.clear();
        LevelLists.swordList.clear();
        LevelLists.toolList.clear();
        LevelLists.totemList.clear();
        LevelLists.tridentList.clear();
        LevelLists.villagerList.clear();
        LevelLists.wanderingTraderList.clear();
        LevelLists.wolfList.clear();
        LevelLists.breedingList.clear();
        LevelLists.furnaceList.clear();
        LevelLists.beaconList.clear();
        LevelLists.customBlockList.clear();
        LevelLists.customItemList.clear();
        LevelLists.customEntityList.clear();

        LevelLists.miningBlockList.clear();
        LevelLists.miningLevelList.clear();
        LevelLists.brewingItemList.clear();
        LevelLists.brewingLevelList.clear();
        // Potion list isn't filled via levelz datapacks
        // LevelLists.potionList.clear();
    }

}