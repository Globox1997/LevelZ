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
                LevelJsonInit.MINING_LEVEL_LIST.add(data.get("level").getAsInt());

                List<Block> BLOCK_LIST = new ArrayList<Block>();
                // for (int i = 0; i < data.size(); i++) {
                // if (data.has("block")) {
                // BLOCK_LIST.add((Block) Registry.BLOCK.get(new Identifier(data.get("block").getAsString())));
                // }
                // }
                for (int i = 0; i < data.getAsJsonArray("block").size(); i++) {
                    BLOCK_LIST.add((Block) Registry.BLOCK.get(new Identifier(data.getAsJsonArray("block").get(i).getAsString())));
                }
                // BLOCK_LIST.add((Block) Registry.BLOCK.get(new Identifier(data.get("block").getAsString())));
                LevelJsonInit.MINING_BLOCK_LIST.add(BLOCK_LIST);
                System.out.println(LevelJsonInit.MINING_BLOCK_LIST);
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        }
    }

}