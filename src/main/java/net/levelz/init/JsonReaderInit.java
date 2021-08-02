package net.levelz.init;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.levelz.data.LevelLoader;
import net.minecraft.resource.ResourceType;

public class JsonReaderInit {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new LevelLoader());
    }

}
