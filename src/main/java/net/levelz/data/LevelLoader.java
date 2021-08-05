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
    private static final Logger LOGGER = LogManager.getLogger();
    private List<List<Integer>> objectList = new ArrayList<>();
    private List<Integer> levelList = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("levelz", "level_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        for (Identifier id : manager.findResources("mining", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
                if (levelList.contains(data.get("level").getAsInt())) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        int index = levelList.indexOf(data.get("level").getAsInt());
                        levelList.remove(index);
                        objectList.remove(index);
                        fillLists(data, false, 1);
                    } else {
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

        for (Identifier id : manager.findResources("item", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
                ArrayList<Object> list = LevelLists.getList(data.get("item").getAsString());
                if (data.get("item").getAsString().equals("minecraft:armor") || data.get("item").getAsString().equals("minecraft:tool") || data.get("item").getAsString().equals("minecraft:hoe")
                        || data.get("item").getAsString().equals("minecraft:sword") || data.get("item").getAsString().equals("minecraft:axe")) {
                    if (list.contains(data.get("material").getAsString())) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            int removeLines = list.indexOf(data.get("material").getAsString());
                            for (int i = 0; i < 4; i++) {
                                list.remove(removeLines);
                            }
                        } else {
                            if (!(boolean) list.get(list.indexOf(data.get("material")) + 3)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    list.add(data.get("material").getAsString());
                    list.add(data.get("skill").getAsString());
                    list.add(data.get("level").getAsInt());
                    list.add(JsonHelper.getBoolean(data, "replace", false));
                } else {
                    if (!list.isEmpty()) {
                        if (JsonHelper.getBoolean(data, "replace", false)) {
                            list.clear();
                        } else {
                            if (!(boolean) list.get(2)) {
                                LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                            }
                            continue;
                        }
                    }
                    list.add(data.get("skill").getAsString());
                    list.add(data.get("level").getAsInt());
                    list.add(JsonHelper.getBoolean(data, "replace", false));
                    // Bow,Crossbow
                    if (data.get("bonus") != null) {
                        list.add(data.get("bonus").getAsFloat());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }

        for (Identifier id : manager.findResources("block", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
                ArrayList<Object> list = LevelLists.getList(data.get("block").getAsString());
                if (!list.isEmpty()) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        list.clear();
                    } else {
                        if (!(boolean) list.get(2)) {
                            LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                        }
                        continue;
                    }
                }
                list.add(data.get("skill").getAsString());
                list.add(data.get("level").getAsInt());
                list.add(JsonHelper.getBoolean(data, "replace", false));
                // EnchantingTable
                if (data.get("enchanting") != null) {
                    for (int i = 0; i < data.getAsJsonArray("enchanting").size(); i++) {
                        list.add(data.get("enchanting").getAsJsonArray().get(i).getAsInt());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }

        for (Identifier id : manager.findResources("entity", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
                ArrayList<Object> list = LevelLists.getList(data.get("entity").getAsString());
                if (!list.isEmpty()) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        list.clear();
                    } else {
                        if (!(boolean) list.get(2)) {
                            LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                        }
                        continue;
                    }
                }
                list.add(data.get("skill").getAsString());
                list.add(data.get("level").getAsInt());
                list.add(JsonHelper.getBoolean(data, "replace", false));
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }

        for (Identifier id : manager.findResources("brewing", path -> path.endsWith(".json"))) {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
                if (levelList.contains(data.get("level").getAsInt())) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        int index = levelList.indexOf(data.get("level").getAsInt());
                        levelList.remove(index);
                        objectList.remove(index);
                        fillLists(data, false, 2);
                    } else {
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

        System.out.println(LevelLists.elytraList);
        System.out.println(LevelLists.armorList);
        System.out.println(LevelLists.bowList);
        System.out.println("Brewing Item List: " + LevelLists.brewingItemList);
        System.out.println("Brewing Level List: " + LevelLists.brewingLevelList);
        System.out.println("Mining Block List: " + LevelLists.miningBlockList);
        System.out.println("Mining Level List: " + LevelLists.miningLevelList);
        System.out.println(LevelLists.enchantingTableList);

        // Test here
    }

    private void fillLists(JsonObject data, boolean addToExisting, int type) {
        List<Integer> idList = new ArrayList<Integer>();
        if (type == 1) {
            for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
                idList.add(Registry.BLOCK.getRawId(Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString()))));
            }
        } else if (type == 2) {
            for (int i = 0; i < data.getAsJsonArray("item").size(); i++) {
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
        }
    }

    // type: 1 = mining; 2 = brewing
    private void sortAndFillLists(List<Integer> levelList, List<List<Integer>> objectList, int type) {
        if (type != 0) {
            if (type == 1) {
                LevelLists.miningLevelList.addAll(levelList);
                LevelLists.miningLevelList.sort(Comparator.naturalOrder());
                for (int i = 0; i < levelList.size(); i++) {
                    LevelLists.miningBlockList.add(i, objectList.get(levelList.indexOf(LevelLists.miningLevelList.get(i))));
                }
            } else if (type == 2) {
                System.out.println("Check:" + levelList);
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

}