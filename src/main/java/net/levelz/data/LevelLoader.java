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
                ArrayList<Object> list = LevelLists.getList(data.get("item").getAsString());
                if (!LevelLists.elytraList.isEmpty()) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        LevelLists.elytraList.clear();
                    } else {
                        LOGGER.info("Resource {} was not loaded cause it already existed", id.toString());
                        continue;
                    }
                }
                LevelLists.elytraList.add(data.get("skill").getAsString());
                LevelLists.elytraList.add(data.get("level").getAsInt());
                // }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
        System.out.println(LevelLists.elytraList);
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

}