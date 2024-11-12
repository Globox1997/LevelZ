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
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.util.Identifier;

public class LevelServerPacket {

    public static final PacketType<OrbPacket> ADD_LEVEL_EXPERIENCE_ORB = s2c("add_level_experience_orb");

    public static void init() {
        PayloadTypeRegistry.playS2C().register(SkillSyncPacket.PACKET_ID, SkillSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerSkillSyncPacket.PACKET_ID, PlayerSkillSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(LevelPacket.PACKET_ID, LevelPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RestrictionPacket.PACKET_ID, RestrictionPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(StatPacket.PACKET_ID, StatPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(StatPacket.PACKET_ID, StatPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StatPacket.PACKET_ID, (payload, context) -> {
            int id = payload.id();
            int level = payload.level();

            context.server().execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) context.player()).getLevelManager();
                if (levelManager.getSkillPoints() - level >= 0) {

                    Skill skill = LevelManager.SKILLS.get(id);
                    PlayerSkill playerSkill = levelManager.getPlayerSkills().get(id);

                    if (ConfigInit.CONFIG.maxLevel <= levelManager.getOverallLevel()) {
                        return;
                    }

                    if (!ConfigInit.CONFIG.allowHigherSkillLevel && playerSkill.getLevel() >= skill.getMaxLevel()) {
                        return;
                    }

                    for (int i = 1; i <= level; i++) {
                        CriteriaInit.SKILL_UP.trigger(context.player(), skill.getKey(), playerSkill.getLevel() + level);
                    }

                    levelManager.setSkillLevel(id, playerSkill.getLevel() + level);
                    levelManager.setSkillPoints(levelManager.getSkillPoints() - level);
                    LevelHelper.updateSkill(context.player(), skill);
                    PacketHelper.updateLevels(context.player());

                    ServerPlayNetworking.send(context.player(), new StatPacket(id, levelManager.getSkillLevel(id)));
//                    switch (skillOld) {
//                        case HEALTH -> {
//                            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
//                                    .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + ConfigInit.CONFIG.healthBonus * level);
//                            player.setHealth(player.getHealth() + (float) ConfigInit.CONFIG.healthBonus * level);
//                        }
//                        case STRENGTH -> player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
//                                .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + ConfigInit.CONFIG.attackBonus * level);
//                        case AGILITY -> player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
//                                .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + ConfigInit.CONFIG.movementBonus * level);
//                        case DEFENSE -> player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
//                                .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + ConfigInit.CONFIG.defenseBonus * level);
//                        case LUCK -> player.getAttributeInstance(EntityAttributes.GENERIC_LUCK)
//                                .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_LUCK) + ConfigInit.CONFIG.luckBonus * level);
//                        case MINING -> syncLockedBlockList(levelManager);
//                        case ALCHEMY -> syncLockedBrewingItemList(levelManager);
//                        case SMITHING -> syncLockedSmithingItemList(levelManager);
//                        default -> {
//                        }
//                    }
//                    syncLockedCraftingItemList(levelManager);

//                    writeS2CSyncLevelPacket(levelManager, player, skillOld);
                }
            });
        });
    }

    private static <T extends Packet<ClientPlayPacketListener>> PacketType<T> s2c(String id) {
        return new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
    }
}
