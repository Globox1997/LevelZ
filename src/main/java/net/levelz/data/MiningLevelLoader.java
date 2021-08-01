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
import net.levelz.init.LevelJsonInit;
import net.minecraft.block.Block;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class MiningLevelLoader implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Identifier getFabricId() {
        return new Identifier("levelz", "level");
    }

    @Override
    public void reload(ResourceManager manager) {
        for (Identifier id : manager.findResources("level", path -> path.endsWith(".json"))) {
            try {

                InputStream stream = manager.getResource(id).getInputStream();
                JsonObject data = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();

                // int index = LevelJsonInit.MINING_LEVEL_LIST.indexOf(data.get("level").getAsInt()); // Get rt index; doesnt have it

                // System.out.println(index + "::" + LevelJsonInit.MINING_LEVEL_LIST);

                if (LevelJsonInit.MINING_LEVEL_LIST.contains(data.get("level").getAsInt())) {
                    if (JsonHelper.getBoolean(data, "replace", false)) {
                        // LevelJsonInit.MINING_LEVEL_LIST.remove(index);
                        int index = LevelJsonInit.MINING_LEVEL_LIST.indexOf(data.get("level").getAsInt());
                        LevelJsonInit.MINING_LEVEL_LIST.remove(index);
                        LevelJsonInit.MINING_BLOCK_LIST.remove(index);
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
        // System.out.println(LevelJsonInit.MINING_BLOCK_LIST + "::" + LevelJsonInit.MINING_LEVEL_LIST);
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
        List<Integer> BLOCK_ID_LIST = new ArrayList<Integer>();
        for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
            BLOCK_ID_LIST.add(Registry.BLOCK.getRawId(Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString()))));
        }
        if (addToExisting) {
            int index = LevelJsonInit.MINING_LEVEL_LIST.indexOf(data.get("level").getAsInt());
            for (int u = 0; u < BLOCK_ID_LIST.size(); u++) {
                LevelJsonInit.MINING_BLOCK_LIST.get(index).add(BLOCK_ID_LIST.get(u));
            }
        } else {
            LevelJsonInit.MINING_LEVEL_LIST.add(data.get("level").getAsInt());
            LevelJsonInit.MINING_BLOCK_LIST.add(BLOCK_ID_LIST);
        }
    }

}