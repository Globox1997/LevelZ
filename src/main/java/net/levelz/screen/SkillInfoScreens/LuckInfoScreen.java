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

public class LuckInfoScreen implements ISkillInfoScreen {
    @Override
    public String getStat() {
        return "luck";
    }

    @Override
    public <T extends Element & Drawable & Selectable> ArrayList<T> getWidgets(int x, int y, TextRenderer textRenderer) {
        List<Text> list = new ArrayList<Text>();
        list.add(Text.translatable("text.levelz.luck_info_1", ConfigInit.CONFIG.luckBase));
        list.add(Text.translatable("text.levelz.luck_info_1_2"));
        list.add(Text.translatable("text.levelz.luck_info_2_1", ConfigInit.CONFIG.luckBonus));
        list.add(Text.translatable("text.levelz.luck_info_2_2", ConfigInit.CONFIG.luckBonus));
        list.add(Text.translatable("text.levelz.luck_info_3_1", ConfigInit.CONFIG.luckCritBonus));
        list.add(Text.translatable("text.levelz.luck_info_3_2", ConfigInit.CONFIG.luckCritBonus));
        list.add(Text.translatable("text.levelz.luck_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F)));
        list.add(Text.translatable("text.levelz.luck_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.luckSurviveChance * 100F)));

        ArrayList<T> widgets = new ArrayList<>();
        widgets.add((T) new SkillScrollableWidget(x + 10, y + 22, 183, 185, list, getStat(), textRenderer));
        return widgets;
    }
}
