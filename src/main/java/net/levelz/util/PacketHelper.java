package net.levelz.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.*;
import net.levelz.network.packet.LevelPacket;
import net.levelz.network.packet.PlayerSkillSyncPacket;
import net.levelz.network.packet.RestrictionPacket;
import net.levelz.network.packet.SkillSyncPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PacketHelper {

    public static void updateLevels(ServerPlayerEntity serverPlayerEntity) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
        int overallLevel = levelManager.getOverallLevel();
        int skillPoints = levelManager.getSkillPoints();
        int totalLevelExperience = levelManager.getTotalLevelExperience();
        float levelProgress = levelManager.getLevelProgress();

        ServerPlayNetworking.send(serverPlayerEntity, new LevelPacket(overallLevel, skillPoints, totalLevelExperience, levelProgress));
    }

    public static void updateSkills(ServerPlayerEntity serverPlayerEntity) {
        List<Integer> skillIds = new ArrayList<>();
        List<String> skillKeys = new ArrayList<>();
        List<Integer> skillMaxLevels = new ArrayList<>();
        List<SkillSyncPacket.SkillAttributesRecord> skillAttributes = new ArrayList<>();
        List<SkillBonus> skillBonuses = new ArrayList<>(LevelManager.BONUSES.values());

        for (Skill skill : LevelManager.SKILLS.values()) {
            skillIds.add(skill.getId());
            skillKeys.add(skill.getKey());
            skillMaxLevels.add(skill.getMaxLevel());

            List<SkillAttribute> skillAttributeList = new ArrayList<>(skill.getAttributes());
            skillAttributes.add(new SkillSyncPacket.SkillAttributesRecord(skillAttributeList));
        }

        SkillSyncPacket.SkillBonusesRecord skillBonusesRecord = new SkillSyncPacket.SkillBonusesRecord(skillBonuses);
        ServerPlayNetworking.send(serverPlayerEntity, new SkillSyncPacket(skillIds, skillKeys, skillMaxLevels, skillAttributes, skillBonusesRecord));
    }

    public static void updatePlayerSkills(ServerPlayerEntity serverPlayerEntity, @Nullable ServerPlayerEntity oldPlayerEntity) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
        if (oldPlayerEntity != null) {
            levelManager = ((LevelManagerAccess) oldPlayerEntity).getLevelManager();
        }
        List<Integer> playerSkillIds = new ArrayList<>();
        List<Integer> playerSkillLevels = new ArrayList<>();
        for (PlayerSkill playerSkill : levelManager.getPlayerSkills().values()) {
            playerSkillIds.add(playerSkill.getId());
            playerSkillLevels.add(playerSkill.getLevel());
        }

        ServerPlayNetworking.send(serverPlayerEntity, new PlayerSkillSyncPacket(playerSkillIds, playerSkillLevels));
    }

    public static void updateRestrictions(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, new RestrictionPacket(new RestrictionPacket.RestrictionRecord(LevelManager.BLOCK_RESTRICTIONS.keySet().stream().toList(), LevelManager.BLOCK_RESTRICTIONS.values().stream().toList()),
                new RestrictionPacket.RestrictionRecord(LevelManager.CRAFTING_RESTRICTIONS.keySet().stream().toList(), LevelManager.CRAFTING_RESTRICTIONS.values().stream().toList()), new RestrictionPacket.RestrictionRecord(LevelManager.ENTITY_RESTRICTIONS.keySet().stream().toList(), LevelManager.ENTITY_RESTRICTIONS.values().stream().toList()),
                new RestrictionPacket.RestrictionRecord(LevelManager.ITEM_RESTRICTIONS.keySet().stream().toList(), LevelManager.ITEM_RESTRICTIONS.values().stream().toList()), new RestrictionPacket.RestrictionRecord(LevelManager.MINING_RESTRICTIONS.keySet().stream().toList(), LevelManager.MINING_RESTRICTIONS.values().stream().toList())));
    }
}
