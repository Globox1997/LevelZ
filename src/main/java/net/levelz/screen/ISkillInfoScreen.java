package net.levelz.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.ArrayList;

public interface ISkillInfoScreen {
    String getStat();
    default <T extends net.minecraft.client.gui.Element & net.minecraft.client.gui.Drawable & net.minecraft.client.gui.Selectable> T getSkillList(int x, int y, TextRenderer textRenderer, MinecraftClient client){
        return null;
    }

    <T extends net.minecraft.client.gui.Element & net.minecraft.client.gui.Drawable & net.minecraft.client.gui.Selectable> ArrayList<T> getWidgets(int x, int y, TextRenderer textRenderer);
}
