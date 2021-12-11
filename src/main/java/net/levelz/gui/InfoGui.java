package net.levelz.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import net.fabricmc.fabric.api.util.TriState;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class InfoGui extends LightweightGuiDescription {

    public InfoGui(String name, MinecraftClient client) {
        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 215);

        root.add(new WLabel(new TranslatableText("text.levelz.info", StringUtils.capitalize(name))), 6, 7);

        WPlainPanel plainPanel = new WPlainPanel();

        WScrollPanel scrollPanel = new WScrollPanel(plainPanel);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);

        int gridYSpace = 4;

        TranslatableText translatableText1 = new TranslatableText("");
        TranslatableText translatableText1B = new TranslatableText("");
        TranslatableText translatableText2A = new TranslatableText("");
        TranslatableText translatableText2B = new TranslatableText("");
        TranslatableText translatableText3A = new TranslatableText("");
        TranslatableText translatableText3B = new TranslatableText("");
        TranslatableText translatableText6A = new TranslatableText("");
        TranslatableText translatableText6B = new TranslatableText("");

        switch (name) {
        case "health":
            translatableText1 = new TranslatableText("text.levelz.health_info_1", ConfigInit.CONFIG.healthBase);
            translatableText2A = new TranslatableText("text.levelz.health_info_2_1", ConfigInit.CONFIG.healthBonus);
            translatableText2B = new TranslatableText("text.levelz.health_info_2_2", ConfigInit.CONFIG.healthBonus);
            translatableText6A = new TranslatableText("text.levelz.health_max_lvl_1", ConfigInit.CONFIG.healthAbsorptionBonus);
            translatableText6B = new TranslatableText("text.levelz.health_max_lvl_2", ConfigInit.CONFIG.healthAbsorptionBonus);
            break;
        case "strength":
            translatableText1 = new TranslatableText("text.levelz.strength_info_1", ConfigInit.CONFIG.attackBase);
            translatableText2A = new TranslatableText("text.levelz.strength_info_2_1", ConfigInit.CONFIG.attackBonus);
            translatableText2B = new TranslatableText("text.levelz.strength_info_2_2", ConfigInit.CONFIG.attackBonus);
            translatableText6A = new TranslatableText("text.levelz.strength_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.strength_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F));
            break;
        case "agility":
            translatableText1 = new TranslatableText("text.levelz.agility_info_1", ConfigInit.CONFIG.movementBase);
            translatableText2A = new TranslatableText("text.levelz.agility_info_2_1", ConfigInit.CONFIG.movementBonus);
            translatableText2B = new TranslatableText("text.levelz.agility_info_2_2", ConfigInit.CONFIG.movementBonus);
            translatableText3A = new TranslatableText("text.levelz.agility_info_3_1", ConfigInit.CONFIG.movementFallBonus);
            translatableText3B = new TranslatableText("text.levelz.agility_info_3_2", ConfigInit.CONFIG.movementFallBonus);
            translatableText6A = new TranslatableText("text.levelz.agility_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.agility_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.movementMissChance * 100F));
            break;
        case "defense":
            translatableText1 = new TranslatableText("text.levelz.defense_info_1", ConfigInit.CONFIG.defenseBase);
            translatableText2A = new TranslatableText("text.levelz.defense_info_2_1", ConfigInit.CONFIG.defenseBonus);
            translatableText2B = new TranslatableText("text.levelz.defense_info_2_2", ConfigInit.CONFIG.defenseBonus);
            translatableText6A = new TranslatableText("text.levelz.defense_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.defense_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.defenseReflectChance * 100F));
            break;
        case "stamina":
            translatableText1 = new TranslatableText("text.levelz.stamina_info_1", ConfigInit.CONFIG.staminaBase);
            translatableText2A = new TranslatableText("text.levelz.stamina_info_2_1", ConfigInit.CONFIG.staminaBonus);
            translatableText2B = new TranslatableText("text.levelz.stamina_info_2_2", ConfigInit.CONFIG.staminaBonus);
            translatableText3A = new TranslatableText("text.levelz.stamina_info_3_1", ConfigInit.CONFIG.staminaHealthBonus);
            translatableText3B = new TranslatableText("text.levelz.stamina_info_3_2", ConfigInit.CONFIG.staminaHealthBonus);
            translatableText6A = new TranslatableText("text.levelz.stamina_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
            translatableText6B = new TranslatableText("text.levelz.stamina_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.staminaFoodBonus * 100F));
            break;
        case "luck":
            translatableText1 = new TranslatableText("text.levelz.luck_info_1", ConfigInit.CONFIG.luckBase);
            translatableText1B = new TranslatableText("text.levelz.luck_info_1_2");
            translatableText2A = new TranslatableText("text.levelz.luck_info_2_1", ConfigInit.CONFIG.luckBonus);
            translatableText2B = new TranslatableText("text.levelz.luck_info_2_2", ConfigInit.CONFIG.luckBonus);
            translatableText3A = new TranslatableText("text.levelz.luck_info_3_1", ConfigInit.CONFIG.luckCritBonus);
            translatableText3B = new TranslatableText("text.levelz.luck_info_3_2", ConfigInit.CONFIG.luckCritBonus);
            translatableText6A = new TranslatableText("text.levelz.luck_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.luck_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F));
            break;
        case "archery":
            translatableText2A = new TranslatableText("text.levelz.archery_info_2_1", ConfigInit.CONFIG.archeryBowExtraDamage);
            translatableText2B = new TranslatableText("text.levelz.archery_info_2_2", ConfigInit.CONFIG.archeryBowExtraDamage);
            translatableText3A = new TranslatableText("text.levelz.archery_info_3_1", ConfigInit.CONFIG.archeryCrossbowExtraDamage);
            translatableText3B = new TranslatableText("text.levelz.archery_info_3_2", ConfigInit.CONFIG.archeryCrossbowExtraDamage);
            translatableText6A = new TranslatableText("text.levelz.archery_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.archeryDoubleDamageChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.archery_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.archeryDoubleDamageChance * 100F));
            break;
        case "trade":
            translatableText2A = new TranslatableText("text.levelz.trade_info_2_1", ConfigInit.CONFIG.tradeXPBonus);
            translatableText2B = new TranslatableText("text.levelz.trade_info_2_2", ConfigInit.CONFIG.tradeXPBonus);
            translatableText3A = new TranslatableText("text.levelz.trade_info_3_1", ConfigInit.CONFIG.tradeBonus);
            translatableText3B = new TranslatableText("text.levelz.trade_info_3_2", ConfigInit.CONFIG.tradeBonus);
            translatableText6A = new TranslatableText("text.levelz.trade_max_lvl_1", ConfigInit.CONFIG.tradeReputation);
            translatableText6B = new TranslatableText("text.levelz.trade_max_lvl_2", ConfigInit.CONFIG.tradeReputation);
            break;
        case "smithing":
            translatableText2A = new TranslatableText("text.levelz.smithing_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F));
            translatableText2B = new TranslatableText("text.levelz.smithing_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F));
            translatableText3A = new TranslatableText("text.levelz.smithing_info_3_1", ConfigInit.CONFIG.smithingCostBonus);
            translatableText3B = new TranslatableText("text.levelz.smithing_info_3_2", ConfigInit.CONFIG.smithingCostBonus);
            translatableText6A = new TranslatableText("text.levelz.smithing_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.smithing_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F));
            break;
        case "mining":
            translatableText1 = new TranslatableText("text.levelz.mining_info_1");
            translatableText2A = new TranslatableText("text.levelz.mining_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
            translatableText2B = new TranslatableText("text.levelz.mining_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningOreChance * 100F));
            translatableText6A = new TranslatableText("text.levelz.mining_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));
            translatableText6B = new TranslatableText("text.levelz.mining_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.miningTntBonus * 100F));

            ZWSprite miningListIcon = new ZWSprite(name, client, 1);
            root.add(miningListIcon, 180, 7, 12, 9);

            break;
        case "farming":
            translatableText2A = new TranslatableText("text.levelz.farming_info_2_1", ConfigInit.CONFIG.farmingBase);
            translatableText2B = new TranslatableText("text.levelz.farming_info_2_2", ConfigInit.CONFIG.farmingBase);
            translatableText3A = new TranslatableText("text.levelz.farming_info_3_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingChanceBonus * 100F));
            translatableText3B = new TranslatableText("text.levelz.farming_info_3_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingChanceBonus * 100F));
            translatableText6A = new TranslatableText("text.levelz.farming_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingTwinChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.farming_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.farmingTwinChance * 100F));
            break;
        case "alchemy":
            translatableText1 = new TranslatableText("text.levelz.alchemy_info_1");
            translatableText6A = new TranslatableText("text.levelz.alchemy_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));
            translatableText6B = new TranslatableText("text.levelz.alchemy_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.alchemyPotionChance * 100F));

            ZWSprite alchemyListIcon = new ZWSprite(name, client, 1);
            root.add(alchemyListIcon, 180, 7, 12, 9);

            break;
        default:
            break;
        }

        if (!translatableText1.equals(new TranslatableText(""))) {
            plainPanel.add(new WLabel(translatableText1), 0, gridYSpace);
            gridYSpace += 14;
        }
        if (!translatableText1B.equals(new TranslatableText(""))) {
            plainPanel.add(new WLabel(translatableText1B), 0, gridYSpace);
            gridYSpace += 14;
        }
        if (!translatableText2A.equals(new TranslatableText(""))) {
            plainPanel.add(new WLabel(translatableText2A), 0, gridYSpace);
            if (!translatableText2B.equals(new TranslatableText(""))) {
                gridYSpace += 10;
                plainPanel.add(new WLabel(translatableText2B), 0, gridYSpace);
            }
            gridYSpace += 14;
        }
        if (!translatableText3A.equals(new TranslatableText(""))) {
            plainPanel.add(new WLabel(translatableText3A), 0, gridYSpace);
            if (!translatableText3B.equals(new TranslatableText(""))) {
                gridYSpace += 10;
                plainPanel.add(new WLabel(translatableText3B), 0, gridYSpace);
            }
            gridYSpace += 14;
        }

        // Special Item
        // 0: material, 1: skill, 2: level, 3: info, 4: boolean
        // Other
        // 0: skill, 1: level, 2: info, 3: boolean

        ArrayList<Object> unlockSkillList = new ArrayList<Object>();

        // Fill skill list
        for (int i = 0; i < LevelLists.listOfAllLists.size(); i++) {
            if (!LevelLists.listOfAllLists.get(i).isEmpty()) {
                if (LevelLists.listOfAllLists.get(i).get(0).toString().equals(name)) {
                    unlockSkillList.add(LevelLists.listOfAllLists.get(i).get(1));
                    unlockSkillList.add(LevelLists.listOfAllLists.get(i).get(2));
                    unlockSkillList.add(null);
                } else
                    for (int k = 0; k < LevelLists.listOfAllLists.get(i).size(); k += 5)
                        if (LevelLists.listOfAllLists.get(i).get(k + 1).toString().equals(name)) {
                            unlockSkillList.add(LevelLists.listOfAllLists.get(i).get(2 + k));
                            unlockSkillList.add(LevelLists.listOfAllLists.get(i).get(3 + k));
                            unlockSkillList.add(LevelLists.listOfAllLists.get(i).get(0 + k));
                        }
            }
        }
        // Sort list
        ArrayList<Object> sortedUnlockSkillList = new ArrayList<Object>();
        // If I set j = 0 it will include 0 level unlocks!
        for (int j = 1; j <= ConfigInit.CONFIG.maxLevel; j++) {
            for (int o = 0; o < unlockSkillList.size(); o += 3) {
                if (unlockSkillList.get(o).equals(j)) {
                    if (!sortedUnlockSkillList.contains(unlockSkillList.get(o))) {
                        sortedUnlockSkillList.add(unlockSkillList.get(o));
                    }
                    sortedUnlockSkillList.add(unlockSkillList.get(o + 1));
                    sortedUnlockSkillList.add(unlockSkillList.get(o + 2));
                }
            }
        }

        boolean hasLvlMaxUnlock = false;
        gridYSpace += 10;
        plainPanel.add(new WLabel(new TranslatableText("text.levelz.general_info")), 0, gridYSpace);
        gridYSpace += 16;
        // level, object, info, object, info,..., level,...

        for (int u = 0; u < sortedUnlockSkillList.size(); u++) {
            if (sortedUnlockSkillList.get(u) != null && sortedUnlockSkillList.get(u).getClass() == Integer.class) {
                // Add level category info
                plainPanel.add(new WLabel(new TranslatableText("text.levelz.level", sortedUnlockSkillList.get(u))), 0, gridYSpace);
                gridYSpace += 16;
                for (int g = 1; g < sortedUnlockSkillList.size() - u; g += 2) {
                    if (sortedUnlockSkillList.get(u + g).getClass() == Integer.class) {
                        break;
                    }
                    String string = sortedUnlockSkillList.get(u + g).toString();
                    if (string.contains("minecraft:custom_"))
                        string = sortedUnlockSkillList.get(u + g + 1).toString();

                    Identifier identifier = new Identifier(string);

                    if (!Registry.BLOCK.get(identifier).equals(Blocks.AIR))
                        string = Registry.BLOCK.get(identifier).getName().getString();
                    else if (!Registry.ITEM.get(identifier).equals(Items.AIR))
                        string = Registry.ITEM.get(identifier).getName().getString();
                    else if (!Registry.ENTITY_TYPE.get(identifier).equals(EntityType.PIG))
                        string = Registry.ENTITY_TYPE.get(identifier).getName().getString();

                    string = string.replace("minecraft:", "");
                    string = string.replace("_", " ");
                    if (sortedUnlockSkillList.get(u + g + 1) != null && !sortedUnlockSkillList.get(u + g).toString().contains("minecraft:custom_")) {
                        String otherString = sortedUnlockSkillList.get(u + g + 1).toString();
                        otherString = otherString.replace("_", " ");
                        plainPanel.add(new WLabel(new TranslatableText("text.levelz.object_info_2", StringUtils.capitalize(otherString), StringUtils.capitalize(string))), 10, gridYSpace);
                    } else
                        plainPanel.add(new WLabel(new TranslatableText("text.levelz.object_info_1", StringUtils.capitalize(string))), 10, gridYSpace);

                    gridYSpace += 16;
                }
                gridYSpace += 4;
                if (sortedUnlockSkillList.get(u).equals(ConfigInit.CONFIG.maxLevel)) {
                    hasLvlMaxUnlock = true;
                }
            }

        }

        if (!hasLvlMaxUnlock) {
            plainPanel.add(new WLabel(new TranslatableText("text.levelz.level", ConfigInit.CONFIG.maxLevel)), 0, gridYSpace);
        } else {
            gridYSpace -= 16;
        }
        if (!translatableText6A.equals(new TranslatableText(""))) {
            gridYSpace += 16;
            plainPanel.add(new WLabel(translatableText6A), 0, gridYSpace);
            if (!translatableText6B.equals(new TranslatableText(""))) {
                gridYSpace += 10;
                plainPanel.add(new WLabel(translatableText6B), 0, gridYSpace);
            }
        }
        if (gridYSpace <= 180) {
            scrollPanel.setScrollingVertically(TriState.FALSE);
        }
        plainPanel.setSize(200, gridYSpace);

        root.add(scrollPanel, 10, 20, 180, 185);
        root.validate(this);
    }
}
