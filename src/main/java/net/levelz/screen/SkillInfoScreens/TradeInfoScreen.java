package net.levelz.screen.SkillInfoScreens;

import net.levelz.init.ConfigInit;
import net.levelz.screen.ISkillInfoScreen;
import net.levelz.screen.widget.SkillScrollableWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TradeInfoScreen implements ISkillInfoScreen {
    @Override
    public String getStat() {
        return "trade";
    }

    @Override
    public <T extends Element & Drawable & Selectable> ArrayList<T> getWidgets(int x, int y, TextRenderer textRenderer) {
        List<Text> list = new ArrayList<Text>();
        list.add(null);
        list.add(null);
        list.add(Text.translatable("text.levelz.trade_info_2_1", ConfigInit.CONFIG.tradeXPBonus));
        list.add(Text.translatable("text.levelz.trade_info_2_2", ConfigInit.CONFIG.tradeXPBonus));
        list.add(Text.translatable("text.levelz.trade_info_3_1", ConfigInit.CONFIG.tradeBonus));
        list.add(Text.translatable("text.levelz.trade_info_3_2", ConfigInit.CONFIG.tradeBonus));
        list.add(Text.translatable("text.levelz.trade_max_lvl_1", ConfigInit.CONFIG.tradeReputation));
        list.add(Text.translatable("text.levelz.trade_max_lvl_2", ConfigInit.CONFIG.tradeReputation));

        ArrayList<T> widgets = new ArrayList<>();
        widgets.add((T) new SkillScrollableWidget(x + 10, y + 22, 183, 185, list, getStat(), textRenderer));
        return widgets;
    }
}
