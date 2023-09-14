package net.levelz.init;

import java.util.ArrayList;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import ht.treechop.api.TreeChopEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.item.AxeItem;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CompatInit {

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            Placeholders.register(new Identifier("levelz", "playerlevel"), (ctx, arg) -> {
                if (ctx.hasPlayer()) {
                    return PlaceholderResult.value(Integer.toString(((PlayerStatsManagerAccess) ctx.player()).getPlayerStatsManager().getOverallLevel()));
                } else {
                    return PlaceholderResult.invalid("No player!");
                }
            });
        }
        if (FabricLoader.getInstance().isModLoaded("treechop")) {
            // DETECT EVENT has player = null
            TreeChopEvents.BEFORE_CHOP.register((world, player, pos, state, chopData) -> {
                if (player != null && player.getMainHandStack().getItem() instanceof MiningToolItem) {
                    ArrayList<Object> levelList = LevelLists.customItemList;
                    if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(player.getMainHandStack().getItem()).toString())) {
                        if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, Registries.ITEM.getId(player.getMainHandStack().getItem()).toString(), true)) {
                            player.getWorld().breakBlock(pos, false);
                            return false;
                        }
                    } else {
                        if (player.getMainHandStack().getItem() instanceof AxeItem) {
                            levelList = LevelLists.axeList;
                            if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, ((AxeItem) player.getMainHandStack().getItem()).getMaterial().toString().toLowerCase(), true)) {
                                player.getWorld().breakBlock(pos, false);
                                return false;
                            }
                        }
                    }
                }
                return true;
            });
        }
    }
}
