package net.levelz;

import net.fabricmc.api.ClientModInitializer;
import net.levelz.init.KeyInit;
import net.levelz.init.RenderInit;
import net.levelz.network.PlayerStatsClientPacket;

public class LevelzClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyInit.init();
        PlayerStatsClientPacket.init();
        RenderInit.init();
    }

}