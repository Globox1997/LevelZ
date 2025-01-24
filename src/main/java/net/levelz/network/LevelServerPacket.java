package net.levelz.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.access.LevelManagerAccess;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.level.LevelManager;
import net.levelz.level.PlayerSkill;
import net.levelz.level.Skill;
import net.levelz.network.packet.*;
import net.levelz.util.LevelHelper;
import net.levelz.util.PacketHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.util.Identifier;

import java.util.List;

public class LevelServerPacket {

    public static final PacketType<OrbPacket> ADD_LEVEL_EXPERIENCE_ORB = s2c("add_level_experience_orb");

    public static void init() {
        PayloadTypeRegistry.playS2C().register(SkillSyncPacket.PACKET_ID, SkillSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerSkillSyncPacket.PACKET_ID, PlayerSkillSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(LevelPacket.PACKET_ID, LevelPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RestrictionPacket.PACKET_ID, RestrictionPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(EnchantmentZPacket.PACKET_ID, EnchantmentZPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(StatPacket.PACKET_ID, StatPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(StatPacket.PACKET_ID, StatPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(AttributeSyncPacket.PACKET_ID, AttributeSyncPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StatPacket.PACKET_ID, (payload, context) -> {
            int id = payload.id();
            int level = payload.level();

            context.server().execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) context.player()).getLevelManager();
                if (levelManager.getSkillPoints() - level >= 0) {

                    Skill skill = LevelManager.SKILLS.get(id);
                    PlayerSkill playerSkill = levelManager.getPlayerSkills().get(id);

                    if (ConfigInit.CONFIG.overallMaxLevel > 0 && ConfigInit.CONFIG.overallMaxLevel <= levelManager.getOverallLevel()) {
                        return;
                    }
                    if (!ConfigInit.CONFIG.allowHigherSkillLevel && playerSkill.getLevel() >= skill.getMaxLevel()) {
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
                        CriteriaInit.SKILL_UP.trigger(context.player(), skill.getKey(), playerSkill.getLevel() + level);
                    }

                    levelManager.setSkillLevel(id, playerSkill.getLevel() + level);
                    levelManager.setSkillPoints(levelManager.getSkillPoints() - level);
                    LevelHelper.updateSkill(context.player(), skill);
                    PacketHelper.updateLevels(context.player());

                    ServerPlayNetworking.send(context.player(), new StatPacket(id, levelManager.getSkillLevel(id)));
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(AttributeSyncPacket.PACKET_ID, (payload, context) -> {
            context.server().execute(() -> {
                // Following are already synced
                // Collection<EntityAttributeInstance> collection = context.player().getAttributes().getAttributesToSend();
                // context.player().networkHandler.sendPacket(new EntityAttributesS2CPacket(context.player().getId(), collection));
                // Is required lul
                context.player().networkHandler.sendPacket(new EntityAttributesS2CPacket(context.player().getId(), List.of(context.player().getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))));
            });
        });
    }

    private static <T extends Packet<ClientPlayPacketListener>> PacketType<T> s2c(String id) {
        return new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
    }
}
