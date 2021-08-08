package net.levelz.gui;

import java.util.ArrayList;

import org.apache.commons.lang3.text.WordUtils;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import net.fabricmc.fabric.api.util.TriState;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class InfoGui extends LightweightGuiDescription {

    public InfoGui(String name) {
        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, 215);

        root.add(new WLabel(new TranslatableText("text.levelz.info", WordUtils.capitalize(name))), 6, 7);

        WPlainPanel plainPanel = new WPlainPanel();

        WScrollPanel scrollPanel = new WScrollPanel(plainPanel);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);

        // root.add(new WLabel(new TranslatableText("text.levelz.general_info")), ConfigInit.CONFIG.test1, ConfigInit.CONFIG.test2);
        int gridYSpace = 0;
        for (int i = 0; i < LevelLists.listOfAllLists.size(); i++) {
            if (LevelLists.listOfAllLists.get(i).get(0).toString().equals(name)) {
                // gridYSpace += 16;
                // gridXSpace += 18;
                // System.out.println("Other stuff");
                // root.add(new WLabel(new TranslatableText("text.levelz.lock_info", LevelLists.listOfAllLists.get(i).get(0).toString(), LevelLists.listOfAllLists.get(i).get(2))),
                // ConfigInit.CONFIG.test1, ConfigInit.CONFIG.test2 + gridYSpace);
            } else if (LevelLists.listOfAllLists.get(i).get(1).toString().equals(name)) {
                for (int u = 0; u < LevelLists.listOfAllLists.get(i).size(); u++) { // 0;4;8
                    if (u % 4 == 0) {
                        plainPanel.add(new WLabel(new TranslatableText("text.levelz.lock_info", LevelLists.listOfAllLists.get(i).get(u).toString(), LevelLists.listOfAllLists.get(i).get(u + 2))),
                                ConfigInit.CONFIG.test1, ConfigInit.CONFIG.test2 + gridYSpace);
                        gridYSpace += ConfigInit.CONFIG.test5;
                    }
                }
                // Tool and other list things
                // System.out.println("TOOL and stuff");

                // gridXSpace += 18;
            }
        }

        // plainPanel.add(new WLabel(new TranslatableText("text.levelz.level", levelList.get(u))), 0, gridYSpace);

        String maxLevelString = "";
        switch (name) {
        case "health":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.healthAbsorptionBonus + " when full life";
            break;
        case "strength":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.attackDoubleDamageChance + "% chance to deal double damage with meele weapons";
            break;
        case "agility":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.movementMissChance + "% chance to evade an enemy attack";
            break;
        case "defense":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.defenseReflectChance + "% chance to reflect an enemy attack";
            break;
        case "stamina":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.staminaFoodBonus + "% bonus on food value";
            break;
        case "luck":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.luckSurviveChance + "% chance to survive a death scenario";
            break;
        case "archery":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.archeryDoubleDamageChance + "% chance to deal double damage with range weapons";
            break;
        case "trade":
            if (ConfigInit.CONFIG.tradeReputation) {
                maxLevelString = "Player gains reputation by villagers and can't lose it";
            }
            break;
        case "smithing":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.smithingAnvilChance + "% chance to regain used anvil experience";
            break;
        case "mining":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.miningTntBonus + "% tnt power increase";
            break;
        case "farming":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.farmingTwinChance + "% chance to breed twins";
            break;
        case "alchemy":
            maxLevelString = "Player gains " + ConfigInit.CONFIG.alchemyPotionChance + "% chance to get double effect value when drinking potions";
            break;
        default:
            maxLevelString = "";
            break;
        }

        plainPanel.add(new WLabel(new LiteralText(maxLevelString)), ConfigInit.CONFIG.test3, ConfigInit.CONFIG.test4);

        if (gridYSpace < 200) {
            scrollPanel.setScrollingVertically(TriState.FALSE);
        }
        plainPanel.setSize(200, gridYSpace);

        root.add(scrollPanel, 10, 20, 180, 185);
        root.validate(this);
    }
}
