package net.levelz.init;

import net.levelz.data.DatapackErrors;
import net.levelz.data.RestrictionLoader;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoaderInit {

    public static final Logger LOGGER = LogManager.getLogger("LevelZ");

    public static void onTagsLoaded(RegistryAccess registries) {
        RestrictionLoader.onTagsLoaded(registries);
    }

    public static void onServerStarting(MinecraftServer server) {
        RestrictionLoader.applyOverrides(server);
    }

    public static void onDataPackReloaded(MinecraftServer server, boolean success) {
        if (success) {
            RestrictionLoader.applyOverrides(server);
            for (int i = 0; i < server.getPlayerList().getPlayers().size(); i++) {
                ServerPlayer serverPlayerEntity = server.getPlayerList().getPlayers().get(i);
                ((LevelManagerAccess) serverPlayerEntity).getLevelManager().refreshSkills();
                for (Skill skill : LevelManager.SKILLS.values()) {
                    LevelHelper.updateSkill(serverPlayerEntity, skill);
                }
                PacketHelper.syncEnchantments(serverPlayerEntity);
                PacketHelper.updateSkills(serverPlayerEntity);
                PacketHelper.updatePlayerSkills(serverPlayerEntity, null);
                PacketHelper.updateRestrictions(serverPlayerEntity);
                PacketHelper.updateLevels(serverPlayerEntity);
                DatapackErrors.notify(serverPlayerEntity);
            }
            LOGGER.info("Finished reload on {}", Thread.currentThread());
        } else {
            LOGGER.error("Failed to reload on {}", Thread.currentThread());
        }
    }

}
