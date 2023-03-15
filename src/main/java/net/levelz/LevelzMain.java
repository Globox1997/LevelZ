package net.levelz;

import net.fabricmc.api.ModInitializer;
import net.levelz.init.*;
import net.levelz.network.PlayerStatsServerPacket;

public class LevelzMain implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandInit.init();
        CompatInit.init();
        ConfigInit.init();
        CriteriaInit.init();
        EntityInit.init();
        EventInit.init();
        JsonReaderInit.init();
        PlayerStatsServerPacket.init();
        TagInit.init();
        ItemInit.init();
    }
}

// You are LOVED!!!
// Jesus loves you unconditionally!
