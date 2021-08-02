package net.levelz.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.levelz.init.JsonReaderInit;
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

                // int index = LevelJsonInit.MINING_LEVEL_LIST.indexOf(data.get("level").getAsInt()); // Get rt index; doesnt have it

                // System.out.println(index + "::" + LevelJsonInit.MINING_LEVEL_LIST);

                if (JsonReaderInit.miningLevelList.contains(data.get("level").getAsInt())) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        // LevelJsonInit.MINING_LEVEL_LIST.remove(index);
                        int index = JsonReaderInit.miningLevelList.indexOf(data.get("level").getAsInt());
                        JsonReaderInit.miningLevelList.remove(index);
                        JsonReaderInit.miningBlockList.remove(index);
                        fillLists(data, false);
                    } else {
                        fillLists(data, true);

                    }
                } else {
                    fillLists(data, false);
                }
                // LevelJsonInit.MINING_LEVEL_LIST.add(data.get("level").getAsInt());

                // List<Block> BLOCK_LIST = new ArrayList<Block>();
                // for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
                // BLOCK_LIST.add((Block) Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString())));
                // }
                // LevelJsonInit.MINING_BLOCK_LIST.add(BLOCK_LIST);

            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
        for (Identifier id : manager.findResources("item", path -> path.endsWith(".json"))) {
            try {

                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
                // if (data.get("item").getAsString().equals("minecraft:elytra")) {
                // JsonReaderInit.elytraUnlocker.add(0, data.get("skill").getAsString());
                // JsonReaderInit.elytraUnlocker.add(1, data.get("level").getAsInt());
                // }
                // for (int i = 0; i < JsonReaderInit.itemUnlockerList.size(); i++) {
                // if (JsonReaderInit.itemUnlockerList.get(i).contains(data.get("item").getAsString())) {
                // // if (JsonHelper.getBoolean(data, "replace", false)) {
                // // } else {

                // // }
                // }
                // }
                ArrayList<Object> itemUnlocker = new ArrayList<Object>();
                itemUnlocker.add(data.get("item").getAsString());
                itemUnlocker.add(data.get("skill").getAsString());
                itemUnlocker.add(data.get("level").getAsInt());
                JsonReaderInit.itemUnlockerList.add(itemUnlocker);

                // if (JsonReaderInit.itemUnlockerList.listIterator().) {
                // ArrayList<Object> itemUnlocker = new ArrayList<Object>();
                // itemUnlocker.add(data.get("minecraft:elytra").getAsString());
                // itemUnlocker.add(data.get("skill").getAsString());
                // JsonReaderInit.itemUnlockerList.add(itemUnlocker);
                // // JsonReaderInit.itemUnlockerList.add(0, data.get("skill").getAsString());
                // // JsonReaderInit.itemUnlockerList.add(1, data.get("level").getAsInt());
                // }
                // if (JsonHelper.getBoolean(data, "replace", false)) {

                // } else {

                // }
                // if (JsonReaderInit.MINING_LEVEL_LIST.contains(data.get("level").getAsInt())) {
                // if (JsonHelper.getBoolean(data, "replace", false)) {
                // // LevelJsonInit.MINING_LEVEL_LIST.remove(index);
                // int index = JsonReaderInit.MINING_LEVEL_LIST.indexOf(data.get("level").getAsInt());
                // JsonReaderInit.MINING_LEVEL_LIST.remove(index);
                // JsonReaderInit.MINING_BLOCK_LIST.remove(index);
                // fillLists(data, false);
                // } else {
                // fillLists(data, true);

                // }
                // } else {
                // fillLists(data, false);
                // }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
        // for (int k = 0; k < JsonReaderInit.itemUnlockerList.size(); k++) {
        // JsonReaderInit.itemUnlockerList.get(k).
        // }
        // Collections.sort(JsonReaderInit.itemUnlockerList, new Comparator<LinkedList<Double>>() {
        // @Override
        // public int compare(LinkedList<Double> o1, LinkedList<Double> o2) {
        // return (int)(o1.get(0) - o2.get(0));
        // }
        // });
        // JsonReaderInit.itemUnlockerList.sort(Comparator.naturalOrder());
        // Collections.natu
        // System.out.println(LevelJsonInit.MINING_BLOCK_LIST + "::" + LevelJsonInit.MINING_LEVEL_LIST);


        // System.out.println("Init:" + JsonReaderInit.itemUnlockerList);

        // ArrayList<String> sorterList = new ArrayList<String>();
        // for (int k = 0; k < JsonReaderInit.itemUnlockerList.size(); k++) {
        //     sorterList.add((String) JsonReaderInit.itemUnlockerList.get(k).get(0));
        // }
        // sorterList.sort(Comparator.naturalOrder());
        // System.out.println("Sorted:" + sorterList);
        // List<List<Object>> list = new ArrayList<>();
        // for (int o = 0; o < sorterList.size(); o++) {
        //     for (int l = 0; l < JsonReaderInit.itemUnlockerList.size(); l++) {
        //         if (JsonReaderInit.itemUnlockerList.get(l).get(0) == sorterList.get(o)) {
        //             list.add(JsonReaderInit.itemUnlockerList.get(l));
        //             break;
        //         }
        //     }
        // }
        // System.out.println("SortedList:" + list);

    }

    // private void fillLists(JsonObject data, boolean addToExisting) {
    // List<Block> BLOCK_LIST = new ArrayList<Block>();
    // for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
    // BLOCK_LIST.add((Block) Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString())));
    // }
    // if (addToExisting) {
    // int index = LevelJsonInit.MINING_LEVEL_LIST.indexOf(data.get("level").getAsInt());
    // for (int u = 0; u < BLOCK_LIST.size(); u++) {
    // LevelJsonInit.MINING_BLOCK_LIST.get(index).add(BLOCK_LIST.get(u));
    // }
    // } else {
    // LevelJsonInit.MINING_LEVEL_LIST.add(data.get("level").getAsInt());
    // LevelJsonInit.MINING_BLOCK_LIST.add(BLOCK_LIST);
    // }
    // }

    private void fillLists(JsonObject data, boolean addToExisting) {
        List<Integer> blockIdList = new ArrayList<Integer>();
        for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
            blockIdList.add(Registry.BLOCK.getRawId(Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString()))));
        }
        if (addToExisting) {
            int index = JsonReaderInit.miningLevelList.indexOf(data.get("level").getAsInt());
            for (int u = 0; u < blockIdList.size(); u++) {
                JsonReaderInit.miningBlockList.get(index).add(blockIdList.get(u));
            }
        } else {
            JsonReaderInit.miningLevelList.add(data.get("level").getAsInt());
            JsonReaderInit.miningBlockList.add(blockIdList);
        }
    }

}