package net.levelz;

import net.fabricmc.api.ModInitializer;
import net.levelz.init.CommandInit;
import net.levelz.init.ConfigInit;
import net.levelz.init.JsonReaderInit;
import net.levelz.network.PlayerStatsServerPacket;

public class LevelzMain implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandInit.init();
        ConfigInit.init();
        JsonReaderInit.init();
        PlayerStatsServerPacket.init();
    }
}

// You are LOVED!!!
// Jesus loves you unconditional!