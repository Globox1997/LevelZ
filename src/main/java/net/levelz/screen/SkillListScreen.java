package net.levelz.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.init.KeyInit;
import net.levelz.screen.widget.SkillListScrollableWidget;
import net.libz.api.Tab;
import net.libz.util.DrawTabHelper;
import net.libz.util.SortList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkillListScreen extends Screen implements Tab {

    private int backgroundWidth = 200;
    private int backgroundHeight = 215;
    private int x;
    private int y;

    private final String title;
    private final boolean crafing;

    public SkillListScreen(String title) {
        super(Text.of(title));
        this.title = title;
        this.crafing = this.title.equals("crafting");
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        this.addDrawableChild(new SkillScreen.WidgetButtonPage(this.x + 180, this.y + (this.crafing ? 5 : 7), 15 - (this.crafing ? 0 : 3), 13 - (this.crafing ? 0 : 4), this.crafing ? 0 : 57, 80,
                this.crafing, true, null, button -> {
                    if (this.crafing) {
                        this.client.setScreen(new SkillScreen());
                    } else {
                        this.client.setScreen(new SkillInfoScreen(this.title));
                    }
                }));

        List<Integer> levelList = new ArrayList<>();
        List<List<Integer>> objectList = new ArrayList<>();
        List<String> skillList = new ArrayList<>();
        if (Objects.equals(this.title, "mining")) {
            levelList = LevelLists.miningLevelList;
            objectList = LevelLists.miningBlockList;
        } else if (Objects.equals(this.title, "alchemy")) {
            levelList = LevelLists.brewingLevelList;
            objectList = LevelLists.brewingItemList;
        } else if (Objects.equals(this.title, "smithing")) {
            levelList = LevelLists.smithingLevelList;
            objectList = LevelLists.smithingItemList;
        } else if (this.crafing) {
            levelList = LevelLists.craftingLevelList;
            objectList = LevelLists.craftingItemList;
            skillList = LevelLists.craftingSkillList;
            if (ConfigInit.CONFIG.sortCraftingRecipesBySkill) {
                SortList.concurrentSort(skillList, skillList, levelList, objectList);
            }
        }

        this.addDrawableChild(new SkillListScrollableWidget(this.x + 10, this.y + 22, 183, 185, levelList, objectList, skillList, this.title, this.textRenderer));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(SkillInfoScreen.BACKGROUND_TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
        context.drawText(this.textRenderer, Text.translatable("text.levelz.locked_list", Text.translatable(String.format("spritetip.levelz.%s_skill", this.title))), this.x + 6, this.y + 7, 0x3F3F3F,
                false);

        super.render(context, mouseX, mouseY, delta);
        DrawTabHelper.drawTab(client, context, this, x, y, mouseX, mouseY);

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            if (ConfigInit.CONFIG.switch_screen) {
                this.client.setScreen(new SkillScreen());
            } else {
                this.close();
            }
            return true;

        } else if (KeyInit.screenKey.matchesKey(keyCode, scanCode)) {
            this.client.setScreen(new SkillScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        DrawTabHelper.onTabButtonClick(client, this, this.x, this.y, mouseX, mouseY, false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public int getWidth() {
        return this.backgroundWidth;
    }

    public int getHeight() {
        return this.backgroundHeight;
    }

}
