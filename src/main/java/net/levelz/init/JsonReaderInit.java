package net.levelz.init;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.levelz.data.MiningLevelLoader;
import net.minecraft.resource.ResourceType;

public class JsonReaderInit {

    // public static final List<List<Block>> MINING_BLOCK_LIST = new ArrayList<>();
    public static final List<List<Integer>> MINING_BLOCK_LIST = new ArrayList<>();
    // public static final List<Item> RACK_RESULT_ITEM_LIST = new ArrayList<>();
    public static final List<Integer> MINING_LEVEL_LIST = new ArrayList<>();

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MiningLevelLoader());
    }
}
