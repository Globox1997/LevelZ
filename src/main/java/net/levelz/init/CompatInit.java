package net.levelz.init;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.access.PlayerStatsManagerAccess;
import net.minecraft.util.Identifier;

public class CompatInit {

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api"))
            Placeholders.register(new Identifier("levelz", "playerlevel"), (ctx, arg) -> {
                if (ctx.hasPlayer()) {
                    return PlaceholderResult.value(Integer.toString(((PlayerStatsManagerAccess) ctx.player()).getPlayerStatsManager(ctx.player()).getLevel("level")));
                } else {
                    return PlaceholderResult.invalid("No player!");
                }
            });
    }
}
