package net.levelz.fabric.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.levelz.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

}
