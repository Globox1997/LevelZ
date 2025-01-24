package net.levelz.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.*;
import net.levelz.network.packet.*;
import net.levelz.registry.EnchantmentRegistry;
import net.levelz.registry.EnchantmentZ;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            LevelManager oldLevelManager = ((LevelManagerAccess) oldPlayerEntity).getLevelManager();
            levelManager.setPlayerSkills(oldLevelManager.getPlayerSkills());
            levelManager.setOverallLevel(oldLevelManager.getOverallLevel());
            levelManager.setTotalLevelExperience(oldLevelManager.getTotalLevelExperience());
            levelManager.setSkillPoints(oldLevelManager.getSkillPoints());
            levelManager.setLevelProgress(oldLevelManager.getLevelProgress());
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
                new RestrictionPacket.RestrictionRecord(LevelManager.ITEM_RESTRICTIONS.keySet().stream().toList(), LevelManager.ITEM_RESTRICTIONS.values().stream().toList()), new RestrictionPacket.RestrictionRecord(LevelManager.MINING_RESTRICTIONS.keySet().stream().toList(), LevelManager.MINING_RESTRICTIONS.values().stream().toList()),
                new RestrictionPacket.RestrictionRecord(LevelManager.ENCHANTMENT_RESTRICTIONS.keySet().stream().toList(), LevelManager.ENCHANTMENT_RESTRICTIONS.values().stream().toList())));
    }

    public static void syncEnchantments(ServerPlayerEntity serverPlayerEntity) {
        List<Integer> keys = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<Integer> levels = new ArrayList<>();
        for (Map.Entry<Integer, EnchantmentZ> entry : EnchantmentRegistry.ENCHANTMENTS.entrySet()) {
            keys.add(entry.getKey());
            ids.add(entry.getValue().getEntry().getIdAsString());
            levels.add(entry.getValue().getLevel());
        }
        ServerPlayNetworking.send(serverPlayerEntity, new EnchantmentZPacket(EnchantmentRegistry.INDEX_ENCHANTMENTS, keys, ids, levels));
    }
}
