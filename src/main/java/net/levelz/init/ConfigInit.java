package net.levelz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.config.LevelzConfig;
import net.levelz.network.PlayerStatsClientPacket;
import net.levelz.network.PlayerStatsServerPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class ConfigInit {
    public static LevelzConfig CONFIG = new LevelzConfig();

    public static void init() {
        AutoConfig.register(LevelzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(LevelzConfig.class).getConfig();

        AutoConfig.getConfigHolder(LevelzConfig.class).registerSaveListener((manager, data) -> {
            if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT) && !MinecraftClient.getInstance().isInSingleplayer()
                    && MinecraftClient.getInstance().getNetworkHandler() != null)
                PlayerStatsClientPacket.writeC2SSyncConfigPacket();
            return ActionResult.SUCCESS;
        });
        AutoConfig.getConfigHolder(LevelzConfig.class).registerLoadListener((manager, newData) -> {
            if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT) && !MinecraftClient.getInstance().isInSingleplayer()
                    && MinecraftClient.getInstance().getNetworkHandler() != null)
                PlayerStatsClientPacket.writeC2SSyncConfigPacket();
            return ActionResult.SUCCESS;
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // if (!server.isSingleplayer()) // set in sp too
            server.execute(() -> {
                PlayerStatsServerPacket.writeS2CConfigSyncPacket(handler.player, ConfigInit.CONFIG.getConfigList());
            });

        });

    }

}