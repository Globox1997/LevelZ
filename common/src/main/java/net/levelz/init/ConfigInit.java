package net.levelz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.levelz.config.LevelzConfig;
import net.levelz.platform.Services;

public class ConfigInit {

    public static final boolean isOriginsLoaded = Services.PLATFORM.isModLoaded("origins");

    public static LevelzConfig CONFIG = new LevelzConfig();

    public static void init() {
        AutoConfig.register(LevelzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(LevelzConfig.class).getConfig();
    }

}