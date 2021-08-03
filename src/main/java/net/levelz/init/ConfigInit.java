package net.levelz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.levelz.config.LevelzConfig;

public class ConfigInit {
    public static LevelzConfig CONFIG = new LevelzConfig();

    public static void init() {
        AutoConfig.register(LevelzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(LevelzConfig.class).getConfig();
    }

}