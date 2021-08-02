package net.levelz.init;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.levelz.data.LevelLoader;
import net.minecraft.resource.ResourceType;

public class JsonReaderInit {

    // public static final List<List<Block>> MINING_BLOCK_LIST = new ArrayList<>();
    public static final List<List<Integer>> miningBlockList = new ArrayList<>();
    // public static final List<Item> RACK_RESULT_ITEM_LIST = new ArrayList<>();
    public static final List<Integer> miningLevelList = new ArrayList<>();

    public static final ArrayList<Object> elytraUnlockList = new ArrayList<Object>();
    // public static final ArrayList<Object> elytraUnlockList = new ArrayList<Object>();
    // public static final ArrayList<Object> elytraUnlockList = new ArrayList<Object>();
    // public static final ArrayList<Object> elytraUnlockList = new ArrayList<Object>();
    // public static final ArrayList<Object> elytraUnlockList = new ArrayList<Object>();

    // public static final List<List<Object>> itemUnlockerList = new ArrayList<>();
    // public static final int elytraUnlock;

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new LevelLoader());

    }

}
