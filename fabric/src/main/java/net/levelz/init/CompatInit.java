package net.levelz.init;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
//import ht.treechop.api.TreeChopEvents;
import net.levelz.LevelzMain;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.platform.Services;

public class CompatInit {

    public static void init() {
        if (Services.PLATFORM.isModLoaded("placeholder-api")) {
            Placeholders.registerServer(LevelzMain.identifierOf("playerlevel"), (ctx, arg) -> {
                if (ctx.hasServerPlayer()) {
                    return PlaceholderResult.value(Integer.toString(((LevelManagerAccess) ctx.serverPlayer()).getLevelManager().getOverallLevel()));
                } else {
                    return PlaceholderResult.invalid("No player!");
                }
            });
        }
//        if (Services.PLATFORM.isModLoaded("treechop")) {
//            TreeChopEvents.BEFORE_CHOP.register((world, player, pos, state, chopData) -> {
//                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
//                if (!levelManager.hasRequiredStackLevel(player.getMainHandItem())) {
//                    player.level().destroyBlock(pos, false);
//                    return false;
//                } else if (!levelManager.hasRequiredMiningLevel(world.getBlockState(pos).getBlock())) {
//                    player.level().destroyBlock(pos, false);
//                    return false;
//                }
//                return true;
//            });
//        }
    }
}
