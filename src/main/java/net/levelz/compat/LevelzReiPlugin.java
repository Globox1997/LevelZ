package net.levelz.compat;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import net.levelz.init.ConfigInit;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import java.util.Collections;

public class LevelzReiPlugin implements REIClientPlugin {

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        if (ConfigInit.CONFIG.reiExclusionzone)
            zones.register(InventoryScreen.class, screen -> {
                int i = (screen.width - 176) / 2;
                int j = (screen.height - 166) / 2;
                return Collections.singleton(new Rectangle(i - 18, j + 6, 18, 20));
            });
    }
}
