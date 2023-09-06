package net.levelz.screen.SkillInfoScreens;

import net.levelz.init.ConfigInit;
import net.levelz.screen.ISkillInfoScreen;
import net.levelz.screen.SkillListScreen;
import net.levelz.screen.SkillScreen;
import net.levelz.screen.widget.SkillScrollableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SmithingInfoScreen implements ISkillInfoScreen {
    @Override
    public String getStat() {
        return "smithing";
    }

    @Override
    public <T extends Element & Drawable & Selectable> T getSkillList(int x, int y, TextRenderer textRenderer, MinecraftClient client) {
        return (T) new SkillScreen.WidgetButtonPage(x + 180, y + 7, 12, 9, 45, 80, false, true, null, button -> {
            client.setScreen(new SkillListScreen(this.getStat()));
        });
    }

    @Override
    public <T extends Element & Drawable & Selectable> ArrayList<T> getWidgets(int x, int y, TextRenderer textRenderer) {
        List<Text> list = new ArrayList<Text>();
        list.add(null);
        list.add(null);
        list.add(Text.translatable("text.levelz.smithing_info_2_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F)));
        list.add(Text.translatable("text.levelz.smithing_info_2_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingToolChance * 100F)));
        list.add(Text.translatable("text.levelz.smithing_info_3_1", ConfigInit.CONFIG.smithingCostBonus));
        list.add(Text.translatable("text.levelz.smithing_info_3_2", ConfigInit.CONFIG.smithingCostBonus));
        list.add(Text.translatable("text.levelz.smithing_max_lvl_1", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F)));
        list.add(Text.translatable("text.levelz.smithing_max_lvl_2", new DecimalFormat("0.0").format(ConfigInit.CONFIG.smithingAnvilChance * 100F)));

        ArrayList<T> widgets = new ArrayList<>();
        widgets.add((T) new SkillScrollableWidget(x + 10, y + 22, 183, 185, list, getStat(), textRenderer));
        return widgets;
    }
}
