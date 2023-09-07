package net.levelz.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.access.PlayerSyncAccess;
import net.levelz.data.LevelLists;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.levelz.init.ConfigInit;
import net.levelz.init.CriteriaInit;
import net.levelz.stats.PlayerStatsManager;
import net.levelz.stats.Skill;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class PlayerStatsServerPacket {

    public static final Identifier STATS_INCREASE_PACKET = new Identifier("levelz", "player_increase_stats");
    public static final Identifier STATS_SYNC_PACKET = new Identifier("levelz", "player_sync_stats");
    public static final Identifier XP_PACKET = new Identifier("levelz", "player_level_xp");
    public static final Identifier LEVEL_PACKET = new Identifier("levelz", "player_level_stats");
    public static final Identifier LIST_PACKET = new Identifier("levelz", "unlocking_list");
    public static final Identifier STRENGTH_PACKET = new Identifier("levelz", "strength_sync");
    public static final Identifier RESET_PACKET = new Identifier("levelz", "reset_skill");
    public static final Identifier LEVEL_EXPERIENCE_ORB_PACKET = new Identifier("levelz", "level_experience_orb");
    public static final Identifier SEND_CONFIG_SYNC_PACKET = new Identifier("levelz", "send_config_sync_packet");
    public static final Identifier CONFIG_SYNC_PACKET = new Identifier("levelz", "config_sync_packet");
    public static final Identifier TAG_PACKET = new Identifier("levelz", "tag_packet");
    public static final Identifier SEND_TAG_PACKET = new Identifier("levelz", "send_tag_packet");
    public static final Identifier LEVEL_UP_BUTTON_PACKET = new Identifier("levelz", "level_up_button");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(STATS_INCREASE_PACKET, (server, player, handler, buffer, sender) -> {
            String skillString = buffer.readString().toUpperCase();
            int level = buffer.readInt();
            server.execute(() -> {
                PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) player).getPlayerStatsManager();
                if (playerStatsManager.getSkillPoints() - level >= 0) {
                    Skill skill = Skill.valueOf(skillString);
                    if ((!ConfigInit.CONFIG.allowHigherSkillLevel && playerStatsManager.getSkillLevel(skill) >= ConfigInit.CONFIG.maxLevel) || skill == null) {
                        return;
                    }

                    for (int i = 1; i <= level; i++) {
                        CriteriaInit.SKILL_UP.trigger(player, skillString.toLowerCase(), playerStatsManager.getSkillLevel(skill) + level);
                    }
                    playerStatsManager.setSkillLevel(skill, playerStatsManager.getSkillLevel(skill) + level);
                    playerStatsManager.setSkillPoints(playerStatsManager.getSkillPoints() - level);
                    if (skill.equals(Skill.HEALTH)) {
                        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                            .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH) + ConfigInit.CONFIG.healthBonus * level);
                        player.setHealth(player.getHealth() + (float) ConfigInit.CONFIG.healthBonus * level);
                    } else if (skill.equals(Skill.STRENGTH)) {
                        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                            .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + ConfigInit.CONFIG.attackBonus * level);
                    } else if (skill.equals(Skill.AGILITY)) {
                        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                            .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) + ConfigInit.CONFIG.movementBonus * level);
                    } else if (skill.equals(Skill.DEFENSE)) {
                        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
                            .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_ARMOR) + ConfigInit.CONFIG.defenseBonus * level);
                    } else if (skill.equals(Skill.LUCK)) {
                        player.getAttributeInstance(EntityAttributes.GENERIC_LUCK)
                            .setBaseValue(player.getAttributeBaseValue(EntityAttributes.GENERIC_LUCK) + ConfigInit.CONFIG.luckBonus * level);
                    }
                    syncLockedLists(playerStatsManager);

                    writeS2CSyncLevelPacket(playerStatsManager, player, skill);
                }

            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SEND_CONFIG_SYNC_PACKET, (server, player, handler, buffer, sender) -> {
            writeS2CConfigSyncPacket(player, ConfigInit.CONFIG.getConfigList());
        });
        ServerPlayNetworking.registerGlobalReceiver(SEND_TAG_PACKET, (server, player, handler, buffer, sender) -> {
            writeS2CTagPacket(player, buffer.readIdentifier());
        });
        ServerPlayNetworking.registerGlobalReceiver(LEVEL_UP_BUTTON_PACKET, (server, player, handler, buffer, sender) -> {
            int levelUp = buffer.readInt();
            server.execute(() -> {
                ((PlayerSyncAccess) player).levelUp(levelUp, true, false);
            });
        });
    }

    public static void writeS2CSyncLevelPacket(PlayerStatsManager playerStatsManager, ServerPlayerEntity serverPlayerEntity, Skill skill) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(skill.getName());
        buf.writeInt(playerStatsManager.getSkillLevel(skill));
        buf.writeInt(playerStatsManager.getSkillPoints());
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(STATS_SYNC_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CXPPacket(PlayerStatsManager playerStatsManager, ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeFloat(playerStatsManager.getLevelProgress());
        buf.writeInt(playerStatsManager.getTotalLevelExperience());
        buf.writeInt(playerStatsManager.getOverallLevel());
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(XP_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CSkillPacket(PlayerStatsManager playerStatsManager, ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeFloat(playerStatsManager.getLevelProgress());
        buf.writeInt(playerStatsManager.getTotalLevelExperience());
        buf.writeInt(playerStatsManager.getOverallLevel());
        buf.writeInt(playerStatsManager.getSkillPoints());
        for (Skill skill : Skill.values()) {
            buf.writeInt(playerStatsManager.getSkillLevel(skill));
        }

        // Set on server
        syncLockedLists(playerStatsManager);

        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(LEVEL_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CStrengthPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeDouble(serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getBaseValue());
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(STRENGTH_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void syncLockedLists(PlayerStatsManager playerStatsManager){
        playerStatsManager.lockedIds.clear();
        for (var entry:LevelLists.levelLists.entrySet()){
            String skillName = entry.getKey();
            for(int i = 0; i < entry.getValue().size(); i++){
                if(entry.getValue().get(i) > playerStatsManager.getSkillLevel(Skill.valueOf(skillName))){
                    var lockedIds = playerStatsManager.lockedIds.getOrDefault(skillName, new ArrayList<>());
                    for (Integer integer : LevelLists.levelObjectsLists.get(skillName).get(i)) {
                        if (!lockedIds.contains(integer)){
                            lockedIds.add(integer);
                        }
                    }
                    playerStatsManager.lockedIds.put(skillName, lockedIds);
                }
            }
        }
    }

    public static void writeS2CConfigSyncPacket(ServerPlayerEntity serverPlayerEntity, List<Object> list) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Integer)
                buf.writeInt((int) list.get(i));
            else if (list.get(i) instanceof Float)
                buf.writeFloat((float) list.get(i));
            else if (list.get(i) instanceof Double)
                buf.writeDouble((double) list.get(i));
            else if (list.get(i) instanceof Boolean)
                buf.writeBoolean((boolean) list.get(i));
        }
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(CONFIG_SYNC_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CTagPacket(ServerPlayerEntity serverPlayerEntity, Identifier identifier) {
        // PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        // System.out.println(Blocks.ACACIA_LOG.getRegistryEntry().isIn(BlockTags.ACACIA_LOGS));
        // System.out.println(Registry.BLOCK.getOrCreateEntryList(BlockTags.ACACIA_LOGS));
        // System.out.println(Registry.BLOCK.getOrCreateEntryList(TagKey.of(Registry.BLOCK_KEY, identifier)));
    }

    public static void writeS2CListPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        for (int i = 0; i < LevelLists.getListNames().size(); i++) {
            String listName = LevelLists.getListNames().get(i);
            ArrayList<Object> list = LevelLists.getList(listName);
            for (int u = 0; u < list.size(); u++) {
                buf.writeString(list.get(u).toString());
            }
        }

        for(var skillEntry: LevelLists.levelLists.entrySet()){
            String skillName = skillEntry.getKey();
            for(int k = 0; k < skillEntry.getValue().size(); k++){
                buf.writeString(skillName+":level");
                buf.writeString(skillEntry.getValue().get(k).toString());
                if(LevelLists.levelHasExtraData.getOrDefault(skillName, false)){
                    buf.writeString(LevelLists.levelExtraDataLists.get(skillName).get(k).toString());
                }
                var objectList = LevelLists.levelObjectsLists.get(skillName).get(k);
                for (Integer integer : objectList) {
                    buf.writeString(integer.toString());
                }
            }
        }
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(LIST_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CResetSkillPacket(ServerPlayerEntity serverPlayerEntity, @NotNull Skill skill) {
        // Sync attributes on server
        PlayerStatsManager playerStatsManager = ((PlayerStatsManagerAccess) serverPlayerEntity).getPlayerStatsManager();
        int skillLevel = playerStatsManager.getSkillLevel(skill);
        if (skill.equals(Skill.HEALTH)) {
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(ConfigInit.CONFIG.healthBase + skillLevel * ConfigInit.CONFIG.healthBonus);
            serverPlayerEntity.setHealth(serverPlayerEntity.getMaxHealth());
        } else if (skill.equals(Skill.STRENGTH)) {
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(ConfigInit.CONFIG.attackBase + skillLevel * ConfigInit.CONFIG.attackBonus);
        } else if (skill.equals(Skill.AGILITY)) {
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(ConfigInit.CONFIG.movementBase + skillLevel * ConfigInit.CONFIG.movementBonus);
        } else if (skill.equals(Skill.DEFENSE)) {
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(ConfigInit.CONFIG.defenseBase + skillLevel * ConfigInit.CONFIG.defenseBonus);
        } else if (skill.equals(Skill.LUCK)) {
            serverPlayerEntity.getAttributeInstance(EntityAttributes.GENERIC_LUCK).setBaseValue(ConfigInit.CONFIG.luckBase + skillLevel * ConfigInit.CONFIG.luckBonus);
        }
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(skill.getName());
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(RESET_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public Packet<ClientPlayPacketListener> createS2CLevelExperienceOrbPacket(LevelExperienceOrbEntity levelExperienceOrbEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(levelExperienceOrbEntity.getId());
        buf.writeDouble(levelExperienceOrbEntity.getX());
        buf.writeDouble(levelExperienceOrbEntity.getY());
        buf.writeDouble(levelExperienceOrbEntity.getZ());
        buf.writeShort(levelExperienceOrbEntity.getExperienceAmount());
        return ServerPlayNetworking.createS2CPacket(LEVEL_EXPERIENCE_ORB_PACKET, buf);
    }

}
