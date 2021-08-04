package net.levelz.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
                if (LevelLists.miningLevelList.contains(data.get("level").getAsInt())) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        int index = LevelLists.miningLevelList.indexOf(data.get("level").getAsInt());
                        LevelLists.miningLevelList.remove(index);
                        LevelLists.miningBlockList.remove(index);
                        fillMiningLists(data, false);
                    } else {
                        fillMiningLists(data, true);

                    }
                } else {
                    fillMiningLists(data, false);
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
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
                if (LevelLists.brewingLevelList.contains(data.get("level").getAsInt())) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        int index = LevelLists.brewingLevelList.indexOf(data.get("level").getAsInt());
                        LevelLists.brewingLevelList.remove(index);
                        LevelLists.brewingItemList.remove(index);
                        fillBrewingLists(data, false);
                    } else {
                        fillBrewingLists(data, true);

                    }
                } else {
                    fillBrewingLists(data, false);
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
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

    private void fillMiningLists(JsonObject data, boolean addToExisting) {
        List<Integer> blockIdList = new ArrayList<Integer>();
        for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
            blockIdList.add(Registry.BLOCK.getRawId(Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString()))));
        }
        if (addToExisting) {
            int index = LevelLists.miningLevelList.indexOf(data.get("level").getAsInt());
            for (int u = 0; u < blockIdList.size(); u++) {
                LevelLists.miningBlockList.get(index).add(blockIdList.get(u));
            }
        } else {
            LevelLists.miningLevelList.add(data.get("level").getAsInt());
            LevelLists.miningBlockList.add(blockIdList);
        }
    }

    private void fillBrewingLists(JsonObject data, boolean addToExisting) {
        List<Integer> itemIdList = new ArrayList<Integer>();
        for (int i = 0; i < data.getAsJsonArray("item").size(); i++) {
            itemIdList.add(Registry.ITEM.getRawId(Registry.ITEM.get(new Identifier(data.getAsJsonArray("item").get(i).getAsString()))));
        }
        if (addToExisting) {
            int index = LevelLists.brewingLevelList.indexOf(data.get("level").getAsInt());
            for (int u = 0; u < itemIdList.size(); u++) {
                LevelLists.brewingItemList.get(index).add(itemIdList.get(u));
            }
        } else {
            LevelLists.brewingLevelList.add(data.get("level").getAsInt());
            LevelLists.brewingItemList.add(itemIdList);
        }
    }

}