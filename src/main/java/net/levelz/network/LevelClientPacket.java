package net.levelz.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.PlayerSkill;
import net.levelz.level.Skill;
import net.levelz.network.packet.*;
import net.levelz.registry.EnchantmentRegistry;
import net.levelz.registry.EnchantmentZ;
import net.levelz.screen.LevelScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class LevelClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SkillSyncPacket.PACKET_ID, (payload, context) -> {
            List<Integer> skillIds = payload.skillIds();
            List<String> skillKeys = payload.skillKeys();
            List<Integer> skillMaxLevels = payload.skillMaxLevels();
            List<SkillSyncPacket.SkillAttributesRecord> skillAttributes = payload.skillAttributes();
            SkillSyncPacket.SkillBonusesRecord skillBonuses = payload.skillBonuses();

            context.client().execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) context.player()).getLevelManager();

                LevelManager.SKILLS.clear();
                for (int i = 0; i < skillIds.size(); i++) {
                    Skill skill = new Skill(skillIds.get(i), skillKeys.get(i), skillMaxLevels.get(i), skillAttributes.get(i).skillAttributes());
                    LevelManager.SKILLS.put(skillIds.get(i), skill);

                    if (!levelManager.getPlayerSkills().containsKey(skillIds.get(i))) {
                        PlayerSkill playerSkill = new PlayerSkill(skillIds.get(i), 0);
                        levelManager.getPlayerSkills().put(skillIds.get(i), playerSkill);
                    }
                }
                LevelManager.BONUSES.clear();
                for (int i = 0; i < skillBonuses.skillBonuses().size(); i++) {
                    String bonusKey = skillBonuses.skillBonuses().get(i).getKey();
                    LevelManager.BONUSES.put(bonusKey, skillBonuses.skillBonuses().get(i));
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerSkillSyncPacket.PACKET_ID, (payload, context) -> {
            List<Integer> playerSkillIds = payload.playerSkillIds();
            List<Integer> playerSkillLevels = payload.playerSkillLevels();
            context.client().execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) context.player()).getLevelManager();
                for (int i = 0; i < playerSkillIds.size(); i++) {
                    levelManager.setSkillLevel(playerSkillIds.get(i), playerSkillLevels.get(i));
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(LevelPacket.PACKET_ID, (payload, context) -> {
            int overallLevel = payload.overallLevel();
            int skillPoints = payload.skillPoints();
            int totalLevelExperience = payload.totalLevelExperience();
            float levelProgress = payload.levelProgress();
            context.client().execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) context.player()).getLevelManager();
                levelManager.setOverallLevel(overallLevel);
                levelManager.setSkillPoints(skillPoints);
                levelManager.setTotalLevelExperience(totalLevelExperience);
                levelManager.setLevelProgress(levelProgress);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RestrictionPacket.PACKET_ID, (payload, context) -> {
            RestrictionPacket.RestrictionRecord blockRestrictions = payload.blockRestrictions();
            RestrictionPacket.RestrictionRecord craftingRestrictions = payload.craftingRestrictions();
            RestrictionPacket.RestrictionRecord entityRestrictions = payload.entityRestrictions();
            RestrictionPacket.RestrictionRecord itemRestrictions = payload.itemRestrictions();
            RestrictionPacket.RestrictionRecord miningRestrictions = payload.miningRestrictions();
            RestrictionPacket.RestrictionRecord enchantmentRestrictions = payload.enchantmentRestrictions();

            context.client().execute(() -> {
                LevelManager.BLOCK_RESTRICTIONS.clear();
                LevelManager.CRAFTING_RESTRICTIONS.clear();
                LevelManager.ENTITY_RESTRICTIONS.clear();
                LevelManager.ITEM_RESTRICTIONS.clear();
                LevelManager.MINING_RESTRICTIONS.clear();
                LevelManager.ENCHANTMENT_RESTRICTIONS.clear();

                for (int i = 0; i < blockRestrictions.ids().size(); i++) {
                    LevelManager.BLOCK_RESTRICTIONS.put(blockRestrictions.ids().get(i), blockRestrictions.restrictions().get(i));
                }
                for (int i = 0; i < craftingRestrictions.ids().size(); i++) {
                    LevelManager.CRAFTING_RESTRICTIONS.put(craftingRestrictions.ids().get(i), craftingRestrictions.restrictions().get(i));
                }
                for (int i = 0; i < entityRestrictions.ids().size(); i++) {
                    LevelManager.ENTITY_RESTRICTIONS.put(entityRestrictions.ids().get(i), entityRestrictions.restrictions().get(i));
                }
                for (int i = 0; i < itemRestrictions.ids().size(); i++) {
                    LevelManager.ITEM_RESTRICTIONS.put(itemRestrictions.ids().get(i), itemRestrictions.restrictions().get(i));
                }
                for (int i = 0; i < miningRestrictions.ids().size(); i++) {
                    LevelManager.MINING_RESTRICTIONS.put(miningRestrictions.ids().get(i), miningRestrictions.restrictions().get(i));
                }
                for (int i = 0; i < enchantmentRestrictions.ids().size(); i++) {
                    LevelManager.ENCHANTMENT_RESTRICTIONS.put(enchantmentRestrictions.ids().get(i), enchantmentRestrictions.restrictions().get(i));
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(StatPacket.PACKET_ID, (payload, context) -> {
            int id = payload.id();
            int level = payload.level();
            context.client().execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) context.player()).getLevelManager();
                levelManager.setSkillLevel(id, level);
                if (context.client().currentScreen instanceof LevelScreen levelScreen) {
                    levelScreen.updateLevelButtons();
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(EnchantmentZPacket.PACKET_ID, (payload, context) -> {
            Map<String, Integer> indexed = payload.indexed();
            List<Integer> keys = payload.keys();
            List<String> ids = payload.ids();
            List<Integer> levels = payload.levels();
            context.client().execute(() -> {
                EnchantmentRegistry.ENCHANTMENTS.clear();
                EnchantmentRegistry.INDEX_ENCHANTMENTS.clear();

                Registry<Enchantment> registry = context.player().getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT);
                for (int i = 0; i < keys.size(); i++) {
                    int key = keys.get(i);
                    RegistryEntry<Enchantment> entry = registry.getEntry(Identifier.of(ids.get(i))).get();
                    int level = levels.get(i);
                    EnchantmentRegistry.ENCHANTMENTS.put(key, new EnchantmentZ(entry, level));
                }
                EnchantmentRegistry.INDEX_ENCHANTMENTS.putAll(indexed);
            });
        });
    }
}
