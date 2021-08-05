package net.levelz.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.fabricmc.fabric.api.util.TriState;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ListGui extends LightweightGuiDescription {

    public ListGui() {
        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(200, ConfigInit.CONFIG.test5);

        WGridPanel gridPanel = new WGridPanel();
        gridPanel.setSize(200, 1000);
        gridPanel.add(new WLabel(new TranslatableText("text.levelz.lockedList")), 0, 0);

        WScrollPanel scrollPanel = new WScrollPanel(gridPanel);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);

        // 9 blocks next to each other
        int gridYSpace = 1;
        for (int u = 0; u < LevelLists.miningLevelList.size(); u++) {
            gridPanel.add(new WLabel(new TranslatableText("text.levelz.level", LevelLists.miningLevelList.get(u))), 0, gridYSpace);// 1
            // int listSize = LevelLists.miningBlockList.get(u).size();
            int listSplitter = 0;
            int gridXSpace = 0;
            for (int k = 0; k < LevelLists.miningBlockList.get(u).size(); k++) {
                Identifier identifier = Registry.BLOCK.getId(Registry.BLOCK.get(LevelLists.miningBlockList.get(u).get(k)));
                String test = "minecraft:textures/block/" + identifier.getPath() + ".png";
                gridPanel.add(new WSprite(new Identifier(test)), gridXSpace, gridYSpace + 1, 1, 1); // 2,3
                gridXSpace++;
                listSplitter++;
                if (listSplitter % 3 == 0) {
                    gridYSpace++;
                    gridXSpace = 0;
                }
            }
            gridYSpace += 2;//
        }

        root.add(scrollPanel, ConfigInit.CONFIG.test3, ConfigInit.CONFIG.test4, ConfigInit.CONFIG.test1, ConfigInit.CONFIG.test2);
        root.validate(this);
    }
}
