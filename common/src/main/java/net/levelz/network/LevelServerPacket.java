package net.levelz.network;

import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.level.LevelManager;
import net.levelz.level.PlayerSkill;
import net.levelz.level.Skill;
import net.levelz.network.packet.*;
import net.levelz.platform.Services;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.List;

public class LevelServerPacket {

    public static final PacketType<OrbPacket> ADD_LEVEL_EXPERIENCE_ORB = s2c("add_level_experience_orb");

    public static void handleStat(ServerPlayer player, StatPacket payload) {
        int id = payload.id();
        int level = payload.level();

        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (level > 0 && levelManager.getSkillPoints() - level >= 0) {

            Skill skill = LevelManager.SKILLS.get(id);
            PlayerSkill playerSkill = levelManager.getPlayerSkills().get(id);

            if (skill == null || playerSkill == null) {
                return;
            }
            if (!ConfigInit.CONFIG.allowHigherSkillLevel && playerSkill.getLevel() + level > skill.getMaxLevel()) {
                return;
            }
            if (ConfigInit.CONFIG.allowHigherSkillLevel) {
                if (playerSkill.getLevel() >= skill.getMaxLevel()) {
                    for (Skill skillCheck : LevelManager.SKILLS.values()) {
                        if (skillCheck.getMaxLevel() > levelManager.getSkillLevel(skillCheck.getId())) {
                            return;
                        }
                    }
                }
            }

            for (int i = 1; i <= level; i++) {
                CriteriaInit.SKILL_UP.trigger(player, skill.getKey(), playerSkill.getLevel() + i);
            }

            levelManager.setSkillLevel(id, playerSkill.getLevel() + level);
            levelManager.setSkillPoints(levelManager.getSkillPoints() - level);
            LevelHelper.updateSkill(player, skill);
            PacketHelper.updateLevels(player);

            Services.network().sendToPlayer(player, new StatPacket(id, levelManager.getSkillLevel(id)));
        }
    }

    public static void handleAttributeSync(ServerPlayer player) {
        player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), List.of(player.getAttribute(Attributes.ATTACK_DAMAGE))));
    }

    private static <T extends Packet<ClientGamePacketListener>> PacketType<T> s2c(String id) {
        return new PacketType<>(PacketFlow.CLIENTBOUND, Identifier.withDefaultNamespace(id));
    }
}
