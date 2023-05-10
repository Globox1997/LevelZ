package net.levelz.screen.widget;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.data.LevelLists;
import net.levelz.init.ConfigInit;
import net.levelz.screen.SkillInfoScreen;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class SkillScrollableWidget extends ScrollableWidget {

    private final List<Text> textList;
    private final String title;
    private final TextRenderer textRenderer;

    private final ArrayList<Object> sortedUnlockSkillList = new ArrayList<Object>();

    private int totalYSpace = 0;
    private int ySpace = 0;

    private boolean scrollbarDragged;

    public SkillScrollableWidget(int x, int y, int width, int height, List<Text> textList, String title, TextRenderer textRenderer) {
        super(x, y, width, height, Text.of(""));
        this.title = title;
        this.textRenderer = textRenderer;
        this.textList = textList;
        // Special Item
        // 0: material, 1: skill, 2: level, 3: info, 4: boolean
        // Other
        // 0: skill, 1: level, 2: info, 3: boolean
        ArrayList<Object> unlockSkillList = new ArrayList<Object>();

        // Fill skill list
        for (int o = 0; o < LevelLists.listOfAllLists.size(); o++) {
            if (!LevelLists.listOfAllLists.get(o).isEmpty()) {
                if (LevelLists.listOfAllLists.get(o).get(0).toString().equals(this.title)) {
                    unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(1));
                    unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(2));
                    unlockSkillList.add(null);
                } else
                    for (int k = 0; k < LevelLists.listOfAllLists.get(o).size(); k += 5)
                        if (LevelLists.listOfAllLists.get(o).get(k + 1).toString().equals(this.title)) {
                            unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(2 + k));
                            unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(3 + k));
                            unlockSkillList.add(LevelLists.listOfAllLists.get(o).get(0 + k));
                        }
            }
        }
        // Sort list
        // If I set j = 0 it will include 0 level unlocks!
        for (int k = 1; k <= ConfigInit.CONFIG.maxLevel; k++) {
            for (int o = 0; o < unlockSkillList.size(); o += 3) {
                if (unlockSkillList.get(o).equals(k)) {
                    if (!sortedUnlockSkillList.contains(unlockSkillList.get(o))) {
                        sortedUnlockSkillList.add(unlockSkillList.get(o));
                    }
                    sortedUnlockSkillList.add(unlockSkillList.get(o + 1));
                    sortedUnlockSkillList.add(unlockSkillList.get(o + 2));
                }
            }
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder narrationMessageBuilder) {
    }

    @Override
    protected int getContentsHeight() {
        return this.totalYSpace;
    }

    @Override
    protected boolean overflows() {
        return this.totalYSpace > 185;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 27;
    }

    @Override
    protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.ySpace = this.y;

        if (textList.size() > 0 && translatableTextIsNotBlank(textList.get(0))) {
            this.textRenderer.draw(matrices, textList.get(0), this.x, this.ySpace, 0x3F3F3F);
            this.ySpace += 14;
        }
        if (textList.size() > 1 && translatableTextIsNotBlank(textList.get(1))) {
            this.textRenderer.draw(matrices, textList.get(1), this.x, this.ySpace, 0x3F3F3F);
            this.ySpace += 14;
        }
        if (textList.size() > 2 && translatableTextIsNotBlank(textList.get(2))) {
            this.textRenderer.draw(matrices, textList.get(2), this.x, this.ySpace, 0x3F3F3F);
            if (textList.size() > 3 && translatableTextIsNotBlank(textList.get(3))) {
                this.ySpace += 10;
                this.textRenderer.draw(matrices, textList.get(3), this.x, this.ySpace, 0x3F3F3F);
            }
            this.ySpace += 14;
        }
        if (textList.size() > 4 && translatableTextIsNotBlank(textList.get(4))) {
            this.textRenderer.draw(matrices, textList.get(4), this.x, this.ySpace, 0x3F3F3F);
            if (textList.size() > 5 && translatableTextIsNotBlank(textList.get(5))) {
                this.ySpace += 10;
                this.textRenderer.draw(matrices, textList.get(5), this.x, this.ySpace, 0x3F3F3F);
            }
            this.ySpace += 14;
        }

        boolean hasLvlMaxUnlock = false;
        this.ySpace += 10;
        this.textRenderer.draw(matrices, Text.translatable("text.levelz.general_info"), this.x, this.ySpace, 0x3F3F3F);
        this.ySpace += 16;
        // level, object, info, object, info,..., level,...
        for (int u = 0; u < sortedUnlockSkillList.size(); u++) {
            if (sortedUnlockSkillList.get(u) != null && sortedUnlockSkillList.get(u).getClass() == Integer.class) {
                // Add level category info
                this.textRenderer.draw(matrices, Text.translatable("text.levelz.level", sortedUnlockSkillList.get(u)), this.x, this.ySpace, 0x3F3F3F);
                this.ySpace += 16;
                for (int g = 1; g < sortedUnlockSkillList.size() - u; g += 2) {
                    if (sortedUnlockSkillList.get(u + g).getClass() == Integer.class) {
                        break;
                    }
                    String string = sortedUnlockSkillList.get(u + g).toString();
                    if (string.contains("minecraft:custom_")) {
                        string = sortedUnlockSkillList.get(u + g + 1).toString();
                    }
                    Identifier identifier = new Identifier(string);
                    boolean hit = true;

                    if (!Registry.BLOCK.get(identifier).equals(Blocks.AIR)) {
                        string = Registry.BLOCK.get(identifier).getTranslationKey();
                    } else if (!Registry.ITEM.get(identifier).equals(Items.AIR)) {
                        string = Registry.ITEM.get(identifier).getTranslationKey();
                    } else if (!Registry.ENTITY_TYPE.get(identifier).equals(EntityType.PIG) && !EntityType.getId(EntityType.PIG).equals(identifier)) {
                        string = Registry.ENTITY_TYPE.get(identifier).getTranslationKey();
                    } else {
                        hit = false;
                    }
                    Language language = Language.getInstance();

                    if (hit)
                        string = language.get(string);
                    else {
                        String translationKey = String.format("text.levelz.object_info.%s", identifier.getPath());
                        if (language.hasTranslation(translationKey)) {
                            string = language.get(translationKey);
                        } else {
                            string = StringUtils.capitalize(string.replace("minecraft:", "").replaceAll("_", " ").replace(':', ' '));
                        }
                    }

                    if (sortedUnlockSkillList.get(u + g + 1) != null && !sortedUnlockSkillList.get(u + g).toString().contains("minecraft:custom_")) {
                        String otherString = sortedUnlockSkillList.get(u + g + 1).toString();
                        String translationKey = String.format("text.levelz.object_prefix.%s", otherString);
                        if (language.hasTranslation(translationKey)) {
                            otherString = language.get(translationKey);
                        } else {
                            otherString = otherString.replace('_', ' ');
                        }
                        this.textRenderer.draw(matrices, Text.translatable("text.levelz.object_info_2", StringUtils.capitalize(otherString), string), this.x + 10, this.ySpace, 0x3F3F3F);
                    } else {
                        this.textRenderer.draw(matrices, Text.translatable("text.levelz.object_info_1", string), this.x + 10, this.ySpace, 0x3F3F3F);
                    }
                    this.ySpace += 16;
                }
                if (sortedUnlockSkillList.get(u).equals(ConfigInit.CONFIG.maxLevel)) {
                    hasLvlMaxUnlock = true;
                } else {
                    this.ySpace += 4;
                }
            }

        }

        if (!hasLvlMaxUnlock) {
            this.textRenderer.draw(matrices, Text.translatable("text.levelz.level", ConfigInit.CONFIG.maxLevel), this.x, this.ySpace, 0x3F3F3F);
        } else {
            this.ySpace -= 16;
        }
        if (textList.size() > 6 && translatableTextIsNotBlank(textList.get(6))) {
            this.ySpace += 16;
            this.textRenderer.draw(matrices, textList.get(6), this.x + 10, this.ySpace, 0x3F3F3F);
            if (textList.size() > 7 && translatableTextIsNotBlank(textList.get(7))) {
                this.ySpace += 10;
                this.textRenderer.draw(matrices, textList.get(7), this.x + 10, this.ySpace, 0x3F3F3F);
            }
        }
        if (this.totalYSpace == 0) {
            this.totalYSpace = this.ySpace - this.y;
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ScrollableWidget.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        matrices.push();
        matrices.translate(0.0, -getScrollY(), 0.0);
        this.renderContents(matrices, mouseX, mouseY, delta);
        matrices.pop();
        ScrollableWidget.disableScissor();
        this.renderOverlay(matrices);
    }

    @Override
    protected void renderOverlay(MatrixStack matrices) {
        if (this.overflows()) {
            int l = Math.max(this.y + 1, (int) this.getScrollY() * (this.height - 27) / this.getMaxScrollY() + this.y - 1);
            RenderSystem.setShaderTexture(0, SkillInfoScreen.BACKGROUND_TEXTURE);
            this.drawTexture(matrices, this.x + 177, this.y, 200, 0, 8, 185);
            this.drawTexture(matrices, this.x + 178, l, 208, 0, 6, 27);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.visible) {
            return false;
        }
        this.setScrollY(this.getScrollY() - amount * this.getDeltaYPerScroll());
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrollbarDragged = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        boolean bl = this.isWithinBounds(mouseX, mouseY);
        boolean bl2 = this.overflows() && mouseX >= (double) (this.x + this.width - 5) && mouseX <= (double) (this.x + this.width + 1) && mouseY >= (double) this.y
                && mouseY < (double) (this.y + this.height);
        this.setFocused(bl || bl2);
        if (bl2 && button == 0) {
            this.scrollbarDragged = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!(this.visible && this.isFocused() && this.scrollbarDragged)) {
            return false;
        }
        if (mouseY < (double) this.y) {
            this.setScrollY(0.0);
        } else if (mouseY > (double) (this.y + this.height)) {
            this.setScrollY(this.getMaxScrollY());
        } else {
            int i = this.getScrollbarThumbHeight();
            double d = Math.max(1, this.getMaxScrollY() / (this.height - i));
            this.setScrollY(this.getScrollY() + deltaY * d);
        }
        return true;
    }

    @Override
    protected boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= (double) this.x && mouseX < (double) (this.x + this.width + 1) && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
    }

    private int getContentsHeightWithPadding() {
        return this.getContentsHeight() + 4;
    }

    private int getScrollbarThumbHeight() {
        return MathHelper.clamp((int) ((float) (this.height * this.height) / (float) this.getContentsHeightWithPadding()), 32, this.height);
    }

    private boolean translatableTextIsNotBlank(@Nullable Text text) {
        if (text == null) {
            return false;
        }
        if (!Texts.hasTranslation(text)) {
            return false;
        }
        return !Language.getInstance().get(text.getString()).isBlank();
    }

}
