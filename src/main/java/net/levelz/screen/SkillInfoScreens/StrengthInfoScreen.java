package net.levelz.screen.SkillInfoScreens;

import net.levelz.init.ConfigInit;
import net.levelz.screen.ISkillInfoScreen;
import net.levelz.screen.widget.SkillScrollableWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StrengthInfoScreen implements ISkillInfoScreen {
    @Override
    public String getStat() {
        return "strength";
    }

    @Override
    public <T extends Element & Drawable & Selectable> ArrayList<T> getWidgets(int x, int y, TextRenderer textRenderer) {
        List<Text> list = new ArrayList<Text>();
        list.add(Text.translatable("text.levelz.strength_info_1", ConfigInit.CONFIG.attackBase));
        list.add(null);
        list.add(Text.translatable("text.levelz.strength_info_2_1", ConfigInit.CONFIG.attackBonus));
        list.add(Text.translatable("text.levelz.strength_info_2_2", ConfigInit.CONFIG.attackBonus));
        list.add(null);
        list.add(null);
        list.add(Text.translatable("text.levelz.strength_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F)));
        list.add(Text.translatable("text.levelz.strength_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.attackDoubleDamageChance * 100F)));

        ArrayList<T> widgets = new ArrayList<>();
        widgets.add((T) new SkillScrollableWidget(x + 10, y + 22, 183, 185, list, getStat(), textRenderer));
        return widgets;
    }
}
