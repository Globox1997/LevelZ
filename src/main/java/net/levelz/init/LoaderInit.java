package net.levelz.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.levelz.data.RestrictionLoader;
import net.levelz.access.LevelManagerAccess;
import net.levelz.data.SkillLoader;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoaderInit {

    public static final Logger LOGGER = LogManager.getLogger("LevelZ");

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SkillLoader());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(RestrictionLoader.ID, RestrictionLoader::new);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (success) {
                for (int i = 0; i < server.getPlayerManager().getPlayerList().size(); i++) {
                    ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayerList().get(i);
                    ((LevelManagerAccess) serverPlayerEntity).getLevelManager().refreshSkills();
                    for (Skill skill : LevelManager.SKILLS.values()) {
                        LevelHelper.updateSkill(serverPlayerEntity, skill);
                    }
                    PacketHelper.syncEnchantments(serverPlayerEntity);
                    PacketHelper.updateSkills(serverPlayerEntity);
                    PacketHelper.updatePlayerSkills(serverPlayerEntity, null);
                    PacketHelper.updateRestrictions(serverPlayerEntity);
                    PacketHelper.updateLevels(serverPlayerEntity);
                }
                LOGGER.info("Finished reload on {}", Thread.currentThread());
            } else {
                LOGGER.error("Failed to reload on {}", Thread.currentThread());
            }
        });
    }

}
