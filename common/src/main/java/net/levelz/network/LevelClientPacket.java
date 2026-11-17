package net.levelz.network;

import net.levelz.access.LevelManagerAccess;
import net.levelz.editor.EditorClientData;
import net.levelz.level.LevelManager;
import net.levelz.level.PlayerSkill;
import net.levelz.level.Skill;
import net.levelz.level.restriction.ComponentRestriction;
import net.levelz.network.packet.*;
import net.levelz.registry.EnchantmentRegistry;
import net.levelz.registry.EnchantmentZ;
import net.levelz.screen.LevelScreen;
import net.levelz.screen.RestrictionEditScreen;
import net.levelz.screen.RestrictionEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelClientPacket {

    public static void handleSkillSync(SkillSyncPacket payload) {
        Minecraft client = Minecraft.getInstance();
        List<Integer> skillIds = payload.skillIds();
        List<String> skillKeys = payload.skillKeys();
        List<Integer> skillMaxLevels = payload.skillMaxLevels();
        List<SkillSyncPacket.SkillAttributesRecord> skillAttributes = payload.skillAttributes();
        SkillSyncPacket.SkillBonusesRecord skillBonuses = payload.skillBonuses();

        LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
        boolean integrated = client.hasSingleplayerServer();

        if (!integrated) {
            LevelManager.SKILLS.clear();
        }
        for (int i = 0; i < skillIds.size(); i++) {
            if (!integrated) {
                Skill skill = new Skill(skillIds.get(i), skillKeys.get(i), skillMaxLevels.get(i), skillAttributes.get(i).skillAttributes());
                LevelManager.SKILLS.put(skillIds.get(i), skill);
            }

            if (!levelManager.getPlayerSkills().containsKey(skillIds.get(i))) {
                PlayerSkill playerSkill = new PlayerSkill(skillIds.get(i), 0);
                levelManager.getPlayerSkills().put(skillIds.get(i), playerSkill);
            }
        }
        if (!integrated) {
            LevelManager.BONUSES.clear();
            for (int i = 0; i < skillBonuses.skillBonuses().size(); i++) {
                String bonusKey = skillBonuses.skillBonuses().get(i).getKey();
                LevelManager.BONUSES.put(bonusKey, skillBonuses.skillBonuses().get(i));
            }
        }
    }

    public static void handlePlayerSkillSync(PlayerSkillSyncPacket payload) {
        Minecraft client = Minecraft.getInstance();
        List<Integer> playerSkillIds = payload.playerSkillIds();
        List<Integer> playerSkillLevels = payload.playerSkillLevels();
        LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
        for (int i = 0; i < playerSkillIds.size(); i++) {
            levelManager.setSkillLevel(playerSkillIds.get(i), playerSkillLevels.get(i));
        }
    }

    public static void handleLevel(LevelPacket payload) {
        Minecraft client = Minecraft.getInstance();
        int overallLevel = payload.overallLevel();
        int skillPoints = payload.skillPoints();
        int totalLevelExperience = payload.totalLevelExperience();
        float levelProgress = payload.levelProgress();
        LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
        levelManager.setOverallLevel(overallLevel);
        levelManager.setSkillPoints(skillPoints);
        levelManager.setTotalLevelExperience(totalLevelExperience);
        levelManager.setLevelProgress(levelProgress);
    }

    public static void handleRestriction(RestrictionPacket payload) {
        Minecraft client = Minecraft.getInstance();
        RestrictionPacket.RestrictionRecord blockRestrictions = payload.blockRestrictions();
        RestrictionPacket.RestrictionRecord craftingRestrictions = payload.craftingRestrictions();
        RestrictionPacket.RestrictionRecord entityRestrictions = payload.entityRestrictions();
        RestrictionPacket.RestrictionRecord itemRestrictions = payload.itemRestrictions();
        RestrictionPacket.RestrictionRecord miningRestrictions = payload.miningRestrictions();
        RestrictionPacket.RestrictionRecord enchantmentRestrictions = payload.enchantmentRestrictions();
        List<ComponentRestriction> componentRestrictions = payload.componentRestrictions();

        if (client.hasSingleplayerServer()) {
            return;
        }
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
        LevelManager.COMPONENT_RESTRICTIONS.clear();
        for (ComponentRestriction componentRestriction : componentRestrictions) {
            LevelManager.COMPONENT_RESTRICTIONS.computeIfAbsent(componentRestriction.getId(), id -> new ArrayList<>()).add(componentRestriction);
        }
    }

    public static void handleStat(StatPacket payload) {
        Minecraft client = Minecraft.getInstance();
        int id = payload.id();
        int level = payload.level();
        LevelManager levelManager = ((LevelManagerAccess) client.player).getLevelManager();
        levelManager.setSkillLevel(id, level);
        if (client.gui.screen() instanceof LevelScreen levelScreen) {
            levelScreen.updateLevelButtons();
        }
    }

    public static void handleEditorData(EditorDataPacket payload) {
        Minecraft client = Minecraft.getInstance();
        EditorClientData.update(payload.entries(), payload.skills(), payload.problems());
        if (payload.openScreen()) {
            client.gui.setScreen(new RestrictionEditorScreen());
        } else if (client.gui.screen() instanceof RestrictionEditorScreen restrictionEditorScreen) {
            restrictionEditorScreen.refresh();
        } else if (client.gui.screen() instanceof RestrictionEditScreen restrictionEditScreen) {
            restrictionEditScreen.onDataRefreshed();
        }
    }

    public static void handleEditorResult(EditorResultPacket payload) {
        Minecraft client = Minecraft.getInstance();
        EditorClientData.lastResult = payload;
        if (client.gui.screen() instanceof RestrictionEditScreen restrictionEditScreen) {
            restrictionEditScreen.onResult(payload);
        } else if (client.gui.screen() instanceof RestrictionEditorScreen restrictionEditorScreen) {
            restrictionEditorScreen.onResult(payload);
        }
    }

    public static void handleEnchantmentZ(EnchantmentZPacket payload) {
        Minecraft client = Minecraft.getInstance();
        Map<String, Integer> indexed = payload.indexed();
        List<Integer> keys = payload.keys();
        List<String> ids = payload.ids();
        List<Integer> levels = payload.levels();
        if (client.hasSingleplayerServer()) {
            return;
        }
        EnchantmentRegistry.clear();

        Registry<Enchantment> registry = client.player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        for (int i = 0; i < keys.size(); i++) {
            int key = keys.get(i);
            Holder<Enchantment> entry = registry.get(Identifier.parse(ids.get(i))).get();
            int level = levels.get(i);
            EnchantmentRegistry.ENCHANTMENTS.put(key, new EnchantmentZ(entry, level));
        }
        EnchantmentRegistry.INDEX_ENCHANTMENTS.putAll(indexed);
    }
}
