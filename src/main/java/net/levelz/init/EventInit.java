package net.levelz.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.levelz.access.PlayerSyncAccess;

public class EventInit {

    public static void init() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((PlayerSyncAccess) player).syncStats(false);
        });
    }

}